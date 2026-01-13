package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;

public class WebSocketServerCompressionHandler extends WebSocketServerExtensionHandler {
   @Deprecated
   public WebSocketServerCompressionHandler() {
      this(0);
   }

   public WebSocketServerCompressionHandler(int maxAllocation) {
      super(new PerMessageDeflateServerExtensionHandshaker(maxAllocation), new DeflateFrameServerExtensionHandshaker(6, maxAllocation));
   }
}
