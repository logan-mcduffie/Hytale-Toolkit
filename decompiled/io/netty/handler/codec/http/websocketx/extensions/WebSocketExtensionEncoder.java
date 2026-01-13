package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public abstract class WebSocketExtensionEncoder extends MessageToMessageEncoder<WebSocketFrame> {
   public WebSocketExtensionEncoder() {
      super(WebSocketFrame.class);
   }
}
