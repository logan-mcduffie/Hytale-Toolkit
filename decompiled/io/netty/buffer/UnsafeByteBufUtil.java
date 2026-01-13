package io.netty.buffer;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

final class UnsafeByteBufUtil {
   private static final boolean UNALIGNED = PlatformDependent.isUnaligned();
   private static final byte ZERO = 0;
   private static final int MAX_HAND_ROLLED_SET_ZERO_BYTES = 64;

   static byte getByte(long address) {
      return PlatformDependent.getByte(address);
   }

   static short getShort(long address) {
      if (UNALIGNED) {
         short v = PlatformDependent.getShort(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Short.reverseBytes(v);
      } else {
         return (short)(PlatformDependent.getByte(address) << 8 | PlatformDependent.getByte(address + 1L) & 255);
      }
   }

   static short getShortLE(long address) {
      if (UNALIGNED) {
         short v = PlatformDependent.getShort(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(v) : v;
      } else {
         return (short)(PlatformDependent.getByte(address) & 255 | PlatformDependent.getByte(address + 1L) << 8);
      }
   }

   static int getUnsignedMedium(long address) {
      return UNALIGNED
         ? (PlatformDependent.getByte(address) & 0xFF) << 16
            | (
                  PlatformDependent.BIG_ENDIAN_NATIVE_ORDER
                     ? PlatformDependent.getShort(address + 1L)
                     : Short.reverseBytes(PlatformDependent.getShort(address + 1L))
               )
               & 65535
         : (PlatformDependent.getByte(address) & 0xFF) << 16
            | (PlatformDependent.getByte(address + 1L) & 0xFF) << 8
            | PlatformDependent.getByte(address + 2L) & 0xFF;
   }

   static int getUnsignedMediumLE(long address) {
      return UNALIGNED
         ? PlatformDependent.getByte(address) & 0xFF
            | (
                  (
                        PlatformDependent.BIG_ENDIAN_NATIVE_ORDER
                           ? Short.reverseBytes(PlatformDependent.getShort(address + 1L))
                           : PlatformDependent.getShort(address + 1L)
                     )
                     & 65535
               )
               << 8
         : PlatformDependent.getByte(address) & 0xFF
            | (PlatformDependent.getByte(address + 1L) & 0xFF) << 8
            | (PlatformDependent.getByte(address + 2L) & 0xFF) << 16;
   }

   static int getInt(long address) {
      if (UNALIGNED) {
         int v = PlatformDependent.getInt(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Integer.reverseBytes(v);
      } else {
         return PlatformDependent.getByte(address) << 24
            | (PlatformDependent.getByte(address + 1L) & 0xFF) << 16
            | (PlatformDependent.getByte(address + 2L) & 0xFF) << 8
            | PlatformDependent.getByte(address + 3L) & 0xFF;
      }
   }

   static int getIntLE(long address) {
      if (UNALIGNED) {
         int v = PlatformDependent.getInt(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(v) : v;
      } else {
         return PlatformDependent.getByte(address) & 0xFF
            | (PlatformDependent.getByte(address + 1L) & 0xFF) << 8
            | (PlatformDependent.getByte(address + 2L) & 0xFF) << 16
            | PlatformDependent.getByte(address + 3L) << 24;
      }
   }

   static long getLong(long address) {
      if (UNALIGNED) {
         long v = PlatformDependent.getLong(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Long.reverseBytes(v);
      } else {
         return (long)PlatformDependent.getByte(address) << 56
            | (PlatformDependent.getByte(address + 1L) & 255L) << 48
            | (PlatformDependent.getByte(address + 2L) & 255L) << 40
            | (PlatformDependent.getByte(address + 3L) & 255L) << 32
            | (PlatformDependent.getByte(address + 4L) & 255L) << 24
            | (PlatformDependent.getByte(address + 5L) & 255L) << 16
            | (PlatformDependent.getByte(address + 6L) & 255L) << 8
            | PlatformDependent.getByte(address + 7L) & 255L;
      }
   }

   static long getLongLE(long address) {
      if (UNALIGNED) {
         long v = PlatformDependent.getLong(address);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(v) : v;
      } else {
         return PlatformDependent.getByte(address) & 255L
            | (PlatformDependent.getByte(address + 1L) & 255L) << 8
            | (PlatformDependent.getByte(address + 2L) & 255L) << 16
            | (PlatformDependent.getByte(address + 3L) & 255L) << 24
            | (PlatformDependent.getByte(address + 4L) & 255L) << 32
            | (PlatformDependent.getByte(address + 5L) & 255L) << 40
            | (PlatformDependent.getByte(address + 6L) & 255L) << 48
            | (long)PlatformDependent.getByte(address + 7L) << 56;
      }
   }

   static void setByte(long address, int value) {
      PlatformDependent.putByte(address, (byte)value);
   }

   static void setShort(long address, int value) {
      if (UNALIGNED) {
         PlatformDependent.putShort(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)value));
      } else {
         PlatformDependent.putByte(address, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 1L, (byte)value);
      }
   }

   static void setShortLE(long address, int value) {
      if (UNALIGNED) {
         PlatformDependent.putShort(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)value) : (short)value);
      } else {
         PlatformDependent.putByte(address, (byte)value);
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
      }
   }

   static void setMedium(long address, int value) {
      PlatformDependent.putByte(address, (byte)(value >>> 16));
      if (UNALIGNED) {
         PlatformDependent.putShort(address + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)value));
      } else {
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 2L, (byte)value);
      }
   }

   static void setMediumLE(long address, int value) {
      PlatformDependent.putByte(address, (byte)value);
      if (UNALIGNED) {
         PlatformDependent.putShort(address + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)(value >>> 8)) : (short)(value >>> 8));
      } else {
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 2L, (byte)(value >>> 16));
      }
   }

   static void setInt(long address, int value) {
      if (UNALIGNED) {
         PlatformDependent.putInt(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes(value));
      } else {
         PlatformDependent.putByte(address, (byte)(value >>> 24));
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 16));
         PlatformDependent.putByte(address + 2L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 3L, (byte)value);
      }
   }

   static void setIntLE(long address, int value) {
      if (UNALIGNED) {
         PlatformDependent.putInt(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(value) : value);
      } else {
         PlatformDependent.putByte(address, (byte)value);
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 2L, (byte)(value >>> 16));
         PlatformDependent.putByte(address + 3L, (byte)(value >>> 24));
      }
   }

   static void setLong(long address, long value) {
      if (UNALIGNED) {
         PlatformDependent.putLong(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes(value));
      } else {
         PlatformDependent.putByte(address, (byte)(value >>> 56));
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 48));
         PlatformDependent.putByte(address + 2L, (byte)(value >>> 40));
         PlatformDependent.putByte(address + 3L, (byte)(value >>> 32));
         PlatformDependent.putByte(address + 4L, (byte)(value >>> 24));
         PlatformDependent.putByte(address + 5L, (byte)(value >>> 16));
         PlatformDependent.putByte(address + 6L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 7L, (byte)value);
      }
   }

   static void setLongLE(long address, long value) {
      if (UNALIGNED) {
         PlatformDependent.putLong(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(value) : value);
      } else {
         PlatformDependent.putByte(address, (byte)value);
         PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
         PlatformDependent.putByte(address + 2L, (byte)(value >>> 16));
         PlatformDependent.putByte(address + 3L, (byte)(value >>> 24));
         PlatformDependent.putByte(address + 4L, (byte)(value >>> 32));
         PlatformDependent.putByte(address + 5L, (byte)(value >>> 40));
         PlatformDependent.putByte(address + 6L, (byte)(value >>> 48));
         PlatformDependent.putByte(address + 7L, (byte)(value >>> 56));
      }
   }

   static byte getByte(byte[] array, int index) {
      return PlatformDependent.getByte(array, index);
   }

   static short getShort(byte[] array, int index) {
      if (UNALIGNED) {
         short v = PlatformDependent.getShort(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Short.reverseBytes(v);
      } else {
         return (short)(PlatformDependent.getByte(array, index) << 8 | PlatformDependent.getByte(array, index + 1) & 255);
      }
   }

   static short getShortLE(byte[] array, int index) {
      if (UNALIGNED) {
         short v = PlatformDependent.getShort(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(v) : v;
      } else {
         return (short)(PlatformDependent.getByte(array, index) & 255 | PlatformDependent.getByte(array, index + 1) << 8);
      }
   }

   static int getUnsignedMedium(byte[] array, int index) {
      return UNALIGNED
         ? (PlatformDependent.getByte(array, index) & 0xFF) << 16
            | (
                  PlatformDependent.BIG_ENDIAN_NATIVE_ORDER
                     ? PlatformDependent.getShort(array, index + 1)
                     : Short.reverseBytes(PlatformDependent.getShort(array, index + 1))
               )
               & 65535
         : (PlatformDependent.getByte(array, index) & 0xFF) << 16
            | (PlatformDependent.getByte(array, index + 1) & 0xFF) << 8
            | PlatformDependent.getByte(array, index + 2) & 0xFF;
   }

   static int getUnsignedMediumLE(byte[] array, int index) {
      return UNALIGNED
         ? PlatformDependent.getByte(array, index) & 0xFF
            | (
                  (
                        PlatformDependent.BIG_ENDIAN_NATIVE_ORDER
                           ? Short.reverseBytes(PlatformDependent.getShort(array, index + 1))
                           : PlatformDependent.getShort(array, index + 1)
                     )
                     & 65535
               )
               << 8
         : PlatformDependent.getByte(array, index) & 0xFF
            | (PlatformDependent.getByte(array, index + 1) & 0xFF) << 8
            | (PlatformDependent.getByte(array, index + 2) & 0xFF) << 16;
   }

   static int getInt(byte[] array, int index) {
      if (UNALIGNED) {
         int v = PlatformDependent.getInt(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Integer.reverseBytes(v);
      } else {
         return PlatformDependent.getByte(array, index) << 24
            | (PlatformDependent.getByte(array, index + 1) & 0xFF) << 16
            | (PlatformDependent.getByte(array, index + 2) & 0xFF) << 8
            | PlatformDependent.getByte(array, index + 3) & 0xFF;
      }
   }

   static int getIntLE(byte[] array, int index) {
      if (UNALIGNED) {
         int v = PlatformDependent.getInt(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(v) : v;
      } else {
         return PlatformDependent.getByte(array, index) & 0xFF
            | (PlatformDependent.getByte(array, index + 1) & 0xFF) << 8
            | (PlatformDependent.getByte(array, index + 2) & 0xFF) << 16
            | PlatformDependent.getByte(array, index + 3) << 24;
      }
   }

   static long getLong(byte[] array, int index) {
      if (UNALIGNED) {
         long v = PlatformDependent.getLong(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Long.reverseBytes(v);
      } else {
         return (long)PlatformDependent.getByte(array, index) << 56
            | (PlatformDependent.getByte(array, index + 1) & 255L) << 48
            | (PlatformDependent.getByte(array, index + 2) & 255L) << 40
            | (PlatformDependent.getByte(array, index + 3) & 255L) << 32
            | (PlatformDependent.getByte(array, index + 4) & 255L) << 24
            | (PlatformDependent.getByte(array, index + 5) & 255L) << 16
            | (PlatformDependent.getByte(array, index + 6) & 255L) << 8
            | PlatformDependent.getByte(array, index + 7) & 255L;
      }
   }

   static long getLongLE(byte[] array, int index) {
      if (UNALIGNED) {
         long v = PlatformDependent.getLong(array, index);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(v) : v;
      } else {
         return PlatformDependent.getByte(array, index) & 255L
            | (PlatformDependent.getByte(array, index + 1) & 255L) << 8
            | (PlatformDependent.getByte(array, index + 2) & 255L) << 16
            | (PlatformDependent.getByte(array, index + 3) & 255L) << 24
            | (PlatformDependent.getByte(array, index + 4) & 255L) << 32
            | (PlatformDependent.getByte(array, index + 5) & 255L) << 40
            | (PlatformDependent.getByte(array, index + 6) & 255L) << 48
            | (long)PlatformDependent.getByte(array, index + 7) << 56;
      }
   }

   static void setByte(byte[] array, int index, int value) {
      PlatformDependent.putByte(array, index, (byte)value);
   }

   static void setShort(byte[] array, int index, int value) {
      if (UNALIGNED) {
         PlatformDependent.putShort(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)value));
      } else {
         PlatformDependent.putByte(array, index, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 1, (byte)value);
      }
   }

   static void setShortLE(byte[] array, int index, int value) {
      if (UNALIGNED) {
         PlatformDependent.putShort(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)value) : (short)value);
      } else {
         PlatformDependent.putByte(array, index, (byte)value);
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
      }
   }

   static void setMedium(byte[] array, int index, int value) {
      PlatformDependent.putByte(array, index, (byte)(value >>> 16));
      if (UNALIGNED) {
         PlatformDependent.putShort(array, index + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)value));
      } else {
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 2, (byte)value);
      }
   }

   static void setMediumLE(byte[] array, int index, int value) {
      PlatformDependent.putByte(array, index, (byte)value);
      if (UNALIGNED) {
         PlatformDependent.putShort(
            array, index + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)(value >>> 8)) : (short)(value >>> 8)
         );
      } else {
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 2, (byte)(value >>> 16));
      }
   }

   static void setInt(byte[] array, int index, int value) {
      if (UNALIGNED) {
         PlatformDependent.putInt(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes(value));
      } else {
         PlatformDependent.putByte(array, index, (byte)(value >>> 24));
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 16));
         PlatformDependent.putByte(array, index + 2, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 3, (byte)value);
      }
   }

   static void setIntLE(byte[] array, int index, int value) {
      if (UNALIGNED) {
         PlatformDependent.putInt(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(value) : value);
      } else {
         PlatformDependent.putByte(array, index, (byte)value);
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 2, (byte)(value >>> 16));
         PlatformDependent.putByte(array, index + 3, (byte)(value >>> 24));
      }
   }

   static void setLong(byte[] array, int index, long value) {
      if (UNALIGNED) {
         PlatformDependent.putLong(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes(value));
      } else {
         PlatformDependent.putByte(array, index, (byte)(value >>> 56));
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 48));
         PlatformDependent.putByte(array, index + 2, (byte)(value >>> 40));
         PlatformDependent.putByte(array, index + 3, (byte)(value >>> 32));
         PlatformDependent.putByte(array, index + 4, (byte)(value >>> 24));
         PlatformDependent.putByte(array, index + 5, (byte)(value >>> 16));
         PlatformDependent.putByte(array, index + 6, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 7, (byte)value);
      }
   }

   static void setLongLE(byte[] array, int index, long value) {
      if (UNALIGNED) {
         PlatformDependent.putLong(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(value) : value);
      } else {
         PlatformDependent.putByte(array, index, (byte)value);
         PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
         PlatformDependent.putByte(array, index + 2, (byte)(value >>> 16));
         PlatformDependent.putByte(array, index + 3, (byte)(value >>> 24));
         PlatformDependent.putByte(array, index + 4, (byte)(value >>> 32));
         PlatformDependent.putByte(array, index + 5, (byte)(value >>> 40));
         PlatformDependent.putByte(array, index + 6, (byte)(value >>> 48));
         PlatformDependent.putByte(array, index + 7, (byte)(value >>> 56));
      }
   }

   private static void batchSetZero(byte[] data, int index, int length) {
      int longBatches = length >>> 3;

      for (int i = 0; i < longBatches; i++) {
         PlatformDependent.putLong(data, index, 0L);
         index += 8;
      }

      int remaining = length & 7;

      for (int i = 0; i < remaining; i++) {
         PlatformDependent.putByte(data, index + i, (byte)0);
      }
   }

   static void setZero(byte[] array, int index, int length) {
      if (length != 0) {
         if (UNALIGNED && length <= 64) {
            batchSetZero(array, index, length);
         } else {
            PlatformDependent.setMemory(array, index, length, (byte)0);
         }
      }
   }

   static ByteBuf copy(AbstractByteBuf buf, long addr, int index, int length) {
      buf.checkIndex(index, length);
      ByteBuf copy = buf.alloc().directBuffer(length, buf.maxCapacity());
      if (length != 0) {
         if (copy.hasMemoryAddress()) {
            PlatformDependent.copyMemory(addr, copy.memoryAddress(), length);
            copy.setIndex(0, length);
         } else {
            copy.writeBytes(buf, index, length);
         }
      }

      return copy;
   }

   static int setBytes(AbstractByteBuf buf, long addr, int index, InputStream in, int length) throws IOException {
      buf.checkIndex(index, length);
      ByteBuf tmpBuf = buf.alloc().heapBuffer(length);

      int var10;
      try {
         byte[] tmp = tmpBuf.array();
         int offset = tmpBuf.arrayOffset();
         int readBytes = in.read(tmp, offset, length);
         if (readBytes > 0) {
            PlatformDependent.copyMemory(tmp, offset, addr, readBytes);
         }

         var10 = readBytes;
      } finally {
         tmpBuf.release();
      }

      return var10;
   }

   static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuf dst, int dstIndex, int length) {
      buf.checkIndex(index, length);
      ObjectUtil.checkNotNull(dst, "dst");
      if (MathUtil.isOutOfBounds(dstIndex, length, dst.capacity())) {
         throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
      } else {
         if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(addr, dst.memoryAddress() + dstIndex, length);
         } else if (dst.hasArray()) {
            PlatformDependent.copyMemory(addr, dst.array(), dst.arrayOffset() + dstIndex, length);
         } else {
            dst.setBytes(dstIndex, buf, index, length);
         }
      }
   }

   static void getBytes(AbstractByteBuf buf, long addr, int index, byte[] dst, int dstIndex, int length) {
      buf.checkIndex(index, length);
      ObjectUtil.checkNotNull(dst, "dst");
      if (MathUtil.isOutOfBounds(dstIndex, length, dst.length)) {
         throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
      } else {
         if (length != 0) {
            PlatformDependent.copyMemory(addr, dst, dstIndex, length);
         }
      }
   }

   static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer dst) {
      buf.checkIndex(index, dst.remaining());
      if (dst.remaining() != 0) {
         if (dst.isDirect()) {
            if (dst.isReadOnly()) {
               throw new ReadOnlyBufferException();
            }

            long dstAddress = PlatformDependent.directBufferAddress(dst);
            PlatformDependent.copyMemory(addr, dstAddress + dst.position(), dst.remaining());
            ((Buffer)dst).position(dst.position() + dst.remaining());
         } else if (dst.hasArray()) {
            PlatformDependent.copyMemory(addr, dst.array(), dst.arrayOffset() + dst.position(), dst.remaining());
            ((Buffer)dst).position(dst.position() + dst.remaining());
         } else {
            dst.put(buf.nioBuffer());
         }
      }
   }

   static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuf src, int srcIndex, int length) {
      buf.checkIndex(index, length);
      ObjectUtil.checkNotNull(src, "src");
      if (MathUtil.isOutOfBounds(srcIndex, length, src.capacity())) {
         throw new IndexOutOfBoundsException("srcIndex: " + srcIndex);
      } else {
         if (length != 0) {
            if (src.hasMemoryAddress()) {
               PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, addr, length);
            } else if (src.hasArray()) {
               PlatformDependent.copyMemory(src.array(), src.arrayOffset() + srcIndex, addr, length);
            } else {
               src.getBytes(srcIndex, buf, index, length);
            }
         }
      }
   }

   static void setBytes(AbstractByteBuf buf, long addr, int index, byte[] src, int srcIndex, int length) {
      buf.checkIndex(index, length);
      ObjectUtil.checkNotNull(src, "src");
      if (MathUtil.isOutOfBounds(srcIndex, length, src.length)) {
         throw new IndexOutOfBoundsException("srcIndex: " + srcIndex);
      } else {
         if (length != 0) {
            PlatformDependent.copyMemory(src, srcIndex, addr, length);
         }
      }
   }

   static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src) {
      int length = src.remaining();
      if (length != 0) {
         if (src.isDirect()) {
            buf.checkIndex(index, length);
            long srcAddress = PlatformDependent.directBufferAddress(src);
            PlatformDependent.copyMemory(srcAddress + src.position(), addr, length);
            ((Buffer)src).position(src.position() + length);
         } else if (src.hasArray()) {
            buf.checkIndex(index, length);
            PlatformDependent.copyMemory(src.array(), src.arrayOffset() + src.position(), addr, length);
            ((Buffer)src).position(src.position() + length);
         } else if (length < 8) {
            setSingleBytes(buf, addr, index, src, length);
         } else {
            assert buf.nioBufferCount() == 1;

            ByteBuffer internalBuffer = buf.internalNioBuffer(index, length);
            internalBuffer.put(src);
         }
      }
   }

   private static void setSingleBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src, int length) {
      buf.checkIndex(index, length);
      int srcPosition = src.position();
      int srcLimit = src.limit();
      long dstAddr = addr;

      for (int srcIndex = srcPosition; srcIndex < srcLimit; srcIndex++) {
         byte value = src.get(srcIndex);
         PlatformDependent.putByte(dstAddr, value);
         dstAddr++;
      }

      ((Buffer)src).position(srcLimit);
   }

   static void getBytes(AbstractByteBuf buf, long addr, int index, OutputStream out, int length) throws IOException {
      buf.checkIndex(index, length);
      if (length != 0) {
         int len = Math.min(length, 8192);
         if (len > 1024 && buf.alloc().isDirectBufferPooled()) {
            ByteBuf tmpBuf = buf.alloc().heapBuffer(len);

            try {
               byte[] tmp = tmpBuf.array();
               int offset = tmpBuf.arrayOffset();
               getBytes(addr, tmp, offset, len, out, length);
            } finally {
               tmpBuf.release();
            }
         } else {
            getBytes(addr, ByteBufUtil.threadLocalTempArray(len), 0, len, out, length);
         }
      }
   }

   private static void getBytes(long inAddr, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException {
      do {
         int len = Math.min(inLen, outLen);
         PlatformDependent.copyMemory(inAddr, in, inOffset, len);
         out.write(in, inOffset, len);
         outLen -= len;
         inAddr += len;
      } while (outLen > 0);
   }

   private static void batchSetZero(long addr, int length) {
      int longBatches = length >>> 3;

      for (int i = 0; i < longBatches; i++) {
         PlatformDependent.putLong(addr, 0L);
         addr += 8L;
      }

      int remaining = length & 7;

      for (int i = 0; i < remaining; i++) {
         PlatformDependent.putByte(addr + i, (byte)0);
      }
   }

   static void setZero(long addr, int length) {
      if (length != 0) {
         if (length <= 64) {
            if (!UNALIGNED) {
               int bytesToGetAligned = zeroTillAligned(addr, length);
               addr += bytesToGetAligned;
               length -= bytesToGetAligned;
               if (length == 0) {
                  return;
               }

               assert is8BytesAligned(addr);
            }

            batchSetZero(addr, length);
         } else {
            PlatformDependent.setMemory(addr, length, (byte)0);
         }
      }
   }

   static long next8bytesAlignedAddr(long addr) {
      return addr + 7L & -8L;
   }

   static boolean is8BytesAligned(long addr) {
      return (addr & 7L) == 0L;
   }

   private static int zeroTillAligned(long addr, int length) {
      long alignedAddr = next8bytesAlignedAddr(addr);
      int bytesToGetAligned = (int)(alignedAddr - addr);
      int toZero = Math.min(bytesToGetAligned, length);

      for (int i = 0; i < toZero; i++) {
         PlatformDependent.putByte(addr + i, (byte)0);
      }

      return toZero;
   }

   static UnpooledUnsafeDirectByteBuf newUnsafeDirectByteBuf(
      ByteBufAllocator alloc, int initialCapacity, int maxCapacity, boolean allowSectionedInternalNioBufferAccess
   ) {
      return (UnpooledUnsafeDirectByteBuf)(PlatformDependent.useDirectBufferNoCleaner()
         ? new UnpooledUnsafeNoCleanerDirectByteBuf(alloc, initialCapacity, maxCapacity, allowSectionedInternalNioBufferAccess)
         : new UnpooledUnsafeDirectByteBuf(alloc, initialCapacity, maxCapacity, allowSectionedInternalNioBufferAccess));
   }

   private UnsafeByteBufUtil() {
   }
}
