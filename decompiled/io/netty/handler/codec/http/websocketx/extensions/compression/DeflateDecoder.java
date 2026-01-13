package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateDecoder extends WebSocketExtensionDecoder {
   static final ByteBuf FRAME_TAIL = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, -1, -1})).asReadOnly();
   static final ByteBuf EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{0})).asReadOnly();
   private final boolean noContext;
   private final WebSocketExtensionFilter extensionDecoderFilter;
   private final int maxAllocation;
   private EmbeddedChannel decoder;

   DeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter, int maxAllocation) {
      this.noContext = noContext;
      this.extensionDecoderFilter = ObjectUtil.checkNotNull(extensionDecoderFilter, "extensionDecoderFilter");
      this.maxAllocation = maxAllocation;
   }

   protected WebSocketExtensionFilter extensionDecoderFilter() {
      return this.extensionDecoderFilter;
   }

   protected abstract boolean appendFrameTail(WebSocketFrame var1);

   protected abstract int newRsv(WebSocketFrame var1);

   protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
      ByteBuf decompressedContent = this.decompressContent(ctx, msg);
      WebSocketFrame outMsg;
      if (msg instanceof TextWebSocketFrame) {
         outMsg = new TextWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
      } else if (msg instanceof BinaryWebSocketFrame) {
         outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
      } else {
         if (!(msg instanceof ContinuationWebSocketFrame)) {
            throw new CodecException("unexpected frame type: " + msg.getClass().getName());
         }

         outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
      }

      out.add(outMsg);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.cleanup();
      super.handlerRemoved(ctx);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.cleanup();
      super.channelInactive(ctx);
   }

   private ByteBuf decompressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
      if (this.decoder == null) {
         if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
            throw new CodecException("unexpected initial frame type: " + msg.getClass().getName());
         }

         this.decoder = EmbeddedChannel.builder().handlers(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE, this.maxAllocation)).build();
      }

      boolean readable = msg.content().isReadable();
      boolean emptyDeflateBlock = EMPTY_DEFLATE_BLOCK.equals(msg.content());
      this.decoder.writeInbound(msg.content().retain());
      if (this.appendFrameTail(msg)) {
         this.decoder.writeInbound(FRAME_TAIL.duplicate());
      }

      CompositeByteBuf compositeDecompressedContent = ctx.alloc().compositeBuffer();

      while (true) {
         ByteBuf partUncompressedContent = this.decoder.readInbound();
         if (partUncompressedContent == null) {
            if (!emptyDeflateBlock && readable && compositeDecompressedContent.numComponents() <= 0 && !(msg instanceof ContinuationWebSocketFrame)) {
               compositeDecompressedContent.release();
               throw new CodecException("cannot read uncompressed buffer");
            }

            if (msg.isFinalFragment() && this.noContext) {
               this.cleanup();
            }

            return compositeDecompressedContent;
         }

         if (!partUncompressedContent.isReadable()) {
            partUncompressedContent.release();
         } else {
            compositeDecompressedContent.addComponent(true, partUncompressedContent);
         }
      }
   }

   private void cleanup() {
      if (this.decoder != null) {
         this.decoder.finishAndReleaseAll();
         this.decoder = null;
      }
   }
}
