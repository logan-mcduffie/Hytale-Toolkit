package io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketServerExtension extends WebSocketExtension {
   WebSocketExtensionData newReponseData();
}
