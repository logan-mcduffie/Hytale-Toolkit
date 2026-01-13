package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;

public final class WebSocketClientProtocolConfig {
   static final boolean DEFAULT_PERFORM_MASKING = true;
   static final boolean DEFAULT_ALLOW_MASK_MISMATCH = false;
   static final boolean DEFAULT_HANDLE_CLOSE_FRAMES = true;
   static final boolean DEFAULT_DROP_PONG_FRAMES = true;
   static final boolean DEFAULT_GENERATE_ORIGIN_HEADER = true;
   static final boolean DEFAULT_WITH_UTF8_VALIDATOR = true;
   private final URI webSocketUri;
   private final String subprotocol;
   private final WebSocketVersion version;
   private final boolean allowExtensions;
   private final HttpHeaders customHeaders;
   private final int maxFramePayloadLength;
   private final boolean performMasking;
   private final boolean allowMaskMismatch;
   private final boolean handleCloseFrames;
   private final WebSocketCloseStatus sendCloseFrame;
   private final boolean dropPongFrames;
   private final long handshakeTimeoutMillis;
   private final long forceCloseTimeoutMillis;
   private final boolean absoluteUpgradeUrl;
   private final boolean generateOriginHeader;
   private final boolean withUTF8Validator;

   private WebSocketClientProtocolConfig(
      URI webSocketUri,
      String subprotocol,
      WebSocketVersion version,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch,
      boolean handleCloseFrames,
      WebSocketCloseStatus sendCloseFrame,
      boolean dropPongFrames,
      long handshakeTimeoutMillis,
      long forceCloseTimeoutMillis,
      boolean absoluteUpgradeUrl,
      boolean generateOriginHeader,
      boolean withUTF8Validator
   ) {
      this.webSocketUri = webSocketUri;
      this.subprotocol = subprotocol;
      this.version = version;
      this.allowExtensions = allowExtensions;
      this.customHeaders = customHeaders;
      this.maxFramePayloadLength = maxFramePayloadLength;
      this.performMasking = performMasking;
      this.allowMaskMismatch = allowMaskMismatch;
      this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
      this.handleCloseFrames = handleCloseFrames;
      this.sendCloseFrame = sendCloseFrame;
      this.dropPongFrames = dropPongFrames;
      this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
      this.absoluteUpgradeUrl = absoluteUpgradeUrl;
      this.generateOriginHeader = generateOriginHeader;
      this.withUTF8Validator = withUTF8Validator;
   }

   public URI webSocketUri() {
      return this.webSocketUri;
   }

