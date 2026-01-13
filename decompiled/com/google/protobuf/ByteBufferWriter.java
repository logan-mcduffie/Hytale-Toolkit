package com.google.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

final class ByteBufferWriter {
   private static final int MIN_CACHED_BUFFER_SIZE = 1024;
   private static final int MAX_CACHED_BUFFER_SIZE = 16384;
   private static final float BUFFER_REALLOCATION_THRESHOLD = 0.5F;
   private static final ThreadLocal<SoftReference<byte[]>> BUFFER = new ThreadLocal<>();
   private static final Class<?> FILE_OUTPUT_STREAM_CLASS = safeGetClass("java.io.FileOutputStream");
   private static final long CHANNEL_FIELD_OFFSET = getChannelFieldOffset(FILE_OUTPUT_STREAM_CLASS);

   private ByteBufferWriter() {
   }

   static void clearCachedBuffer() {
      BUFFER.set(null);
   }

   static void write(ByteBuffer buffer, OutputStream output) throws IOException {
      int initialPos = buffer.position();

      try {
         if (buffer.hasArray()) {
            output.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
         } else if (!writeToChannel(buffer, output)) {
            byte[] array = getOrCreateBuffer(buffer.remaining());

            while (buffer.hasRemaining()) {
               int length = Math.min(buffer.remaining(), array.length);
               buffer.get(array, 0, length);
               output.write(array, 0, length);
            }
         }
      } finally {
         Java8Compatibility.position(buffer, initialPos);
      }
   }

   private static byte[] getOrCreateBuffer(int requestedSize) {
      requestedSize = Math.max(requestedSize, 1024);
      byte[] buffer = getBuffer();
      if (buffer == null || needToReallocate(requestedSize, buffer.length)) {
         buffer = new byte[requestedSize];
         if (requestedSize <= 16384) {
            setBuffer(buffer);
         }
      }

      return buffer;
   }

   private static boolean needToReallocate(int requestedSize, int bufferLength) {
      return bufferLength < requestedSize && bufferLength < requestedSize * 0.5F;
   }

   private static byte[] getBuffer() {
      SoftReference<byte[]> sr = BUFFER.get();
      return sr == null ? null : sr.get();
   }

   private static void setBuffer(byte[] value) {
      BUFFER.set(new SoftReference<>(value));
   }

   private static boolean writeToChannel(ByteBuffer buffer, OutputStream output) throws IOException {
      if (CHANNEL_FIELD_OFFSET >= 0L && FILE_OUTPUT_STREAM_CLASS.isInstance(output)) {
         WritableByteChannel channel = null;

         try {
            channel = (WritableByteChannel)UnsafeUtil.getObject(output, CHANNEL_FIELD_OFFSET);
         } catch (ClassCastException var4) {
         }

         if (channel != null) {
            channel.write(buffer);
            return true;
         }
      }

      return false;
   }

   private static Class<?> safeGetClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   private static long getChannelFieldOffset(Class<?> clazz) {
      try {
         if (clazz != null && UnsafeUtil.hasUnsafeArrayOperations()) {
            java.lang.reflect.Field field = clazz.getDeclaredField("channel");
            return UnsafeUtil.objectFieldOffset(field);
         }
      } catch (Throwable var2) {
      }

      return -1L;
   }
}
