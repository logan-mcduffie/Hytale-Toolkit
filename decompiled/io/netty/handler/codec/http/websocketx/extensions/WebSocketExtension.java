package io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketExtension {
   int RSV1 = 4;
   int RSV2 = 2;
   int RSV3 = 1;

   int rsv();

   WebSocketExtensionEncoder newExtensionEncoder();

   WebSocketExtensionDecoder newExtensionDecoder();
}
