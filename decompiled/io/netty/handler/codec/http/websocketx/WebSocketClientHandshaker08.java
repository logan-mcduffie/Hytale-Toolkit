package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;

public class WebSocketClientHandshaker08 extends WebSocketClientHandshaker {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker08.class);
   public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private String expectedChallengeResponseString;
   private final boolean allowExtensions;
   private final boolean performMasking;
   private final boolean allowMaskMismatch;

   public WebSocketClientHandshaker08(
      URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength
   ) {
      this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false, 10000L);
   }

   public WebSocketClientHandshaker08(
      URI webSocketURL,
      WebSocketVersion version,
      String subprotocol,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch
   ) {
      this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, 10000L);
   }

   public WebSocketClientHandshaker08(
      URI webSocketURL,
      WebSocketVersion version,
      String subprotocol,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch,
      long forceCloseTimeoutMillis
   ) {
      this(
         webSocketURL,
         version,
         subprotocol,
         allowExtensions,
         customHeaders,
         maxFramePayloadLength,
         performMasking,
         allowMaskMismatch,
         forceCloseTimeoutMillis,
         false,
         true
      );
   }

   WebSocketClientHandshaker08(
      URI webSocketURL,
      WebSocketVersion version,
      String subprotocol,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch,
      long forceCloseTimeoutMillis,
      boolean absoluteUpgradeUrl
   ) {
      this(
         webSocketURL,
         version,
         subprotocol,
         allowExtensions,
         customHeaders,
         maxFramePayloadLength,
         performMasking,
         allowMaskMismatch,
         forceCloseTimeoutMillis,
         absoluteUpgradeUrl,
         true
      );
   }

   WebSocketClientHandshaker08(
      URI webSocketURL,
      WebSocketVersion version,
      String subprotocol,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch,
      long forceCloseTimeoutMillis,
      boolean absoluteUpgradeUrl,
      boolean generateOriginHeader
   ) {
      super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl, generateOriginHeader);
      this.allowExtensions = allowExtensions;
      this.performMasking = performMasking;
      this.allowMaskMismatch = allowMaskMismatch;
   }

   @Override
   protected FullHttpRequest newHandshakeRequest() {
      URI wsURL = this.uri();
      byte[] nonce = WebSocketUtil.randomBytes(16);
      String key = WebSocketUtil.base64(nonce);
      String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
      this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
      if (logger.isDebugEnabled()) {
         logger.debug("WebSocket version 08 client handshake key: {}, expected response: {}", key, this.expectedChallengeResponseString);
      }

      FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(wsURL), Unpooled.EMPTY_BUFFER);
      HttpHeaders headers = request.headers();
      if (this.customHeaders != null) {
         headers.add(this.customHeaders);
         if (!headers.contains(HttpHeaderNames.HOST)) {
            headers.set(HttpHeaderNames.HOST, websocketHostValue(wsURL));
         }
      } else {
         headers.set(HttpHeaderNames.HOST, websocketHostValue(wsURL));
      }

      headers.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
         .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
         .set(HttpHeaderNames.SEC_WEBSOCKET_KEY, key);
      if (this.generateOriginHeader && !headers.contains(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) {
         headers.set(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, websocketOriginValue(wsURL));
      }

      String expectedSubprotocol = this.expectedSubprotocol();
      if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
         headers.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
      }

      headers.set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, this.version().toAsciiString());
      return request;
   }

   @Override
   protected void verify(FullHttpResponse response) {
      HttpResponseStatus status = response.status();
      if (!HttpResponseStatus.SWITCHING_PROTOCOLS.equals(status)) {
         throw new WebSocketClientHandshakeException("Invalid handshake response getStatus: " + status, response);
      } else {
         HttpHeaders headers = response.headers();
         CharSequence upgrade = headers.get(HttpHeaderNames.UPGRADE);
         if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response upgrade: " + upgrade, response);
         } else if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response connection: " + headers.get(HttpHeaderNames.CONNECTION), response);
         } else {
            CharSequence accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
            if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
               throw new WebSocketClientHandshakeException(
                  String.format("Invalid challenge. Actual: %s. Expected: %s", accept, this.expectedChallengeResponseString), response
               );
            }
         }
      }
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket08FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket08FrameEncoder(this.performMasking);
   }

   public WebSocketClientHandshaker08 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
      super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
      return this;
   }
}
