package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ThrowableUtil;
import java.nio.channels.ClosedChannelException;
import org.jetbrains.annotations.Nullable;

final class Http3ControlStreamInboundHandler extends Http3FrameTypeInboundValidationHandler<Http3ControlStreamFrame> {
   final boolean server;
   private final ChannelHandler controlFrameHandler;
   private final QpackEncoder qpackEncoder;
   private final Http3ControlStreamOutboundHandler remoteControlStreamHandler;
   private boolean firstFrameRead;
   private Long receivedGoawayId;
   private Long receivedMaxPushId;

   Http3ControlStreamInboundHandler(
      boolean server, @Nullable ChannelHandler controlFrameHandler, QpackEncoder qpackEncoder, Http3ControlStreamOutboundHandler remoteControlStreamHandler
   ) {
      super(Http3ControlStreamFrame.class);
      this.server = server;
      this.controlFrameHandler = controlFrameHandler;
      this.qpackEncoder = qpackEncoder;
      this.remoteControlStreamHandler = remoteControlStreamHandler;
   }

   boolean isServer() {
      return this.server;
   }

   boolean isGoAwayReceived() {
      return this.receivedGoawayId != null;
   }

   long maxPushIdReceived() {
      return this.receivedMaxPushId == null ? -1L : this.receivedMaxPushId;
   }

   private boolean forwardControlFrames() {
      return this.controlFrameHandler != null;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      super.handlerAdded(ctx);
      if (this.controlFrameHandler != null) {
         ctx.pipeline().addLast(this.controlFrameHandler);
      }
   }

