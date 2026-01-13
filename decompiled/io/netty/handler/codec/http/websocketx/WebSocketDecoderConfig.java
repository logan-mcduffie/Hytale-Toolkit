package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketDecoderConfig {
   static final WebSocketDecoderConfig DEFAULT = new WebSocketDecoderConfig(65536, true, false, false, true, true);
   private final int maxFramePayloadLength;
   private final boolean expectMaskedFrames;
   private final boolean allowMaskMismatch;
   private final boolean allowExtensions;
   private final boolean closeOnProtocolViolation;
   private final boolean withUTF8Validator;

   private WebSocketDecoderConfig(
      int maxFramePayloadLength,
      boolean expectMaskedFrames,
      boolean allowMaskMismatch,
      boolean allowExtensions,
      boolean closeOnProtocolViolation,
      boolean withUTF8Validator
   ) {
      this.maxFramePayloadLength = maxFramePayloadLength;
      this.expectMaskedFrames = expectMaskedFrames;
      this.allowMaskMismatch = allowMaskMismatch;
      this.allowExtensions = allowExtensions;
      this.closeOnProtocolViolation = closeOnProtocolViolation;
      this.withUTF8Validator = withUTF8Validator;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean expectMaskedFrames() {
      return this.expectMaskedFrames;
   }

   public boolean allowMaskMismatch() {
      return this.allowMaskMismatch;
   }

   public boolean allowExtensions() {
      return this.allowExtensions;
   }

   public boolean closeOnProtocolViolation() {
      return this.closeOnProtocolViolation;
   }

   public boolean withUTF8Validator() {
      return this.withUTF8Validator;
   }

   @Override
   public String toString() {
      return "WebSocketDecoderConfig [maxFramePayloadLength="
         + this.maxFramePayloadLength
         + ", expectMaskedFrames="
         + this.expectMaskedFrames
         + ", allowMaskMismatch="
         + this.allowMaskMismatch
         + ", allowExtensions="
         + this.allowExtensions
         + ", closeOnProtocolViolation="
         + this.closeOnProtocolViolation
         + ", withUTF8Validator="
         + this.withUTF8Validator
         + "]";
   }

   public WebSocketDecoderConfig.Builder toBuilder() {
      return new WebSocketDecoderConfig.Builder(this);
   }

   public static WebSocketDecoderConfig.Builder newBuilder() {
      return new WebSocketDecoderConfig.Builder(DEFAULT);
   }

   public static final class Builder {
      private int maxFramePayloadLength;
      private boolean expectMaskedFrames;
      private boolean allowMaskMismatch;
      private boolean allowExtensions;
      private boolean closeOnProtocolViolation;
      private boolean withUTF8Validator;

      private Builder(WebSocketDecoderConfig decoderConfig) {
         ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
         this.maxFramePayloadLength = decoderConfig.maxFramePayloadLength();
         this.expectMaskedFrames = decoderConfig.expectMaskedFrames();
         this.allowMaskMismatch = decoderConfig.allowMaskMismatch();
         this.allowExtensions = decoderConfig.allowExtensions();
         this.closeOnProtocolViolation = decoderConfig.closeOnProtocolViolation();
         this.withUTF8Validator = decoderConfig.withUTF8Validator();
      }

      public WebSocketDecoderConfig.Builder maxFramePayloadLength(int maxFramePayloadLength) {
         this.maxFramePayloadLength = maxFramePayloadLength;
         return this;
      }

      public WebSocketDecoderConfig.Builder expectMaskedFrames(boolean expectMaskedFrames) {
         this.expectMaskedFrames = expectMaskedFrames;
         return this;
      }

      public WebSocketDecoderConfig.Builder allowMaskMismatch(boolean allowMaskMismatch) {
         this.allowMaskMismatch = allowMaskMismatch;
         return this;
      }

      public WebSocketDecoderConfig.Builder allowExtensions(boolean allowExtensions) {
         this.allowExtensions = allowExtensions;
         return this;
      }

      public WebSocketDecoderConfig.Builder closeOnProtocolViolation(boolean closeOnProtocolViolation) {
         this.closeOnProtocolViolation = closeOnProtocolViolation;
         return this;
      }

      public WebSocketDecoderConfig.Builder withUTF8Validator(boolean withUTF8Validator) {
         this.withUTF8Validator = withUTF8Validator;
         return this;
      }

      public WebSocketDecoderConfig build() {
         return new WebSocketDecoderConfig(
            this.maxFramePayloadLength,
            this.expectMaskedFrames,
            this.allowMaskMismatch,
            this.allowExtensions,
            this.closeOnProtocolViolation,
            this.withUTF8Validator
         );
      }
   }
}
