package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WebSocketServerHandshaker {
   protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
   private final String uri;
   private final String[] subprotocols;
   private final WebSocketVersion version;
   private final WebSocketDecoderConfig decoderConfig;
   private String selectedSubprotocol;
   public static final String SUB_PROTOCOL_WILDCARD = "*";

   protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, int maxFramePayloadLength) {
      this(version, uri, subprotocols, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayloadLength).build());
   }

   protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, WebSocketDecoderConfig decoderConfig) {
      this.version = version;
      this.uri = uri;
      if (subprotocols != null) {
         String[] subprotocolArray = subprotocols.split(",");

         for (int i = 0; i < subprotocolArray.length; i++) {
            subprotocolArray[i] = subprotocolArray[i].trim();
         }

         this.subprotocols = subprotocolArray;
      } else {
         this.subprotocols = EmptyArrays.EMPTY_STRINGS;
      }

      this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
   }

   @Deprecated
   public String uri() {
      return this.uri;
   }

   public Set<String> subprotocols() {
      Set<String> ret = new LinkedHashSet<>();
      Collections.addAll(ret, this.subprotocols);
      return ret;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public int maxFramePayloadLength() {
      return this.decoderConfig.maxFramePayloadLength();
   }

   public WebSocketDecoderConfig decoderConfig() {
      return this.decoderConfig;
   }

   public ChannelFuture handshake(Channel channel, FullHttpRequest req) {
      return this.handshake(channel, req, null, channel.newPromise());
   }

   public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, final ChannelPromise promise) {
      if (logger.isDebugEnabled()) {
         logger.debug("{} WebSocket version {} server handshake", channel, this.version());
      }

      FullHttpResponse response = this.newHandshakeResponse(req, responseHeaders);
      ChannelPipeline p = channel.pipeline();
      if (p.get(HttpObjectAggregator.class) != null) {
         p.remove(HttpObjectAggregator.class);
      }

      if (p.get(HttpContentCompressor.class) != null) {
         p.remove(HttpContentCompressor.class);
      }

      ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
      final String encoderName;
      if (ctx == null) {
         ctx = p.context(HttpServerCodec.class);
         if (ctx == null) {
            promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
            response.release();
            return promise;
         }

         p.addBefore(ctx.name(), "wsencoder", this.newWebSocketEncoder());
         p.addBefore(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
         encoderName = ctx.name();
      } else {
         p.replace(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
         encoderName = p.context(HttpResponseEncoder.class).name();
         p.addBefore(encoderName, "wsencoder", this.newWebSocketEncoder());
      }

      channel.writeAndFlush(response).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
               ChannelPipeline px = future.channel().pipeline();
               px.remove(encoderName);
               promise.setSuccess();
            } else {
               promise.setFailure(future.cause());
            }
         }
      });
      return promise;
   }

   public ChannelFuture handshake(Channel channel, HttpRequest req) {
      return this.handshake(channel, req, null, channel.newPromise());
   }

   public final ChannelFuture handshake(final Channel channel, HttpRequest req, final HttpHeaders responseHeaders, final ChannelPromise promise) {
      if (req instanceof FullHttpRequest) {
         return this.handshake(channel, (FullHttpRequest)req, responseHeaders, promise);
      } else {
         if (logger.isDebugEnabled()) {
            logger.debug("{} WebSocket version {} server handshake", channel, this.version());
         }

         ChannelPipeline p = channel.pipeline();
         ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
         if (ctx == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
               promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
               return promise;
            }
         }

         String aggregatorCtx = ctx.name();
         if (HttpUtil.isContentLengthSet(req) || HttpUtil.isTransferEncodingChunked(req) || this.version == WebSocketVersion.V00) {
            aggregatorCtx = "httpAggregator";
            p.addAfter(ctx.name(), aggregatorCtx, new HttpObjectAggregator(8192));
         }

         p.addAfter(
            aggregatorCtx,
            "handshaker",
            new ChannelInboundHandlerAdapter() {
               private FullHttpRequest fullHttpRequest;

               @Override
               public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                  if (msg instanceof HttpObject) {
                     try {
                        this.handleHandshakeRequest(ctx, (HttpObject)msg);
                     } finally {
                        ReferenceCountUtil.release(msg);
                     }
                  } else {
                     super.channelRead(ctx, msg);
                  }
               }

               @Override
               public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                  ctx.pipeline().remove(this);
                  promise.tryFailure(cause);
                  ctx.fireExceptionCaught(cause);
               }

               @Override
               public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                  try {
                     if (!promise.isDone()) {
                        promise.tryFailure(new ClosedChannelException());
                     }

                     ctx.fireChannelInactive();
                  } finally {
                     this.releaseFullHttpRequest();
                  }
               }

               @Override
               public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                  this.releaseFullHttpRequest();
               }

               private void handleHandshakeRequest(ChannelHandlerContext ctx, HttpObject httpObject) {
                  if (httpObject instanceof FullHttpRequest) {
                     ctx.pipeline().remove(this);
                     WebSocketServerHandshaker.this.handshake(channel, (FullHttpRequest)httpObject, responseHeaders, promise);
                  } else if (httpObject instanceof LastHttpContent) {
                     assert this.fullHttpRequest != null;

                     FullHttpRequest handshakeRequest = this.fullHttpRequest;
                     this.fullHttpRequest = null;

                     try {
                        ctx.pipeline().remove(this);
                        WebSocketServerHandshaker.this.handshake(channel, handshakeRequest, responseHeaders, promise);
                     } finally {
                        handshakeRequest.release();
                     }
                  } else {
                     if (httpObject instanceof HttpRequest) {
                        HttpRequest httpRequest = (HttpRequest)httpObject;
                        this.fullHttpRequest = new DefaultFullHttpRequest(
                           httpRequest.protocolVersion(),
                           httpRequest.method(),
                           httpRequest.uri(),
                           Unpooled.EMPTY_BUFFER,
                           httpRequest.headers(),
                           EmptyHttpHeaders.INSTANCE
                        );
                        if (httpRequest.decoderResult().isFailure()) {
                           this.fullHttpRequest.setDecoderResult(httpRequest.decoderResult());
                        }
                     }
                  }
               }

               private void releaseFullHttpRequest() {
                  if (this.fullHttpRequest != null) {
                     this.fullHttpRequest.release();
                     this.fullHttpRequest = null;
                  }
               }
            }
         );

         try {
            ctx.fireChannelRead(ReferenceCountUtil.retain(req));
         } catch (Throwable var9) {
            promise.setFailure(var9);
         }

         return promise;
      }
   }

   protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2);

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
      ObjectUtil.checkNotNull(channel, "channel");
      return this.close(channel, frame, channel.newPromise());
   }

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
      return this.close0(channel, frame, promise);
   }

   public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
      ObjectUtil.checkNotNull(ctx, "ctx");
      return this.close(ctx, frame, ctx.newPromise());
   }

   public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame, ChannelPromise promise) {
      ObjectUtil.checkNotNull(ctx, "ctx");
      return this.close0(ctx, frame, promise).addListener(ChannelFutureListener.CLOSE);
   }

   private ChannelFuture close0(ChannelOutboundInvoker invoker, CloseWebSocketFrame frame, ChannelPromise promise) {
      return invoker.writeAndFlush(frame, promise).addListener(ChannelFutureListener.CLOSE);
   }

   protected String selectSubprotocol(String requestedSubprotocols) {
      if (requestedSubprotocols != null && this.subprotocols.length != 0) {
         String[] requestedSubprotocolArray = requestedSubprotocols.split(",");

         for (String p : requestedSubprotocolArray) {
            String requestedSubprotocol = p.trim();

            for (String supportedSubprotocol : this.subprotocols) {
               if ("*".equals(supportedSubprotocol) || requestedSubprotocol.equals(supportedSubprotocol)) {
                  this.selectedSubprotocol = requestedSubprotocol;
                  return requestedSubprotocol;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String selectedSubprotocol() {
      return this.selectedSubprotocol;
   }

   protected abstract WebSocketFrameDecoder newWebsocketDecoder();

   protected abstract WebSocketFrameEncoder newWebSocketEncoder();
}
