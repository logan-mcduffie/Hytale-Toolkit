package io.netty.handler.codec.http.cors;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeadersFactory;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.List;

public class CorsHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
   private static final String ANY_ORIGIN = "*";
   private static final String NULL_ORIGIN = "null";
   private CorsConfig config;
   private HttpRequest request;
   private final List<CorsConfig> configList;
   private final boolean isShortCircuit;
   private boolean consumeContent;

   public CorsHandler(CorsConfig config) {
      this(Collections.singletonList(ObjectUtil.checkNotNull(config, "config")), config.isShortCircuit());
   }

   public CorsHandler(List<CorsConfig> configList, boolean isShortCircuit) {
      ObjectUtil.checkNonEmpty(configList, "configList");
      this.configList = configList;
      this.isShortCircuit = isShortCircuit;
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof HttpRequest) {
         this.request = (HttpRequest)msg;
         String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
         this.config = this.getForOrigin(origin);
         if (isPreflightRequest(this.request)) {
            this.handlePreflight(ctx, this.request);
            this.consumeContent = true;
         } else if (this.isShortCircuit && origin != null && this.config == null) {
            forbidden(ctx, this.request);
            this.consumeContent = true;
         } else {
            this.consumeContent = false;
            ctx.fireChannelRead(msg);
         }
      } else if (this.consumeContent && msg instanceof HttpContent) {
         ReferenceCountUtil.release(msg);
      } else {
         ctx.fireChannelRead(msg);
      }
   }

   private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
      HttpResponse response = new DefaultFullHttpResponse(
         request.protocolVersion(),
         HttpResponseStatus.OK,
         Unpooled.buffer(0),
         DefaultHttpHeadersFactory.headersFactory().withCombiningHeaders(true),
         DefaultHttpHeadersFactory.trailersFactory().withCombiningHeaders(true)
      );
      if (this.setOrigin(response)) {
         this.setAllowMethods(response);
         this.setAllowHeaders(response);
         this.setAllowCredentials(response);
         this.setMaxAge(response);
         this.setPreflightHeaders(response);
         this.setAllowPrivateNetwork(response);
      }

      if (!response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
         response.headers().set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
      }

      ReferenceCountUtil.release(request);
      respond(ctx, request, response);
   }

   private void setPreflightHeaders(HttpResponse response) {
      response.headers().add(this.config.preflightResponseHeaders());
   }

   private CorsConfig getForOrigin(String requestOrigin) {
      for (CorsConfig corsConfig : this.configList) {
         if (corsConfig.isAnyOriginSupported()) {
            return corsConfig;
         }

         if (corsConfig.origins().contains(requestOrigin)) {
            return corsConfig;
         }

         if (corsConfig.isNullOriginAllowed() || "null".equals(requestOrigin)) {
            return corsConfig;
         }
      }

      return null;
   }

   private boolean setOrigin(HttpResponse response) {
      String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
      if (origin != null && this.config != null) {
         if ("null".equals(origin) && this.config.isNullOriginAllowed()) {
            setNullOrigin(response);
            return true;
         }

         if (this.config.isAnyOriginSupported()) {
            if (this.config.isCredentialsAllowed()) {
               this.echoRequestOrigin(response);
               setVaryHeader(response);
            } else {
               setAnyOrigin(response);
            }

            return true;
         }

         if (this.config.origins().contains(origin)) {
            setOrigin(response, origin);
            setVaryHeader(response);
            return true;
         }

         logger.debug("Request origin [{}]] was not among the configured origins [{}]", origin, this.config.origins());
      }

      return false;
   }

   private void echoRequestOrigin(HttpResponse response) {
      setOrigin(response, this.request.headers().get(HttpHeaderNames.ORIGIN));
   }

   private static void setVaryHeader(HttpResponse response) {
      response.headers().set(HttpHeaderNames.VARY, HttpHeaderNames.ORIGIN);
   }

   private static void setAnyOrigin(HttpResponse response) {
      setOrigin(response, "*");
   }

   private static void setNullOrigin(HttpResponse response) {
      setOrigin(response, "null");
   }

   private static void setOrigin(HttpResponse response, String origin) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
   }

   private void setAllowCredentials(HttpResponse response) {
      if (this.config.isCredentialsAllowed() && !response.headers().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals("*")) {
         response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      }
   }

   private static boolean isPreflightRequest(HttpRequest request) {
      HttpHeaders headers = request.headers();
      return HttpMethod.OPTIONS.equals(request.method())
         && headers.contains(HttpHeaderNames.ORIGIN)
         && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
   }

   private void setExposeHeaders(HttpResponse response) {
      if (!this.config.exposedHeaders().isEmpty()) {
         response.headers().set(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, (Iterable<?>)this.config.exposedHeaders());
      }
   }

   private void setAllowMethods(HttpResponse response) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, (Iterable<?>)this.config.allowedRequestMethods());
   }

   private void setAllowHeaders(HttpResponse response) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, (Iterable<?>)this.config.allowedRequestHeaders());
   }

   private void setMaxAge(HttpResponse response) {
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, this.config.maxAge());
   }

   private void setAllowPrivateNetwork(HttpResponse response) {
      if (this.request.headers().contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_PRIVATE_NETWORK)) {
         if (this.config.isPrivateNetworkAllowed()) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "true");
         } else {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "false");
         }
      }
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (this.config != null && this.config.isCorsSupportEnabled() && msg instanceof HttpResponse) {
         HttpResponse response = (HttpResponse)msg;
         if (this.setOrigin(response)) {
            this.setAllowCredentials(response);
            this.setExposeHeaders(response);
         }
      }

      ctx.write(msg, promise);
   }

   private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
      HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0));
      response.headers().set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
      ReferenceCountUtil.release(request);
      respond(ctx, request, response);
   }

   private static void respond(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response) {
      boolean keepAlive = HttpUtil.isKeepAlive(request);
      HttpUtil.setKeepAlive(response, keepAlive);
      ChannelFuture future = ctx.writeAndFlush(response);
      if (!keepAlive) {
         future.addListener(ChannelFutureListener.CLOSE);
      }
   }
}
