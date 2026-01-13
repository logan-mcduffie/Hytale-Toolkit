package io.netty.buffer;

import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.Recycler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SWARUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

public final class ByteBufUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
   private static final FastThreadLocal<byte[]> BYTE_ARRAYS = new FastThreadLocal<byte[]>() {
      protected byte[] initialValue() throws Exception {
         return PlatformDependent.allocateUninitializedArray(1024);
      }
   };
   private static final byte WRITE_UTF_UNKNOWN = 63;
   private static final int MAX_CHAR_BUFFER_SIZE;
   private static final int THREAD_LOCAL_BUFFER_SIZE;
   private static final int MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
   static final int WRITE_CHUNK_SIZE = 8192;
   static final ByteBufAllocator DEFAULT_ALLOCATOR;
   static final int MAX_TL_ARRAY_LEN = 1024;
   private static final ByteProcessor FIND_NON_ASCII;

   static byte[] threadLocalTempArray(int minLength) {
      return minLength <= 1024 && FastThreadLocalThread.currentThreadHasFastThreadLocal()
         ? BYTE_ARRAYS.get()
         : PlatformDependent.allocateUninitializedArray(minLength);
   }

   public static boolean isAccessible(ByteBuf buffer) {
      return buffer.isAccessible();
   }

   public static ByteBuf ensureAccessible(ByteBuf buffer) {
      if (!buffer.isAccessible()) {
         throw new IllegalReferenceCountException(buffer.refCnt());
      } else {
         return buffer;
      }
   }

   public static String hexDump(ByteBuf buffer) {
      return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
   }

   public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
      return ByteBufUtil.HexUtil.hexDump(buffer, fromIndex, length);
   }

   public static String hexDump(byte[] array) {
      return hexDump(array, 0, array.length);
   }

   public static String hexDump(byte[] array, int fromIndex, int length) {
      return ByteBufUtil.HexUtil.hexDump(array, fromIndex, length);
   }

   public static byte decodeHexByte(CharSequence s, int pos) {
      return StringUtil.decodeHexByte(s, pos);
   }

   public static byte[] decodeHexDump(CharSequence hexDump) {
      return StringUtil.decodeHexDump(hexDump, 0, hexDump.length());
   }

   public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
      return StringUtil.decodeHexDump(hexDump, fromIndex, length);
   }

   public static boolean ensureWritableSuccess(int ensureWritableResult) {
      return ensureWritableResult == 0 || ensureWritableResult == 2;
   }

   public static int hashCode(ByteBuf buffer) {
      int aLen = buffer.readableBytes();
      int intCount = aLen >>> 2;
      int byteCount = aLen & 3;
      int hashCode = 1;
      int arrayIndex = buffer.readerIndex();
      if (buffer.order() == ByteOrder.BIG_ENDIAN) {
         for (int i = intCount; i > 0; i--) {
            hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
            arrayIndex += 4;
         }
      } else {
         for (int i = intCount; i > 0; i--) {
            hashCode = 31 * hashCode + swapInt(buffer.getInt(arrayIndex));
            arrayIndex += 4;
         }
      }

      for (int i = byteCount; i > 0; i--) {
         hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
      }

      if (hashCode == 0) {
         hashCode = 1;
      }

      return hashCode;
   }

   public static int indexOf(ByteBuf needle, ByteBuf haystack) {
      if (haystack != null && needle != null) {
         if (needle.readableBytes() > haystack.readableBytes()) {
            return -1;
         } else {
            int n = haystack.readableBytes();
            int m = needle.readableBytes();
            if (m == 0) {
               return 0;
            } else if (m == 1) {
               return haystack.indexOf(haystack.readerIndex(), haystack.writerIndex(), needle.getByte(needle.readerIndex()));
            } else {
               int j = 0;
               int aStartIndex = needle.readerIndex();
               int bStartIndex = haystack.readerIndex();
               long suffixes = maxSuf(needle, m, aStartIndex, true);
               long prefixes = maxSuf(needle, m, aStartIndex, false);
               int ell = Math.max((int)(suffixes >> 32), (int)(prefixes >> 32));
               int per = Math.max((int)suffixes, (int)prefixes);
               int length = Math.min(m - per, ell + 1);
               if (equals(needle, aStartIndex, needle, aStartIndex + per, length)) {
                  int memory = -1;

                  while (j <= n - m) {
                     int i = Math.max(ell, memory) + 1;

                     while (i < m && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex)) {
                        i++;
                     }

                     if (i > n) {
                        return -1;
                     }

                     if (i >= m) {
                        i = ell;

                        while (i > memory && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex)) {
                           i--;
                        }

                        if (i <= memory) {
                           return j + bStartIndex;
                        }

                        j += per;
                        memory = m - per - 1;
                     } else {
                        j += i - ell;
                        memory = -1;
                     }
                  }
               } else {
                  per = Math.max(ell + 1, m - ell - 1) + 1;

                  while (j <= n - m) {
                     int i = ell + 1;

                     while (i < m && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex)) {
                        i++;
                     }

                     if (i > n) {
                        return -1;
                     }

                     if (i >= m) {
                        i = ell;

                        while (i >= 0 && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex)) {
                           i--;
                        }

                        if (i < 0) {
                           return j + bStartIndex;
                        }

                        j += per;
                     } else {
                        j += i - ell;
                     }
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static long maxSuf(ByteBuf x, int m, int start, boolean isSuffix) {
      int p = 1;
      int ms = -1;
      int j = start;
      int k = 1;

      while (j + k < m) {
         byte a = x.getByte(j + k);
         byte b = x.getByte(ms + k);
         boolean suffix = isSuffix ? a < b : a > b;
         if (suffix) {
            j += k;
            k = 1;
            p = j - ms;
         } else if (a == b) {
            if (k != p) {
               k++;
            } else {
               j += p;
               k = 1;
            }
         } else {
            ms = j++;
            p = 1;
            k = 1;
         }
      }

      return ((long)ms << 32) + p;
   }

   public static boolean equals(ByteBuf a, int aStartIndex, ByteBuf b, int bStartIndex, int length) {
      ObjectUtil.checkNotNull(a, "a");
      ObjectUtil.checkNotNull(b, "b");
      ObjectUtil.checkPositiveOrZero(aStartIndex, "aStartIndex");
      ObjectUtil.checkPositiveOrZero(bStartIndex, "bStartIndex");
      ObjectUtil.checkPositiveOrZero(length, "length");
      if (a.writerIndex() - length >= aStartIndex && b.writerIndex() - length >= bStartIndex) {
         int longCount = length >>> 3;
         int byteCount = length & 7;
         if (a.order() == b.order()) {
            for (int i = longCount; i > 0; i--) {
               if (a.getLong(aStartIndex) != b.getLong(bStartIndex)) {
                  return false;
               }

               aStartIndex += 8;
               bStartIndex += 8;
            }
         } else {
            for (int i = longCount; i > 0; i--) {
               if (a.getLong(aStartIndex) != swapLong(b.getLong(bStartIndex))) {
                  return false;
               }

               aStartIndex += 8;
               bStartIndex += 8;
            }
         }

         for (int i = byteCount; i > 0; i--) {
            if (a.getByte(aStartIndex) != b.getByte(bStartIndex)) {
               return false;
            }

            aStartIndex++;
            bStartIndex++;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
      if (bufferA == bufferB) {
         return true;
      } else {
         int aLen = bufferA.readableBytes();
         return aLen != bufferB.readableBytes() ? false : equals(bufferA, bufferA.readerIndex(), bufferB, bufferB.readerIndex(), aLen);
      }
   }

   public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
      if (bufferA == bufferB) {
         return 0;
      } else {
         int aLen = bufferA.readableBytes();
         int bLen = bufferB.readableBytes();
         int minLength = Math.min(aLen, bLen);
         int uintCount = minLength >>> 2;
         int byteCount = minLength & 3;
         int aIndex = bufferA.readerIndex();
         int bIndex = bufferB.readerIndex();
         if (uintCount > 0) {
            boolean bufferAIsBigEndian = bufferA.order() == ByteOrder.BIG_ENDIAN;
            int uintCountIncrement = uintCount << 2;
            long res;
            if (bufferA.order() == bufferB.order()) {
               res = bufferAIsBigEndian
                  ? compareUintBigEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement)
                  : compareUintLittleEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            } else {
               res = bufferAIsBigEndian
                  ? compareUintBigEndianA(bufferA, bufferB, aIndex, bIndex, uintCountIncrement)
                  : compareUintBigEndianB(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            }

            if (res != 0L) {
               return (int)Math.min(2147483647L, Math.max(-2147483648L, res));
            }

            aIndex += uintCountIncrement;
            bIndex += uintCountIncrement;
         }

         for (int aEnd = aIndex + byteCount; aIndex < aEnd; bIndex++) {
            int comp = bufferA.getUnsignedByte(aIndex) - bufferB.getUnsignedByte(bIndex);
            if (comp != 0) {
               return comp;
            }

            aIndex++;
         }

         return aLen - bLen;
      }
   }

   private static long compareUintBigEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
      int aEnd = aIndex + uintCountIncrement;

      while (aIndex < aEnd) {
         long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedInt(bIndex);
         if (comp != 0L) {
            return comp;
         }

         aIndex += 4;
         bIndex += 4;
      }

      return 0L;
   }

   private static long compareUintLittleEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
      int aEnd = aIndex + uintCountIncrement;

      while (aIndex < aEnd) {
         long comp = uintFromLE(bufferA.getUnsignedIntLE(aIndex)) - uintFromLE(bufferB.getUnsignedIntLE(bIndex));
         if (comp != 0L) {
            return comp;
         }

         aIndex += 4;
         bIndex += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianA(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
      int aEnd = aIndex + uintCountIncrement;

      while (aIndex < aEnd) {
         long a = bufferA.getUnsignedInt(aIndex);
         long b = uintFromLE(bufferB.getUnsignedIntLE(bIndex));
         long comp = a - b;
         if (comp != 0L) {
            return comp;
         }

         aIndex += 4;
         bIndex += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianB(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
      int aEnd = aIndex + uintCountIncrement;

      while (aIndex < aEnd) {
         long a = uintFromLE(bufferA.getUnsignedIntLE(aIndex));
         long b = bufferB.getUnsignedInt(bIndex);
         long comp = a - b;
         if (comp != 0L) {
            return comp;
         }

         aIndex += 4;
         bIndex += 4;
      }

      return 0L;
   }

   private static long uintFromLE(long value) {
      return Long.reverseBytes(value) >>> 32;
   }

   private static int unrolledFirstIndexOf(AbstractByteBuf buffer, int fromIndex, int byteCount, byte value) {
      assert byteCount > 0 && byteCount < 8;

      if (buffer._getByte(fromIndex) == value) {
         return fromIndex;
      } else if (byteCount == 1) {
         return -1;
      } else if (buffer._getByte(fromIndex + 1) == value) {
         return fromIndex + 1;
      } else if (byteCount == 2) {
         return -1;
      } else if (buffer._getByte(fromIndex + 2) == value) {
         return fromIndex + 2;
      } else if (byteCount == 3) {
         return -1;
      } else if (buffer._getByte(fromIndex + 3) == value) {
         return fromIndex + 3;
      } else if (byteCount == 4) {
         return -1;
      } else if (buffer._getByte(fromIndex + 4) == value) {
         return fromIndex + 4;
      } else if (byteCount == 5) {
         return -1;
      } else if (buffer._getByte(fromIndex + 5) == value) {
         return fromIndex + 5;
      } else if (byteCount == 6) {
         return -1;
      } else {
         return buffer._getByte(fromIndex + 6) == value ? fromIndex + 6 : -1;
      }
   }

   static int firstIndexOf(AbstractByteBuf buffer, int fromIndex, int toIndex, byte value) {
      fromIndex = Math.max(fromIndex, 0);
      if (fromIndex < toIndex && buffer.capacity() != 0) {
         int length = toIndex - fromIndex;
         buffer.checkIndex(fromIndex, length);
         if (!PlatformDependent.isUnaligned()) {
            return linearFirstIndexOf(buffer, fromIndex, toIndex, value);
         } else {
            assert PlatformDependent.isUnaligned();

            int offset = fromIndex;
            int byteCount = length & 7;
            if (byteCount > 0) {
               int index = unrolledFirstIndexOf(buffer, fromIndex, byteCount, value);
               if (index != -1) {
                  return index;
               }

               offset = fromIndex + byteCount;
               if (offset == toIndex) {
                  return -1;
               }
            }

            int longCount = length >>> 3;
            ByteOrder nativeOrder = ByteOrder.nativeOrder();
            boolean isNative = nativeOrder == buffer.order();
            boolean useLE = nativeOrder == ByteOrder.LITTLE_ENDIAN;
            long pattern = SWARUtil.compilePattern(value);

            for (int i = 0; i < longCount; i++) {
               long word = useLE ? buffer._getLongLE(offset) : buffer._getLong(offset);
               long result = SWARUtil.applyPattern(word, pattern);
               if (result != 0L) {
                  return offset + SWARUtil.getIndex(result, isNative);
               }

               offset += 8;
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int linearFirstIndexOf(AbstractByteBuf buffer, int fromIndex, int toIndex, byte value) {
      for (int i = fromIndex; i < toIndex; i++) {
         if (buffer._getByte(i) == value) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
      return buffer.indexOf(fromIndex, toIndex, value);
   }

   public static short swapShort(short value) {
      return Short.reverseBytes(value);
   }

   public static int swapMedium(int value) {
      int swapped = value << 16 & 0xFF0000 | value & 0xFF00 | value >>> 16 & 0xFF;
      if ((swapped & 8388608) != 0) {
         swapped |= -16777216;
      }

      return swapped;
   }

   public static int swapInt(int value) {
      return Integer.reverseBytes(value);
   }

   public static long swapLong(long value) {
      return Long.reverseBytes(value);
   }

   public static ByteBuf writeShortBE(ByteBuf buf, int shortValue) {
      return buf.order() == ByteOrder.BIG_ENDIAN ? buf.writeShort(shortValue) : buf.writeShort(swapShort((short)shortValue));
   }

   public static ByteBuf setShortBE(ByteBuf buf, int index, int shortValue) {
      return buf.order() == ByteOrder.BIG_ENDIAN ? buf.setShort(index, shortValue) : buf.setShort(index, swapShort((short)shortValue));
   }

   public static ByteBuf writeMediumBE(ByteBuf buf, int mediumValue) {
      return buf.order() == ByteOrder.BIG_ENDIAN ? buf.writeMedium(mediumValue) : buf.writeMedium(swapMedium(mediumValue));
   }

   public static int readUnsignedShortBE(ByteBuf buf) {
      return buf.order() == ByteOrder.BIG_ENDIAN ? buf.readUnsignedShort() : swapShort((short)buf.readUnsignedShort()) & 65535;
   }

   public static int readIntBE(ByteBuf buf) {
      return buf.order() == ByteOrder.BIG_ENDIAN ? buf.readInt() : swapInt(buf.readInt());
   }

   public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) {
      boolean release = true;
      ByteBuf dst = alloc.buffer(length);

      ByteBuf var5;
      try {
         buffer.readBytes(dst);
         release = false;
         var5 = dst;
      } finally {
         if (release) {
            dst.release();
         }
      }

      return var5;
   }

   static int lastIndexOf(AbstractByteBuf buffer, int fromIndex, int toIndex, byte value) {
      assert fromIndex > toIndex;

      int capacity = buffer.capacity();
      fromIndex = Math.min(fromIndex, capacity);
      if (fromIndex <= 0) {
         return -1;
      } else {
         int length = fromIndex - toIndex;
         buffer.checkIndex(toIndex, length);
         if (!PlatformDependent.isUnaligned()) {
            return linearLastIndexOf(buffer, fromIndex, toIndex, value);
         } else {
            int longCount = length >>> 3;
            if (longCount > 0) {
               ByteOrder nativeOrder = ByteOrder.nativeOrder();
               boolean isNative = nativeOrder == buffer.order();
               boolean useLE = nativeOrder == ByteOrder.LITTLE_ENDIAN;
               long pattern = SWARUtil.compilePattern(value);
               int i = 0;

               for (int offset = fromIndex - 8; i < longCount; offset -= 8) {
                  long word = useLE ? buffer._getLongLE(offset) : buffer._getLong(offset);
                  long result = SWARUtil.applyPattern(word, pattern);
                  if (result != 0L) {
                     return offset + 8 - 1 - SWARUtil.getIndex(result, !isNative);
                  }

                  i++;
               }
            }

            return unrolledLastIndexOf(buffer, fromIndex - (longCount << 3), length & 7, value);
         }
      }
   }

   private static int linearLastIndexOf(AbstractByteBuf buffer, int fromIndex, int toIndex, byte value) {
      for (int i = fromIndex - 1; i >= toIndex; i--) {
         if (buffer._getByte(i) == value) {
            return i;
         }
      }

      return -1;
   }

   private static int unrolledLastIndexOf(AbstractByteBuf buffer, int fromIndex, int byteCount, byte value) {
      assert byteCount >= 0 && byteCount < 8;

      if (byteCount == 0) {
         return -1;
      } else if (buffer._getByte(fromIndex - 1) == value) {
         return fromIndex - 1;
      } else if (byteCount == 1) {
         return -1;
      } else if (buffer._getByte(fromIndex - 2) == value) {
         return fromIndex - 2;
      } else if (byteCount == 2) {
         return -1;
      } else if (buffer._getByte(fromIndex - 3) == value) {
         return fromIndex - 3;
      } else if (byteCount == 3) {
         return -1;
      } else if (buffer._getByte(fromIndex - 4) == value) {
         return fromIndex - 4;
      } else if (byteCount == 4) {
         return -1;
      } else if (buffer._getByte(fromIndex - 5) == value) {
         return fromIndex - 5;
      } else if (byteCount == 5) {
         return -1;
      } else if (buffer._getByte(fromIndex - 6) == value) {
         return fromIndex - 6;
      } else if (byteCount == 6) {
         return -1;
      } else {
         return buffer._getByte(fromIndex - 7) == value ? fromIndex - 7 : -1;
      }
   }

   private static CharSequence checkCharSequenceBounds(CharSequence seq, int start, int end) {
      if (MathUtil.isOutOfBounds(start, end - start, seq.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= seq.length(" + seq.length() + ')');
      } else {
         return seq;
      }
   }

   public static ByteBuf writeUtf8(ByteBufAllocator alloc, CharSequence seq) {
      ByteBuf buf = alloc.buffer(utf8MaxBytes(seq));
      writeUtf8(buf, seq);
      return buf;
   }

   public static int writeUtf8(ByteBuf buf, CharSequence seq) {
      int seqLength = seq.length();
      return reserveAndWriteUtf8Seq(buf, seq, 0, seqLength, utf8MaxBytes(seqLength));
   }

   public static int writeUtf8(ByteBuf buf, CharSequence seq, int start, int end) {
      checkCharSequenceBounds(seq, start, end);
      return reserveAndWriteUtf8Seq(buf, seq, start, end, utf8MaxBytes(end - start));
   }

   public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int reserveBytes) {
      return reserveAndWriteUtf8Seq(buf, seq, 0, seq.length(), reserveBytes);
   }

   public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) {
      return reserveAndWriteUtf8Seq(buf, checkCharSequenceBounds(seq, start, end), start, end, reserveBytes);
   }

   private static int reserveAndWriteUtf8Seq(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) {
      while (true) {
         if (buf instanceof WrappedCompositeByteBuf) {
            buf = buf.unwrap();
         } else {
            if (buf instanceof AbstractByteBuf) {
               AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
               byteBuf.ensureWritable0(reserveBytes);
               int written = writeUtf8(byteBuf, byteBuf.writerIndex, reserveBytes, seq, start, end);
               byteBuf.writerIndex += written;
               return written;
            }

            if (!(buf instanceof WrappedByteBuf)) {
               byte[] bytes = seq.subSequence(start, end).toString().getBytes(CharsetUtil.UTF_8);
               buf.writeBytes(bytes);
               return bytes.length;
            }

            buf = buf.unwrap();
         }
      }
   }

   static int writeUtf8(AbstractByteBuf buffer, int writerIndex, int reservedBytes, CharSequence seq, int len) {
      return writeUtf8(buffer, writerIndex, reservedBytes, seq, 0, len);
   }

   static int writeUtf8(AbstractByteBuf buffer, int writerIndex, int reservedBytes, CharSequence seq, int start, int end) {
      if (seq instanceof AsciiString) {
         writeAsciiString(buffer, writerIndex, (AsciiString)seq, start, end);
         return end - start;
      } else {
         if (PlatformDependent.hasUnsafe()) {
            if (buffer.hasArray()) {
               return unsafeWriteUtf8(buffer.array(), PlatformDependent.byteArrayBaseOffset(), buffer.arrayOffset() + writerIndex, seq, start, end);
            }

            if (buffer.hasMemoryAddress()) {
               return unsafeWriteUtf8(null, buffer.memoryAddress(), writerIndex, seq, start, end);
            }
         } else {
            if (buffer.hasArray()) {
               return safeArrayWriteUtf8(buffer.array(), buffer.arrayOffset() + writerIndex, seq, start, end);
            }

            if (buffer.isDirect()) {
               assert buffer.nioBufferCount() == 1;

               ByteBuffer internalDirectBuffer = buffer.internalNioBuffer(writerIndex, reservedBytes);
               int bufferPosition = internalDirectBuffer.position();
               return safeDirectWriteUtf8(internalDirectBuffer, bufferPosition, seq, start, end);
            }
         }

         return safeWriteUtf8(buffer, writerIndex, seq, start, end);
      }
   }

   static void writeAsciiString(AbstractByteBuf buffer, int writerIndex, AsciiString seq, int start, int end) {
      int begin = seq.arrayOffset() + start;
      int length = end - start;
      if (PlatformDependent.hasUnsafe()) {
         if (buffer.hasArray()) {
            PlatformDependent.copyMemory(seq.array(), begin, buffer.array(), buffer.arrayOffset() + writerIndex, length);
            return;
         }

         if (buffer.hasMemoryAddress()) {
            PlatformDependent.copyMemory(seq.array(), begin, buffer.memoryAddress() + writerIndex, length);
            return;
         }
      }

      if (buffer.hasArray()) {
         System.arraycopy(seq.array(), begin, buffer.array(), buffer.arrayOffset() + writerIndex, length);
      } else {
         buffer.setBytes(writerIndex, seq.array(), begin, length);
      }
   }

   private static int safeDirectWriteUtf8(ByteBuffer buffer, int writerIndex, CharSequence seq, int start, int end) {
      assert !(seq instanceof AsciiString);

      int oldWriterIndex = writerIndex;

      for (int i = start; i < end; i++) {
         char c = seq.charAt(i);
         if (c < 128) {
            buffer.put(writerIndex++, (byte)c);
         } else if (c < 2048) {
            buffer.put(writerIndex++, (byte)(192 | c >> 6));
            buffer.put(writerIndex++, (byte)(128 | c & '?'));
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               buffer.put(writerIndex++, (byte)63);
            } else {
               if (++i == end) {
                  buffer.put(writerIndex++, (byte)63);
                  break;
               }

               char c2 = seq.charAt(i);
               if (!Character.isLowSurrogate(c2)) {
                  buffer.put(writerIndex++, (byte)63);
                  buffer.put(writerIndex++, Character.isHighSurrogate(c2) ? 63 : (byte)c2);
               } else {
                  int codePoint = Character.toCodePoint(c, c2);
                  buffer.put(writerIndex++, (byte)(240 | codePoint >> 18));
                  buffer.put(writerIndex++, (byte)(128 | codePoint >> 12 & 63));
                  buffer.put(writerIndex++, (byte)(128 | codePoint >> 6 & 63));
                  buffer.put(writerIndex++, (byte)(128 | codePoint & 63));
               }
            }
         } else {
            buffer.put(writerIndex++, (byte)(224 | c >> '\f'));
            buffer.put(writerIndex++, (byte)(128 | c >> 6 & 63));
            buffer.put(writerIndex++, (byte)(128 | c & '?'));
         }
      }

      return writerIndex - oldWriterIndex;
   }

   private static int safeWriteUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int start, int end) {
      assert !(seq instanceof AsciiString);

      int oldWriterIndex = writerIndex;

      for (int i = start; i < end; i++) {
         char c = seq.charAt(i);
         if (c < 128) {
            buffer._setByte(writerIndex++, (byte)c);
         } else if (c < 2048) {
            buffer._setByte(writerIndex++, (byte)(192 | c >> 6));
            buffer._setByte(writerIndex++, (byte)(128 | c & '?'));
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               buffer._setByte(writerIndex++, 63);
            } else {
               if (++i == end) {
                  buffer._setByte(writerIndex++, 63);
                  break;
               }

               char c2 = seq.charAt(i);
               if (!Character.isLowSurrogate(c2)) {
                  buffer._setByte(writerIndex++, 63);
                  buffer._setByte(writerIndex++, Character.isHighSurrogate(c2) ? 63 : c2);
               } else {
                  int codePoint = Character.toCodePoint(c, c2);
                  buffer._setByte(writerIndex++, (byte)(240 | codePoint >> 18));
                  buffer._setByte(writerIndex++, (byte)(128 | codePoint >> 12 & 63));
                  buffer._setByte(writerIndex++, (byte)(128 | codePoint >> 6 & 63));
                  buffer._setByte(writerIndex++, (byte)(128 | codePoint & 63));
               }
            }
         } else {
            buffer._setByte(writerIndex++, (byte)(224 | c >> '\f'));
            buffer._setByte(writerIndex++, (byte)(128 | c >> 6 & 63));
            buffer._setByte(writerIndex++, (byte)(128 | c & '?'));
         }
      }

      return writerIndex - oldWriterIndex;
   }

   private static int safeArrayWriteUtf8(byte[] buffer, int writerIndex, CharSequence seq, int start, int end) {
      int oldWriterIndex = writerIndex;

      for (int i = start; i < end; i++) {
         char c = seq.charAt(i);
         if (c < 128) {
            buffer[writerIndex++] = (byte)c;
         } else if (c < 2048) {
            buffer[writerIndex++] = (byte)(192 | c >> 6);
            buffer[writerIndex++] = (byte)(128 | c & '?');
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               buffer[writerIndex++] = 63;
            } else {
               if (++i == end) {
                  buffer[writerIndex++] = 63;
                  break;
               }

               char c2 = seq.charAt(i);
               if (!Character.isLowSurrogate(c2)) {
                  buffer[writerIndex++] = 63;
                  buffer[writerIndex++] = (byte)(Character.isHighSurrogate(c2) ? 63 : c2);
               } else {
                  int codePoint = Character.toCodePoint(c, c2);
                  buffer[writerIndex++] = (byte)(240 | codePoint >> 18);
                  buffer[writerIndex++] = (byte)(128 | codePoint >> 12 & 63);
                  buffer[writerIndex++] = (byte)(128 | codePoint >> 6 & 63);
                  buffer[writerIndex++] = (byte)(128 | codePoint & 63);
               }
            }
         } else {
            buffer[writerIndex++] = (byte)(224 | c >> '\f');
            buffer[writerIndex++] = (byte)(128 | c >> 6 & 63);
            buffer[writerIndex++] = (byte)(128 | c & '?');
         }
      }

      return writerIndex - oldWriterIndex;
   }

   private static int unsafeWriteUtf8(byte[] buffer, long memoryOffset, int writerIndex, CharSequence seq, int start, int end) {
      assert !(seq instanceof AsciiString);

      long writerOffset = memoryOffset + writerIndex;
      long oldWriterOffset = writerOffset;

      for (int i = start; i < end; i++) {
         char c = seq.charAt(i);
         if (c < 128) {
            PlatformDependent.putByte(buffer, writerOffset++, (byte)c);
         } else if (c < 2048) {
            PlatformDependent.putByte(buffer, writerOffset++, (byte)(192 | c >> 6));
            PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | c & '?'));
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
            } else {
               if (++i == end) {
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
                  break;
               }

               char c2 = seq.charAt(i);
               if (!Character.isLowSurrogate(c2)) {
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)(Character.isHighSurrogate(c2) ? 63 : c2));
               } else {
                  int codePoint = Character.toCodePoint(c, c2);
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)(240 | codePoint >> 18));
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | codePoint >> 12 & 63));
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | codePoint >> 6 & 63));
                  PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | codePoint & 63));
               }
            }
         } else {
            PlatformDependent.putByte(buffer, writerOffset++, (byte)(224 | c >> '\f'));
            PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | c >> 6 & 63));
            PlatformDependent.putByte(buffer, writerOffset++, (byte)(128 | c & '?'));
         }
      }

      return (int)(writerOffset - oldWriterOffset);
   }

   public static int utf8MaxBytes(int seqLength) {
      return seqLength * MAX_BYTES_PER_CHAR_UTF8;
   }

   public static int utf8MaxBytes(CharSequence seq) {
      return seq instanceof AsciiString ? seq.length() : utf8MaxBytes(seq.length());
   }

   public static int utf8Bytes(CharSequence seq) {
      return utf8ByteCount(seq, 0, seq.length());
   }

   public static int utf8Bytes(CharSequence seq, int start, int end) {
      return utf8ByteCount(checkCharSequenceBounds(seq, start, end), start, end);
   }

   private static int utf8ByteCount(CharSequence seq, int start, int end) {
      if (seq instanceof AsciiString) {
         return end - start;
      } else {
         int i = start;

         while (i < end && seq.charAt(i) < 128) {
            i++;
         }

         return i < end ? i - start + utf8BytesNonAscii(seq, i, end) : i - start;
      }
   }

   private static int utf8BytesNonAscii(CharSequence seq, int start, int end) {
      int encodedLength = 0;

      for (int i = start; i < end; i++) {
         char c = seq.charAt(i);
         if (c < 2048) {
            encodedLength += (127 - c >>> 31) + 1;
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               encodedLength++;
            } else {
               if (++i == end) {
                  encodedLength++;
                  break;
               }

               if (!Character.isLowSurrogate(seq.charAt(i))) {
                  encodedLength += 2;
               } else {
                  encodedLength += 4;
               }
            }
         } else {
            encodedLength += 3;
         }
      }

      return encodedLength;
   }

   public static ByteBuf writeAscii(ByteBufAllocator alloc, CharSequence seq) {
      ByteBuf buf = alloc.buffer(seq.length());
      writeAscii(buf, seq);
      return buf;
   }

   public static int writeAscii(ByteBuf buf, CharSequence seq) {
      while (true) {
         if (buf instanceof WrappedCompositeByteBuf) {
            buf = buf.unwrap();
         } else {
            if (buf instanceof AbstractByteBuf) {
               int len = seq.length();
               AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
               byteBuf.ensureWritable0(len);
               if (seq instanceof AsciiString) {
                  writeAsciiString(byteBuf, byteBuf.writerIndex, (AsciiString)seq, 0, len);
               } else {
                  int written = writeAscii(byteBuf, byteBuf.writerIndex, seq, len);

                  assert written == len;
               }

               byteBuf.writerIndex += len;
               return len;
            }

            if (!(buf instanceof WrappedByteBuf)) {
               byte[] bytes = seq.toString().getBytes(CharsetUtil.US_ASCII);
               buf.writeBytes(bytes);
               return bytes.length;
            }

            buf = buf.unwrap();
         }
      }
   }

   static int writeAscii(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
      if (seq instanceof AsciiString) {
         writeAsciiString(buffer, writerIndex, (AsciiString)seq, 0, len);
      } else {
         writeAsciiCharSequence(buffer, writerIndex, seq, len);
      }

      return len;
   }

   private static int writeAsciiCharSequence(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
      for (int i = 0; i < len; i++) {
         buffer._setByte(writerIndex++, AsciiString.c2b(seq.charAt(i)));
      }

      return len;
   }

   public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
      return encodeString0(alloc, false, src, charset, 0);
   }

   public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset, int extraCapacity) {
      return encodeString0(alloc, false, src, charset, extraCapacity);
   }

   static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset, int extraCapacity) {
      CharsetEncoder encoder = CharsetUtil.encoder(charset);
      int length = (int)((double)src.remaining() * encoder.maxBytesPerChar()) + extraCapacity;
      boolean release = true;
      ByteBuf dst;
      if (enforceHeap) {
         dst = alloc.heapBuffer(length);
      } else {
         dst = alloc.buffer(length);
      }

      ByteBuf var12;
      try {
         ByteBuffer dstBuf = dst.internalNioBuffer(dst.readerIndex(), length);
         int pos = dstBuf.position();
         CoderResult cr = encoder.encode(src, dstBuf, true);
         if (!cr.isUnderflow()) {
            cr.throwException();
         }

         cr = encoder.flush(dstBuf);
         if (!cr.isUnderflow()) {
            cr.throwException();
         }

         dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
         release = false;
         var12 = dst;
      } catch (CharacterCodingException var16) {
         throw new IllegalStateException(var16);
      } finally {
         if (release) {
            dst.release();
         }
      }

      return var12;
   }

   static String decodeString(ByteBuf src, int readerIndex, int len, Charset charset) {
      if (len == 0) {
         return "";
      } else {
         byte[] array;
         int offset;
         if (src.hasArray()) {
            array = src.array();
            offset = src.arrayOffset() + readerIndex;
         } else {
            array = threadLocalTempArray(len);
            offset = 0;
            src.getBytes(readerIndex, array, 0, len);
         }

         return CharsetUtil.US_ASCII.equals(charset) ? new String(array, 0, offset, len) : new String(array, offset, len, charset);
      }
   }

   public static ByteBuf threadLocalDirectBuffer() {
      if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
         return null;
      } else {
         return (ByteBuf)(PlatformDependent.hasUnsafe()
            ? ByteBufUtil.ThreadLocalUnsafeDirectByteBuf.newInstance()
            : ByteBufUtil.ThreadLocalDirectByteBuf.newInstance());
      }
   }

   public static byte[] getBytes(ByteBuf buf) {
      return getBytes(buf, buf.readerIndex(), buf.readableBytes());
   }

   public static byte[] getBytes(ByteBuf buf, int start, int length) {
      return getBytes(buf, start, length, true);
   }

   public static byte[] getBytes(ByteBuf buf, int start, int length, boolean copy) {
      int capacity = buf.capacity();
      if (MathUtil.isOutOfBounds(start, length, capacity)) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + capacity + ')');
      } else if (buf.hasArray()) {
         int baseOffset = buf.arrayOffset() + start;
         byte[] bytes = buf.array();
         return !copy && baseOffset == 0 && length == bytes.length ? bytes : Arrays.copyOfRange(bytes, baseOffset, baseOffset + length);
      } else {
         byte[] bytes = PlatformDependent.allocateUninitializedArray(length);
         buf.getBytes(start, bytes);
         return bytes;
      }
   }

   public static void copy(AsciiString src, ByteBuf dst) {
      copy(src, 0, dst, src.length());
   }

   public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int dstIdx, int length) {
      if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
      } else {
         ObjectUtil.checkNotNull(dst, "dst").setBytes(dstIdx, src.array(), srcIdx + src.arrayOffset(), length);
      }
   }

   public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int length) {
      if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
      } else {
         ObjectUtil.checkNotNull(dst, "dst").writeBytes(src.array(), srcIdx + src.arrayOffset(), length);
      }
   }

   public static String prettyHexDump(ByteBuf buffer) {
      return prettyHexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
   }

   public static String prettyHexDump(ByteBuf buffer, int offset, int length) {
      return ByteBufUtil.HexUtil.prettyHexDump(buffer, offset, length);
   }

   public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf) {
      appendPrettyHexDump(dump, buf, buf.readerIndex(), buf.readableBytes());
   }

   public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) {
      ByteBufUtil.HexUtil.appendPrettyHexDump(dump, buf, offset, length);
   }

   public static boolean isText(ByteBuf buf, Charset charset) {
      return isText(buf, buf.readerIndex(), buf.readableBytes(), charset);
   }

   public static boolean isText(ByteBuf buf, int index, int length, Charset charset) {
      ObjectUtil.checkNotNull(buf, "buf");
      ObjectUtil.checkNotNull(charset, "charset");
      int maxIndex = buf.readerIndex() + buf.readableBytes();
      if (index < 0 || length < 0 || index > maxIndex - length) {
         throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
      } else if (charset.equals(CharsetUtil.UTF_8)) {
         return isUtf8(buf, index, length);
      } else if (charset.equals(CharsetUtil.US_ASCII)) {
         return isAscii(buf, index, length);
      } else {
         CharsetDecoder decoder = CharsetUtil.decoder(charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT);

         try {
            if (buf.nioBufferCount() == 1) {
               decoder.decode(buf.nioBuffer(index, length));
            } else {
               ByteBuf heapBuffer = buf.alloc().heapBuffer(length);

               try {
                  heapBuffer.writeBytes(buf, index, length);
                  decoder.decode(heapBuffer.internalNioBuffer(heapBuffer.readerIndex(), length));
               } finally {
                  heapBuffer.release();
               }
            }

            return true;
         } catch (CharacterCodingException var11) {
            return false;
         }
      }
   }

   private static boolean isAscii(ByteBuf buf, int index, int length) {
      return buf.forEachByte(index, length, FIND_NON_ASCII) == -1;
   }

   private static boolean isUtf8(ByteBuf buf, int index, int length) {
      int endIndex = index + length;

      while (index < endIndex) {
         byte b1 = buf.getByte(index++);
         if ((b1 & 128) != 0) {
            if ((b1 & 224) != 192) {
               if ((b1 & 240) == 224) {
                  if (index > endIndex - 2) {
                     return false;
                  }

                  byte b2 = buf.getByte(index++);
                  byte b3 = buf.getByte(index++);
                  if ((b2 & 192) != 128 || (b3 & 192) != 128) {
                     return false;
                  }

                  if ((b1 & 15) == 0 && (b2 & 255) < 160) {
                     return false;
                  }

                  if ((b1 & 15) == 13 && (b2 & 255) > 159) {
                     return false;
                  }
               } else {
                  if ((b1 & 248) != 240) {
                     return false;
                  }

                  if (index > endIndex - 3) {
                     return false;
                  }

                  byte b2x = buf.getByte(index++);
                  byte b3x = buf.getByte(index++);
                  byte b4 = buf.getByte(index++);
                  if ((b2x & 192) == 128 && (b3x & 192) == 128 && (b4 & 192) == 128) {
                     if ((b1 & 255) <= 244 && ((b1 & 255) != 240 || (b2x & 255) >= 144) && ((b1 & 255) != 244 || (b2x & 255) <= 143)) {
                        continue;
                     }

                     return false;
                  }

                  return false;
               }
            } else {
               if (index >= endIndex) {
                  return false;
               }

               byte b2x = buf.getByte(index++);
               if ((b2x & 192) != 128) {
                  return false;
               }

               if ((b1 & 255) < 194) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   static void readBytes(ByteBufAllocator allocator, ByteBuffer buffer, int position, int length, OutputStream out) throws IOException {
      if (buffer.hasArray()) {
         out.write(buffer.array(), position + buffer.arrayOffset(), length);
      } else {
         int chunkLen = Math.min(length, 8192);
         ((Buffer)buffer).clear().position(position).limit(position + length);
         if (length > 1024 && allocator.isDirectBufferPooled()) {
            ByteBuf tmpBuf = allocator.heapBuffer(chunkLen);

            try {
               byte[] tmp = tmpBuf.array();
               int offset = tmpBuf.arrayOffset();
               getBytes(buffer, tmp, offset, chunkLen, out, length);
            } finally {
               tmpBuf.release();
            }
         } else {
            getBytes(buffer, threadLocalTempArray(chunkLen), 0, chunkLen, out, length);
         }
      }
   }

   private static void getBytes(ByteBuffer inBuffer, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException {
      do {
         int len = Math.min(inLen, outLen);
         inBuffer.get(in, inOffset, len);
         out.write(in, inOffset, len);
         outLen -= len;
      } while (outLen > 0);
   }

   public static void setLeakListener(ResourceLeakDetector.LeakListener leakListener) {
      AbstractByteBuf.leakDetector.setLeakListener(leakListener);
   }

   private ByteBufUtil() {
   }

   static {
      String allocType = SystemPropertyUtil.get("io.netty.allocator.type", "adaptive");
      ByteBufAllocator alloc;
      if ("unpooled".equals(allocType)) {
         alloc = UnpooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", allocType);
      } else if ("pooled".equals(allocType)) {
         alloc = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", allocType);
      } else if ("adaptive".equals(allocType)) {
         alloc = new AdaptiveByteBufAllocator();
         logger.debug("-Dio.netty.allocator.type: {}", allocType);
      } else {
         alloc = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
      }

      DEFAULT_ALLOCATOR = alloc;
      THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 0);
      logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", THREAD_LOCAL_BUFFER_SIZE);
      MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
      logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", MAX_CHAR_BUFFER_SIZE);
      FIND_NON_ASCII = new ByteProcessor() {
         @Override
         public boolean process(byte value) {
            return value >= 0;
         }
      };
   }

   private static final class HexUtil {
      private static final char[] BYTE2CHAR = new char[256];
      private static final char[] HEXDUMP_TABLE = new char[1024];
      private static final String[] HEXPADDING = new String[16];
      private static final String[] HEXDUMP_ROWPREFIXES = new String[4096];
      private static final String[] BYTE2HEX = new String[256];
      private static final String[] BYTEPADDING = new String[16];

      private static String hexDump(ByteBuf buffer, int fromIndex, int length) {
         ObjectUtil.checkPositiveOrZero(length, "length");
         if (length == 0) {
            return "";
         } else {
            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];
            int srcIdx = fromIndex;

            for (int dstIdx = 0; srcIdx < endIndex; dstIdx += 2) {
               System.arraycopy(HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);
               srcIdx++;
            }

            return new String(buf);
         }
      }

      private static String hexDump(byte[] array, int fromIndex, int length) {
         ObjectUtil.checkPositiveOrZero(length, "length");
         if (length == 0) {
            return "";
         } else {
            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];
            int srcIdx = fromIndex;

            for (int dstIdx = 0; srcIdx < endIndex; dstIdx += 2) {
               System.arraycopy(HEXDUMP_TABLE, (array[srcIdx] & 255) << 1, buf, dstIdx, 2);
               srcIdx++;
            }

            return new String(buf);
         }
      }

      private static String prettyHexDump(ByteBuf buffer, int offset, int length) {
         if (length == 0) {
            return "";
         } else {
            int rows = length / 16 + ((length & 15) == 0 ? 0 : 1) + 4;
            StringBuilder buf = new StringBuilder(rows * 80);
            appendPrettyHexDump(buf, buffer, offset, length);
            return buf.toString();
         }
      }

      private static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) {
         if (MathUtil.isOutOfBounds(offset, length, buf.capacity())) {
            throw new IndexOutOfBoundsException(
               "expected: 0 <= offset(" + offset + ") <= offset + length(" + length + ") <= buf.capacity(" + buf.capacity() + ')'
            );
         } else if (length != 0) {
            dump.append(
               "         +-------------------------------------------------+"
                  + StringUtil.NEWLINE
                  + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |"
                  + StringUtil.NEWLINE
                  + "+--------+-------------------------------------------------+----------------+"
            );
            int fullRows = length >>> 4;
            int remainder = length & 15;

            for (int row = 0; row < fullRows; row++) {
               int rowStartIndex = (row << 4) + offset;
               appendHexDumpRowPrefix(dump, row, rowStartIndex);
               int rowEndIndex = rowStartIndex + 16;

               for (int j = rowStartIndex; j < rowEndIndex; j++) {
                  dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
               }

               dump.append(" |");

               for (int j = rowStartIndex; j < rowEndIndex; j++) {
                  dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
               }

               dump.append('|');
            }

            if (remainder != 0) {
               int rowStartIndex = (fullRows << 4) + offset;
               appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);
               int rowEndIndex = rowStartIndex + remainder;

               for (int j = rowStartIndex; j < rowEndIndex; j++) {
                  dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
               }

               dump.append(HEXPADDING[remainder]);
               dump.append(" |");

               for (int j = rowStartIndex; j < rowEndIndex; j++) {
                  dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
               }

               dump.append(BYTEPADDING[remainder]);
               dump.append('|');
            }

            dump.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
         }
      }

      private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
         if (row < HEXDUMP_ROWPREFIXES.length) {
            dump.append(HEXDUMP_ROWPREFIXES[row]);
         } else {
            dump.append(StringUtil.NEWLINE);
            dump.append(Long.toHexString(rowStartIndex & 4294967295L | 4294967296L));
            dump.setCharAt(dump.length() - 9, '|');
            dump.append('|');
         }
      }

      static {
         char[] DIGITS = "0123456789abcdef".toCharArray();

         for (int i = 0; i < 256; i++) {
            HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 15];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 15];
         }

         for (int i = 0; i < HEXPADDING.length; i++) {
            int padding = HEXPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);

            for (int j = 0; j < padding; j++) {
               buf.append("   ");
            }

            HEXPADDING[i] = buf.toString();
         }

         for (int var6 = 0; var6 < HEXDUMP_ROWPREFIXES.length; var6++) {
            StringBuilder buf = new StringBuilder(12);
            buf.append(StringUtil.NEWLINE);
            buf.append(Long.toHexString(var6 << 4 & 4294967295L | 4294967296L));
            buf.setCharAt(buf.length() - 9, '|');
            buf.append('|');
            HEXDUMP_ROWPREFIXES[var6] = buf.toString();
         }

         for (int var7 = 0; var7 < BYTE2HEX.length; var7++) {
            BYTE2HEX[var7] = ' ' + StringUtil.byteToHexStringPadded(var7);
         }

         for (int var8 = 0; var8 < BYTEPADDING.length; var8++) {
            int padding = BYTEPADDING.length - var8;
            StringBuilder buf = new StringBuilder(padding);

            for (int j = 0; j < padding; j++) {
               buf.append(' ');
            }

            BYTEPADDING[var8] = buf.toString();
         }

         for (int var9 = 0; var9 < BYTE2CHAR.length; var9++) {
            if (var9 > 31 && var9 < 127) {
               BYTE2CHAR[var9] = (char)var9;
            } else {
               BYTE2CHAR[var9] = '.';
            }
         }
      }
   }

   static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf {
      private static final Recycler<ByteBufUtil.ThreadLocalDirectByteBuf> RECYCLER = new Recycler<ByteBufUtil.ThreadLocalDirectByteBuf>() {
         protected ByteBufUtil.ThreadLocalDirectByteBuf newObject(Recycler.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> handle) {
            return new ByteBufUtil.ThreadLocalDirectByteBuf(handle);
         }
      };
      private final Recycler.EnhancedHandle<ByteBufUtil.ThreadLocalDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalDirectByteBuf buf = RECYCLER.get();
         buf.resetRefCnt();
         return buf;
      }

      private ThreadLocalDirectByteBuf(ObjectPool.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> handle) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = (Recycler.EnhancedHandle<ByteBufUtil.ThreadLocalDirectByteBuf>)handle;
      }

      @Override
      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.unguardedRecycle(this);
         }
      }
   }

   static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      private static final Recycler<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> RECYCLER = new Recycler<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf>() {
         protected ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newObject(Recycler.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle) {
            return new ByteBufUtil.ThreadLocalUnsafeDirectByteBuf(handle);
         }
      };
      private final Recycler.EnhancedHandle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalUnsafeDirectByteBuf buf = RECYCLER.get();
         buf.resetRefCnt();
         return buf;
      }

      private ThreadLocalUnsafeDirectByteBuf(ObjectPool.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = (Recycler.EnhancedHandle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf>)handle;
      }

      @Override
      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.unguardedRecycle(this);
         }
      }
   }
}
