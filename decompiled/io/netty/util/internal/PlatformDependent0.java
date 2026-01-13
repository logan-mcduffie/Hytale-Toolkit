package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Unsafe;

final class PlatformDependent0 {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
   private static final long ADDRESS_FIELD_OFFSET;
   private static final long BYTE_ARRAY_BASE_OFFSET;
   private static final long INT_ARRAY_BASE_OFFSET;
   private static final long INT_ARRAY_INDEX_SCALE;
   private static final long LONG_ARRAY_BASE_OFFSET;
   private static final long LONG_ARRAY_INDEX_SCALE;
   private static final MethodHandle DIRECT_BUFFER_CONSTRUCTOR;
   private static final MethodHandle ALLOCATE_ARRAY_METHOD;
   private static final MethodHandle ALIGN_SLICE;
   private static final MethodHandle OFFSET_SLICE;
   private static final boolean IS_ANDROID = isAndroid0();
   private static final int JAVA_VERSION = javaVersion0();
   private static final Throwable EXPLICIT_NO_UNSAFE_CAUSE = explicitNoUnsafeCause0();
   private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
   private static final boolean RUNNING_IN_NATIVE_IMAGE = SystemPropertyUtil.contains("org.graalvm.nativeimage.imagecode");
   private static final boolean IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE = explicitTryReflectionSetAccessible0();
   static final MethodHandle IS_VIRTUAL_THREAD_METHOD_HANDLE = getIsVirtualThreadMethodHandle();
   static final Unsafe UNSAFE;
   static final int HASH_CODE_ASCII_SEED = -1028477387;
   static final int HASH_CODE_C1 = -862048943;
   static final int HASH_CODE_C2 = 461845907;
   private static final long UNSAFE_COPY_THRESHOLD = 1048576L;
   private static final boolean UNALIGNED;
   private static final long BITS_MAX_DIRECT_MEMORY;

   private static MethodHandle getIsVirtualThreadMethodHandle() {
      try {
         MethodHandle methodHandle = MethodHandles.publicLookup().findVirtual(Thread.class, "isVirtual", MethodType.methodType(boolean.class));
         boolean isVirtual = (boolean)methodHandle.invokeExact((Thread)Thread.currentThread());
         return methodHandle;
      } catch (Throwable var2) {
         if (logger.isTraceEnabled()) {
            logger.debug("Thread.isVirtual() is not available: ", var2);
         } else {
            logger.debug("Thread.isVirtual() is not available: ", var2.getMessage());
         }

         return null;
      }
   }

   static boolean isVirtualThread(Thread thread) {
      if (thread != null && IS_VIRTUAL_THREAD_METHOD_HANDLE != null) {
         try {
            return (boolean)IS_VIRTUAL_THREAD_METHOD_HANDLE.invokeExact((Thread)thread);
         } catch (Throwable var2) {
            if (var2 instanceof Error) {
               throw (Error)var2;
            } else {
               throw new Error(var2);
            }
         }
      } else {
         return false;
      }
   }

   static boolean isNativeImage() {
      return RUNNING_IN_NATIVE_IMAGE;
   }

   static boolean isExplicitNoUnsafe() {
      return EXPLICIT_NO_UNSAFE_CAUSE != null;
   }

