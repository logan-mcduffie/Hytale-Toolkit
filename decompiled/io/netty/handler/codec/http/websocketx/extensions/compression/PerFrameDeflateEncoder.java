package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerFrameDeflateEncoder extends DeflateEncoder {
   PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
      super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
      super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
   }

   @Override
   public boolean acceptOutboundMessage(Object msg) throws Exception {
      if (!super.acceptOutboundMessage(msg)) {
         return false;
      } else {
         WebSocketFrame wsFrame = (WebSocketFrame)msg;
         return this.extensionEncoderFilter().mustSkip(wsFrame)
            ? false
            : (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame || msg instanceof ContinuationWebSocketFrame)
               && wsFrame.content().readableBytes() > 0
               && (wsFrame.rsv() & 4) == 0;
      }
   }

   @Override
   protected int rsv(WebSocketFrame msg) {
      return msg.rsv() | 4;
   }

   @Override
   protected boolean removeFrameTail(WebSocketFrame msg) {
      return true;
   }
}
