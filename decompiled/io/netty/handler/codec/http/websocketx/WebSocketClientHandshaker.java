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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class WebSocketClientHandshaker {
   private static final String HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
   private static final String HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
   protected static final int DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS = 10000;
   private final URI uri;
   private final WebSocketVersion version;
   private volatile boolean handshakeComplete;
   private volatile long forceCloseTimeoutMillis = 10000L;
   private volatile int forceCloseInit;
   private static final AtomicIntegerFieldUpdater<WebSocketClientHandshaker> FORCE_CLOSE_INIT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      WebSocketClientHandshaker.class, "forceCloseInit"
   );
   private volatile boolean forceCloseComplete;
   private final String expectedSubprotocol;
   private volatile String actualSubprotocol;
   protected final HttpHeaders customHeaders;
   private final int maxFramePayloadLength;
   private final boolean absoluteUpgradeUrl;
   protected final boolean generateOriginHeader;

   protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength) {
      this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, 10000L);
   }

   protected WebSocketClientHandshaker(
      URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis
   ) {
      this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, false);
   }

   protected WebSocketClientHandshaker(
      URI uri,
      WebSocketVersion version,
      String subprotocol,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      long forceCloseTimeoutMillis,
      boolean absoluteUpgradeUrl
   ) {
      this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl, true);
   }

   protected WebSocketClientHandshaker(
      URI uri,
      WebSocketVersion version,
      String subprotocol,
      HttpHeaders customHeaders,
      int maxFramePayloadLength,
      long forceCloseTimeoutMillis,
      boolean absoluteUpgradeUrl,
      boolean generateOriginHeader
   ) {
      this.uri = uri;
      this.version = version;
      this.expectedSubprotocol = subprotocol;
      this.customHeaders = customHeaders;
      this.maxFramePayloadLength = maxFramePayloadLength;
      this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
      this.absoluteUpgradeUrl = absoluteUpgradeUrl;
      this.generateOriginHeader = generateOriginHeader;
   }

   public URI uri() {
      return this.uri;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean isHandshakeComplete() {
      return this.handshakeComplete;
   }

   private void setHandshakeComplete() {
      this.handshakeComplete = true;
   }

   public String expectedSubprotocol() {
      return this.expectedSubprotocol;
   }

   public String actualSubprotocol() {
      return this.actualSubprotocol;
   }

   private void setActualSubprotocol(String actualSubprotocol) {
      this.actualSubprotocol = actualSubprotocol;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   protected boolean isForceCloseComplete() {
      return this.forceCloseComplete;
   }

   public WebSocketClientHandshaker setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
      this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
      return this;
   }

   public ChannelFuture handshake(Channel channel) {
      ObjectUtil.checkNotNull(channel, "channel");
      return this.handshake(channel, channel.newPromise());
   }

   public final ChannelFuture handshake(Channel channel, final ChannelPromise promise) {
      ChannelPipeline pipeline = channel.pipeline();
      HttpResponseDecoder decoder = pipeline.get(HttpResponseDecoder.class);
      if (decoder == null) {
         HttpClientCodec codec = pipeline.get(HttpClientCodec.class);
         if (codec == null) {
            promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
            return promise;
         }
      }

      if (this.uri.getHost() == null) {
         if (this.customHeaders == null || !this.customHeaders.contains(HttpHeaderNames.HOST)) {
            promise.setFailure(
               new IllegalArgumentException("Cannot generate the 'host' header value, webSocketURI should contain host or passed through customHeaders")
            );
            return promise;
         }

         if (this.generateOriginHeader && !this.customHeaders.contains(HttpHeaderNames.ORIGIN)) {
            String originName;
            if (this.version != WebSocketVersion.V07 && this.version != WebSocketVersion.V08) {
               originName = HttpHeaderNames.ORIGIN.toString();
            } else {
               originName = HttpHeaderNames.SEC_WEBSOCKET_ORIGIN.toString();
            }

            promise.setFailure(
               new IllegalArgumentException(
                  "Cannot generate the '"
                     + originName
                     + "' header value, webSocketURI should contain host or disable generateOriginHeader or pass value through customHeaders"
               )
            );
            return promise;
         }
      }

      FullHttpRequest request = this.newHandshakeRequest();
      channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
               ChannelPipeline p = future.channel().pipeline();
               ChannelHandlerContext ctx = p.context(HttpRequestEncoder.class);
               if (ctx == null) {
                  ctx = p.context(HttpClientCodec.class);
               }

               if (ctx == null) {
                  promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec"));
                  return;
               }

               p.addAfter(ctx.name(), "ws-encoder", WebSocketClientHandshaker.this.newWebSocketEncoder());
               promise.setSuccess();
            } else {
               promise.setFailure(future.cause());
            }
         }
      });
      return promise;
   }

   protected abstract FullHttpRequest newHandshakeRequest();

   public final void finishHandshake(Channel channel, FullHttpResponse response) {
      this.verify(response);
      String receivedProtocol = response.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
      receivedProtocol = receivedProtocol != null ? receivedProtocol.trim() : null;
      String expectedProtocol = this.expectedSubprotocol != null ? this.expectedSubprotocol : "";
      boolean protocolValid = false;
      if (expectedProtocol.isEmpty() && receivedProtocol == null) {
         protocolValid = true;
         this.setActualSubprotocol(this.expectedSubprotocol);
      } else if (!expectedProtocol.isEmpty() && receivedProtocol != null && !receivedProtocol.isEmpty()) {
         for (String protocol : expectedProtocol.split(",")) {
            if (protocol.trim().equals(receivedProtocol)) {
               protocolValid = true;
               this.setActualSubprotocol(receivedProtocol);
               break;
            }
         }
      }

      if (!protocolValid) {
         throw new WebSocketClientHandshakeException(
            String.format("Invalid subprotocol. Actual: %s. Expected one of: %s", receivedProtocol, this.expectedSubprotocol), response
         );
      } else {
         this.setHandshakeComplete();
         final ChannelPipeline p = channel.pipeline();
         HttpContentDecompressor decompressor = p.get(HttpContentDecompressor.class);
         if (decompressor != null) {
            p.remove(decompressor);
         }

         HttpObjectAggregator aggregator = p.get(HttpObjectAggregator.class);
         if (aggregator != null) {
            p.remove(aggregator);
         }

         final ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
         if (ctx == null) {
            ctx = p.context(HttpClientCodec.class);
            if (ctx == null) {
               throw new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec");
            }

            final HttpClientCodec codec = (HttpClientCodec)ctx.handler();
            codec.removeOutboundHandler();
            p.addAfter(ctx.name(), "ws-decoder", this.newWebsocketDecoder());
            channel.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  p.remove(codec);
               }
            });
         } else {
            if (p.get(HttpRequestEncoder.class) != null) {
               p.remove(HttpRequestEncoder.class);
            }

            p.addAfter(ctx.name(), "ws-decoder", this.newWebsocketDecoder());
            channel.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  p.remove(ctx.handler());
               }
            });
         }
      }
   }

   public final ChannelFuture processHandshake(Channel channel, HttpResponse response) {
      return this.processHandshake(channel, response, channel.newPromise());
   }

   public final ChannelFuture processHandshake(final Channel channel, HttpResponse response, final ChannelPromise promise) {
      if (response instanceof FullHttpResponse) {
         try {
            this.finishHandshake(channel, (FullHttpResponse)response);
            promise.setSuccess();
         } catch (Throwable var9) {
            promise.setFailure(var9);
         }
      } else {
         ChannelPipeline p = channel.pipeline();
         ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
         if (ctx == null) {
            ctx = p.context(HttpClientCodec.class);
            if (ctx == null) {
               return promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
            }
         }

         String aggregatorCtx = ctx.name();
         if (this.version == WebSocketVersion.V00) {
            aggregatorCtx = "httpAggregator";
            p.addAfter(ctx.name(), aggregatorCtx, new HttpObjectAggregator(8192));
         }

         p.addAfter(
            aggregatorCtx,
            "handshaker",
            new ChannelInboundHandlerAdapter() {
               private FullHttpResponse fullHttpResponse;

               @Override
               public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                  if (msg instanceof HttpObject) {
                     try {
                        this.handleHandshakeResponse(ctx, (HttpObject)msg);
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
                  promise.setFailure(cause);
               }

               @Override
               public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                  try {
                     if (!promise.isDone()) {
                        promise.tryFailure(new ClosedChannelException());
                     }

                     ctx.fireChannelInactive();
                  } finally {
                     this.releaseFullHttpResponse();
                  }
               }

               @Override
               public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                  this.releaseFullHttpResponse();
               }

               private void handleHandshakeResponse(ChannelHandlerContext ctx, HttpObject responsex) {
                  if (responsex instanceof FullHttpResponse) {
                     ctx.pipeline().remove(this);
                     this.tryFinishHandshake((FullHttpResponse)responsex);
                  } else if (responsex instanceof LastHttpContent) {
                     assert this.fullHttpResponse != null;

                     FullHttpResponse handshakeResponse = this.fullHttpResponse;
                     this.fullHttpResponse = null;

                     try {
                        ctx.pipeline().remove(this);
                        this.tryFinishHandshake(handshakeResponse);
                     } finally {
                        handshakeResponse.release();
                     }
                  } else {
                     if (responsex instanceof HttpResponse) {
                        HttpResponse httpResponse = (HttpResponse)responsex;
                        this.fullHttpResponse = new DefaultFullHttpResponse(
                           httpResponse.protocolVersion(), httpResponse.status(), Unpooled.EMPTY_BUFFER, httpResponse.headers(), EmptyHttpHeaders.INSTANCE
                        );
                        if (httpResponse.decoderResult().isFailure()) {
                           this.fullHttpResponse.setDecoderResult(httpResponse.decoderResult());
                        }
                     }
                  }
               }

               private void tryFinishHandshake(FullHttpResponse fullHttpResponse) {
                  try {
                     WebSocketClientHandshaker.this.finishHandshake(channel, fullHttpResponse);
                     promise.setSuccess();
                  } catch (Throwable var3) {
                     promise.setFailure(var3);
                  }
               }

               private void releaseFullHttpResponse() {
                  if (this.fullHttpResponse != null) {
                     this.fullHttpResponse.release();
                     this.fullHttpResponse = null;
                  }
               }
            }
         );

         try {
            ctx.fireChannelRead(ReferenceCountUtil.retain(response));
         } catch (Throwable var8) {
            promise.setFailure(var8);
         }
      }

      return promise;
   }

   protected abstract void verify(FullHttpResponse var1);

   protected abstract WebSocketFrameDecoder newWebsocketDecoder();

   protected abstract WebSocketFrameEncoder newWebSocketEncoder();

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
      ObjectUtil.checkNotNull(channel, "channel");
      return this.close(channel, frame, channel.newPromise());
   }

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
      ObjectUtil.checkNotNull(channel, "channel");
      return this.close0(channel, channel, frame, promise);
   }

   public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
      ObjectUtil.checkNotNull(ctx, "ctx");
      return this.close(ctx, frame, ctx.newPromise());
   }

   public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame, ChannelPromise promise) {
      ObjectUtil.checkNotNull(ctx, "ctx");
      return this.close0(ctx, ctx.channel(), frame, promise);
   }

   private ChannelFuture close0(final ChannelOutboundInvoker invoker, final Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
      invoker.writeAndFlush(frame, promise);
      final long forceCloseTimeoutMillis = this.forceCloseTimeoutMillis;
      if (forceCloseTimeoutMillis > 0L && channel.isActive() && this.forceCloseInit == 0) {
         promise.addListener(
            new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) {
                  if (future.isSuccess()
                     && channel.isActive()
                     && WebSocketClientHandshaker.FORCE_CLOSE_INIT_UPDATER.compareAndSet(WebSocketClientHandshaker.this, 0, 1)) {
                     final Future<?> forceCloseFuture = channel.eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (channel.isActive()) {
                              invoker.close();
                              WebSocketClientHandshaker.this.forceCloseComplete = true;
                           }
                        }
                     }, forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
                     channel.closeFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                           forceCloseFuture.cancel(false);
                        }
                     });
                  }
               }
            }
         );
         return promise;
      } else {
         return promise;
      }
   }

   protected String upgradeUrl(URI wsURL) {
      if (this.absoluteUpgradeUrl) {
         return wsURL.toString();
      } else {
         String path = wsURL.getRawPath();
         path = path != null && !path.isEmpty() ? path : "/";
         String query = wsURL.getRawQuery();
         return query != null && !query.isEmpty() ? path + '?' + query : path;
      }
   }

   static CharSequence websocketHostValue(URI wsURL) {
      int port = wsURL.getPort();
      if (port == -1) {
         return wsURL.getHost();
      } else {
         String host = wsURL.getHost();
         String scheme = wsURL.getScheme();
         if (port == HttpScheme.HTTP.port()) {
            return !HttpScheme.HTTP.name().contentEquals(scheme) && !WebSocketScheme.WS.name().contentEquals(scheme)
               ? NetUtil.toSocketAddressString(host, port)
               : host;
         } else if (port != HttpScheme.HTTPS.port()) {
            return NetUtil.toSocketAddressString(host, port);
         } else {
            return !HttpScheme.HTTPS.name().contentEquals(scheme) && !WebSocketScheme.WSS.name().contentEquals(scheme)
               ? NetUtil.toSocketAddressString(host, port)
               : host;
         }
      }
   }

   static CharSequence websocketOriginValue(URI wsURL) {
      String scheme = wsURL.getScheme();
      int port = wsURL.getPort();
      String schemePrefix;
      int defaultPort;
      if (!WebSocketScheme.WSS.name().contentEquals(scheme)
         && !HttpScheme.HTTPS.name().contentEquals(scheme)
         && (scheme != null || port != WebSocketScheme.WSS.port())) {
         schemePrefix = HTTP_SCHEME_PREFIX;
         defaultPort = WebSocketScheme.WS.port();
      } else {
         schemePrefix = HTTPS_SCHEME_PREFIX;
         defaultPort = WebSocketScheme.WSS.port();
      }

      String host = wsURL.getHost().toLowerCase(Locale.US);
      return port != defaultPort && port != -1 ? schemePrefix + NetUtil.toSocketAddressString(host, port) : schemePrefix + host;
   }
}
