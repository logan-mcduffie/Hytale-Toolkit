package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketServerProtocolConfig {
   static final long DEFAULT_HANDSHAKE_TIMEOUT_MILLIS = 10000L;
   private final String websocketPath;
   private final String subprotocols;
   private final boolean checkStartsWith;
   private final long handshakeTimeoutMillis;
   private final long forceCloseTimeoutMillis;
   private final boolean handleCloseFrames;
   private final WebSocketCloseStatus sendCloseFrame;
   private final boolean dropPongFrames;
   private final WebSocketDecoderConfig decoderConfig;

   private WebSocketServerProtocolConfig(
      String websocketPath,
      String subprotocols,
      boolean checkStartsWith,
      long handshakeTimeoutMillis,
      long forceCloseTimeoutMillis,
      boolean handleCloseFrames,
      WebSocketCloseStatus sendCloseFrame,
      boolean dropPongFrames,
      WebSocketDecoderConfig decoderConfig
   ) {
      this.websocketPath = websocketPath;
      this.subprotocols = subprotocols;
      this.checkStartsWith = checkStartsWith;
      this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
      this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
      this.handleCloseFrames = handleCloseFrames;
      this.sendCloseFrame = sendCloseFrame;
      this.dropPongFrames = dropPongFrames;
      this.decoderConfig = decoderConfig == null ? WebSocketDecoderConfig.DEFAULT : decoderConfig;
   }

   public String websocketPath() {
      return this.websocketPath;
   }

   public String subprotocols() {
      return this.subprotocols;
   }

   public boolean checkStartsWith() {
      return this.checkStartsWith;
   }

   public long handshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   public boolean handleCloseFrames() {
      return this.handleCloseFrames;
   }

   public WebSocketCloseStatus sendCloseFrame() {
      return this.sendCloseFrame;
   }

   public boolean dropPongFrames() {
      return this.dropPongFrames;
   }

   public WebSocketDecoderConfig decoderConfig() {
      return this.decoderConfig;
   }

   @Override
   public String toString() {
      return "WebSocketServerProtocolConfig {websocketPath="
         + this.websocketPath
         + ", subprotocols="
         + this.subprotocols
         + ", checkStartsWith="
         + this.checkStartsWith
         + ", handshakeTimeoutMillis="
         + this.handshakeTimeoutMillis
         + ", forceCloseTimeoutMillis="
         + this.forceCloseTimeoutMillis
         + ", handleCloseFrames="
         + this.handleCloseFrames
         + ", sendCloseFrame="
         + this.sendCloseFrame
         + ", dropPongFrames="
         + this.dropPongFrames
         + ", decoderConfig="
         + this.decoderConfig
         + "}";
   }

   public WebSocketServerProtocolConfig.Builder toBuilder() {
      return new WebSocketServerProtocolConfig.Builder(this);
   }

   public static WebSocketServerProtocolConfig.Builder newBuilder() {
      return new WebSocketServerProtocolConfig.Builder(
         "/", null, false, 10000L, 0L, true, WebSocketCloseStatus.NORMAL_CLOSURE, true, WebSocketDecoderConfig.DEFAULT
      );
   }

   public static final class Builder {
      private String websocketPath;
      private String subprotocols;
      private boolean checkStartsWith;
      private long handshakeTimeoutMillis;
      private long forceCloseTimeoutMillis;
      private boolean handleCloseFrames;
      private WebSocketCloseStatus sendCloseFrame;
      private boolean dropPongFrames;
      private WebSocketDecoderConfig decoderConfig;
      private WebSocketDecoderConfig.Builder decoderConfigBuilder;

      private Builder(WebSocketServerProtocolConfig serverConfig) {
         this(
            ObjectUtil.checkNotNull(serverConfig, "serverConfig").websocketPath(),
            serverConfig.subprotocols(),
            serverConfig.checkStartsWith(),
            serverConfig.handshakeTimeoutMillis(),
            serverConfig.forceCloseTimeoutMillis(),
            serverConfig.handleCloseFrames(),
            serverConfig.sendCloseFrame(),
            serverConfig.dropPongFrames(),
            serverConfig.decoderConfig()
         );
      }

      private Builder(
         String websocketPath,
         String subprotocols,
         boolean checkStartsWith,
         long handshakeTimeoutMillis,
         long forceCloseTimeoutMillis,
         boolean handleCloseFrames,
         WebSocketCloseStatus sendCloseFrame,
         boolean dropPongFrames,
         WebSocketDecoderConfig decoderConfig
      ) {
         this.websocketPath = websocketPath;
         this.subprotocols = subprotocols;
         this.checkStartsWith = checkStartsWith;
         this.handshakeTimeoutMillis = handshakeTimeoutMillis;
         this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
         this.handleCloseFrames = handleCloseFrames;
         this.sendCloseFrame = sendCloseFrame;
         this.dropPongFrames = dropPongFrames;
         this.decoderConfig = decoderConfig;
      }

      public WebSocketServerProtocolConfig.Builder websocketPath(String websocketPath) {
         this.websocketPath = websocketPath;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder subprotocols(String subprotocols) {
         this.subprotocols = subprotocols;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder checkStartsWith(boolean checkStartsWith) {
         this.checkStartsWith = checkStartsWith;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder handshakeTimeoutMillis(long handshakeTimeoutMillis) {
         this.handshakeTimeoutMillis = handshakeTimeoutMillis;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder forceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
         this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder handleCloseFrames(boolean handleCloseFrames) {
         this.handleCloseFrames = handleCloseFrames;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder sendCloseFrame(WebSocketCloseStatus sendCloseFrame) {
         this.sendCloseFrame = sendCloseFrame;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder dropPongFrames(boolean dropPongFrames) {
         this.dropPongFrames = dropPongFrames;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder decoderConfig(WebSocketDecoderConfig decoderConfig) {
         this.decoderConfig = decoderConfig == null ? WebSocketDecoderConfig.DEFAULT : decoderConfig;
         this.decoderConfigBuilder = null;
         return this;
      }

      private WebSocketDecoderConfig.Builder decoderConfigBuilder() {
         if (this.decoderConfigBuilder == null) {
            this.decoderConfigBuilder = this.decoderConfig.toBuilder();
         }

         return this.decoderConfigBuilder;
      }

      public WebSocketServerProtocolConfig.Builder maxFramePayloadLength(int maxFramePayloadLength) {
         this.decoderConfigBuilder().maxFramePayloadLength(maxFramePayloadLength);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder expectMaskedFrames(boolean expectMaskedFrames) {
         this.decoderConfigBuilder().expectMaskedFrames(expectMaskedFrames);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder allowMaskMismatch(boolean allowMaskMismatch) {
         this.decoderConfigBuilder().allowMaskMismatch(allowMaskMismatch);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder allowExtensions(boolean allowExtensions) {
         this.decoderConfigBuilder().allowExtensions(allowExtensions);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder closeOnProtocolViolation(boolean closeOnProtocolViolation) {
         this.decoderConfigBuilder().closeOnProtocolViolation(closeOnProtocolViolation);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder withUTF8Validator(boolean withUTF8Validator) {
         this.decoderConfigBuilder().withUTF8Validator(withUTF8Validator);
         return this;
      }

      public WebSocketServerProtocolConfig build() {
         return new WebSocketServerProtocolConfig(
            this.websocketPath,
            this.subprotocols,
            this.checkStartsWith,
            this.handshakeTimeoutMillis,
            this.forceCloseTimeoutMillis,
            this.handleCloseFrames,
            this.sendCloseFrame,
            this.dropPongFrames,
            this.decoderConfigBuilder == null ? this.decoderConfig : this.decoderConfigBuilder.build()
         );
      }
   }
}
