package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Utf8FrameValidator extends ChannelInboundHandlerAdapter {
   private final boolean closeOnProtocolViolation;
   private int fragmentedFramesCount;
   private Utf8Validator utf8Validator;

   public Utf8FrameValidator() {
      this(true);
   }

   public Utf8FrameValidator(boolean closeOnProtocolViolation) {
      this.closeOnProtocolViolation = closeOnProtocolViolation;
   }

   private static boolean isControlFrame(WebSocketFrame frame) {
      return frame instanceof CloseWebSocketFrame || frame instanceof PingWebSocketFrame || frame instanceof PongWebSocketFrame;
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof WebSocketFrame) {
         WebSocketFrame frame = (WebSocketFrame)msg;

         try {
            if (frame.isFinalFragment()) {
               if (!isControlFrame(frame)) {
                  this.fragmentedFramesCount = 0;
                  if (frame instanceof TextWebSocketFrame || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                     this.checkUTF8String(frame.content());
                     this.utf8Validator.finish();
                  }
               }
            } else {
               if (this.fragmentedFramesCount == 0) {
                  if (frame instanceof TextWebSocketFrame) {
                     this.checkUTF8String(frame.content());
                  }
               } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                  this.checkUTF8String(frame.content());
               }

               this.fragmentedFramesCount++;
            }
         } catch (CorruptedWebSocketFrameException var5) {
            this.protocolViolation(ctx, frame, var5);
         }
      }

      super.channelRead(ctx, msg);
   }

   private void checkUTF8String(ByteBuf buffer) {
      if (this.utf8Validator == null) {
         this.utf8Validator = new Utf8Validator();
      }

      this.utf8Validator.check(buffer);
   }

   private void protocolViolation(ChannelHandlerContext ctx, WebSocketFrame frame, CorruptedWebSocketFrameException ex) {
      frame.release();
      if (this.closeOnProtocolViolation && ctx.channel().isOpen()) {
         WebSocketCloseStatus closeStatus = ex.closeStatus();
         String reasonText = ex.getMessage();
         if (reasonText == null) {
            reasonText = closeStatus.reasonText();
         }

         CloseWebSocketFrame closeFrame = new CloseWebSocketFrame(closeStatus.code(), reasonText);
         ctx.writeAndFlush(closeFrame).addListener(ChannelFutureListener.CLOSE);
      }

      throw ex;
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      super.exceptionCaught(ctx, cause);
   }
}
