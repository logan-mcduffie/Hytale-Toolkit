package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import org.jetbrains.annotations.Nullable;

final class Http3ControlStreamOutboundHandler extends Http3FrameTypeDuplexValidationHandler<Http3ControlStreamFrame> {
   private final boolean server;
   private final ChannelHandler codec;
   private Long sentMaxPushId;
   private Long sendGoAwayId;
   private Http3SettingsFrame localSettings;

   Http3ControlStreamOutboundHandler(boolean server, Http3SettingsFrame localSettings, ChannelHandler codec) {
      super(Http3ControlStreamFrame.class);
      this.server = server;
      this.localSettings = ObjectUtil.checkNotNull(localSettings, "localSettings");
      this.codec = ObjectUtil.checkNotNull(codec, "codec");
   }

   @Nullable
   Long sentMaxPushId() {
      return this.sentMaxPushId;
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) {
      ByteBuf buffer = ctx.alloc().buffer(8);
      Http3CodecUtils.writeVariableLengthInteger(buffer, 0L);
      ctx.write(buffer);
      ctx.pipeline().addFirst(this.codec);

      assert this.localSettings != null;

      Http3CodecUtils.closeOnFailure(ctx.writeAndFlush(this.localSettings));
      this.localSettings = null;
      ctx.fireChannelActive();
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
      if (evt instanceof ChannelInputShutdownEvent) {
         Http3CodecUtils.criticalStreamClosed(ctx);
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) {
      Http3CodecUtils.criticalStreamClosed(ctx);
      ctx.fireChannelInactive();
   }

   void write(ChannelHandlerContext ctx, Http3ControlStreamFrame msg, ChannelPromise promise) {
      if (msg instanceof Http3MaxPushIdFrame && !this.handleHttp3MaxPushIdFrame(promise, (Http3MaxPushIdFrame)msg)) {
         ReferenceCountUtil.release(msg);
      } else if (msg instanceof Http3GoAwayFrame && !this.handleHttp3GoAwayFrame(promise, (Http3GoAwayFrame)msg)) {
         ReferenceCountUtil.release(msg);
      } else {
         ctx.write(msg, promise);
      }
   }

   private boolean handleHttp3MaxPushIdFrame(ChannelPromise promise, Http3MaxPushIdFrame maxPushIdFrame) {
      long id = maxPushIdFrame.id();
      if (this.sentMaxPushId != null && id < this.sentMaxPushId) {
         promise.setFailure(new Http3Exception(Http3ErrorCode.H3_ID_ERROR, "MAX_PUSH_ID reduced limit."));
         return false;
      } else {
         this.sentMaxPushId = maxPushIdFrame.id();
         return true;
      }
   }

   private boolean handleHttp3GoAwayFrame(ChannelPromise promise, Http3GoAwayFrame goAwayFrame) {
      long id = goAwayFrame.id();
      if (this.server && id % 4L != 0L) {
         promise.setFailure(new Http3Exception(Http3ErrorCode.H3_ID_ERROR, "GOAWAY id not valid : " + id));
         return false;
      } else if (this.sendGoAwayId != null && id > this.sendGoAwayId) {
         promise.setFailure(new Http3Exception(Http3ErrorCode.H3_ID_ERROR, "GOAWAY id is bigger then the last sent: " + id + " > " + this.sendGoAwayId));
         return false;
      } else {
         this.sendGoAwayId = id;
         return true;
      }
   }

   @Override
   public boolean isSharable() {
      return false;
   }
}