   public String subprotocol() {
      return this.subprotocol;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public boolean allowExtensions() {
      return this.allowExtensions;
   }

   public HttpHeaders customHeaders() {
      return this.customHeaders;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean performMasking() {
      return this.performMasking;
   }

   public boolean allowMaskMismatch() {
      return this.allowMaskMismatch;
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

   public long handshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   public boolean absoluteUpgradeUrl() {
      return this.absoluteUpgradeUrl;
   }

   public boolean generateOriginHeader() {
      return this.generateOriginHeader;
   }

   public boolean withUTF8Validator() {
      return this.withUTF8Validator;
   }

   @Override
   public String toString() {
      return "WebSocketClientProtocolConfig {webSocketUri="
         + this.webSocketUri
         + ", subprotocol="
         + this.subprotocol
         + ", version="
         + this.version
         + ", allowExtensions="
         + this.allowExtensions
         + ", customHeaders="
         + this.customHeaders
         + ", maxFramePayloadLength="
         + this.maxFramePayloadLength
         + ", performMasking="
         + this.performMasking
         + ", allowMaskMismatch="
         + this.allowMaskMismatch
         + ", handleCloseFrames="
         + this.handleCloseFrames
         + ", sendCloseFrame="
         + this.sendCloseFrame
         + ", dropPongFrames="
         + this.dropPongFrames
         + ", handshakeTimeoutMillis="
         + this.handshakeTimeoutMillis
         + ", forceCloseTimeoutMillis="
         + this.forceCloseTimeoutMillis
         + ", absoluteUpgradeUrl="
         + this.absoluteUpgradeUrl
         + ", generateOriginHeader="
         + this.generateOriginHeader
         + "}";
   }

   public WebSocketClientProtocolConfig.Builder toBuilder() {
      return new WebSocketClientProtocolConfig.Builder(this);
   }

   public static WebSocketClientProtocolConfig.Builder newBuilder() {
      return new WebSocketClientProtocolConfig.Builder(
         URI.create("https://localhost/"),
         null,
         WebSocketVersion.V13,
         false,
         EmptyHttpHeaders.INSTANCE,
         65536,
         true,
         false,
         true,
         WebSocketCloseStatus.NORMAL_CLOSURE,
         true,
         10000L,
         -1L,
         false,
         true,
         true
      );
   }

   public static final class Builder {
      private URI webSocketUri;
      private String subprotocol;
      private WebSocketVersion version;
      private boolean allowExtensions;
      private HttpHeaders customHeaders;
      private int maxFramePayloadLength;
      private boolean performMasking;
      private boolean allowMaskMismatch;
      private boolean handleCloseFrames;
      private WebSocketCloseStatus sendCloseFrame;
      private boolean dropPongFrames;
      private long handshakeTimeoutMillis;
      private long forceCloseTimeoutMillis;
      private boolean absoluteUpgradeUrl;
      private boolean generateOriginHeader;
      private boolean withUTF8Validator;

      private Builder(WebSocketClientProtocolConfig clientConfig) {
         this(
            ObjectUtil.checkNotNull(clientConfig, "clientConfig").webSocketUri(),
            clientConfig.subprotocol(),
            clientConfig.version(),
            clientConfig.allowExtensions(),
            clientConfig.customHeaders(),
            clientConfig.maxFramePayloadLength(),
            clientConfig.performMasking(),
            clientConfig.allowMaskMismatch(),
            clientConfig.handleCloseFrames(),
            clientConfig.sendCloseFrame(),
            clientConfig.dropPongFrames(),
            clientConfig.handshakeTimeoutMillis(),
            clientConfig.forceCloseTimeoutMillis(),
            clientConfig.absoluteUpgradeUrl(),
            clientConfig.generateOriginHeader(),
            clientConfig.withUTF8Validator()
         );
      }

      private Builder(
         URI webSocketUri,
         String subprotocol,
         WebSocketVersion version,
         boolean allowExtensions,
         HttpHeaders customHeaders,
         int maxFramePayloadLength,
         boolean performMasking,
         boolean allowMaskMismatch,
         boolean handleCloseFrames,
         WebSocketCloseStatus sendCloseFrame,
         boolean dropPongFrames,
         long handshakeTimeoutMillis,
         long forceCloseTimeoutMillis,
         boolean absoluteUpgradeUrl,
         boolean generateOriginHeader,
         boolean withUTF8Validator
      ) {
         this.webSocketUri = webSocketUri;
         this.subprotocol = subprotocol;
         this.version = version;
         this.allowExtensions = allowExtensions;
         this.customHeaders = customHeaders;
         this.maxFramePayloadLength = maxFramePayloadLength;
         this.performMasking = performMasking;
         this.allowMaskMismatch = allowMaskMismatch;
         this.handleCloseFrames = handleCloseFrames;
         this.sendCloseFrame = sendCloseFrame;
         this.dropPongFrames = dropPongFrames;
         this.handshakeTimeoutMillis = handshakeTimeoutMillis;
         this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
         this.absoluteUpgradeUrl = absoluteUpgradeUrl;
         this.generateOriginHeader = generateOriginHeader;
         this.withUTF8Validator = withUTF8Validator;
      }

      public WebSocketClientProtocolConfig.Builder webSocketUri(String webSocketUri) {
         return this.webSocketUri(URI.create(webSocketUri));
      }

      public WebSocketClientProtocolConfig.Builder webSocketUri(URI webSocketUri) {
         this.webSocketUri = webSocketUri;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder subprotocol(String subprotocol) {
         this.subprotocol = subprotocol;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder version(WebSocketVersion version) {
         this.version = version;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder allowExtensions(boolean allowExtensions) {
         this.allowExtensions = allowExtensions;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder customHeaders(HttpHeaders customHeaders) {
         this.customHeaders = customHeaders;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder maxFramePayloadLength(int maxFramePayloadLength) {
         this.maxFramePayloadLength = maxFramePayloadLength;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder performMasking(boolean performMasking) {
         this.performMasking = performMasking;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder allowMaskMismatch(boolean allowMaskMismatch) {
         this.allowMaskMismatch = allowMaskMismatch;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder handleCloseFrames(boolean handleCloseFrames) {
         this.handleCloseFrames = handleCloseFrames;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder sendCloseFrame(WebSocketCloseStatus sendCloseFrame) {
         this.sendCloseFrame = sendCloseFrame;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder dropPongFrames(boolean dropPongFrames) {
         this.dropPongFrames = dropPongFrames;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder handshakeTimeoutMillis(long handshakeTimeoutMillis) {
         this.handshakeTimeoutMillis = handshakeTimeoutMillis;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder forceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
         this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder absoluteUpgradeUrl(boolean absoluteUpgradeUrl) {
         this.absoluteUpgradeUrl = absoluteUpgradeUrl;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder generateOriginHeader(boolean generateOriginHeader) {
         this.generateOriginHeader = generateOriginHeader;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder withUTF8Validator(boolean withUTF8Validator) {
         this.withUTF8Validator = withUTF8Validator;
         return this;
      }

      public WebSocketClientProtocolConfig build() {
         return new WebSocketClientProtocolConfig(
            this.webSocketUri,
            this.subprotocol,
            this.version,
            this.allowExtensions,
            this.customHeaders,
            this.maxFramePayloadLength,
            this.performMasking,
            this.allowMaskMismatch,
            this.handleCloseFrames,
            this.sendCloseFrame,
            this.dropPongFrames,
            this.handshakeTimeoutMillis,
            this.forceCloseTimeoutMillis,
            this.absoluteUpgradeUrl,
            this.generateOriginHeader,
            this.withUTF8Validator
         );
      }
   }
}
