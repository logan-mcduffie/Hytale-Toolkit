package io.netty.handler.codec.http.websocketx;

public class WebSocket13FrameEncoder extends WebSocket08FrameEncoder {
   public WebSocket13FrameEncoder(boolean maskPayload) {
      super(maskPayload);
   }

   public WebSocket13FrameEncoder(WebSocketFrameMaskGenerator maskGenerator) {
      super(maskGenerator);
   }
}
