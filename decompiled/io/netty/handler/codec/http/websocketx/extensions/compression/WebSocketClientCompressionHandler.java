package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;

@ChannelHandler.Sharable
public final class WebSocketClientCompressionHandler extends WebSocketClientExtensionHandler {
   @Deprecated
   public static final WebSocketClientCompressionHandler INSTANCE = new WebSocketClientCompressionHandler();

   private WebSocketClientCompressionHandler() {
      this(0);
   }

   public WebSocketClientCompressionHandler(int maxAllocation) {
      super(
         new PerMessageDeflateClientExtensionHandshaker(maxAllocation),
         new DeflateFrameClientExtensionHandshaker(false, maxAllocation),
         new DeflateFrameClientExtensionHandshaker(true, maxAllocation)
      );
   }
}