   private static Throwable explicitNoUnsafeCause0() {
      boolean explicitProperty = SystemPropertyUtil.contains("io.netty.noUnsafe");
      boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
      logger.debug("-Dio.netty.noUnsafe: {}", noUnsafe);
      String reason = "io.netty.noUnsafe";
      String unspecified = "<unspecified>";
      String unsafeMemoryAccess = SystemPropertyUtil.get("sun.misc.unsafe.memory.access", unspecified);
      if (!explicitProperty && unspecified.equals(unsafeMemoryAccess) && javaVersion() >= 25) {
         reason = "io.netty.noUnsafe=true by default on Java 25+";
         noUnsafe = true;
      } else if (!"allow".equals(unsafeMemoryAccess) && !unspecified.equals(unsafeMemoryAccess)) {
         reason = "--sun-misc-unsafe-memory-access=" + unsafeMemoryAccess;
         noUnsafe = true;
      }

      if (noUnsafe) {
         String msg = "sun.misc.Unsafe: unavailable (" + reason + ')';
         logger.debug(msg);
         return new UnsupportedOperationException(msg);
      } else {
         String unsafePropName;
         if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            unsafePropName = "io.netty.tryUnsafe";
         } else {
            unsafePropName = "org.jboss.netty.tryUnsafe";
         }

         if (!SystemPropertyUtil.getBoolean(unsafePropName, true)) {
            String msg = "sun.misc.Unsafe: unavailable (" + unsafePropName + ')';
            logger.debug(msg);
            return new UnsupportedOperationException(msg);
         } else {
            return null;
         }
      }
   }

   static boolean isUnaligned() {
      return UNALIGNED;
   }

   static long bitsMaxDirectMemory() {
      return BITS_MAX_DIRECT_MEMORY;
   }

   static boolean hasUnsafe() {
      return UNSAFE != null;
   }

   static Throwable getUnsafeUnavailabilityCause() {
      return UNSAFE_UNAVAILABILITY_CAUSE;
   }

   static boolean unalignedAccess() {
      return UNALIGNED;
   }

   static void throwException(Throwable cause) {
      throwException0(cause);
   }

   private static <E extends Throwable> void throwException0(Throwable t) throws E {
      throw t;
   }

   static boolean hasDirectBufferNoCleanerConstructor() {
      return DIRECT_BUFFER_CONSTRUCTOR != null;
   }

   static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
      return newDirectBuffer(UNSAFE.reallocateMemory(directBufferAddress(buffer), capacity), capacity);
   }

   static ByteBuffer allocateDirectNoCleaner(int capacity) {
      return newDirectBuffer(UNSAFE.allocateMemory(Math.max(1, capacity)), capacity);
   }

   static boolean hasAlignSliceMethod() {
      return ALIGN_SLICE != null;
   }

   static ByteBuffer alignSlice(ByteBuffer buffer, int alignment) {
      try {
         return (ByteBuffer)ALIGN_SLICE.invokeExact((ByteBuffer)buffer, (int)alignment);
      } catch (Throwable var3) {
         rethrowIfPossible(var3);
         throw new LinkageError("ByteBuffer.alignedSlice not available", var3);
      }
   }

   static boolean hasOffsetSliceMethod() {
      return OFFSET_SLICE != null;
   }

   static ByteBuffer offsetSlice(ByteBuffer buffer, int index, int length) {
      try {
         return (ByteBuffer)OFFSET_SLICE.invokeExact((ByteBuffer)buffer, (int)index, (int)length);
      } catch (Throwable var4) {
         rethrowIfPossible(var4);
         throw new LinkageError("ByteBuffer.slice(int, int) not available", var4);
      }
   }

   static boolean hasAllocateArrayMethod() {
      return ALLOCATE_ARRAY_METHOD != null;
   }

   static byte[] allocateUninitializedArray(int size) {
      try {
         return (byte[])(Object)ALLOCATE_ARRAY_METHOD.invokeExact((Class)byte.class, (int)size);
      } catch (Throwable var2) {
         rethrowIfPossible(var2);
         throw new LinkageError("Unsafe.allocateUninitializedArray not available", var2);
      }
   }

   static ByteBuffer newDirectBuffer(long address, int capacity) {
      ObjectUtil.checkPositiveOrZero(capacity, "capacity");

      try {
         return (ByteBuffer)DIRECT_BUFFER_CONSTRUCTOR.invokeExact((long)address, (int)capacity);
      } catch (Throwable var4) {
         rethrowIfPossible(var4);
         throw new LinkageError("DirectByteBuffer constructor not available", var4);
      }
   }

   private static void rethrowIfPossible(Throwable cause) {
      if (cause instanceof Error) {
         throw (Error)cause;
      } else if (cause instanceof RuntimeException) {
         throw (RuntimeException)cause;
      }
   }

   static long directBufferAddress(ByteBuffer buffer) {
      return getLong(buffer, ADDRESS_FIELD_OFFSET);
   }

   static long byteArrayBaseOffset() {
      return BYTE_ARRAY_BASE_OFFSET;
   }

   static Object getObject(Object object, long fieldOffset) {
      return UNSAFE.getObject(object, fieldOffset);
   }

   static int getInt(Object object, long fieldOffset) {
      return UNSAFE.getInt(object, fieldOffset);
   }

   static int getIntVolatile(Object object, long fieldOffset) {
      return UNSAFE.getIntVolatile(object, fieldOffset);
   }

   static void putOrderedInt(Object object, long fieldOffset, int value) {
      UNSAFE.putOrderedInt(object, fieldOffset, value);
   }

   static int getAndAddInt(Object object, long fieldOffset, int value) {
      return UNSAFE.getAndAddInt(object, fieldOffset, value);
   }

   static boolean compareAndSwapInt(Object object, long fieldOffset, int expected, int value) {
      return UNSAFE.compareAndSwapInt(object, fieldOffset, expected, value);
   }

   static void safeConstructPutInt(Object object, long fieldOffset, int value) {
      UNSAFE.putInt(object, fieldOffset, value);
      UNSAFE.storeFence();
   }

   private static long getLong(Object object, long fieldOffset) {
      return UNSAFE.getLong(object, fieldOffset);
   }

   static long objectFieldOffset(Field field) {
      return UNSAFE.objectFieldOffset(field);
   }

   static byte getByte(long address) {
      return UNSAFE.getByte(address);
   }

   static short getShort(long address) {
      return UNSAFE.getShort(address);
   }

   static int getInt(long address) {
      return UNSAFE.getInt(address);
   }

   static long getLong(long address) {
      return UNSAFE.getLong(address);
   }

   static byte getByte(byte[] data, int index) {
      return UNSAFE.getByte(data, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static byte getByte(byte[] data, long index) {
      return UNSAFE.getByte(data, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static short getShort(byte[] data, int index) {
      return UNSAFE.getShort(data, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static int getInt(byte[] data, int index) {
      return UNSAFE.getInt(data, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static int getInt(int[] data, long index) {
      return UNSAFE.getInt(data, INT_ARRAY_BASE_OFFSET + INT_ARRAY_INDEX_SCALE * index);
   }

   static long getLong(byte[] data, int index) {
      return UNSAFE.getLong(data, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static long getLong(long[] data, long index) {
      return UNSAFE.getLong(data, LONG_ARRAY_BASE_OFFSET + LONG_ARRAY_INDEX_SCALE * index);
   }

   static void putByte(long address, byte value) {
      UNSAFE.putByte(address, value);
   }

   static void putShort(long address, short value) {
      UNSAFE.putShort(address, value);
   }

   static void putShortOrdered(long address, short newValue) {
      UNSAFE.storeFence();
      UNSAFE.putShort(null, address, newValue);
   }

   static void putInt(long address, int value) {
      UNSAFE.putInt(address, value);
   }

   static void putLong(long address, long value) {
      UNSAFE.putLong(address, value);
   }

   static void putByte(byte[] data, int index, byte value) {
      UNSAFE.putByte(data, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static void putByte(Object data, long offset, byte value) {
      UNSAFE.putByte(data, offset, value);
   }

   static void putShort(byte[] data, int index, short value) {
      UNSAFE.putShort(data, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static void putInt(byte[] data, int index, int value) {
      UNSAFE.putInt(data, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static void putLong(byte[] data, int index, long value) {
      UNSAFE.putLong(data, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static void putObject(Object o, long offset, Object x) {
      UNSAFE.putObject(o, offset, x);
   }

   static void copyMemory(long srcAddr, long dstAddr, long length) {
      if (javaVersion() <= 8) {
         copyMemoryWithSafePointPolling(srcAddr, dstAddr, length);
      } else {
         UNSAFE.copyMemory(srcAddr, dstAddr, length);
      }
   }

   private static void copyMemoryWithSafePointPolling(long srcAddr, long dstAddr, long length) {
      while (length > 0L) {
         long size = Math.min(length, 1048576L);
         UNSAFE.copyMemory(srcAddr, dstAddr, size);
         length -= size;
         srcAddr += size;
         dstAddr += size;
      }
   }

   static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
      if (javaVersion() <= 8) {
         copyMemoryWithSafePointPolling(src, srcOffset, dst, dstOffset, length);
      } else {
         UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, length);
      }
   }

   private static void copyMemoryWithSafePointPolling(Object src, long srcOffset, Object dst, long dstOffset, long length) {
      while (length > 0L) {
         long size = Math.min(length, 1048576L);
         UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, size);
         length -= size;
         srcOffset += size;
         dstOffset += size;
      }
   }

   static void setMemory(long address, long bytes, byte value) {
      UNSAFE.setMemory(address, bytes, value);
   }

   static void setMemory(Object o, long offset, long bytes, byte value) {
      UNSAFE.setMemory(o, offset, bytes, value);
   }

   static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
      int remainingBytes = length & 7;
      long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + startPos1;
      long diff = startPos2 - startPos1;
      if (length >= 8) {
         long end = baseOffset1 + remainingBytes;

         for (long i = baseOffset1 - 8L + length; i >= end; i -= 8L) {
            if (UNSAFE.getLong(bytes1, i) != UNSAFE.getLong(bytes2, i + diff)) {
               return false;
            }
         }
      }

      if (remainingBytes >= 4) {
         remainingBytes -= 4;
         long pos = baseOffset1 + remainingBytes;
         if (UNSAFE.getInt(bytes1, pos) != UNSAFE.getInt(bytes2, pos + diff)) {
            return false;
         }
      }

      long baseOffset2 = baseOffset1 + diff;
      return remainingBytes >= 2
         ? UNSAFE.getChar(bytes1, baseOffset1) == UNSAFE.getChar(bytes2, baseOffset2)
            && (remainingBytes == 2 || UNSAFE.getByte(bytes1, baseOffset1 + 2L) == UNSAFE.getByte(bytes2, baseOffset2 + 2L))
         : remainingBytes == 0 || UNSAFE.getByte(bytes1, baseOffset1) == UNSAFE.getByte(bytes2, baseOffset2);
   }

   static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
      long result = 0L;
      long remainingBytes = length & 7;
      long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + startPos1;
      long end = baseOffset1 + remainingBytes;
      long diff = startPos2 - startPos1;

      for (long i = baseOffset1 - 8L + length; i >= end; i -= 8L) {
         result |= UNSAFE.getLong(bytes1, i) ^ UNSAFE.getLong(bytes2, i + diff);
      }

      if (remainingBytes >= 4L) {
         result |= UNSAFE.getInt(bytes1, baseOffset1) ^ UNSAFE.getInt(bytes2, baseOffset1 + diff);
         remainingBytes -= 4L;
      }

      if (remainingBytes >= 2L) {
         long pos = end - remainingBytes;
         result |= UNSAFE.getChar(bytes1, pos) ^ UNSAFE.getChar(bytes2, pos + diff);
         remainingBytes -= 2L;
      }

      if (remainingBytes == 1L) {
         long pos = end - 1L;
         result |= UNSAFE.getByte(bytes1, pos) ^ UNSAFE.getByte(bytes2, pos + diff);
      }

      return ConstantTimeUtils.equalsConstantTime(result, 0L);
   }

   static boolean isZero(byte[] bytes, int startPos, int length) {
      if (length <= 0) {
         return true;
      } else {
         long baseOffset = BYTE_ARRAY_BASE_OFFSET + startPos;
         int remainingBytes = length & 7;
         long end = baseOffset + remainingBytes;

         for (long i = baseOffset - 8L + length; i >= end; i -= 8L) {
            if (UNSAFE.getLong(bytes, i) != 0L) {
               return false;
            }
         }

         if (remainingBytes >= 4) {
            remainingBytes -= 4;
            if (UNSAFE.getInt(bytes, baseOffset + remainingBytes) != 0) {
               return false;
            }
         }

         return remainingBytes < 2 ? bytes[startPos] == 0 : UNSAFE.getChar(bytes, baseOffset) == 0 && (remainingBytes == 2 || bytes[startPos + 2] == 0);
      }
   }

   static int hashCodeAscii(byte[] bytes, int startPos, int length) {
      int hash = -1028477387;
      long baseOffset = BYTE_ARRAY_BASE_OFFSET + startPos;
      int remainingBytes = length & 7;
      long end = baseOffset + remainingBytes;

      for (long i = baseOffset - 8L + length; i >= end; i -= 8L) {
         hash = hashCodeAsciiCompute(UNSAFE.getLong(bytes, i), hash);
      }

      if (remainingBytes == 0) {
         return hash;
      } else {
         int hcConst = -862048943;
         if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(bytes, baseOffset));
            hcConst = 461845907;
            baseOffset++;
         }

         if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * hcConst + hashCodeAsciiSanitize(UNSAFE.getShort(bytes, baseOffset));
            hcConst = hcConst == -862048943 ? 461845907 : -862048943;
            baseOffset += 2L;
         }

         return remainingBytes >= 4 ? hash * hcConst + hashCodeAsciiSanitize(UNSAFE.getInt(bytes, baseOffset)) : hash;
      }
   }

   static int hashCodeAsciiCompute(long value, int hash) {
      return hash * -862048943 + hashCodeAsciiSanitize((int)value) * 461845907 + (int)((value & 2242545357458243584L) >>> 32);
   }

   static int hashCodeAsciiSanitize(int value) {
      return value & 522133279;
   }

   static int hashCodeAsciiSanitize(short value) {
      return value & 7967;
   }

   static int hashCodeAsciiSanitize(byte value) {
      return value & 31;
   }

   static ClassLoader getClassLoader(final Class<?> clazz) {
      return System.getSecurityManager() == null ? clazz.getClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return clazz.getClassLoader();
         }
      });
   }

   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null
         ? Thread.currentThread().getContextClassLoader()
         : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return Thread.currentThread().getContextClassLoader();
            }
         });
   }

   static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static int addressSize() {
      return UNSAFE.addressSize();
   }

   static long allocateMemory(long size) {
      return UNSAFE.allocateMemory(size);
   }

   static void freeMemory(long address) {
      UNSAFE.freeMemory(address);
   }

   static long reallocateMemory(long address, long newSize) {
      return UNSAFE.reallocateMemory(address, newSize);
   }

   static boolean isAndroid() {
      return IS_ANDROID;
   }

   private static boolean isAndroid0() {
      String vmName = SystemPropertyUtil.get("java.vm.name");
      boolean isAndroid = "Dalvik".equals(vmName);
      if (isAndroid) {
         logger.debug("Platform: Android");
      }

      return isAndroid;
   }

   private static boolean explicitTryReflectionSetAccessible0() {
      return SystemPropertyUtil.getBoolean("io.netty.tryReflectionSetAccessible", javaVersion() < 9 || RUNNING_IN_NATIVE_IMAGE);
   }

   static boolean isExplicitTryReflectionSetAccessible() {
      return IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
   }

   static int javaVersion() {
      return JAVA_VERSION;
   }

   private static int javaVersion0() {
      int majorVersion;
      if (isAndroid()) {
         majorVersion = 6;
      } else {
         majorVersion = majorVersionFromJavaSpecificationVersion();
      }

      logger.debug("Java version: {}", majorVersion);
      return majorVersion;
   }

   static int majorVersionFromJavaSpecificationVersion() {
      return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.6"));
   }

   static int majorVersion(String javaSpecVersion) {
      String[] components = javaSpecVersion.split("\\.");
      int[] version = new int[components.length];

      for (int i = 0; i < components.length; i++) {
         version[i] = Integer.parseInt(components[i]);
      }

      if (version[0] == 1) {
         assert version[1] >= 6;

         return version[1];
      } else {
         return version[0];
      }
   }

   private PlatformDependent0() {
   }

   static {
      final Lookup lookup = MethodHandles.lookup();
      Field addressField = null;
      MethodHandle allocateArrayMethod = null;
      Throwable unsafeUnavailabilityCause = EXPLICIT_NO_UNSAFE_CAUSE;
      final ByteBuffer direct;
      Unsafe unsafe;
      if (EXPLICIT_NO_UNSAFE_CAUSE != null) {
         direct = null;
         addressField = null;
         unsafe = null;
      } else {
         direct = ByteBuffer.allocateDirect(1);
         Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                  Throwable cause = ReflectionUtil.trySetAccessible(unsafeField, false);
                  return cause != null ? cause : unsafeField.get(null);
               } catch (IllegalAccessException | SecurityException | NoSuchFieldException var3) {
                  return var3;
               } catch (NoClassDefFoundError var4) {
                  return var4;
               }
            }
         });
         if (maybeUnsafe instanceof Throwable) {
            unsafe = null;
            unsafeUnavailabilityCause = (Throwable)maybeUnsafe;
            if (logger.isTraceEnabled()) {
               logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", unsafeUnavailabilityCause);
            } else {
               logger.debug("sun.misc.Unsafe.theUnsafe: unavailable: {}", unsafeUnavailabilityCause.getMessage());
            }
         } else {
            unsafe = (Unsafe)maybeUnsafe;
            logger.debug("sun.misc.Unsafe.theUnsafe: available");
         }

         if (unsafe != null) {
            final Unsafe finalUnsafe = unsafe;
            Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Class<? extends Unsafe> cls = (Class<? extends Unsafe>)finalUnsafe.getClass();
                     cls.getDeclaredMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
                     if (PlatformDependent0.javaVersion() > 23) {
                        cls.getDeclaredMethod("objectFieldOffset", Field.class);
                        cls.getDeclaredMethod("staticFieldOffset", Field.class);
                        cls.getDeclaredMethod("staticFieldBase", Field.class);
                        cls.getDeclaredMethod("arrayBaseOffset", Class.class);
                        cls.getDeclaredMethod("arrayIndexScale", Class.class);
                        cls.getDeclaredMethod("allocateMemory", long.class);
                        cls.getDeclaredMethod("reallocateMemory", long.class, long.class);
                        cls.getDeclaredMethod("freeMemory", long.class);
                        cls.getDeclaredMethod("setMemory", long.class, long.class, byte.class);
                        cls.getDeclaredMethod("setMemory", Object.class, long.class, long.class, byte.class);
                        cls.getDeclaredMethod("getBoolean", Object.class, long.class);
                        cls.getDeclaredMethod("getByte", long.class);
                        cls.getDeclaredMethod("getByte", Object.class, long.class);
                        cls.getDeclaredMethod("getInt", long.class);
                        cls.getDeclaredMethod("getInt", Object.class, long.class);
                        cls.getDeclaredMethod("getLong", long.class);
                        cls.getDeclaredMethod("getLong", Object.class, long.class);
                        cls.getDeclaredMethod("putByte", long.class, byte.class);
                        cls.getDeclaredMethod("putByte", Object.class, long.class, byte.class);
                        cls.getDeclaredMethod("putInt", long.class, int.class);
                        cls.getDeclaredMethod("putInt", Object.class, long.class, int.class);
                        cls.getDeclaredMethod("putLong", long.class, long.class);
                        cls.getDeclaredMethod("putLong", Object.class, long.class, long.class);
                        cls.getDeclaredMethod("addressSize");
                     }

                     if (PlatformDependent0.javaVersion() >= 23) {
                        long address = finalUnsafe.allocateMemory(8L);
                        finalUnsafe.putLong(address, 42L);
                        finalUnsafe.freeMemory(address);
                     }

                     return null;
                  } catch (SecurityException | NoSuchMethodException | UnsupportedOperationException var4) {
                     return var4;
                  }
               }
            });
            if (maybeException == null) {
               logger.debug("sun.misc.Unsafe base methods: all available");
            } else {
               unsafe = null;
               unsafeUnavailabilityCause = (Throwable)maybeException;
               if (logger.isTraceEnabled()) {
                  logger.debug("sun.misc.Unsafe method unavailable:", unsafeUnavailabilityCause);
               } else {
                  logger.debug("sun.misc.Unsafe method unavailable: {}", unsafeUnavailabilityCause.getMessage());
               }
            }
         }

         if (unsafe != null) {
            final Unsafe finalUnsafe = unsafe;
            Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field field = Buffer.class.getDeclaredField("address");
                     long offset = finalUnsafe.objectFieldOffset(field);
                     long address = finalUnsafe.getLong(direct, offset);
                     return address == 0L ? null : field;
                  } catch (SecurityException | NoSuchFieldException var6) {
                     return var6;
                  }
               }
            });
            if (maybeAddressField instanceof Field) {
               addressField = (Field)maybeAddressField;
               logger.debug("java.nio.Buffer.address: available");
            } else {
               unsafeUnavailabilityCause = (Throwable)maybeAddressField;
               if (logger.isTraceEnabled()) {
                  logger.debug("java.nio.Buffer.address: unavailable", (Throwable)maybeAddressField);
               } else {
                  logger.debug("java.nio.Buffer.address: unavailable: {}", ((Throwable)maybeAddressField).getMessage());
               }

               unsafe = null;
            }
         }

         if (unsafe != null) {
            long byteArrayIndexScale = unsafe.arrayIndexScale(byte[].class);
            if (byteArrayIndexScale != 1L) {
               logger.debug("unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", byteArrayIndexScale);
               unsafeUnavailabilityCause = new UnsupportedOperationException("Unexpected unsafe.arrayIndexScale");
               unsafe = null;
            }
         }
      }

      UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause;
      UNSAFE = unsafe;
      if (unsafe == null) {
         ADDRESS_FIELD_OFFSET = -1L;
         BYTE_ARRAY_BASE_OFFSET = -1L;
         LONG_ARRAY_BASE_OFFSET = -1L;
         LONG_ARRAY_INDEX_SCALE = -1L;
         INT_ARRAY_BASE_OFFSET = -1L;
         INT_ARRAY_INDEX_SCALE = -1L;
         UNALIGNED = false;
         BITS_MAX_DIRECT_MEMORY = -1L;
         DIRECT_BUFFER_CONSTRUCTOR = null;
         ALLOCATE_ARRAY_METHOD = null;
      } else {
         long address = -1L;

         MethodHandle directBufferConstructor;
         try {
            Object maybeDirectBufferConstructor = AccessController.doPrivileged(
               new PrivilegedAction<Object>() {
                  @Override
                  public Object run() {
                     try {
                        Class<? extends ByteBuffer> directClass = (Class<? extends ByteBuffer>)direct.getClass();
                        Constructor<?> constructor = PlatformDependent0.javaVersion() >= 21
                           ? directClass.getDeclaredConstructor(long.class, long.class)
                           : directClass.getDeclaredConstructor(long.class, int.class);
                        Throwable cause = ReflectionUtil.trySetAccessible(constructor, true);
                        return cause != null
                           ? cause
                           : lookup.unreflectConstructor(constructor).asType(MethodType.methodType(ByteBuffer.class, long.class, int.class));
                     } catch (Throwable var4) {
                        return var4;
                     }
                  }
               }
            );
            if (maybeDirectBufferConstructor instanceof MethodHandle) {
               address = UNSAFE.allocateMemory(1L);

               try {
                  MethodHandle constructor = (MethodHandle)maybeDirectBufferConstructor;
                  ByteBuffer ignore = (ByteBuffer)constructor.invokeExact((long)address, (int)1);
                  directBufferConstructor = constructor;
                  logger.debug("direct buffer constructor: available");
               } catch (Throwable var20) {
                  directBufferConstructor = null;
               }
            } else {
               if (logger.isTraceEnabled()) {
                  logger.debug("direct buffer constructor: unavailable", (Throwable)maybeDirectBufferConstructor);
               } else {
                  logger.debug("direct buffer constructor: unavailable: {}", ((Throwable)maybeDirectBufferConstructor).getMessage());
               }

               directBufferConstructor = null;
            }
         } finally {
            if (address != -1L) {
               UNSAFE.freeMemory(address);
            }
         }

         DIRECT_BUFFER_CONSTRUCTOR = directBufferConstructor;
         ADDRESS_FIELD_OFFSET = objectFieldOffset(addressField);
         BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
         INT_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(int[].class);
         INT_ARRAY_INDEX_SCALE = UNSAFE.arrayIndexScale(int[].class);
         LONG_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(long[].class);
         LONG_ARRAY_INDEX_SCALE = UNSAFE.arrayIndexScale(long[].class);
         final AtomicLong maybeMaxMemory = new AtomicLong(-1L);
         Object maybeUnaligned = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Class<?> bitsClass = Class.forName("java.nio.Bits", false, PlatformDependent0.getSystemClassLoader());
                  int version = PlatformDependent0.javaVersion();
                  if (version >= 9) {
                     String fieldName = version >= 11 ? "MAX_MEMORY" : "maxMemory";

                     try {
                        Field maxMemoryField = bitsClass.getDeclaredField(fieldName);
                        if (maxMemoryField.getType() == long.class) {
                           long offset = PlatformDependent0.UNSAFE.staticFieldOffset(maxMemoryField);
                           Object object = PlatformDependent0.UNSAFE.staticFieldBase(maxMemoryField);
                           maybeMaxMemory.lazySet(PlatformDependent0.UNSAFE.getLong(object, offset));
                        }
                     } catch (Throwable var9) {
                     }

                     fieldName = version >= 11 ? "UNALIGNED" : "unaligned";

                     try {
                        Field unalignedField = bitsClass.getDeclaredField(fieldName);
                        if (unalignedField.getType() == boolean.class) {
                           long offset = PlatformDependent0.UNSAFE.staticFieldOffset(unalignedField);
                           Object object = PlatformDependent0.UNSAFE.staticFieldBase(unalignedField);
                           return PlatformDependent0.UNSAFE.getBoolean(object, offset);
                        }
                     } catch (NoSuchFieldException var8) {
                     }
                  }

                  Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
                  Throwable cause = ReflectionUtil.trySetAccessible(unalignedMethod, true);
                  return cause != null ? cause : unalignedMethod.invoke(null);
               } catch (SecurityException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException var10) {
                  return var10;
               }
            }
         });
         boolean unaligned;
         if (maybeUnaligned instanceof Boolean) {
            unaligned = (Boolean)maybeUnaligned;
            logger.debug("java.nio.Bits.unaligned: available, {}", unaligned);
         } else {
            String arch = SystemPropertyUtil.get("os.arch", "");
            unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            Throwable t = (Throwable)maybeUnaligned;
            if (logger.isTraceEnabled()) {
               logger.debug("java.nio.Bits.unaligned: unavailable, {}", unaligned, t);
            } else {
               logger.debug("java.nio.Bits.unaligned: unavailable, {}, {}", unaligned, t.getMessage());
            }
         }

         UNALIGNED = unaligned;
         BITS_MAX_DIRECT_MEMORY = maybeMaxMemory.get() >= 0L ? maybeMaxMemory.get() : -1L;
         if (javaVersion() >= 9) {
            Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Class<?> cls = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass("jdk.internal.misc.Unsafe");
                     return (Object)lookup.findStatic(cls, "getUnsafe", MethodType.methodType(cls)).invoke();
                  } catch (Throwable var2) {
                     return var2;
                  }
               }
            });
            if (!(maybeException instanceof Throwable)) {
               final Object finalInternalUnsafe = maybeException;
               maybeException = AccessController.doPrivileged(
                  new PrivilegedAction<Object>() {
                     @Override
                     public Object run() {
                        try {
                           Class<?> finalInternalUnsafeClass = finalInternalUnsafe.getClass();
                           return lookup.findVirtual(
                              finalInternalUnsafeClass, "allocateUninitializedArray", MethodType.methodType(Object.class, Class.class, int.class)
                           );
                        } catch (Throwable var2) {
                           return var2;
                        }
                     }
                  }
               );
               if (maybeException instanceof MethodHandle) {
                  try {
                     MethodHandle m = (MethodHandle)maybeException;
                     m = m.bindTo(finalInternalUnsafe);
                     byte[] bytes = (byte[])(Object)m.invokeExact((Class)byte.class, (int)8);

                     assert bytes.length == 8;

                     allocateArrayMethod = m;
                  } catch (Throwable var19) {
                     maybeException = var19;
                  }
               }
            }

            if (maybeException instanceof Throwable) {
               if (logger.isTraceEnabled()) {
                  logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)maybeException);
               } else {
                  logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable: {}", ((Throwable)maybeException).getMessage());
               }
            } else {
               logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
            }
         } else {
            logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
         }

         ALLOCATE_ARRAY_METHOD = allocateArrayMethod;
      }

      if (javaVersion() > 9) {
         ALIGN_SLICE = (MethodHandle)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return MethodHandles.publicLookup().findVirtual(ByteBuffer.class, "alignedSlice", MethodType.methodType(ByteBuffer.class, int.class));
               } catch (Throwable var2) {
                  return null;
               }
            }
         });
      } else {
         ALIGN_SLICE = null;
      }

      if (javaVersion() >= 13) {
         OFFSET_SLICE = (MethodHandle)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return MethodHandles.publicLookup().findVirtual(ByteBuffer.class, "slice", MethodType.methodType(ByteBuffer.class, int.class, int.class));
               } catch (Throwable var2) {
                  return null;
               }
            }
         });
      } else {
         OFFSET_SLICE = null;
      }

      logger.debug("java.nio.DirectByteBuffer.<init>(long, {int,long}): {}", DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable");
   }
}
