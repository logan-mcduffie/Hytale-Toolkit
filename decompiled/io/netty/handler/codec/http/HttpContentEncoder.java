package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder extends MessageToMessageCodec<HttpRequest, HttpObject> {
   private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
   private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
   private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque<>();
   private EmbeddedChannel encoder;
   private HttpContentEncoder.State state = HttpContentEncoder.State.AWAIT_HEADERS;

   public HttpContentEncoder() {
      super(HttpRequest.class, HttpObject.class);
   }

   @Override
   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return msg instanceof HttpContent || msg instanceof HttpResponse;
   }

   protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
      List<String> acceptEncodingHeaders = msg.headers().getAll(HttpHeaderNames.ACCEPT_ENCODING);
      CharSequence acceptEncoding;
      switch (acceptEncodingHeaders.size()) {
         case 0:
            acceptEncoding = HttpContentDecoder.IDENTITY;
            break;
         case 1:
            acceptEncoding = acceptEncodingHeaders.get(0);
            break;
         default:
            acceptEncoding = StringUtil.join(",", acceptEncodingHeaders);
      }

      HttpMethod method = msg.method();
      if (HttpMethod.HEAD.equals(method)) {
         acceptEncoding = ZERO_LENGTH_HEAD;
      } else if (HttpMethod.CONNECT.equals(method)) {
         acceptEncoding = ZERO_LENGTH_CONNECT;
      }

      this.acceptEncodingQueue.add(acceptEncoding);
      out.add(ReferenceCountUtil.retain(msg));
   }

   protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
      boolean isFull = msg instanceof HttpResponse && msg instanceof LastHttpContent;
      switch (this.state) {
         case AWAIT_HEADERS:
            ensureHeaders(msg);

            assert this.encoder == null;

            HttpResponse res = (HttpResponse)msg;
            int code = res.status().code();
            HttpStatusClass codeClass = res.status().codeClass();
            CharSequence acceptEncoding;
            if (codeClass == HttpStatusClass.INFORMATIONAL) {
               acceptEncoding = null;
            } else {
               acceptEncoding = this.acceptEncodingQueue.poll();
               if (acceptEncoding == null) {
                  throw new IllegalStateException("cannot send more responses than requests");
               }
            }

            if (isPassthru(res.protocolVersion(), code, acceptEncoding)) {
               out.add(ReferenceCountUtil.retain(res));
               if (!isFull) {
                  this.state = HttpContentEncoder.State.PASS_THROUGH;
               }
               break;
            } else if (isFull && !((ByteBufHolder)res).content().isReadable()) {
               out.add(ReferenceCountUtil.retain(res));
               break;
            } else {
               HttpContentEncoder.Result result = this.beginEncode(res, acceptEncoding.toString());
               if (result == null) {
                  out.add(ReferenceCountUtil.retain(res));
                  if (!isFull) {
                     this.state = HttpContentEncoder.State.PASS_THROUGH;
                  }
                  break;
               } else {
                  this.encoder = result.contentEncoder();
                  res.headers().set(HttpHeaderNames.CONTENT_ENCODING, result.targetContentEncoding());
                  if (isFull) {
                     HttpResponse newRes = new DefaultHttpResponse(res.protocolVersion(), res.status());
                     newRes.headers().set(res.headers());
                     out.add(newRes);
                     ensureContent(res);
                     this.encodeFullResponse(newRes, (HttpContent)res, out);
                     break;
                  } else {
                     res.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                     res.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                     out.add(ReferenceCountUtil.retain(res));
                     this.state = HttpContentEncoder.State.AWAIT_CONTENT;
                     if (!(msg instanceof HttpContent)) {
                        break;
                     }
                  }
               }
            }
         case AWAIT_CONTENT:
            ensureContent(msg);
            if (this.encodeContent((HttpContent)msg, out)) {
               this.state = HttpContentEncoder.State.AWAIT_HEADERS;
            } else if (out.isEmpty()) {
               out.add(new DefaultHttpContent(Unpooled.EMPTY_BUFFER));
            }
            break;
         case PASS_THROUGH:
            ensureContent(msg);
            out.add(ReferenceCountUtil.retain(msg));
            if (msg instanceof LastHttpContent) {
               this.state = HttpContentEncoder.State.AWAIT_HEADERS;
            }
      }
   }

   private void encodeFullResponse(HttpResponse newRes, HttpContent content, List<Object> out) {
      int existingMessages = out.size();
      this.encodeContent(content, out);
      if (HttpUtil.isContentLengthSet(newRes)) {
         int messageSize = 0;

         for (int i = existingMessages; i < out.size(); i++) {
            Object item = out.get(i);
            if (item instanceof HttpContent) {
               messageSize += ((HttpContent)item).content().readableBytes();
            }
         }

         HttpUtil.setContentLength(newRes, messageSize);
      } else {
         newRes.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
      }
   }

   private static boolean isPassthru(HttpVersion version, int code, CharSequence httpMethod) {
      return code < 200
         || code == 204
         || code == 304
         || httpMethod == ZERO_LENGTH_HEAD
         || httpMethod == ZERO_LENGTH_CONNECT && code == 200
         || version == HttpVersion.HTTP_1_0;
   }

   private static void ensureHeaders(HttpObject msg) {
      if (!(msg instanceof HttpResponse)) {
         throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
      }
   }

   private static void ensureContent(HttpObject msg) {
      if (!(msg instanceof HttpContent)) {
         throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
      }
   }

   private boolean encodeContent(HttpContent c, List<Object> out) {
      ByteBuf content = c.content();
      this.encode(content, out);
      if (c instanceof LastHttpContent) {
         this.finishEncode(out);
         LastHttpContent last = (LastHttpContent)c;
         HttpHeaders headers = last.trailingHeaders();
         if (headers.isEmpty()) {
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
         } else {
            out.add(new ComposedLastHttpContent(headers, DecoderResult.SUCCESS));
         }

         return true;
      } else {
         return false;
      }
   }

   protected abstract HttpContentEncoder.Result beginEncode(HttpResponse var1, String var2) throws Exception;

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.cleanupSafely(ctx);
      super.handlerRemoved(ctx);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.cleanupSafely(ctx);
      super.channelInactive(ctx);
   }

   private void cleanup() {
      if (this.encoder != null) {
         this.encoder.finishAndReleaseAll();
         this.encoder = null;
      }
   }

   private void cleanupSafely(ChannelHandlerContext ctx) {
      try {
         this.cleanup();
      } catch (Throwable var3) {
         ctx.fireExceptionCaught(var3);
      }
   }

   private void encode(ByteBuf in, List<Object> out) {
      this.encoder.writeOutbound(in.retain());
      this.fetchEncoderOutput(out);
   }

   private void finishEncode(List<Object> out) {
      if (this.encoder.finish()) {
         this.fetchEncoderOutput(out);
      }

      this.encoder = null;
   }

   private void fetchEncoderOutput(List<Object> out) {
      while (true) {
         ByteBuf buf = this.encoder.readOutbound();
         if (buf == null) {
            return;
         }

         if (!buf.isReadable()) {
            buf.release();
         } else {
            out.add(new DefaultHttpContent(buf));
         }
      }
   }

   public static final class Result {
      private final String targetContentEncoding;
      private final EmbeddedChannel contentEncoder;

      public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
         this.targetContentEncoding = ObjectUtil.checkNotNull(targetContentEncoding, "targetContentEncoding");
         this.contentEncoder = ObjectUtil.checkNotNull(contentEncoder, "contentEncoder");
      }

      public String targetContentEncoding() {
         return this.targetContentEncoding;
      }

      public EmbeddedChannel contentEncoder() {
         return this.contentEncoder;
      }
   }

   private static enum State {
      PASS_THROUGH,
      AWAIT_HEADERS,
      AWAIT_CONTENT;
   }
}
