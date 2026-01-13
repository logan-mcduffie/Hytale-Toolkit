package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class UnsafeUtil {
   private static final Unsafe UNSAFE = getUnsafe();
   private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
   private static final boolean IS_ANDROID_64 = determineAndroidSupportByAddressSize(long.class);
   private static final boolean IS_ANDROID_32 = determineAndroidSupportByAddressSize(int.class);
   private static final UnsafeUtil.MemoryAccessor MEMORY_ACCESSOR = getMemoryAccessor();
   private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS = supportsUnsafeByteBufferOperations();
   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = supportsUnsafeArrayOperations();
   static final long BYTE_ARRAY_BASE_OFFSET = arrayBaseOffset(byte[].class);
   private static final long BOOLEAN_ARRAY_BASE_OFFSET = arrayBaseOffset(boolean[].class);
   private static final long BOOLEAN_ARRAY_INDEX_SCALE = arrayIndexScale(boolean[].class);
   private static final long INT_ARRAY_BASE_OFFSET = arrayBaseOffset(int[].class);
   private static final long INT_ARRAY_INDEX_SCALE = arrayIndexScale(int[].class);
   private static final long LONG_ARRAY_BASE_OFFSET = arrayBaseOffset(long[].class);
   private static final long LONG_ARRAY_INDEX_SCALE = arrayIndexScale(long[].class);
   private static final long FLOAT_ARRAY_BASE_OFFSET = arrayBaseOffset(float[].class);
   private static final long FLOAT_ARRAY_INDEX_SCALE = arrayIndexScale(float[].class);
   private static final long DOUBLE_ARRAY_BASE_OFFSET = arrayBaseOffset(double[].class);
   private static final long DOUBLE_ARRAY_INDEX_SCALE = arrayIndexScale(double[].class);
   private static final long OBJECT_ARRAY_BASE_OFFSET = arrayBaseOffset(Object[].class);
   private static final long OBJECT_ARRAY_INDEX_SCALE = arrayIndexScale(Object[].class);
   private static final long BUFFER_ADDRESS_OFFSET = fieldOffset(bufferAddressField());
   private static final int STRIDE = 8;
   private static final int STRIDE_ALIGNMENT_MASK = 7;
   private static final int BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 7L);
   static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

   private UnsafeUtil() {
   }

   static boolean hasUnsafeArrayOperations() {
      return HAS_UNSAFE_ARRAY_OPERATIONS;
   }

   static boolean hasUnsafeByteBufferOperations() {
      return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
   }

   static <T> T allocateInstance(Class<T> clazz) {
      try {
         return (T)UNSAFE.allocateInstance(clazz);
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      }
   }

   static long objectFieldOffset(java.lang.reflect.Field field) {
      return MEMORY_ACCESSOR.objectFieldOffset(field);
   }

   private static int arrayBaseOffset(Class<?> clazz) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(clazz) : -1;
   }

   private static int arrayIndexScale(Class<?> clazz) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(clazz) : -1;
   }

   static byte getByte(Object target, long offset) {
      return MEMORY_ACCESSOR.getByte(target, offset);
   }

   static void putByte(Object target, long offset, byte value) {
      MEMORY_ACCESSOR.putByte(target, offset, value);
   }

   static int getInt(Object target, long offset) {
      return MEMORY_ACCESSOR.getInt(target, offset);
   }

   static void putInt(Object target, long offset, int value) {
      MEMORY_ACCESSOR.putInt(target, offset, value);
   }

   static long getLong(Object target, long offset) {
      return MEMORY_ACCESSOR.getLong(target, offset);
   }

   static void putLong(Object target, long offset, long value) {
      MEMORY_ACCESSOR.putLong(target, offset, value);
   }

   static boolean getBoolean(Object target, long offset) {
      return MEMORY_ACCESSOR.getBoolean(target, offset);
   }

   static void putBoolean(Object target, long offset, boolean value) {
      MEMORY_ACCESSOR.putBoolean(target, offset, value);
   }

   static float getFloat(Object target, long offset) {
      return MEMORY_ACCESSOR.getFloat(target, offset);
   }

   static void putFloat(Object target, long offset, float value) {
      MEMORY_ACCESSOR.putFloat(target, offset, value);
   }

   static double getDouble(Object target, long offset) {
      return MEMORY_ACCESSOR.getDouble(target, offset);
   }

   static void putDouble(Object target, long offset, double value) {
      MEMORY_ACCESSOR.putDouble(target, offset, value);
   }

   static Object getObject(Object target, long offset) {
      return MEMORY_ACCESSOR.getObject(target, offset);
   }

   static void putObject(Object target, long offset, Object value) {
      MEMORY_ACCESSOR.putObject(target, offset, value);
   }

   static byte getByte(byte[] target, long index) {
      return MEMORY_ACCESSOR.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static void putByte(byte[] target, long index, byte value) {
      MEMORY_ACCESSOR.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static int getInt(int[] target, long index) {
      return MEMORY_ACCESSOR.getInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE);
   }

   static void putInt(int[] target, long index, int value) {
      MEMORY_ACCESSOR.putInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE, value);
   }

   static long getLong(long[] target, long index) {
      return MEMORY_ACCESSOR.getLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE);
   }

   static void putLong(long[] target, long index, long value) {
      MEMORY_ACCESSOR.putLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE, value);
   }

   static boolean getBoolean(boolean[] target, long index) {
      return MEMORY_ACCESSOR.getBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE);
   }

   static void putBoolean(boolean[] target, long index, boolean value) {
      MEMORY_ACCESSOR.putBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE, value);
   }

   static float getFloat(float[] target, long index) {
      return MEMORY_ACCESSOR.getFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE);
   }

   static void putFloat(float[] target, long index, float value) {
      MEMORY_ACCESSOR.putFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE, value);
   }

   static double getDouble(double[] target, long index) {
      return MEMORY_ACCESSOR.getDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE);
   }

   static void putDouble(double[] target, long index, double value) {
      MEMORY_ACCESSOR.putDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE, value);
   }

   static Object getObject(Object[] target, long index) {
      return MEMORY_ACCESSOR.getObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE);
   }

   static void putObject(Object[] target, long index, Object value) {
      MEMORY_ACCESSOR.putObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE, value);
   }

   static void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
      MEMORY_ACCESSOR.copyMemory(src, srcIndex, targetOffset, length);
   }

   static void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
      MEMORY_ACCESSOR.copyMemory(srcOffset, target, targetIndex, length);
   }

   static void copyMemory(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
      System.arraycopy(src, (int)srcIndex, target, (int)targetIndex, (int)length);
   }

   static byte getByte(long address) {
      return MEMORY_ACCESSOR.getByte(address);
   }

   static void putByte(long address, byte value) {
      MEMORY_ACCESSOR.putByte(address, value);
   }

   static int getInt(long address) {
      return MEMORY_ACCESSOR.getInt(address);
   }

   static void putInt(long address, int value) {
      MEMORY_ACCESSOR.putInt(address, value);
   }

   static long getLong(long address) {
      return MEMORY_ACCESSOR.getLong(address);
   }

   static void putLong(long address, long value) {
      MEMORY_ACCESSOR.putLong(address, value);
   }

   static long addressOffset(ByteBuffer buffer) {
      return MEMORY_ACCESSOR.getLong(buffer, BUFFER_ADDRESS_OFFSET);
   }

   static Object getStaticObject(java.lang.reflect.Field field) {
      return MEMORY_ACCESSOR.getStaticObject(field);
   }

   static Unsafe getUnsafe() {
      Unsafe unsafe = null;

      try {
         unsafe = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
               Class<Unsafe> k = Unsafe.class;

               for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                  f.setAccessible(true);
                  Object x = f.get(null);
                  if (k.isInstance(x)) {
                     return k.cast(x);
                  }
               }

               return null;
            }
         });
      } catch (Throwable var2) {
      }

      return unsafe;
   }

   private static UnsafeUtil.MemoryAccessor getMemoryAccessor() {
      if (UNSAFE == null) {
         return null;
      } else if (Android.isOnAndroidDevice()) {
         if (IS_ANDROID_64) {
            return new UnsafeUtil.Android64MemoryAccessor(UNSAFE);
         } else {
            return IS_ANDROID_32 ? new UnsafeUtil.Android32MemoryAccessor(UNSAFE) : null;
         }
      } else {
         return new UnsafeUtil.JvmMemoryAccessor(UNSAFE);
      }
   }

   private static boolean supportsUnsafeArrayOperations() {
      return MEMORY_ACCESSOR == null ? false : MEMORY_ACCESSOR.supportsUnsafeArrayOperations();
   }

   private static boolean supportsUnsafeByteBufferOperations() {
      return MEMORY_ACCESSOR == null ? false : MEMORY_ACCESSOR.supportsUnsafeByteBufferOperations();
   }

   static boolean determineAndroidSupportByAddressSize(Class<?> addressClass) {
      if (!Android.isOnAndroidDevice()) {
         return false;
      } else {
         try {
            Class<?> clazz = MEMORY_CLASS;
            clazz.getMethod("peekLong", addressClass, boolean.class);
            clazz.getMethod("pokeLong", addressClass, long.class, boolean.class);
            clazz.getMethod("pokeInt", addressClass, int.class, boolean.class);
            clazz.getMethod("peekInt", addressClass, boolean.class);
            clazz.getMethod("pokeByte", addressClass, byte.class);
            clazz.getMethod("peekByte", addressClass);
            clazz.getMethod("pokeByteArray", addressClass, byte[].class, int.class, int.class);
            clazz.getMethod("peekByteArray", addressClass, byte[].class, int.class, int.class);
            return true;
         } catch (Throwable var2) {
            return false;
         }
      }
   }

   private static java.lang.reflect.Field bufferAddressField() {
      if (Android.isOnAndroidDevice()) {
         java.lang.reflect.Field field = field(Buffer.class, "effectiveDirectAddress");
         if (field != null) {
            return field;
         }
      }

      java.lang.reflect.Field field = field(Buffer.class, "address");
      return field != null && field.getType() == long.class ? field : null;
   }

   private static int firstDifferingByteIndexNativeEndian(long left, long right) {
      int n = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(left ^ right) : Long.numberOfTrailingZeros(left ^ right);
      return n >> 3;
   }

   static int mismatch(byte[] left, int leftOff, byte[] right, int rightOff, int length) {
      if (leftOff >= 0 && rightOff >= 0 && length >= 0 && leftOff + length <= left.length && rightOff + length <= right.length) {
         int index = 0;
         if (HAS_UNSAFE_ARRAY_OPERATIONS) {
            for (int leftAlignment = BYTE_ARRAY_ALIGNMENT + leftOff & 7; index < length && (leftAlignment & 7) != 0; leftAlignment++) {
               if (left[leftOff + index] != right[rightOff + index]) {
                  return index;
               }

               index++;
            }

            for (int strideLength = (length - index & -8) + index; index < strideLength; index += 8) {
               long leftLongWord = getLong(left, BYTE_ARRAY_BASE_OFFSET + leftOff + index);
               long rightLongWord = getLong(right, BYTE_ARRAY_BASE_OFFSET + rightOff + index);
               if (leftLongWord != rightLongWord) {
                  return index + firstDifferingByteIndexNativeEndian(leftLongWord, rightLongWord);
               }
            }
         }

         while (index < length) {
            if (left[leftOff + index] != right[rightOff + index]) {
               return index;
            }

            index++;
         }

         return -1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static long fieldOffset(java.lang.reflect.Field field) {
      return field != null && MEMORY_ACCESSOR != null ? MEMORY_ACCESSOR.objectFieldOffset(field) : -1L;
   }

   private static java.lang.reflect.Field field(Class<?> clazz, String fieldName) {
      java.lang.reflect.Field field;
      try {
         field = clazz.getDeclaredField(fieldName);
      } catch (Throwable var4) {
         field = null;
      }

      return field;
   }

   private static byte getByteBigEndian(Object target, long offset) {
      return (byte)(getInt(target, offset & -4L) >>> (int)((~offset & 3L) << 3) & 0xFF);
   }

   private static byte getByteLittleEndian(Object target, long offset) {
      return (byte)(getInt(target, offset & -4L) >>> (int)((offset & 3L) << 3) & 0xFF);
   }

   private static void putByteBigEndian(Object target, long offset, byte value) {
      int intValue = getInt(target, offset & -4L);
      int shift = (~((int)offset) & 3) << 3;
      int output = intValue & ~(255 << shift) | (255 & value) << shift;
      putInt(target, offset & -4L, output);
   }

   private static void putByteLittleEndian(Object target, long offset, byte value) {
      int intValue = getInt(target, offset & -4L);
      int shift = ((int)offset & 3) << 3;
      int output = intValue & ~(255 << shift) | (255 & value) << shift;
      putInt(target, offset & -4L, output);
   }

   private static boolean getBooleanBigEndian(Object target, long offset) {
      return getByteBigEndian(target, offset) != 0;
   }

   private static boolean getBooleanLittleEndian(Object target, long offset) {
      return getByteLittleEndian(target, offset) != 0;
   }

   private static void putBooleanBigEndian(Object target, long offset, boolean value) {
      putByteBigEndian(target, offset, (byte)(value ? 1 : 0));
   }

   private static void putBooleanLittleEndian(Object target, long offset, boolean value) {
      putByteLittleEndian(target, offset, (byte)(value ? 1 : 0));
   }

   private static void logMissingMethod(Throwable e) {
      Logger.getLogger(UnsafeUtil.class.getName()).log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + e);
   }

   private static final class Android32MemoryAccessor extends UnsafeUtil.MemoryAccessor {
      private static final long SMALL_ADDRESS_MASK = -1L;

      private static int smallAddress(long address) {
         return (int)(-1L & address);
      }

      Android32MemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field field) {
         try {
            return field.get(null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }

      @Override
      public byte getByte(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(target, offset) : UnsafeUtil.getByteLittleEndian(target, offset);
      }

      @Override
      public void putByte(Object target, long offset, byte value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putByteLittleEndian(target, offset, value);
         }
      }

      @Override
      public boolean getBoolean(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(target, offset) : UnsafeUtil.getBooleanLittleEndian(target, offset);
      }

      @Override
      public void putBoolean(Object target, long offset, boolean value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putBooleanLittleEndian(target, offset, value);
         }
      }

      @Override
      public float getFloat(Object target, long offset) {
         return Float.intBitsToFloat(this.getInt(target, offset));
      }

      @Override
      public void putFloat(Object target, long offset, float value) {
         this.putInt(target, offset, Float.floatToIntBits(value));
      }

      @Override
      public double getDouble(Object target, long offset) {
         return Double.longBitsToDouble(this.getLong(target, offset));
      }

      @Override
      public void putDouble(Object target, long offset, double value) {
         this.putLong(target, offset, Double.doubleToLongBits(value));
      }

      @Override
      public boolean supportsUnsafeByteBufferOperations() {
         return false;
      }

      @Override
      public byte getByte(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putByte(long address, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getInt(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putInt(long address, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putLong(long address, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class Android64MemoryAccessor extends UnsafeUtil.MemoryAccessor {
      Android64MemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field field) {
         try {
            return field.get(null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }

      @Override
      public byte getByte(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(target, offset) : UnsafeUtil.getByteLittleEndian(target, offset);
      }

      @Override
      public void putByte(Object target, long offset, byte value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putByteLittleEndian(target, offset, value);
         }
      }

      @Override
      public boolean getBoolean(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(target, offset) : UnsafeUtil.getBooleanLittleEndian(target, offset);
      }

      @Override
      public void putBoolean(Object target, long offset, boolean value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putBooleanLittleEndian(target, offset, value);
         }
      }

      @Override
      public float getFloat(Object target, long offset) {
         return Float.intBitsToFloat(this.getInt(target, offset));
      }

      @Override
      public void putFloat(Object target, long offset, float value) {
         this.putInt(target, offset, Float.floatToIntBits(value));
      }

      @Override
      public double getDouble(Object target, long offset) {
         return Double.longBitsToDouble(this.getLong(target, offset));
      }

      @Override
      public void putDouble(Object target, long offset, double value) {
         this.putLong(target, offset, Double.doubleToLongBits(value));
      }

      @Override
      public boolean supportsUnsafeByteBufferOperations() {
         return false;
      }

      @Override
      public byte getByte(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putByte(long address, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getInt(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putInt(long address, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(long address) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putLong(long address, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class JvmMemoryAccessor extends UnsafeUtil.MemoryAccessor {
      JvmMemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field field) {
         return this.getObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field));
      }

      @Override
      public boolean supportsUnsafeArrayOperations() {
         if (!super.supportsUnsafeArrayOperations()) {
            return false;
         } else {
            try {
               Class<?> clazz = this.unsafe.getClass();
               clazz.getMethod("getByte", Object.class, long.class);
               clazz.getMethod("putByte", Object.class, long.class, byte.class);
               clazz.getMethod("getBoolean", Object.class, long.class);
               clazz.getMethod("putBoolean", Object.class, long.class, boolean.class);
               clazz.getMethod("getFloat", Object.class, long.class);
               clazz.getMethod("putFloat", Object.class, long.class, float.class);
               clazz.getMethod("getDouble", Object.class, long.class);
               clazz.getMethod("putDouble", Object.class, long.class, double.class);
               return true;
            } catch (Throwable var2) {
               UnsafeUtil.logMissingMethod(var2);
               return false;
            }
         }
      }

      @Override
      public byte getByte(Object target, long offset) {
         return this.unsafe.getByte(target, offset);
      }

      @Override
      public void putByte(Object target, long offset, byte value) {
         this.unsafe.putByte(target, offset, value);
      }

      @Override
      public boolean getBoolean(Object target, long offset) {
         return this.unsafe.getBoolean(target, offset);
      }

      @Override
      public void putBoolean(Object target, long offset, boolean value) {
         this.unsafe.putBoolean(target, offset, value);
      }

      @Override
      public float getFloat(Object target, long offset) {
         return this.unsafe.getFloat(target, offset);
      }

      @Override
      public void putFloat(Object target, long offset, float value) {
         this.unsafe.putFloat(target, offset, value);
      }

      @Override
      public double getDouble(Object target, long offset) {
         return this.unsafe.getDouble(target, offset);
      }

      @Override
      public void putDouble(Object target, long offset, double value) {
         this.unsafe.putDouble(target, offset, value);
      }

      @Override
      public boolean supportsUnsafeByteBufferOperations() {
         if (!super.supportsUnsafeByteBufferOperations()) {
            return false;
         } else {
            try {
               Class<?> clazz = this.unsafe.getClass();
               clazz.getMethod("getByte", long.class);
               clazz.getMethod("putByte", long.class, byte.class);
               clazz.getMethod("getInt", long.class);
               clazz.getMethod("putInt", long.class, int.class);
               clazz.getMethod("getLong", long.class);
               clazz.getMethod("putLong", long.class, long.class);
               clazz.getMethod("copyMemory", long.class, long.class, long.class);
               clazz.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
               return true;
            } catch (Throwable var2) {
               UnsafeUtil.logMissingMethod(var2);
               return false;
            }
         }
      }

      @Override
      public byte getByte(long address) {
         return this.unsafe.getByte(address);
      }

      @Override
      public void putByte(long address, byte value) {
         this.unsafe.putByte(address, value);
      }

      @Override
      public int getInt(long address) {
         return this.unsafe.getInt(address);
      }

      @Override
      public void putInt(long address, int value) {
         this.unsafe.putInt(address, value);
      }

      @Override
      public long getLong(long address) {
         return this.unsafe.getLong(address);
      }

      @Override
      public void putLong(long address, long value) {
         this.unsafe.putLong(address, value);
      }

      @Override
      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         this.unsafe.copyMemory(null, srcOffset, target, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + targetIndex, length);
      }

      @Override
      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         this.unsafe.copyMemory(src, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + srcIndex, null, targetOffset, length);
      }
   }

   private abstract static class MemoryAccessor {
      Unsafe unsafe;

      MemoryAccessor(Unsafe unsafe) {
         this.unsafe = unsafe;
      }

      public final long objectFieldOffset(java.lang.reflect.Field field) {
         return this.unsafe.objectFieldOffset(field);
      }

      public final int arrayBaseOffset(Class<?> clazz) {
         return this.unsafe.arrayBaseOffset(clazz);
      }

      public final int arrayIndexScale(Class<?> clazz) {
         return this.unsafe.arrayIndexScale(clazz);
      }

      public abstract Object getStaticObject(java.lang.reflect.Field field);

      public boolean supportsUnsafeArrayOperations() {
         if (this.unsafe == null) {
            return false;
         } else {
            try {
               Class<?> clazz = this.unsafe.getClass();
               clazz.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
               clazz.getMethod("arrayBaseOffset", Class.class);
               clazz.getMethod("arrayIndexScale", Class.class);
               clazz.getMethod("getInt", Object.class, long.class);
               clazz.getMethod("putInt", Object.class, long.class, int.class);
               clazz.getMethod("getLong", Object.class, long.class);
               clazz.getMethod("putLong", Object.class, long.class, long.class);
               clazz.getMethod("getObject", Object.class, long.class);
               clazz.getMethod("putObject", Object.class, long.class, Object.class);
               return true;
            } catch (Throwable var2) {
               UnsafeUtil.logMissingMethod(var2);
               return false;
            }
         }
      }

      public abstract byte getByte(Object target, long offset);

      public abstract void putByte(Object target, long offset, byte value);

      public final int getInt(Object target, long offset) {
         return this.unsafe.getInt(target, offset);
      }

      public final void putInt(Object target, long offset, int value) {
         this.unsafe.putInt(target, offset, value);
      }

      public final long getLong(Object target, long offset) {
         return this.unsafe.getLong(target, offset);
      }

      public final void putLong(Object target, long offset, long value) {
         this.unsafe.putLong(target, offset, value);
      }

      public abstract boolean getBoolean(Object target, long offset);

      public abstract void putBoolean(Object target, long offset, boolean value);

      public abstract float getFloat(Object target, long offset);

      public abstract void putFloat(Object target, long offset, float value);

      public abstract double getDouble(Object target, long offset);

      public abstract void putDouble(Object target, long offset, double value);

      public final Object getObject(Object target, long offset) {
         return this.unsafe.getObject(target, offset);
      }

      public final void putObject(Object target, long offset, Object value) {
         this.unsafe.putObject(target, offset, value);
      }

      public boolean supportsUnsafeByteBufferOperations() {
         if (this.unsafe == null) {
            return false;
         } else {
            try {
               Class<?> clazz = this.unsafe.getClass();
               clazz.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
               clazz.getMethod("getLong", Object.class, long.class);
               return UnsafeUtil.bufferAddressField() != null;
            } catch (Throwable var2) {
               UnsafeUtil.logMissingMethod(var2);
               return false;
            }
         }
      }

      public abstract byte getByte(long address);

      public abstract void putByte(long address, byte value);

      public abstract int getInt(long address);

      public abstract void putInt(long address, int value);

      public abstract long getLong(long address);

      public abstract void putLong(long address, long value);

      public abstract void copyMemory(long srcOffset, byte[] target, long targetIndex, long length);

      public abstract void copyMemory(byte[] src, long srcIndex, long targetOffset, long length);
   }
}
