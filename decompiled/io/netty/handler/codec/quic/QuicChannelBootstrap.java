package io.netty.handler.codec.quic;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class QuicChannelBootstrap {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(QuicChannelBootstrap.class);
   private final Channel parent;
   private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> attrs = new HashMap<>();
   private final Map<ChannelOption<?>, Object> streamOptions = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> streamAttrs = new HashMap<>();
   private SocketAddress local;
   private SocketAddress remote;
   private QuicConnectionAddress connectionAddress = QuicConnectionAddress.EPHEMERAL;
   private ChannelHandler handler;
   private ChannelHandler streamHandler;

   @Deprecated
   public QuicChannelBootstrap(Channel parent) {
      Quic.ensureAvailability();
      this.parent = ObjectUtil.checkNotNull(parent, "parent");
   }

   public <T> QuicChannelBootstrap option(ChannelOption<T> option, @Nullable T value) {
      Quic.updateOptions(this.options, option, value);
      return this;
   }

   public <T> QuicChannelBootstrap attr(AttributeKey<T> key, @Nullable T value) {
      Quic.updateAttributes(this.attrs, key, value);
      return this;
   }

   public QuicChannelBootstrap handler(ChannelHandler handler) {
      this.handler = ObjectUtil.checkNotNull(handler, "handler");
      return this;
   }

   public <T> QuicChannelBootstrap streamOption(ChannelOption<T> option, @Nullable T value) {
      Quic.updateOptions(this.streamOptions, option, value);
      return this;
   }

   public <T> QuicChannelBootstrap streamAttr(AttributeKey<T> key, @Nullable T value) {
      Quic.updateAttributes(this.streamAttrs, key, value);
      return this;
   }

   public QuicChannelBootstrap streamHandler(ChannelHandler streamHandler) {
      this.streamHandler = ObjectUtil.checkNotNull(streamHandler, "streamHandler");
      return this;
   }

   public QuicChannelBootstrap localAddress(SocketAddress local) {
      this.local = ObjectUtil.checkNotNull(local, "local");
      return this;
   }

   public QuicChannelBootstrap remoteAddress(SocketAddress remote) {
      this.remote = ObjectUtil.checkNotNull(remote, "remote");
      return this;
   }

   public QuicChannelBootstrap connectionAddress(QuicConnectionAddress connectionAddress) {
      this.connectionAddress = ObjectUtil.checkNotNull(connectionAddress, "connectionAddress");
      return this;
   }

   public Future<QuicChannel> connect() {
      return this.connect(this.parent.eventLoop().newPromise());
   }

   public Future<QuicChannel> connect(Promise<QuicChannel> promise) {
      if (this.handler == null && this.streamHandler == null) {
         throw new IllegalStateException("handler and streamHandler not set");
      } else {
         SocketAddress local = this.local;
         if (local == null) {
            local = this.parent.localAddress();
         }

         if (local == null) {
            local = new InetSocketAddress(0);
         }

         SocketAddress remote = this.remote;
         if (remote == null) {
            remote = this.parent.remoteAddress();
         }

         if (remote == null) {
            throw new IllegalStateException("remote not set");
         } else {
            QuicConnectionAddress address = this.connectionAddress;
            QuicChannel channel = QuicheQuicChannel.forClient(
               this.parent,
               (InetSocketAddress)local,
               (InetSocketAddress)remote,
               this.streamHandler,
               Quic.toOptionsArray(this.streamOptions),
               Quic.toAttributesArray(this.streamAttrs)
            );
            Quic.setupChannel(channel, Quic.toOptionsArray(this.options), Quic.toAttributesArray(this.attrs), this.handler, logger);
            EventLoop eventLoop = this.parent.eventLoop();
            eventLoop.register(channel).addListener(future -> {
               Throwable cause = future.cause();
               if (cause != null) {
                  promise.setFailure(cause);
               } else {
                  channel.connect(address).addListener(f -> {
                     Throwable error = f.cause();
                     if (error != null) {
                        promise.setFailure(error);
                     } else {
                        promise.setSuccess(channel);
                     }
                  });
               }
            });
            return promise;
         }
      }
   }
}
