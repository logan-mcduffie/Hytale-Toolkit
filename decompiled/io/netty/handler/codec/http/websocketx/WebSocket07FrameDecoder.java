package io.netty.handler.codec.http.websocketx;

public class WebSocket07FrameDecoder extends WebSocket08FrameDecoder {
   public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
      this(
         WebSocketDecoderConfig.newBuilder()
            .expectMaskedFrames(expectMaskedFrames)
            .allowExtensions(allowExtensions)
            .maxFramePayloadLength(maxFramePayloadLength)
            .build()
      );
   }

   public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
      this(
         WebSocketDecoderConfig.newBuilder()
            .expectMaskedFrames(expectMaskedFrames)
            .allowExtensions(allowExtensions)
            .maxFramePayloadLength(maxFramePayloadLength)
            .allowMaskMismatch(allowMaskMismatch)
            .build()
      );
   }

   public WebSocket07FrameDecoder(WebSocketDecoderConfig decoderConfig) {
      super(decoderConfig);
   }
}
