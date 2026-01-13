package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;
import java.util.List;

public class LengthFieldBasedFrameDecoder extends ByteToMessageDecoder {
   private final ByteOrder byteOrder;
   private final int maxFrameLength;
   private final int lengthFieldOffset;
   private final int lengthFieldLength;
   private final int lengthFieldEndOffset;
   private final int lengthAdjustment;
   private final int initialBytesToStrip;
   private final boolean failFast;
   private boolean discardingTooLongFrame;
   private long tooLongFrameLength;
   private long bytesToDiscard;
   private int frameLengthInt = -1;

   public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
      this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
   }

   public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
      this(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, true);
   }

   public LengthFieldBasedFrameDecoder(
      int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast
   ) {
      this(ByteOrder.BIG_ENDIAN, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
   }

   public LengthFieldBasedFrameDecoder(
      ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast
   ) {
      this.byteOrder = ObjectUtil.checkNotNull(byteOrder, "byteOrder");
      ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
      ObjectUtil.checkPositiveOrZero(lengthFieldOffset, "lengthFieldOffset");
      ObjectUtil.checkPositiveOrZero(initialBytesToStrip, "initialBytesToStrip");
      if (lengthFieldOffset > maxFrameLength - lengthFieldLength) {
         throw new IllegalArgumentException(
            "maxFrameLength ("
               + maxFrameLength
               + ") must be equal to or greater than lengthFieldOffset ("
               + lengthFieldOffset
               + ") + lengthFieldLength ("
               + lengthFieldLength
               + ")."
         );
      } else {
         this.maxFrameLength = maxFrameLength;
         this.lengthFieldOffset = lengthFieldOffset;
         this.lengthFieldLength = lengthFieldLength;
         this.lengthAdjustment = lengthAdjustment;
         this.lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
         this.initialBytesToStrip = initialBytesToStrip;
         this.failFast = failFast;
      }
   }

   @Override
   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      Object decoded = this.decode(ctx, in);
      if (decoded != null) {
         out.add(decoded);
      }
   }

   private void discardingTooLongFrame(ByteBuf in) {
      long bytesToDiscard = this.bytesToDiscard;
      int localBytesToDiscard = (int)Math.min(bytesToDiscard, (long)in.readableBytes());
      in.skipBytes(localBytesToDiscard);
      bytesToDiscard -= localBytesToDiscard;
      this.bytesToDiscard = bytesToDiscard;
      this.failIfNecessary(false);
   }

   private static void failOnNegativeLengthField(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
      in.skipBytes(lengthFieldEndOffset);
      throw new CorruptedFrameException("negative pre-adjustment length field: " + frameLength);
   }

   private static void failOnFrameLengthLessThanLengthFieldEndOffset(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
      in.skipBytes(lengthFieldEndOffset);
      throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less than lengthFieldEndOffset: " + lengthFieldEndOffset);
   }

   private void exceededFrameLength(ByteBuf in, long frameLength) {
      long discard = frameLength - in.readableBytes();
      this.tooLongFrameLength = frameLength;
      if (discard < 0L) {
         in.skipBytes((int)frameLength);
      } else {
         this.discardingTooLongFrame = true;
         this.bytesToDiscard = discard;
         in.skipBytes(in.readableBytes());
      }

      this.failIfNecessary(true);
   }

   private static void failOnFrameLengthLessThanInitialBytesToStrip(ByteBuf in, long frameLength, int initialBytesToStrip) {
      in.skipBytes((int)frameLength);
      throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less than initialBytesToStrip: " + initialBytesToStrip);
   }

   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
      long frameLength = 0L;
      if (this.frameLengthInt == -1) {
         if (this.discardingTooLongFrame) {
            this.discardingTooLongFrame(in);
         }

         if (in.readableBytes() < this.lengthFieldEndOffset) {
            return null;
         }

         int actualLengthFieldOffset = in.readerIndex() + this.lengthFieldOffset;
         frameLength = this.getUnadjustedFrameLength(in, actualLengthFieldOffset, this.lengthFieldLength, this.byteOrder);
         if (frameLength < 0L) {
            failOnNegativeLengthField(in, frameLength, this.lengthFieldEndOffset);
         }

         frameLength += this.lengthAdjustment + this.lengthFieldEndOffset;
         if (frameLength < this.lengthFieldEndOffset) {
            failOnFrameLengthLessThanLengthFieldEndOffset(in, frameLength, this.lengthFieldEndOffset);
         }

         if (frameLength > this.maxFrameLength) {
            this.exceededFrameLength(in, frameLength);
            return null;
         }

         this.frameLengthInt = (int)frameLength;
      }

      if (in.readableBytes() < this.frameLengthInt) {
         return null;
      } else {
         if (this.initialBytesToStrip > this.frameLengthInt) {
            failOnFrameLengthLessThanInitialBytesToStrip(in, frameLength, this.initialBytesToStrip);
         }

         in.skipBytes(this.initialBytesToStrip);
         int readerIndex = in.readerIndex();
         int actualFrameLength = this.frameLengthInt - this.initialBytesToStrip;
         ByteBuf frame = this.extractFrame(ctx, in, readerIndex, actualFrameLength);
         in.readerIndex(readerIndex + actualFrameLength);
         this.frameLengthInt = -1;
         return frame;
      }
   }

   protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
      buf = buf.order(order);
      long frameLength;
      switch (length) {
         case 1:
            frameLength = buf.getUnsignedByte(offset);
            break;
         case 2:
            frameLength = buf.getUnsignedShort(offset);
            break;
         case 3:
            frameLength = buf.getUnsignedMedium(offset);
            break;
         case 4:
            frameLength = buf.getUnsignedInt(offset);
            break;
         case 5:
         case 6:
         case 7:
         default:
            throw new DecoderException("unsupported lengthFieldLength: " + this.lengthFieldLength + " (expected: 1, 2, 3, 4, or 8)");
         case 8:
            frameLength = buf.getLong(offset);
      }

      return frameLength;
   }

   private void failIfNecessary(boolean firstDetectionOfTooLongFrame) {
      if (this.bytesToDiscard == 0L) {
         long tooLongFrameLength = this.tooLongFrameLength;
         this.tooLongFrameLength = 0L;
         this.discardingTooLongFrame = false;
         if (!this.failFast || firstDetectionOfTooLongFrame) {
            this.fail(tooLongFrameLength);
         }
      } else if (this.failFast && firstDetectionOfTooLongFrame) {
         this.fail(this.tooLongFrameLength);
      }
   }

   protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
      return buffer.retainedSlice(index, length);
   }

   private void fail(long frameLength) {
      if (frameLength > 0L) {
         throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
      } else {
         throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding");
      }
   }
}
