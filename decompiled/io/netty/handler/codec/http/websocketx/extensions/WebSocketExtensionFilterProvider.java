package io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketExtensionFilterProvider {
   WebSocketExtensionFilterProvider DEFAULT = new WebSocketExtensionFilterProvider() {
      @Override
      public WebSocketExtensionFilter encoderFilter() {
         return WebSocketExtensionFilter.NEVER_SKIP;
      }

      @Override
      public WebSocketExtensionFilter decoderFilter() {
         return WebSocketExtensionFilter.NEVER_SKIP;
      }
   };

   WebSocketExtensionFilter encoderFilter();

   WebSocketExtensionFilter decoderFilter();
}
