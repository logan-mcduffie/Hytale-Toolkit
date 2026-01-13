package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler {
   private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
   private final WebSocketServerProtocolConfig serverConfig;

   public WebSocketServerProtocolHandler(WebSocketServerProtocolConfig serverConfig) {
      super(ObjectUtil.checkNotNull(serverConfig, "serverConfig").dropPongFrames(), serverConfig.sendCloseFrame(), serverConfig.forceCloseTimeoutMillis());
      this.serverConfig = serverConfig;
   }

   public WebSocketServerProtocolHandler(String websocketPath) {
      this(websocketPath, 10000L);
   }

   public WebSocketServerProtocolHandler(String websocketPath, long handshakeTimeoutMillis) {
      this(websocketPath, false, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith) {
      this(websocketPath, checkStartsWith, 10000L);
   }

   public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith, long handshakeTimeoutMillis) {
      this(websocketPath, null, false, 65536, false, checkStartsWith, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols) {
      this(websocketPath, subprotocols, 10000L);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, long handshakeTimeoutMillis) {
      this(websocketPath, subprotocols, false, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions) {
      this(websocketPath, subprotocols, allowExtensions, 10000L);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, long handshakeTimeoutMillis) {
      this(websocketPath, subprotocols, allowExtensions, 65536, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, 10000L);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, long handshakeTimeoutMillis) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, false, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, 10000L);
   }

   public WebSocketServerProtocolHandler(
      String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, long handshakeTimeoutMillis
   ) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, false, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(
      String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith
   ) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, 10000L);
   }

   public WebSocketServerProtocolHandler(
      String websocketPath,
      String subprotocols,
      boolean allowExtensions,
      int maxFrameSize,
      boolean allowMaskMismatch,
      boolean checkStartsWith,
      long handshakeTimeoutMillis
   ) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, true, handshakeTimeoutMillis);
   }

   public WebSocketServerProtocolHandler(
      String websocketPath,
      String subprotocols,
      boolean allowExtensions,
      int maxFrameSize,
      boolean allowMaskMismatch,
      boolean checkStartsWith,
      boolean dropPongFrames
   ) {
      this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, dropPongFrames, 10000L);
   }

   public WebSocketServerProtocolHandler(
      String websocketPath,
      String subprotocols,
      boolean allowExtensions,
      int maxFrameSize,
      boolean allowMaskMismatch,
      boolean checkStartsWith,
      boolean dropPongFrames,
      long handshakeTimeoutMillis
   ) {
      this(
         websocketPath,
         subprotocols,
         checkStartsWith,
         dropPongFrames,
         handshakeTimeoutMillis,
         WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFrameSize).allowMaskMismatch(allowMaskMismatch).allowExtensions(allowExtensions).build()
      );
   }

   public WebSocketServerProtocolHandler(
      String websocketPath,
      String subprotocols,
      boolean checkStartsWith,
      boolean dropPongFrames,
      long handshakeTimeoutMillis,
      WebSocketDecoderConfig decoderConfig
   ) {
      this(
         WebSocketServerProtocolConfig.newBuilder()
            .websocketPath(websocketPath)
            .subprotocols(subprotocols)
            .checkStartsWith(checkStartsWith)
            .handshakeTimeoutMillis(handshakeTimeoutMillis)
            .dropPongFrames(dropPongFrames)
            .decoderConfig(decoderConfig)
            .build()
      );
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) {
      ChannelPipeline cp = ctx.pipeline();
      if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
         cp.addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.serverConfig));
      }

      if (this.serverConfig.decoderConfig().withUTF8Validator() && cp.get(Utf8FrameValidator.class) == null) {
         cp.addBefore(ctx.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator(this.serverConfig.decoderConfig().closeOnProtocolViolation()));
      }
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
      if (this.serverConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
         WebSocketServerHandshaker handshaker = getHandshaker(ctx.channel());
         if (handshaker != null) {
            frame.retain();
            ChannelPromise promise = ctx.newPromise();
            this.closeSent(promise);
            handshaker.close(ctx, (CloseWebSocketFrame)frame, promise);
         } else {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
         }
      } else {
         super.decode(ctx, frame, out);
      }
   }

   protected WebSocketServerHandshakeException buildHandshakeException(String message) {
      return new WebSocketServerHandshakeException(message);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (cause instanceof WebSocketHandshakeException) {
         FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes())
         );
         ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
      } else {
         ctx.fireExceptionCaught(cause);
         ctx.close();
      }
   }

   static WebSocketServerHandshaker getHandshaker(Channel channel) {
      return channel.attr(HANDSHAKER_ATTR_KEY).get();
   }

   static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
      channel.attr(HANDSHAKER_ATTR_KEY).set(handshaker);
   }

   public static final class HandshakeComplete {
      private final String requestUri;
      private final HttpHeaders requestHeaders;
      private final String selectedSubprotocol;

      public HandshakeComplete(String requestUri, HttpHeaders requestHeaders, String selectedSubprotocol) {
         this.requestUri = requestUri;
         this.requestHeaders = requestHeaders;
         this.selectedSubprotocol = selectedSubprotocol;
      }

      public String requestUri() {
         return this.requestUri;
      }

      public HttpHeaders requestHeaders() {
         return this.requestHeaders;
      }

      public String selectedSubprotocol() {
         return this.selectedSubprotocol;
      }
   }

   public static enum ServerHandshakeStateEvent {
      @Deprecated
      HANDSHAKE_COMPLETE,
      HANDSHAKE_TIMEOUT;
   }
}