   @Override
   void readFrameDiscarded(ChannelHandlerContext ctx, Object discardedFrame) {
      if (!this.firstFrameRead && !(discardedFrame instanceof Http3SettingsFrame)) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_MISSING_SETTINGS, "Missing settings frame.", this.forwardControlFrames());
      }
   }

   void channelRead(ChannelHandlerContext ctx, Http3ControlStreamFrame frame) throws QpackException {
      boolean isSettingsFrame = frame instanceof Http3SettingsFrame;
      if (!this.firstFrameRead && !isSettingsFrame) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_MISSING_SETTINGS, "Missing settings frame.", this.forwardControlFrames());
         ReferenceCountUtil.release(frame);
      } else if (this.firstFrameRead && isSettingsFrame) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_FRAME_UNEXPECTED, "Second settings frame received.", this.forwardControlFrames());
         ReferenceCountUtil.release(frame);
      } else {
         this.firstFrameRead = true;
         boolean valid;
         if (isSettingsFrame) {
            valid = this.handleHttp3SettingsFrame(ctx, (Http3SettingsFrame)frame);
         } else if (frame instanceof Http3GoAwayFrame) {
            valid = this.handleHttp3GoAwayFrame(ctx, (Http3GoAwayFrame)frame);
         } else if (frame instanceof Http3MaxPushIdFrame) {
            valid = this.handleHttp3MaxPushIdFrame(ctx, (Http3MaxPushIdFrame)frame);
         } else if (frame instanceof Http3CancelPushFrame) {
            valid = this.handleHttp3CancelPushFrame(ctx, (Http3CancelPushFrame)frame);
         } else {
            assert frame instanceof Http3UnknownFrame;

            valid = true;
         }

         if (valid && this.controlFrameHandler != null) {
            ctx.fireChannelRead(frame);
         } else {
            ReferenceCountUtil.release(frame);
         }
      }
   }

   private boolean handleHttp3SettingsFrame(ChannelHandlerContext ctx, Http3SettingsFrame settingsFrame) throws QpackException {
      QuicChannel quicChannel = (QuicChannel)ctx.channel().parent();
      QpackAttributes qpackAttributes = Http3.getQpackAttributes(quicChannel);

      assert qpackAttributes != null;

      GenericFutureListener<Future<? super QuicStreamChannel>> closeOnFailure = future -> {
         if (!future.isSuccess()) {
            Http3CodecUtils.criticalStreamClosed(ctx);
         }
      };
      if (qpackAttributes.dynamicTableDisabled()) {
         this.qpackEncoder.configureDynamicTable(qpackAttributes, 0L, 0);
         return true;
      } else {
         quicChannel.createStream(
               QuicStreamType.UNIDIRECTIONAL,
               new Http3ControlStreamInboundHandler.QPackEncoderStreamInitializer(
                  this.qpackEncoder,
                  qpackAttributes,
                  settingsFrame.getOrDefault(Http3SettingsFrame.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY, 0L),
                  settingsFrame.getOrDefault(Http3SettingsFrame.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS, 0L)
               )
            )
            .addListener(closeOnFailure);
         quicChannel.createStream(QuicStreamType.UNIDIRECTIONAL, new Http3ControlStreamInboundHandler.QPackDecoderStreamInitializer(qpackAttributes))
            .addListener(closeOnFailure);
         return true;
      }
   }

   private boolean handleHttp3GoAwayFrame(ChannelHandlerContext ctx, Http3GoAwayFrame goAwayFrame) {
      long id = goAwayFrame.id();
      if (!this.server && id % 4L != 0L) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_FRAME_UNEXPECTED, "GOAWAY received with ID of non-request stream.", this.forwardControlFrames());
         return false;
      } else if (this.receivedGoawayId != null && id > this.receivedGoawayId) {
         Http3CodecUtils.connectionError(
            ctx, Http3ErrorCode.H3_ID_ERROR, "GOAWAY received with ID larger than previously received.", this.forwardControlFrames()
         );
         return false;
      } else {
         this.receivedGoawayId = id;
         return true;
      }
   }

   private boolean handleHttp3MaxPushIdFrame(ChannelHandlerContext ctx, Http3MaxPushIdFrame frame) {
      long id = frame.id();
      if (!this.server) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_FRAME_UNEXPECTED, "MAX_PUSH_ID received by client.", this.forwardControlFrames());
         return false;
      } else if (this.receivedMaxPushId != null && id < this.receivedMaxPushId) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_ID_ERROR, "MAX_PUSH_ID reduced limit.", this.forwardControlFrames());
         return false;
      } else {
         this.receivedMaxPushId = id;
         return true;
      }
   }

   private boolean handleHttp3CancelPushFrame(ChannelHandlerContext ctx, Http3CancelPushFrame cancelPushFrame) {
      Long maxPushId = this.server ? this.receivedMaxPushId : this.remoteControlStreamHandler.sentMaxPushId();
      if (maxPushId != null && maxPushId >= cancelPushFrame.id()) {
         return true;
      } else {
         Http3CodecUtils.connectionError(
            ctx, Http3ErrorCode.H3_ID_ERROR, "CANCEL_PUSH received with an ID greater than MAX_PUSH_ID.", this.forwardControlFrames()
         );
         return false;
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.fireChannelReadComplete();
      Http3CodecUtils.readIfNoAutoRead(ctx);
   }

   @Override
   public boolean isSharable() {
      return false;
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
      if (evt instanceof ChannelInputShutdownEvent) {
         Http3CodecUtils.criticalStreamClosed(ctx);
      }

      ctx.fireUserEventTriggered(evt);
   }

   private abstract static class AbstractQPackStreamInitializer extends ChannelInboundHandlerAdapter {
      private final int streamType;
      protected final QpackAttributes attributes;

      AbstractQPackStreamInitializer(int streamType, QpackAttributes attributes) {
         this.streamType = streamType;
         this.attributes = attributes;
      }

      @Override
      public final void channelActive(ChannelHandlerContext ctx) {
         ByteBuf buffer = ctx.alloc().buffer(8);
         Http3CodecUtils.writeVariableLengthInteger(buffer, this.streamType);
         Http3CodecUtils.closeOnFailure(ctx.writeAndFlush(buffer));
         this.streamAvailable(ctx);
         ctx.fireChannelActive();
      }

      @Override
      public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
         this.streamClosed(ctx);
         if (evt instanceof ChannelInputShutdownEvent) {
            Http3CodecUtils.criticalStreamClosed(ctx);
         }

         ctx.fireUserEventTriggered(evt);
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) {
         this.streamClosed(ctx);
         Http3CodecUtils.criticalStreamClosed(ctx);
         ctx.fireChannelInactive();
      }

      protected abstract void streamAvailable(ChannelHandlerContext var1);

      protected abstract void streamClosed(ChannelHandlerContext var1);
   }

   private static final class QPackDecoderStreamInitializer extends Http3ControlStreamInboundHandler.AbstractQPackStreamInitializer {
      private static final ClosedChannelException DECODER_STREAM_INACTIVE = ThrowableUtil.unknownStackTrace(
         new ClosedChannelException(), ClosedChannelException.class, "streamClosed()"
      );

      private QPackDecoderStreamInitializer(QpackAttributes attributes) {
         super(3, attributes);
      }

      @Override
      protected void streamAvailable(ChannelHandlerContext ctx) {
         this.attributes.decoderStream((QuicStreamChannel)ctx.channel());
      }

      @Override
      protected void streamClosed(ChannelHandlerContext ctx) {
         this.attributes.decoderStreamInactive(DECODER_STREAM_INACTIVE);
      }
   }

   private static final class QPackEncoderStreamInitializer extends Http3ControlStreamInboundHandler.AbstractQPackStreamInitializer {
      private static final ClosedChannelException ENCODER_STREAM_INACTIVE = ThrowableUtil.unknownStackTrace(
         new ClosedChannelException(), ClosedChannelException.class, "streamClosed()"
      );
      private final QpackEncoder encoder;
      private final long maxTableCapacity;
      private final long blockedStreams;

      QPackEncoderStreamInitializer(QpackEncoder encoder, QpackAttributes attributes, long maxTableCapacity, long blockedStreams) {
         super(2, attributes);
         this.encoder = encoder;
         this.maxTableCapacity = maxTableCapacity;
         this.blockedStreams = blockedStreams;
      }

      @Override
      protected void streamAvailable(ChannelHandlerContext ctx) {
         QuicStreamChannel stream = (QuicStreamChannel)ctx.channel();
         this.attributes.encoderStream(stream);

         try {
            this.encoder.configureDynamicTable(this.attributes, this.maxTableCapacity, QpackUtil.toIntOrThrow(this.blockedStreams));
         } catch (QpackException var4) {
            Http3CodecUtils.connectionError(
               ctx, new Http3Exception(Http3ErrorCode.QPACK_ENCODER_STREAM_ERROR, "Dynamic table configuration failed.", var4), true
            );
         }
      }

      @Override
      protected void streamClosed(ChannelHandlerContext ctx) {
         this.attributes.encoderStreamInactive(ENCODER_STREAM_INACTIVE);
      }
   }
}
