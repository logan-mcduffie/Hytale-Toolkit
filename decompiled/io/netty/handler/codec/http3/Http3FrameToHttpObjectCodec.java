package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.concurrent.PromiseCombiner;
import java.net.SocketAddress;
import org.jetbrains.annotations.Nullable;

public final class Http3FrameToHttpObjectCodec extends Http3RequestStreamInboundHandler implements ChannelOutboundHandler {
   private final boolean isServer;
   private final boolean validateHeaders;
   private boolean inboundTranslationInProgress;

   public Http3FrameToHttpObjectCodec(boolean isServer, boolean validateHeaders) {
      this.isServer = isServer;
      this.validateHeaders = validateHeaders;
   }

   public Http3FrameToHttpObjectCodec(boolean isServer) {
      this(isServer, true);
   }

   @Override
   public boolean isSharable() {
      return false;
   }

   @Override
   protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame) throws Exception {
      Http3Headers headers = frame.headers();
      long id = ((QuicStreamChannel)ctx.channel()).streamId();
      CharSequence status = headers.status();
      if (null != status && HttpResponseStatus.CONTINUE.codeAsText().contentEquals(status)) {
         FullHttpMessage fullMsg = this.newFullMessage(id, headers, ctx.alloc());
         ctx.fireChannelRead(fullMsg);
      } else {
         if (headers.method() == null && status == null) {
            LastHttpContent last = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            HttpConversionUtil.addHttp3ToHttpHeaders(id, headers, last.trailingHeaders(), HttpVersion.HTTP_1_1, true, true);
            this.inboundTranslationInProgress = false;
            ctx.fireChannelRead(last);
         } else {
            HttpMessage req = this.newMessage(id, headers);
            if (!HttpUtil.isContentLengthSet(req)) {
               req.headers().add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            }

            this.inboundTranslationInProgress = true;
            ctx.fireChannelRead(req);
         }
      }
   }

   @Override
   protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame) throws Exception {
      this.inboundTranslationInProgress = true;
      ctx.fireChannelRead(new DefaultHttpContent(frame.content()));
   }

   @Override
   protected void channelInputClosed(ChannelHandlerContext ctx) throws Exception {
      if (this.inboundTranslationInProgress) {
         ctx.fireChannelRead(LastHttpContent.EMPTY_LAST_CONTENT);
      }
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (!(msg instanceof HttpObject)) {
         throw new UnsupportedMessageTypeException(msg, HttpObject.class);
      } else {
         if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse)msg;
            if (res.status().equals(HttpResponseStatus.CONTINUE)) {
               if (res instanceof FullHttpResponse) {
                  Http3Headers headers = this.toHttp3Headers(res);
                  ctx.write(new DefaultHttp3HeadersFrame(headers), promise);
                  ((FullHttpResponse)res).release();
                  return;
               }

               throw new EncoderException(HttpResponseStatus.CONTINUE + " must be a FullHttpResponse");
            }
         }

         PromiseCombiner combiner = null;
         boolean isLast = msg instanceof LastHttpContent;
         if (msg instanceof HttpMessage) {
            Http3Headers headers = this.toHttp3Headers((HttpMessage)msg);
            DefaultHttp3HeadersFrame frame = new DefaultHttp3HeadersFrame(headers);
            if (msg instanceof HttpContent && (!promise.isVoid() || isLast)) {
               combiner = new PromiseCombiner(ctx.executor());
            }

            promise = writeWithOptionalCombiner(ctx, frame, promise, combiner, isLast);
         }

         if (isLast) {
            LastHttpContent last = (LastHttpContent)msg;

            try {
               boolean readable = last.content().isReadable();
               boolean hasTrailers = !last.trailingHeaders().isEmpty();
               if (combiner == null && readable && hasTrailers && !promise.isVoid()) {
                  combiner = new PromiseCombiner(ctx.executor());
               }

               if (readable) {
                  promise = writeWithOptionalCombiner(ctx, new DefaultHttp3DataFrame(last.content().retain()), promise, combiner, true);
               }

               if (hasTrailers) {
                  Http3Headers headers = HttpConversionUtil.toHttp3Headers(last.trailingHeaders(), this.validateHeaders);
                  promise = writeWithOptionalCombiner(ctx, new DefaultHttp3HeadersFrame(headers), promise, combiner, true);
               } else if (!readable && combiner == null) {
                  promise = writeWithOptionalCombiner(ctx, new DefaultHttp3DataFrame(last.content().retain()), promise, combiner, true);
               }

               promise = promise.unvoid().addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
            } finally {
               last.release();
            }
         } else if (msg instanceof HttpContent) {
            promise = writeWithOptionalCombiner(ctx, new DefaultHttp3DataFrame(((HttpContent)msg).content()), promise, combiner, false);
         }

         if (combiner != null) {
            combiner.finish(promise);
         }
      }
   }

   private static ChannelPromise writeWithOptionalCombiner(
      ChannelHandlerContext ctx, Object msg, ChannelPromise outerPromise, @Nullable PromiseCombiner combiner, boolean unvoidPromise
   ) {
      if (unvoidPromise) {
         outerPromise = outerPromise.unvoid();
      }

      if (combiner == null) {
         ctx.write(msg, outerPromise);
      } else {
         combiner.add(ctx.write(msg));
      }

      return outerPromise;
   }

   private Http3Headers toHttp3Headers(HttpMessage msg) {
      if (msg instanceof HttpRequest) {
         msg.headers().set(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTPS);
      }

      return HttpConversionUtil.toHttp3Headers(msg, this.validateHeaders);
   }

   private HttpMessage newMessage(long id, Http3Headers headers) throws Http3Exception {
      return (HttpMessage)(this.isServer
         ? HttpConversionUtil.toHttpRequest(id, headers, this.validateHeaders)
         : HttpConversionUtil.toHttpResponse(id, headers, this.validateHeaders));
   }

   private FullHttpMessage newFullMessage(long id, Http3Headers headers, ByteBufAllocator alloc) throws Http3Exception {
      return (FullHttpMessage)(this.isServer
         ? HttpConversionUtil.toFullHttpRequest(id, headers, alloc, this.validateHeaders)
         : HttpConversionUtil.toFullHttpResponse(id, headers, alloc, this.validateHeaders));
   }

   @Override
   public void flush(ChannelHandlerContext ctx) {
      ctx.flush();
   }

   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
      ctx.bind(localAddress, promise);
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.disconnect(promise);
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.close(promise);
   }

   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.deregister(promise);
   }

   @Override
   public void read(ChannelHandlerContext ctx) throws Exception {
      ctx.read();
   }
}
