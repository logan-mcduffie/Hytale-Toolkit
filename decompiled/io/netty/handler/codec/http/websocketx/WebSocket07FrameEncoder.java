package io.netty.handler.codec.http.websocketx;

public class WebSocket07FrameEncoder extends WebSocket08FrameEncoder {
   public WebSocket07FrameEncoder(boolean maskPayload) {
      super(maskPayload);
   }

   public WebSocket07FrameEncoder(WebSocketFrameMaskGenerator maskGenerator) {
      super(maskGenerator);
   }
}
