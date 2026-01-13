package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpObjectAggregator extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
   private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
   private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER
   );
   private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER
   );
   private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER
   );
   private final boolean closeOnExpectationFailed;

   public HttpObjectAggregator(int maxContentLength) {
      this(maxContentLength, false);
   }

   public HttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
      super(maxContentLength, HttpObject.class);
      this.closeOnExpectationFailed = closeOnExpectationFailed;
   }

   protected boolean isStartMessage(HttpObject msg) throws Exception {
      return msg instanceof HttpMessage;
   }

   protected boolean isContentMessage(HttpObject msg) throws Exception {
      return msg instanceof HttpContent;
   }

   protected boolean isLastContentMessage(HttpContent msg) throws Exception {
      return msg instanceof LastHttpContent;
   }

   protected boolean isAggregated(HttpObject msg) throws Exception {
      return msg instanceof FullHttpMessage;
   }

   protected boolean isContentLengthInvalid(HttpMessage start, int maxContentLength) {
      try {
         return HttpUtil.getContentLength(start, -1L) > maxContentLength;
      } catch (NumberFormatException var4) {
         return false;
      }
   }

   private Object continueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
      if (HttpUtil.isUnsupportedExpectation(start)) {
         pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
         return EXPECTATION_FAILED.retainedDuplicate();
      } else if (HttpUtil.is100ContinueExpected(start)) {
         if (!this.isContentLengthInvalid(start, maxContentLength)) {
            return CONTINUE.retainedDuplicate();
         } else {
            pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
            return TOO_LARGE.retainedDuplicate();
         }
      } else {
         return null;
      }
   }

   protected Object newContinueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
      Object response = this.continueResponse(start, maxContentLength, pipeline);
      if (response != null) {
         start.headers().remove(HttpHeaderNames.EXPECT);
      }

      return response;
   }

   @Override
   protected boolean closeAfterContinueResponse(Object msg) {
      return this.closeOnExpectationFailed && this.ignoreContentAfterContinueResponse(msg);
   }

   @Override
   protected boolean ignoreContentAfterContinueResponse(Object msg) {
      if (msg instanceof HttpResponse) {
         HttpResponse httpResponse = (HttpResponse)msg;
         return httpResponse.status().codeClass().equals(HttpStatusClass.CLIENT_ERROR);
      } else {
         return false;
      }
   }

   protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
      assert !(start instanceof FullHttpMessage);

      HttpUtil.setTransferEncodingChunked(start, false);
      if (start instanceof HttpRequest) {
         return new HttpObjectAggregator.AggregatedFullHttpRequest((HttpRequest)start, content, null);
      } else if (start instanceof HttpResponse) {
         return new HttpObjectAggregator.AggregatedFullHttpResponse((HttpResponse)start, content, null);
      } else {
         throw new Error("Unexpected http message type: " + StringUtil.className(start));
      }
   }

   protected void aggregate(FullHttpMessage aggregated, HttpContent content) throws Exception {
      if (content instanceof LastHttpContent) {
         ((HttpObjectAggregator.AggregatedFullHttpMessage)aggregated).setTrailingHeaders(((LastHttpContent)content).trailingHeaders());
      }
   }

   protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
      if (!HttpUtil.isContentLengthSet(aggregated)) {
         aggregated.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(aggregated.content().readableBytes()));
      }
   }

   protected void handleOversizedMessage(final ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
      if (!(oversized instanceof HttpRequest)) {
         if (oversized instanceof HttpResponse) {
            ctx.close();
            throw new TooLongHttpContentException("Response entity too large: " + oversized);
         } else {
            throw new IllegalStateException();
         }
      } else {
         if (!(oversized instanceof FullHttpMessage) && (HttpUtil.is100ContinueExpected(oversized) || HttpUtil.isKeepAlive(oversized))) {
            ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if (!future.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                     ctx.close();
                  }
               }
            });
         } else {
            ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
            future.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if (!future.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                  }

                  ctx.close();
               }
            });
         }
      }
   }

   static {
      EXPECTATION_FAILED.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE_CLOSE.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE_CLOSE.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
   }

   private abstract static class AggregatedFullHttpMessage implements FullHttpMessage {
      protected final HttpMessage message;
      private final ByteBuf content;
      private HttpHeaders trailingHeaders;

      AggregatedFullHttpMessage(HttpMessage message, ByteBuf content, HttpHeaders trailingHeaders) {
         this.message = message;
         this.content = content;
         this.trailingHeaders = trailingHeaders;
      }

      @Override
      public HttpHeaders trailingHeaders() {
         HttpHeaders trailingHeaders = this.trailingHeaders;
         return (HttpHeaders)(trailingHeaders == null ? EmptyHttpHeaders.INSTANCE : trailingHeaders);
      }

      void setTrailingHeaders(HttpHeaders trailingHeaders) {
         this.trailingHeaders = trailingHeaders;
      }

      @Override
      public HttpVersion getProtocolVersion() {
         return this.message.protocolVersion();
      }

      @Override
      public HttpVersion protocolVersion() {
         return this.message.protocolVersion();
      }

      public FullHttpMessage setProtocolVersion(HttpVersion version) {
         this.message.setProtocolVersion(version);
         return this;
      }

      @Override
      public HttpHeaders headers() {
         return this.message.headers();
      }

      @Override
      public DecoderResult decoderResult() {
         return this.message.decoderResult();
      }

      @Override
      public DecoderResult getDecoderResult() {
         return this.message.decoderResult();
      }

      @Override
      public void setDecoderResult(DecoderResult result) {
         this.message.setDecoderResult(result);
      }

      @Override
      public ByteBuf content() {
         return this.content;
      }

      @Override
      public int refCnt() {
         return this.content.refCnt();
      }

      @Override
      public FullHttpMessage retain() {
         this.content.retain();
         return this;
      }

      @Override
      public FullHttpMessage retain(int increment) {
         this.content.retain(increment);
         return this;
      }

      @Override
      public FullHttpMessage touch(Object hint) {
         this.content.touch(hint);
         return this;
      }

      @Override
      public FullHttpMessage touch() {
         this.content.touch();
         return this;
      }

      @Override
      public boolean release() {
         return this.content.release();
      }

      @Override
      public boolean release(int decrement) {
         return this.content.release(decrement);
      }

      @Override
      public abstract FullHttpMessage copy();

      @Override
      public abstract FullHttpMessage duplicate();

      @Override
      public abstract FullHttpMessage retainedDuplicate();
   }

   private static final class AggregatedFullHttpRequest extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpRequest {
      AggregatedFullHttpRequest(HttpRequest request, ByteBuf content, HttpHeaders trailingHeaders) {
         super(request, content, trailingHeaders);
      }

      @Override
      public FullHttpRequest copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpRequest duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpRequest retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpRequest replace(ByteBuf content) {
         DefaultFullHttpRequest dup = new DefaultFullHttpRequest(
            this.protocolVersion(), this.method(), this.uri(), content, this.headers().copy(), this.trailingHeaders().copy()
         );
         dup.setDecoderResult(this.decoderResult());
         return dup;
      }

      @Override
      public FullHttpRequest retain(int increment) {
         super.retain(increment);
         return this;
      }

      @Override
      public FullHttpRequest retain() {
         super.retain();
         return this;
      }

      @Override
      public FullHttpRequest touch() {
         super.touch();
         return this;
      }

      @Override
      public FullHttpRequest touch(Object hint) {
         super.touch(hint);
         return this;
      }

      @Override
      public FullHttpRequest setMethod(HttpMethod method) {
         ((HttpRequest)this.message).setMethod(method);
         return this;
      }

      @Override
      public FullHttpRequest setUri(String uri) {
         ((HttpRequest)this.message).setUri(uri);
         return this;
      }

      @Override
      public HttpMethod getMethod() {
         return ((HttpRequest)this.message).method();
      }

      @Override
      public String getUri() {
         return ((HttpRequest)this.message).uri();
      }

      @Override
      public HttpMethod method() {
         return this.getMethod();
      }

      @Override
      public String uri() {
         return this.getUri();
      }

      @Override
      public FullHttpRequest setProtocolVersion(HttpVersion version) {
         super.setProtocolVersion(version);
         return this;
      }

      @Override
      public String toString() {
         return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
      }
   }

   private static final class AggregatedFullHttpResponse extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpResponse {
      AggregatedFullHttpResponse(HttpResponse message, ByteBuf content, HttpHeaders trailingHeaders) {
         super(message, content, trailingHeaders);
      }

      @Override
      public FullHttpResponse copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpResponse duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpResponse retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpResponse replace(ByteBuf content) {
         DefaultFullHttpResponse dup = new DefaultFullHttpResponse(
            this.getProtocolVersion(), this.getStatus(), content, this.headers().copy(), this.trailingHeaders().copy()
         );
         dup.setDecoderResult(this.decoderResult());
         return dup;
      }

      @Override
      public FullHttpResponse setStatus(HttpResponseStatus status) {
         ((HttpResponse)this.message).setStatus(status);
         return this;
      }

      @Override
      public HttpResponseStatus getStatus() {
         return ((HttpResponse)this.message).status();
      }

      @Override
      public HttpResponseStatus status() {
         return this.getStatus();
      }

      @Override
      public FullHttpResponse setProtocolVersion(HttpVersion version) {
         super.setProtocolVersion(version);
         return this;
      }

      @Override
      public FullHttpResponse retain(int increment) {
         super.retain(increment);
         return this;
      }

      @Override
      public FullHttpResponse retain() {
         super.retain();
         return this;
      }

      @Override
      public FullHttpResponse touch(Object hint) {
         super.touch(hint);
         return this;
      }

      @Override
      public FullHttpResponse touch() {
         super.touch();
         return this;
      }

      @Override
      public String toString() {
         return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
      }
   }
}
