package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketExtensionFilter {
   WebSocketExtensionFilter NEVER_SKIP = new WebSocketExtensionFilter() {
      @Override
      public boolean mustSkip(WebSocketFrame frame) {
         return false;
      }
   };
   WebSocketExtensionFilter ALWAYS_SKIP = new WebSocketExtensionFilter() {
      @Override
      public boolean mustSkip(WebSocketFrame frame) {
         return true;
      }
   };

   boolean mustSkip(WebSocketFrame var1);
}
