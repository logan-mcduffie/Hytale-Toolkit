package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class WebSocketServerHandshakerFactory {
   private final String webSocketURL;
   private final String subprotocols;
   private final WebSocketDecoderConfig decoderConfig;

   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
      this(webSocketURL, subprotocols, allowExtensions, 65536);
   }

   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
      this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
   }

   public WebSocketServerHandshakerFactory(
      String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch
   ) {
      this(
         webSocketURL,
         subprotocols,
         WebSocketDecoderConfig.newBuilder()
            .allowExtensions(allowExtensions)
            .maxFramePayloadLength(maxFramePayloadLength)
            .allowMaskMismatch(allowMaskMismatch)
            .build()
      );
   }

   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
      this.webSocketURL = webSocketURL;
      this.subprotocols = subprotocols;
      this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
   }

   public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
      return resolveHandshaker0(req, this.webSocketURL, this.subprotocols, this.decoderConfig);
   }

   public static WebSocketServerHandshaker resolveHandshaker(HttpRequest req, String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
      ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
      return resolveHandshaker0(req, webSocketURL, subprotocols, decoderConfig);
   }

   private static WebSocketServerHandshaker resolveHandshaker0(HttpRequest req, String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
      CharSequence version = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
      if (version != null) {
         if (version.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13(webSocketURL, subprotocols, decoderConfig);
         } else if (version.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08(webSocketURL, subprotocols, decoderConfig);
         } else {
            return version.equals(WebSocketVersion.V07.toHttpHeaderValue()) ? new WebSocketServerHandshaker07(webSocketURL, subprotocols, decoderConfig) : null;
         }
      } else {
         return new WebSocketServerHandshaker00(webSocketURL, subprotocols, decoderConfig);
      }
   }

   @Deprecated
   public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
      sendUnsupportedVersionResponse(channel);
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel) {
      return sendUnsupportedVersionResponse(channel, channel.newPromise());
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise) {
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED, channel.alloc().buffer(0));
      res.headers().set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
      HttpUtil.setContentLength(res, 0L);
      return channel.writeAndFlush(res, promise);
   }
}
