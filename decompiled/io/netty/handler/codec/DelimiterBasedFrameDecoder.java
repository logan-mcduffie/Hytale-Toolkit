package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DelimiterBasedFrameDecoder extends ByteToMessageDecoder {
   private final ByteBuf[] delimiters;
   private final int maxFrameLength;
   private final boolean stripDelimiter;
   private final boolean failFast;
   private boolean discardingTooLongFrame;
   private int tooLongFrameLength;
   private final LineBasedFrameDecoder lineBasedDecoder;

   public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf delimiter) {
      this(maxFrameLength, true, delimiter);
   }

   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf delimiter) {
      this(maxFrameLength, stripDelimiter, true, delimiter);
   }

   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
      this(maxFrameLength, stripDelimiter, failFast, delimiter.slice(delimiter.readerIndex(), delimiter.readableBytes()));
   }

   public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf... delimiters) {
      this(maxFrameLength, true, delimiters);
   }

   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf... delimiters) {
      this(maxFrameLength, stripDelimiter, true, delimiters);
   }

   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf... delimiters) {
      validateMaxFrameLength(maxFrameLength);
      ObjectUtil.checkNonEmpty(delimiters, "delimiters");
      if (isLineBased(delimiters) && !this.isSubclass()) {
         this.lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
         this.delimiters = null;
      } else {
         this.delimiters = new ByteBuf[delimiters.length];

         for (int i = 0; i < delimiters.length; i++) {
            ByteBuf d = delimiters[i];
            validateDelimiter(d);
            this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
         }

         this.lineBasedDecoder = null;
      }

      this.maxFrameLength = maxFrameLength;
      this.stripDelimiter = stripDelimiter;
      this.failFast = failFast;
   }

   private static boolean isLineBased(ByteBuf[] delimiters) {
      if (delimiters.length != 2) {
         return false;
      } else {
         ByteBuf a = delimiters[0];
         ByteBuf b = delimiters[1];
         if (a.capacity() < b.capacity()) {
            a = delimiters[1];
            b = delimiters[0];
         }

         return a.capacity() == 2 && b.capacity() == 1 && a.getByte(0) == 13 && a.getByte(1) == 10 && b.getByte(0) == 10;
      }
   }

   private boolean isSubclass() {
      return this.getClass() != DelimiterBasedFrameDecoder.class;
   }

   @Override
   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      Object decoded = this.decode(ctx, in);
      if (decoded != null) {
         out.add(decoded);
      }
   }

   protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
      if (this.lineBasedDecoder != null) {
         return this.lineBasedDecoder.decode(ctx, buffer);
      } else {
         int minFrameLength = Integer.MAX_VALUE;
         ByteBuf minDelim = null;

         for (ByteBuf delim : this.delimiters) {
            int frameLength = indexOf(buffer, delim);
            if (frameLength >= 0 && frameLength < minFrameLength) {
               minFrameLength = frameLength;
               minDelim = delim;
            }
         }

         if (minDelim != null) {
            int minDelimLength = minDelim.capacity();
            if (this.discardingTooLongFrame) {
               this.discardingTooLongFrame = false;
               buffer.skipBytes(minFrameLength + minDelimLength);
               int tooLongFrameLength = this.tooLongFrameLength;
               this.tooLongFrameLength = 0;
               if (!this.failFast) {
                  this.fail(tooLongFrameLength);
               }

               return null;
            } else if (minFrameLength > this.maxFrameLength) {
               buffer.skipBytes(minFrameLength + minDelimLength);
               this.fail(minFrameLength);
               return null;
            } else {
               ByteBuf frame;
               if (this.stripDelimiter) {
                  frame = buffer.readRetainedSlice(minFrameLength);
                  buffer.skipBytes(minDelimLength);
               } else {
                  frame = buffer.readRetainedSlice(minFrameLength + minDelimLength);
               }

               return frame;
            }
         } else {
            if (!this.discardingTooLongFrame) {
               if (buffer.readableBytes() > this.maxFrameLength) {
                  this.tooLongFrameLength = buffer.readableBytes();
                  buffer.skipBytes(buffer.readableBytes());
                  this.discardingTooLongFrame = true;
                  if (this.failFast) {
                     this.fail(this.tooLongFrameLength);
                  }
               }
            } else {
               this.tooLongFrameLength = this.tooLongFrameLength + buffer.readableBytes();
               buffer.skipBytes(buffer.readableBytes());
            }

            return null;
         }
      }
   }

   private void fail(long frameLength) {
      if (frameLength > 0L) {
         throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
      } else {
         throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
      }
   }

   private static int indexOf(ByteBuf haystack, ByteBuf needle) {
      int index = ByteBufUtil.indexOf(needle, haystack);
      return index == -1 ? -1 : index - haystack.readerIndex();
   }

   private static void validateDelimiter(ByteBuf delimiter) {
      ObjectUtil.checkNotNull(delimiter, "delimiter");
      if (!delimiter.isReadable()) {
         throw new IllegalArgumentException("empty delimiter");
      }
   }

   private static void validateMaxFrameLength(int maxFrameLength) {
      ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
   }
}
