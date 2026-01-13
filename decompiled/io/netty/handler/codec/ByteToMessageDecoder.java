package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter {
   public static final ByteToMessageDecoder.Cumulator MERGE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      @Override
      public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
         if (cumulation == in) {
            in.release();
            return cumulation;
         } else if (!cumulation.isReadable() && in.isContiguous()) {
            cumulation.release();
            return in;
         } else {
            ByteBuf var5;
            try {
               int required = in.readableBytes();
               if (required <= cumulation.maxWritableBytes()
                  && (required <= cumulation.maxFastWritableBytes() || cumulation.refCnt() <= 1)
                  && !cumulation.isReadOnly()) {
                  cumulation.writeBytes(in, in.readerIndex(), required);
                  in.readerIndex(in.writerIndex());
                  return cumulation;
               }

               var5 = ByteToMessageDecoder.expandCumulation(alloc, cumulation, in);
            } finally {
               in.release();
            }

            return var5;
         }
      }
   };
   public static final ByteToMessageDecoder.Cumulator COMPOSITE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      @Override
      public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
         if (cumulation == in) {
            in.release();
            return cumulation;
         } else if (!cumulation.isReadable()) {
            cumulation.release();
            return in;
         } else {
            CompositeByteBuf composite = null;

            CompositeByteBuf var5;
            try {
               if (cumulation instanceof CompositeByteBuf && cumulation.refCnt() == 1) {
                  composite = (CompositeByteBuf)cumulation;
                  if (composite.writerIndex() != composite.capacity()) {
                     composite.capacity(composite.writerIndex());
                  }
               } else {
                  composite = alloc.compositeBuffer(Integer.MAX_VALUE).addFlattenedComponents(true, cumulation);
               }

               composite.addFlattenedComponents(true, in);
               in = null;
               var5 = composite;
            } finally {
               if (in != null) {
                  in.release();
                  if (composite != null && composite != cumulation) {
                     composite.release();
                  }
               }
            }

            return var5;
         }
      }
   };
   private static final byte STATE_INIT = 0;
   private static final byte STATE_CALLING_CHILD_DECODE = 1;
   private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
   private Queue<Object> inputMessages;
   ByteBuf cumulation;
   private ByteToMessageDecoder.Cumulator cumulator = MERGE_CUMULATOR;
   private boolean singleDecode;
   private boolean first;
   private boolean firedChannelRead;
   private boolean selfFiredChannelRead;
   private byte decodeState = 0;
   private int discardAfterReads = 16;
   private int numReads;

   protected ByteToMessageDecoder() {
      this.ensureNotSharable();
   }

   public void setSingleDecode(boolean singleDecode) {
      this.singleDecode = singleDecode;
   }

   public boolean isSingleDecode() {
      return this.singleDecode;
   }

   public void setCumulator(ByteToMessageDecoder.Cumulator cumulator) {
      this.cumulator = ObjectUtil.checkNotNull(cumulator, "cumulator");
   }

   public void setDiscardAfterReads(int discardAfterReads) {
      ObjectUtil.checkPositive(discardAfterReads, "discardAfterReads");
      this.discardAfterReads = discardAfterReads;
   }

   protected int actualReadableBytes() {
      return this.internalBuffer().readableBytes();
   }

   protected ByteBuf internalBuffer() {
      return this.cumulation != null ? this.cumulation : Unpooled.EMPTY_BUFFER;
   }

   @Override
   public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      if (this.decodeState == 1) {
         this.decodeState = 2;
      } else {
         ByteBuf buf = this.cumulation;
         if (buf != null) {
            this.cumulation = null;
            this.numReads = 0;
            int readable = buf.readableBytes();
            if (readable > 0) {
               ctx.fireChannelRead(buf);
               ctx.fireChannelReadComplete();
            } else {
               buf.release();
            }
         }

         this.handlerRemoved0(ctx);
      }
   }

   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object input) throws Exception {
      if (this.decodeState == 0) {
         do {
            if (input instanceof ByteBuf) {
               this.selfFiredChannelRead = true;
               CodecOutputList out = CodecOutputList.newInstance();

               try {
                  this.first = this.cumulation == null;
                  this.cumulation = this.cumulator.cumulate(ctx.alloc(), this.first ? Unpooled.EMPTY_BUFFER : this.cumulation, (ByteBuf)input);
                  this.callDecode(ctx, this.cumulation, out);
               } catch (DecoderException var31) {
                  throw var31;
               } catch (Exception var32) {
                  throw new DecoderException(var32);
               } finally {
                  try {
                     if (this.cumulation != null && !this.cumulation.isReadable()) {
                        this.numReads = 0;

                        try {
                           this.cumulation.release();
                        } catch (IllegalReferenceCountException var30) {
                           throw new IllegalReferenceCountException(
                              this.getClass().getSimpleName()
                                 + "#decode() might have released its input buffer, or passed it down the pipeline without a retain() call, which is not allowed.",
                              var30
                           );
                        }

                        this.cumulation = null;
                     } else if (++this.numReads >= this.discardAfterReads) {
                        this.numReads = 0;
                        this.discardSomeReadBytes();
                     }

                     int size = out.size();
                     this.firedChannelRead = this.firedChannelRead | out.insertSinceRecycled();
                     fireChannelRead(ctx, out, size);
                  } finally {
                     out.recycle();
                  }
               }
            } else {
               ctx.fireChannelRead(input);
            }
         } while (this.inputMessages != null && (input = this.inputMessages.poll()) != null);
      } else {
         if (this.inputMessages == null) {
            this.inputMessages = new ArrayDeque<>(2);
         }

         this.inputMessages.offer(input);
      }
   }

   static void fireChannelRead(ChannelHandlerContext ctx, List<Object> msgs, int numElements) {
      if (msgs instanceof CodecOutputList) {
         fireChannelRead(ctx, (CodecOutputList)msgs, numElements);
      } else {
         for (int i = 0; i < numElements; i++) {
            ctx.fireChannelRead(msgs.get(i));
         }
      }
   }

   static void fireChannelRead(ChannelHandlerContext ctx, CodecOutputList msgs, int numElements) {
      for (int i = 0; i < numElements; i++) {
         ctx.fireChannelRead(msgs.getUnsafe(i));
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      this.numReads = 0;
      this.discardSomeReadBytes();
      if (this.selfFiredChannelRead && !this.firedChannelRead && !ctx.channel().config().isAutoRead()) {
         ctx.read();
      }

      this.firedChannelRead = false;
      this.selfFiredChannelRead = false;
      ctx.fireChannelReadComplete();
   }

   protected final void discardSomeReadBytes() {
      if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
         this.cumulation.discardSomeReadBytes();
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.channelInputClosed(ctx, true);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof ChannelInputShutdownEvent) {
         this.channelInputClosed(ctx, false);
      }

      super.userEventTriggered(ctx, evt);
   }

   private void channelInputClosed(ChannelHandlerContext ctx, boolean callChannelInactive) {
      CodecOutputList out = CodecOutputList.newInstance();

      try {
         this.channelInputClosed(ctx, out);
      } catch (DecoderException var24) {
         throw var24;
      } catch (Exception var25) {
         throw new DecoderException(var25);
      } finally {
         try {
            if (this.cumulation != null) {
               this.cumulation.release();
               this.cumulation = null;
            }

            int size = out.size();
            fireChannelRead(ctx, out, size);
            if (size > 0) {
               ctx.fireChannelReadComplete();
            }

            if (callChannelInactive) {
               ctx.fireChannelInactive();
            }
         } finally {
            out.recycle();
         }
      }
   }

   void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
      if (this.cumulation != null) {
         this.callDecode(ctx, this.cumulation, out);
         if (!ctx.isRemoved()) {
            ByteBuf buffer = this.cumulation == null ? Unpooled.EMPTY_BUFFER : this.cumulation;
            this.decodeLast(ctx, buffer, out);
         }
      } else {
         this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
      }
   }

   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      try {
         while (in.isReadable()) {
            int outSize = out.size();
            if (outSize > 0) {
               fireChannelRead(ctx, out, outSize);
               out.clear();
               if (ctx.isRemoved()) {
                  break;
               }
            }

            int oldInputLength = in.readableBytes();
            this.decodeRemovalReentryProtection(ctx, in, out);
            if (!ctx.isRemoved()) {
               if (out.isEmpty()) {
                  if (oldInputLength != in.readableBytes()) {
                     continue;
                  }
               } else {
                  if (oldInputLength == in.readableBytes()) {
                     throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                  }

                  if (!this.isSingleDecode()) {
                     continue;
                  }
               }
            }
            break;
         }
      } catch (DecoderException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new DecoderException(var7);
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

   final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      this.decodeState = 1;

      try {
         this.decode(ctx, in, out);
      } finally {
         if (this.inputMessages == null || this.inputMessages.isEmpty()) {
            boolean removePending = this.decodeState == 2;
            this.decodeState = 0;
            if (removePending) {
               fireChannelRead(ctx, out, out.size());
               out.clear();
               this.handlerRemoved(ctx);
            }
         }
      }
   }

   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (in.isReadable()) {
         this.decodeRemovalReentryProtection(ctx, in, out);
      }
   }

   static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf oldCumulation, ByteBuf in) {
      int oldBytes = oldCumulation.readableBytes();
      int newBytes = in.readableBytes();
      int totalBytes = oldBytes + newBytes;
      ByteBuf newCumulation = alloc.buffer(alloc.calculateNewCapacity(totalBytes, Integer.MAX_VALUE));
      ByteBuf toRelease = newCumulation;

      ByteBuf var8;
      try {
         newCumulation.setBytes(0, oldCumulation, oldCumulation.readerIndex(), oldBytes)
            .setBytes(oldBytes, in, in.readerIndex(), newBytes)
            .writerIndex(totalBytes);
         in.readerIndex(in.writerIndex());
         toRelease = oldCumulation;
         var8 = newCumulation;
      } finally {
         toRelease.release();
      }

      return var8;
   }

   public interface Cumulator {
      ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);
   }
}
