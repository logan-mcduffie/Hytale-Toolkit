package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import java.util.List;

class PerMessageDeflateEncoder extends DeflateEncoder {
   private boolean compressing;

   PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
      super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
      super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
   }

   @Override
   public boolean acceptOutboundMessage(Object msg) throws Exception {
      if (!super.acceptOutboundMessage(msg)) {
         return false;
      } else {
         WebSocketFrame wsFrame = (WebSocketFrame)msg;
         if (this.extensionEncoderFilter().mustSkip(wsFrame)) {
            if (this.compressing) {
               throw new IllegalStateException("Cannot skip per message deflate encoder, compression in progress");
            } else {
               return false;
            }
         } else {
            return (wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) && (wsFrame.rsv() & 4) == 0
               || wsFrame instanceof ContinuationWebSocketFrame && this.compressing;
         }
      }
   }

   @Override
   protected int rsv(WebSocketFrame msg) {
      return !(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame) ? msg.rsv() : msg.rsv() | 4;
   }

   @Override
   protected boolean removeFrameTail(WebSocketFrame msg) {
      return msg.isFinalFragment();
   }

   @Override
   protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
      super.encode(ctx, msg, out);
      if (msg.isFinalFragment()) {
         this.compressing = false;
      } else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }
   }
}
