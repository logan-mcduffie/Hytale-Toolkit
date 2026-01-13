package io.netty.buffer;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class Unpooled {
   private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
   public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
   public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
   public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);

   public static ByteBuf buffer() {
      return ALLOC.heapBuffer();
   }

   public static ByteBuf directBuffer() {
      return ALLOC.directBuffer();
   }

   public static ByteBuf buffer(int initialCapacity) {
      return ALLOC.heapBuffer(initialCapacity);
   }

   public static ByteBuf directBuffer(int initialCapacity) {
      return ALLOC.directBuffer(initialCapacity);
   }

   public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
      return ALLOC.heapBuffer(initialCapacity, maxCapacity);
   }

   public static ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
      return ALLOC.directBuffer(initialCapacity, maxCapacity);
   }

   public static ByteBuf wrappedBuffer(byte[] array) {
      return (ByteBuf)(array.length == 0 ? EMPTY_BUFFER : new UnpooledHeapByteBuf(ALLOC, array, array.length));
   }

   public static ByteBuf wrappedBuffer(byte[] array, int offset, int length) {
      if (length == 0) {
         return EMPTY_BUFFER;
      } else {
         return offset == 0 && length == array.length ? wrappedBuffer(array) : wrappedBuffer(array).slice(offset, length);
      }
   }

   public static ByteBuf wrappedBuffer(ByteBuffer buffer) {
      if (!buffer.hasRemaining()) {
         return EMPTY_BUFFER;
      } else if (!buffer.isDirect() && buffer.hasArray()) {
         return wrappedBuffer(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining()).order(buffer.order());
      } else if (PlatformDependent.hasUnsafe()) {
         if (buffer.isReadOnly()) {
            return (ByteBuf)(buffer.isDirect() ? new ReadOnlyUnsafeDirectByteBuf(ALLOC, buffer) : new ReadOnlyByteBufferBuf(ALLOC, buffer));
         } else {
            return new UnpooledUnsafeDirectByteBuf(ALLOC, buffer, buffer.remaining());
         }
      } else {
         return (ByteBuf)(buffer.isReadOnly() ? new ReadOnlyByteBufferBuf(ALLOC, buffer) : new UnpooledDirectByteBuf(ALLOC, buffer, buffer.remaining()));
      }
   }

   public static ByteBuf wrappedBuffer(long memoryAddress, int size, boolean doFree) {
      return new WrappedUnpooledUnsafeDirectByteBuf(ALLOC, memoryAddress, size, doFree);
   }

   public static ByteBuf wrappedBuffer(ByteBuf buffer) {
      if (buffer.isReadable()) {
         return buffer.slice();
      } else {
         buffer.release();
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf wrappedBuffer(byte[]... arrays) {
      return wrappedBuffer(arrays.length, arrays);
   }

   public static ByteBuf wrappedBuffer(ByteBuf... buffers) {
      return wrappedBuffer(buffers.length, buffers);
   }

   public static ByteBuf wrappedBuffer(ByteBuffer... buffers) {
      return wrappedBuffer(buffers.length, buffers);
   }

   static <T> ByteBuf wrappedBuffer(int maxNumComponents, CompositeByteBuf.ByteWrapper<T> wrapper, T[] array) {
      switch (array.length) {
         case 0:
            break;
         case 1:
            if (!wrapper.isEmpty(array[0])) {
               return wrapper.wrap(array[0]);
            }
            break;
         default:
            int i = 0;

            for (int len = array.length; i < len; i++) {
               T bytes = array[i];
               if (bytes == null) {
                  return EMPTY_BUFFER;
               }

               if (!wrapper.isEmpty(bytes)) {
                  return new CompositeByteBuf(ALLOC, false, maxNumComponents, wrapper, array, i);
               }
            }
      }

      return EMPTY_BUFFER;
   }

   public static ByteBuf wrappedBuffer(int maxNumComponents, byte[]... arrays) {
      return wrappedBuffer(maxNumComponents, CompositeByteBuf.BYTE_ARRAY_WRAPPER, arrays);
   }

   public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuf... buffers) {
      switch (buffers.length) {
         case 0:
            break;
         case 1:
            ByteBuf buffer = buffers[0];
            if (buffer.isReadable()) {
               return wrappedBuffer(buffer.order(BIG_ENDIAN));
            }

            buffer.release();
            break;
         default:
            for (int i = 0; i < buffers.length; i++) {
               ByteBuf buf = buffers[i];
               if (buf.isReadable()) {
                  return new CompositeByteBuf(ALLOC, false, maxNumComponents, buffers, i);
               }

               buf.release();
            }
      }

      return EMPTY_BUFFER;
   }

   public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuffer... buffers) {
      return wrappedBuffer(maxNumComponents, CompositeByteBuf.BYTE_BUFFER_WRAPPER, buffers);
   }

   public static CompositeByteBuf compositeBuffer() {
      return compositeBuffer(16);
   }

   public static CompositeByteBuf compositeBuffer(int maxNumComponents) {
      return new CompositeByteBuf(ALLOC, false, maxNumComponents);
   }

   public static ByteBuf copiedBuffer(byte[] array) {
      return array.length == 0 ? EMPTY_BUFFER : wrappedBuffer((byte[])array.clone());
   }

   public static ByteBuf copiedBuffer(byte[] array, int offset, int length) {
      if (length == 0) {
         return EMPTY_BUFFER;
      } else {
         byte[] copy = PlatformDependent.allocateUninitializedArray(length);
         System.arraycopy(array, offset, copy, 0, length);
         return wrappedBuffer(copy);
      }
   }

   public static ByteBuf copiedBuffer(ByteBuffer buffer) {
      int length = buffer.remaining();
      if (length == 0) {
         return EMPTY_BUFFER;
      } else {
         byte[] copy = PlatformDependent.allocateUninitializedArray(length);
         ByteBuffer duplicate = buffer.duplicate();
         duplicate.get(copy);
         return wrappedBuffer(copy).order(duplicate.order());
      }
   }

   public static ByteBuf copiedBuffer(ByteBuf buffer) {
      int readable = buffer.readableBytes();
      if (readable > 0) {
         ByteBuf copy = buffer(readable);
         copy.writeBytes(buffer, buffer.readerIndex(), readable);
         return copy;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copiedBuffer(byte[]... arrays) {
      switch (arrays.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            if (arrays[0].length == 0) {
               return EMPTY_BUFFER;
            }

            return copiedBuffer(arrays[0]);
         default:
            int length = 0;

            for (byte[] a : arrays) {
               if (Integer.MAX_VALUE - length < a.length) {
                  throw new IllegalArgumentException("The total length of the specified arrays is too big.");
               }

               length += a.length;
            }

            if (length == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
               int i = 0;

               for (int j = 0; i < arrays.length; i++) {
                  byte[] a = arrays[i];
                  System.arraycopy(a, 0, mergedArray, j, a.length);
                  j += a.length;
               }

               return wrappedBuffer(mergedArray);
            }
      }
   }

   public static ByteBuf copiedBuffer(ByteBuf... buffers) {
      switch (buffers.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return copiedBuffer(buffers[0]);
         default:
            ByteOrder order = null;
            int length = 0;

            for (ByteBuf b : buffers) {
               int bLen = b.readableBytes();
               if (bLen > 0) {
                  if (Integer.MAX_VALUE - length < bLen) {
                     throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                  }

                  length += bLen;
                  if (order != null) {
                     if (!order.equals(b.order())) {
                        throw new IllegalArgumentException("inconsistent byte order");
                     }
                  } else {
                     order = b.order();
                  }
               }
            }

            if (length == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
               int i = 0;

               for (int j = 0; i < buffers.length; i++) {
                  ByteBuf bx = buffers[i];
                  int bLen = bx.readableBytes();
                  bx.getBytes(bx.readerIndex(), mergedArray, j, bLen);
                  j += bLen;
               }

               return wrappedBuffer(mergedArray).order(order);
            }
      }
   }

   public static ByteBuf copiedBuffer(ByteBuffer... buffers) {
      switch (buffers.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return copiedBuffer(buffers[0]);
         default:
            ByteOrder order = null;
            int length = 0;

            for (ByteBuffer b : buffers) {
               int bLen = b.remaining();
               if (bLen > 0) {
                  if (Integer.MAX_VALUE - length < bLen) {
                     throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                  }

                  length += bLen;
                  if (order != null) {
                     if (!order.equals(b.order())) {
                        throw new IllegalArgumentException("inconsistent byte order");
                     }
                  } else {
                     order = b.order();
                  }
               }
            }

            if (length == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
               int i = 0;

               for (int j = 0; i < buffers.length; i++) {
                  ByteBuffer bx = buffers[i].duplicate();
                  int bLen = bx.remaining();
                  bx.get(mergedArray, j, bLen);
                  j += bLen;
               }

               return wrappedBuffer(mergedArray).order(order);
            }
      }
   }

   public static ByteBuf copiedBuffer(CharSequence string, Charset charset) {
      ObjectUtil.checkNotNull(string, "string");
      if (CharsetUtil.UTF_8.equals(charset)) {
         return copiedBufferUtf8(string);
      } else if (CharsetUtil.US_ASCII.equals(charset)) {
         return copiedBufferAscii(string);
      } else {
         return string instanceof CharBuffer ? copiedBuffer((CharBuffer)string, charset) : copiedBuffer(CharBuffer.wrap(string), charset);
      }
   }

   private static ByteBuf copiedBufferUtf8(CharSequence string) {
      boolean release = true;
      int byteLength = ByteBufUtil.utf8Bytes(string);
      ByteBuf buffer = ALLOC.heapBuffer(byteLength);

      ByteBuf var4;
      try {
         ByteBufUtil.reserveAndWriteUtf8(buffer, string, byteLength);
         release = false;
         var4 = buffer;
      } finally {
         if (release) {
            buffer.release();
         }
      }

      return var4;
   }

   private static ByteBuf copiedBufferAscii(CharSequence string) {
      boolean release = true;
      ByteBuf buffer = ALLOC.heapBuffer(string.length());

      ByteBuf var3;
      try {
         ByteBufUtil.writeAscii(buffer, string);
         release = false;
         var3 = buffer;
      } finally {
         if (release) {
            buffer.release();
         }
      }

      return var3;
   }

   public static ByteBuf copiedBuffer(CharSequence string, int offset, int length, Charset charset) {
      ObjectUtil.checkNotNull(string, "string");
      if (length == 0) {
         return EMPTY_BUFFER;
      } else if (string instanceof CharBuffer) {
         CharBuffer buf = (CharBuffer)string;
         if (buf.hasArray()) {
            return copiedBuffer(buf.array(), buf.arrayOffset() + buf.position() + offset, length, charset);
         } else {
            buf = buf.slice();
            ((Buffer)buf).limit(length);
            ((Buffer)buf).position(offset);
            return copiedBuffer(buf, charset);
         }
      } else {
         return copiedBuffer(CharBuffer.wrap(string, offset, offset + length), charset);
      }
   }

   public static ByteBuf copiedBuffer(char[] array, Charset charset) {
      ObjectUtil.checkNotNull(array, "array");
      return copiedBuffer(array, 0, array.length, charset);
   }

   public static ByteBuf copiedBuffer(char[] array, int offset, int length, Charset charset) {
      ObjectUtil.checkNotNull(array, "array");
      return length == 0 ? EMPTY_BUFFER : copiedBuffer(CharBuffer.wrap(array, offset, length), charset);
   }

   private static ByteBuf copiedBuffer(CharBuffer buffer, Charset charset) {
      return ByteBufUtil.encodeString0(ALLOC, true, buffer, charset, 0);
   }

   @Deprecated
   public static ByteBuf unmodifiableBuffer(ByteBuf buffer) {
      ByteOrder endianness = buffer.order();
      return (ByteBuf)(endianness == BIG_ENDIAN ? newReadyOnlyBuffer(buffer) : newReadyOnlyBuffer(buffer.order(BIG_ENDIAN)).order(LITTLE_ENDIAN));
   }

   private static ReadOnlyByteBuf newReadyOnlyBuffer(ByteBuf buffer) {
      return (ReadOnlyByteBuf)(buffer instanceof AbstractByteBuf ? new ReadOnlyAbstractByteBuf((AbstractByteBuf)buffer) : new ReadOnlyByteBuf(buffer));
   }

   public static ByteBuf copyInt(int value) {
      ByteBuf buf = buffer(4);
      buf.writeInt(value);
      return buf;
   }

   public static ByteBuf copyInt(int... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 4);

         for (int v : values) {
            buffer.writeInt(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyShort(int value) {
      ByteBuf buf = buffer(2);
      buf.writeShort(value);
      return buf;
   }

   public static ByteBuf copyShort(short... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 2);

         for (int v : values) {
            buffer.writeShort(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyShort(int... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 2);

         for (int v : values) {
            buffer.writeShort(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyMedium(int value) {
      ByteBuf buf = buffer(3);
      buf.writeMedium(value);
      return buf;
   }

   public static ByteBuf copyMedium(int... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 3);

         for (int v : values) {
            buffer.writeMedium(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyLong(long value) {
      ByteBuf buf = buffer(8);
      buf.writeLong(value);
      return buf;
   }

   public static ByteBuf copyLong(long... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 8);

         for (long v : values) {
            buffer.writeLong(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyBoolean(boolean value) {
      ByteBuf buf = buffer(1);
      buf.writeBoolean(value);
      return buf;
   }

   public static ByteBuf copyBoolean(boolean... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length);

         for (boolean v : values) {
            buffer.writeBoolean(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyFloat(float value) {
      ByteBuf buf = buffer(4);
      buf.writeFloat(value);
      return buf;
   }

   public static ByteBuf copyFloat(float... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 4);

         for (float v : values) {
            buffer.writeFloat(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyDouble(double value) {
      ByteBuf buf = buffer(8);
      buf.writeDouble(value);
      return buf;
   }

   public static ByteBuf copyDouble(double... values) {
      if (values != null && values.length != 0) {
         ByteBuf buffer = buffer(values.length * 8);

         for (double v : values) {
            buffer.writeDouble(v);
         }

         return buffer;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf unreleasableBuffer(ByteBuf buf) {
      return new UnreleasableByteBuf(buf);
   }

   @Deprecated
   public static ByteBuf unmodifiableBuffer(ByteBuf... buffers) {
      return wrappedUnmodifiableBuffer(true, buffers);
   }

   public static ByteBuf wrappedUnmodifiableBuffer(ByteBuf... buffers) {
      return wrappedUnmodifiableBuffer(false, buffers);
   }

   private static ByteBuf wrappedUnmodifiableBuffer(boolean copy, ByteBuf... buffers) {
      switch (buffers.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return buffers[0].asReadOnly();
         default:
            if (copy) {
               buffers = Arrays.copyOf(buffers, buffers.length, ByteBuf[].class);
            }

            return new FixedCompositeByteBuf(ALLOC, buffers);
      }
   }

   private Unpooled() {
   }

   static {
      assert EMPTY_BUFFER instanceof EmptyByteBuf : "EMPTY_BUFFER must be an EmptyByteBuf.";
   }
}
