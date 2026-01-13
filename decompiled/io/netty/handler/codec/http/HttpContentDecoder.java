package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder extends MessageToMessageDecoder<HttpObject> {
   static final String IDENTITY = HttpHeaderValues.IDENTITY.toString();
   protected ChannelHandlerContext ctx;
   private EmbeddedChannel decoder;
   private boolean continueResponse;
   private boolean needRead = true;
   private HttpContentDecoder.ByteBufForwarder forwarder;

   public HttpContentDecoder() {
      super(HttpObject.class);
   }

   protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
      this.needRead = true;
      if (msg instanceof HttpResponse && ((HttpResponse)msg).status().code() == 100) {
         if (!(msg instanceof LastHttpContent)) {
            this.continueResponse = true;
         }

         this.needRead = false;
         ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
      } else if (this.continueResponse) {
         if (msg instanceof LastHttpContent) {
            this.continueResponse = false;
         }

         this.needRead = false;
         ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
      } else {
         if (msg instanceof HttpMessage) {
            this.cleanup();
            HttpMessage message = (HttpMessage)msg;
            HttpHeaders headers = message.headers();
            String contentEncoding = headers.get(HttpHeaderNames.CONTENT_ENCODING);
            if (contentEncoding != null) {
               contentEncoding = contentEncoding.trim();
            } else {
               String transferEncoding = headers.get(HttpHeaderNames.TRANSFER_ENCODING);
               if (transferEncoding != null) {
                  int idx = transferEncoding.indexOf(44);
                  if (idx != -1) {
                     contentEncoding = transferEncoding.substring(0, idx).trim();
                  } else {
                     contentEncoding = transferEncoding.trim();
                  }
               } else {
                  contentEncoding = IDENTITY;
               }
            }

            this.decoder = this.newContentDecoder(contentEncoding);
            if (this.decoder == null) {
               if (message instanceof HttpContent) {
                  ((HttpContent)message).retain();
               }

               this.needRead = false;
               ctx.fireChannelRead(message);
               return;
            }

            this.decoder.pipeline().addLast(this.forwarder);
            if (headers.contains(HttpHeaderNames.CONTENT_LENGTH)) {
               headers.remove(HttpHeaderNames.CONTENT_LENGTH);
               headers.set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            }

            CharSequence targetContentEncoding = this.getTargetContentEncoding(contentEncoding);
            if (HttpHeaderValues.IDENTITY.contentEquals(targetContentEncoding)) {
               headers.remove(HttpHeaderNames.CONTENT_ENCODING);
            } else {
               headers.set(HttpHeaderNames.CONTENT_ENCODING, targetContentEncoding);
            }

            if (message instanceof HttpContent) {
               HttpMessage copy;
               if (message instanceof HttpRequest) {
                  HttpRequest r = (HttpRequest)message;
                  copy = new DefaultHttpRequest(r.protocolVersion(), r.method(), r.uri());
               } else {
                  if (!(message instanceof HttpResponse)) {
                     throw new CodecException("Object of class " + message.getClass().getName() + " is not an HttpRequest or HttpResponse");
                  }

                  HttpResponse r = (HttpResponse)message;
                  copy = new DefaultHttpResponse(r.protocolVersion(), r.status());
               }

               copy.headers().set(message.headers());
               copy.setDecoderResult(message.decoderResult());
               this.needRead = false;
               ctx.fireChannelRead(copy);
            } else {
               this.needRead = false;
               ctx.fireChannelRead(message);
            }
         }

         if (msg instanceof HttpContent) {
            HttpContent c = (HttpContent)msg;
            if (this.decoder == null) {
               this.needRead = false;
               ctx.fireChannelRead(c.retain());
            } else {
               this.decoder.writeInbound(c.content().retain());
               if (c instanceof LastHttpContent) {
                  boolean notEmpty = this.decoder.finish();
                  this.decoder = null;

                  assert !notEmpty;

                  LastHttpContent last = (LastHttpContent)c;
                  HttpHeaders headersx = last.trailingHeaders();
                  this.needRead = false;
                  if (headersx.isEmpty()) {
                     ctx.fireChannelRead(LastHttpContent.EMPTY_LAST_CONTENT);
                  } else {
                     ctx.fireChannelRead(new ComposedLastHttpContent(headersx, DecoderResult.SUCCESS));
                  }
               }
            }
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      boolean needRead = this.needRead;
      this.needRead = true;

      try {
         ctx.fireChannelReadComplete();
      } finally {
         if (needRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
         }
      }
   }

   protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

   protected String getTargetContentEncoding(String contentEncoding) throws Exception {
      return IDENTITY;
   }

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

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
      this.forwarder = new HttpContentDecoder.ByteBufForwarder(ctx);
      super.handlerAdded(ctx);
   }

   private void cleanup() {
      if (this.decoder != null) {
         boolean nonEmpty = this.decoder.finishAndReleaseAll();
         this.decoder = null;

         assert !nonEmpty;
      }
   }

   private void cleanupSafely(ChannelHandlerContext ctx) {
      try {
         this.cleanup();
      } catch (Throwable var3) {
         ctx.fireExceptionCaught(var3);
      }
   }

   private final class ByteBufForwarder extends ChannelInboundHandlerAdapter {
      private final ChannelHandlerContext targetCtx;

      ByteBufForwarder(ChannelHandlerContext targetCtx) {
         this.targetCtx = targetCtx;
      }

      @Override
      public boolean isSharable() {
         return true;
      }

      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
         ByteBuf buf = (ByteBuf)msg;
         if (!buf.isReadable()) {
            buf.release();
         } else {
            HttpContentDecoder.this.needRead = false;
            this.targetCtx.fireChannelRead(new DefaultHttpContent(buf));
         }
      }
   }
}
