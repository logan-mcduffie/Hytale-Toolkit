package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;

public final class WebSocketClientHandshakerFactory {
   private WebSocketClientHandshakerFactory() {
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders
   ) {
      return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, 65536);
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength
   ) {
      return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI webSocketURL,
      WebSocketVersion version,
      String subprotocol,
      boolean allowExtensions,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      boolean performMasking,
      boolean allowMaskMismatch
   ) {
      return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, -1L);
   }

   public static WebSocketClientHandshaker newHandshaker(
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
      if (version == WebSocketVersion.V13) {
         return new WebSocketClientHandshaker13(
            webSocketURL,
            WebSocketVersion.V13,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis
         );
      } else if (version == WebSocketVersion.V08) {
         return new WebSocketClientHandshaker08(
            webSocketURL,
            WebSocketVersion.V08,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis
         );
      } else if (version == WebSocketVersion.V07) {
         return new WebSocketClientHandshaker07(
            webSocketURL,
            WebSocketVersion.V07,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis
         );
      } else if (version == WebSocketVersion.V00) {
         return new WebSocketClientHandshaker00(webSocketURL, WebSocketVersion.V00, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis);
      } else {
         throw new WebSocketClientHandshakeException("Protocol version " + version + " not supported.");
      }
   }

   public static WebSocketClientHandshaker newHandshaker(
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
      if (version == WebSocketVersion.V13) {
         return new WebSocketClientHandshaker13(
            webSocketURL,
            WebSocketVersion.V13,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl
         );
      } else if (version == WebSocketVersion.V08) {
         return new WebSocketClientHandshaker08(
            webSocketURL,
            WebSocketVersion.V08,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl
         );
      } else if (version == WebSocketVersion.V07) {
         return new WebSocketClientHandshaker07(
            webSocketURL,
            WebSocketVersion.V07,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl
         );
      } else if (version == WebSocketVersion.V00) {
         return new WebSocketClientHandshaker00(
            webSocketURL, WebSocketVersion.V00, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl
         );
      } else {
         throw new WebSocketClientHandshakeException("Protocol version " + version + " not supported.");
      }
   }

   public static WebSocketClientHandshaker newHandshaker(
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
      if (version == WebSocketVersion.V13) {
         return new WebSocketClientHandshaker13(
            webSocketURL,
            WebSocketVersion.V13,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl,
            generateOriginHeader
         );
      } else if (version == WebSocketVersion.V08) {
         return new WebSocketClientHandshaker08(
            webSocketURL,
            WebSocketVersion.V08,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl,
            generateOriginHeader
         );
      } else if (version == WebSocketVersion.V07) {
         return new WebSocketClientHandshaker07(
            webSocketURL,
            WebSocketVersion.V07,
            subprotocol,
            allowExtensions,
            customHeaders,
            maxFramePayloadLength,
            performMasking,
            allowMaskMismatch,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl,
            generateOriginHeader
         );
      } else if (version == WebSocketVersion.V00) {
         return new WebSocketClientHandshaker00(
            webSocketURL,
            WebSocketVersion.V00,
            subprotocol,
            customHeaders,
            maxFramePayloadLength,
            forceCloseTimeoutMillis,
            absoluteUpgradeUrl,
            generateOriginHeader
         );
      } else {
         throw new WebSocketClientHandshakeException("Protocol version " + version + " not supported.");
      }
   }
}
