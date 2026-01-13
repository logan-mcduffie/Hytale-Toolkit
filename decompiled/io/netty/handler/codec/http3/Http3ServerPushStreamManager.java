package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamChannelBootstrap;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

public final class Http3ServerPushStreamManager {
   private static final AtomicLongFieldUpdater<Http3ServerPushStreamManager> nextIdUpdater = AtomicLongFieldUpdater.newUpdater(
      Http3ServerPushStreamManager.class, "nextId"
   );
   private static final Object CANCELLED_STREAM = new Object();
   private static final Object PUSH_ID_GENERATED = new Object();
   private static final Object AWAITING_STREAM_ESTABLISHMENT = new Object();
   private final QuicChannel channel;
   private final ConcurrentMap<Long, Object> pushStreams;
   private final ChannelInboundHandler controlStreamListener;
   private volatile long nextId;

   public Http3ServerPushStreamManager(QuicChannel channel) {
      this(channel, 8);
   }

   public Http3ServerPushStreamManager(QuicChannel channel, int initialPushStreamsCountHint) {
      this.channel = Objects.requireNonNull(channel, "channel");
      this.pushStreams = PlatformDependent.newConcurrentHashMap(initialPushStreamsCountHint);
      this.controlStreamListener = new ChannelInboundHandlerAdapter() {
         @Override
         public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof Http3CancelPushFrame) {
               long pushId = ((Http3CancelPushFrame)msg).id();
               if (pushId >= Http3ServerPushStreamManager.this.nextId) {
                  Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_ID_ERROR, "CANCEL_PUSH id greater than the last known id", true);
                  return;
               }

               Http3ServerPushStreamManager.this.pushStreams.computeIfPresent(pushId, (id, existing) -> {
                  if (existing == Http3ServerPushStreamManager.AWAITING_STREAM_ESTABLISHMENT) {
                     return Http3ServerPushStreamManager.CANCELLED_STREAM;
                  } else if (existing == Http3ServerPushStreamManager.PUSH_ID_GENERATED) {
                     throw new IllegalStateException("Unexpected push stream state " + existing + " for pushId: " + id);
                  } else {
                     assert existing instanceof QuicStreamChannel;

                     ((QuicStreamChannel)existing).close();
                     return null;
                  }
               });
            }

            ReferenceCountUtil.release(msg);
         }
      };
   }

   public boolean isPushAllowed() {
      return this.isPushAllowed(Http3.maxPushIdReceived(this.channel));
   }

   public long reserveNextPushId() {
      long maxPushId = Http3.maxPushIdReceived(this.channel);
      if (this.isPushAllowed(maxPushId)) {
         return this.nextPushId();
      } else {
         throw new IllegalStateException("MAX allowed push ID: " + maxPushId + ", next push ID: " + this.nextId);
      }
   }

   public Future<QuicStreamChannel> newPushStream(long pushId, @Nullable ChannelHandler handler) {
      Promise<QuicStreamChannel> promise = this.channel.eventLoop().newPromise();
      this.newPushStream(pushId, handler, promise);
      return promise;
   }

   public void newPushStream(long pushId, @Nullable ChannelHandler handler, Promise<QuicStreamChannel> promise) {
      this.validatePushId(pushId);
      this.channel.createStream(QuicStreamType.UNIDIRECTIONAL, this.pushStreamInitializer(pushId, handler), promise);
      setupCancelPushIfStreamCreationFails(pushId, promise, this.channel);
   }

   public void newPushStream(
      long pushId, @Nullable ChannelHandler handler, UnaryOperator<QuicStreamChannelBootstrap> bootstrapConfigurator, Promise<QuicStreamChannel> promise
   ) {
      this.validatePushId(pushId);
      QuicStreamChannelBootstrap bootstrap = bootstrapConfigurator.apply(this.channel.newStreamBootstrap());
      bootstrap.type(QuicStreamType.UNIDIRECTIONAL).handler(this.pushStreamInitializer(pushId, handler)).create(promise);
      setupCancelPushIfStreamCreationFails(pushId, promise, this.channel);
   }

   public ChannelInboundHandler controlStreamListener() {
      return this.controlStreamListener;
   }

   private boolean isPushAllowed(long maxPushId) {
      return this.nextId <= maxPushId;
   }

   private long nextPushId() {
      long pushId = nextIdUpdater.getAndIncrement(this);
      this.pushStreams.put(pushId, PUSH_ID_GENERATED);
      return pushId;
   }

   private void validatePushId(long pushId) {
      if (!this.pushStreams.replace(pushId, PUSH_ID_GENERATED, AWAITING_STREAM_ESTABLISHMENT)) {
         throw new IllegalArgumentException("Unknown push ID: " + pushId);
      }
   }

   private Http3PushStreamServerInitializer pushStreamInitializer(final long pushId, @Nullable final ChannelHandler handler) {
      final Http3PushStreamServerInitializer initializer;
      if (handler instanceof Http3PushStreamServerInitializer) {
         initializer = (Http3PushStreamServerInitializer)handler;
      } else {
         initializer = null;
      }

      return new Http3PushStreamServerInitializer(pushId) {
         @Override
         protected void initPushStream(final QuicStreamChannel ch) {
            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
               private boolean stateUpdated;

               @Override
               public void channelActive(ChannelHandlerContext ctx) {
                  if (!this.stateUpdated) {
                     this.updatePushStreamsMap();
                  }
               }

               @Override
               public void handlerAdded(ChannelHandlerContext ctx) {
                  if (!this.stateUpdated && ctx.channel().isActive()) {
                     this.updatePushStreamsMap();
                  }
               }

               private void updatePushStreamsMap() {
                  assert !this.stateUpdated;

                  this.stateUpdated = true;
                  Http3ServerPushStreamManager.this.pushStreams.compute(pushId, (id, existing) -> {
                     if (existing == Http3ServerPushStreamManager.AWAITING_STREAM_ESTABLISHMENT) {
                        return ch;
                     } else if (existing == Http3ServerPushStreamManager.CANCELLED_STREAM) {
                        ch.close();
                        return null;
                     } else {
                        throw new IllegalStateException("Unexpected push stream state " + existing + " for pushId: " + id);
                     }
                  });
               }

               @Override
               public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                  if (evt == ChannelInputShutdownReadComplete.INSTANCE) {
                     Http3ServerPushStreamManager.this.pushStreams.remove(pushId);
                  }

                  ctx.fireUserEventTriggered(evt);
               }
            });
            if (initializer != null) {
               initializer.initPushStream(ch);
            } else if (handler != null) {
               ch.pipeline().addLast(handler);
            }
         }
      };
   }

   private static void setupCancelPushIfStreamCreationFails(long pushId, Future<QuicStreamChannel> future, QuicChannel channel) {
      if (future.isDone()) {
         sendCancelPushIfFailed(future, pushId, channel);
      } else {
         future.addListener(f -> sendCancelPushIfFailed(future, pushId, channel));
      }
   }

   private static void sendCancelPushIfFailed(Future<QuicStreamChannel> future, long pushId, QuicChannel channel) {
      if (!future.isSuccess()) {
         QuicStreamChannel localControlStream = Http3.getLocalControlStream(channel);

         assert localControlStream != null;

         localControlStream.writeAndFlush(new DefaultHttp3CancelPushFrame(pushId));
      }
   }
}
