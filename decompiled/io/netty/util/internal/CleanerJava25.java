package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.ByteBuffer;

final class CleanerJava25 implements Cleaner {
   private static final InternalLogger logger;
   private static final MethodHandle INVOKE_ALLOCATOR;

   static boolean isSupported() {
      return INVOKE_ALLOCATOR != null;
   }

   @Override
   public CleanableDirectBuffer allocate(int capacity) {
      try {
         return (CleanerJava25.CleanableDirectBufferImpl)INVOKE_ALLOCATOR.invokeExact((int)capacity);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new IllegalStateException("Unexpected allocation exception", var4);
      }
   }

   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      throw new UnsupportedOperationException("Cannot clean arbitrary ByteBuffer instances");
   }

   static {
      boolean suitableJavaVersion;
      if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
         String v = System.getProperty("java.specification.version");

         try {
            suitableJavaVersion = Integer.parseInt(v) >= 25;
         } catch (NumberFormatException var21) {
            suitableJavaVersion = false;
         }

         logger = null;
      } else {
         suitableJavaVersion = PlatformDependent0.javaVersion() >= 25;
         logger = InternalLoggerFactory.getInstance(CleanerJava25.class);
      }

      Throwable error;
      MethodHandle method;
      if (suitableJavaVersion) {
         try {
            Class<?> arenaCls = Class.forName("java.lang.foreign.Arena");
            Class<?> memsegCls = Class.forName("java.lang.foreign.MemorySegment");
            Class<CleanerJava25.CleanableDirectBufferImpl> bufCls = CleanerJava25.CleanableDirectBufferImpl.class;
            Lookup lookup = MethodHandles.lookup();
            MethodHandle ofShared = lookup.findStatic(arenaCls, "ofShared", MethodType.methodType(arenaCls));
            Object shared = (Object)ofShared.invoke();
            ((AutoCloseable)shared).close();
            MethodHandle allocate = lookup.findVirtual(arenaCls, "allocate", MethodType.methodType(memsegCls, long.class));
            MethodHandle asByteBuffer = lookup.findVirtual(memsegCls, "asByteBuffer", MethodType.methodType(ByteBuffer.class));
            MethodHandle address = lookup.findVirtual(memsegCls, "address", MethodType.methodType(long.class));
            MethodHandle bufClsCtor = lookup.findConstructor(bufCls, MethodType.methodType(void.class, AutoCloseable.class, ByteBuffer.class, long.class));
            MethodHandle allocateInt = MethodHandles.explicitCastArguments(allocate, MethodType.methodType(memsegCls, arenaCls, int.class));
            MethodHandle ctorArenaMemsegMemseg = MethodHandles.explicitCastArguments(
               MethodHandles.filterArguments(bufClsCtor, 1, asByteBuffer, address), MethodType.methodType(bufCls, arenaCls, memsegCls, memsegCls)
            );
            MethodHandle ctorArenaMemsegNull = MethodHandles.permuteArguments(
               ctorArenaMemsegMemseg, MethodType.methodType(bufCls, arenaCls, memsegCls, memsegCls), 0, 1, 1
            );
            MethodHandle ctorArenaMemseg = MethodHandles.insertArguments(ctorArenaMemsegNull, 2, null);
            MethodHandle ctorArenaArenaInt = MethodHandles.collectArguments(ctorArenaMemseg, 1, allocateInt);
            MethodHandle ctorArenaNullInt = MethodHandles.permuteArguments(
               ctorArenaArenaInt, MethodType.methodType(bufCls, arenaCls, arenaCls, int.class), 0, 0, 2
            );
            MethodHandle ctorArenaInt = MethodHandles.insertArguments(ctorArenaNullInt, 1, null);
            method = MethodHandles.foldArguments(ctorArenaInt, ofShared);
            error = null;
         } catch (Throwable var20) {
            method = null;
            error = var20;
         }
      } else {
         method = null;
         error = new UnsupportedOperationException("java.lang.foreign.MemorySegment unavailable");
      }

      if (logger != null) {
         if (error == null) {
            logger.debug("java.nio.ByteBuffer.cleaner(): available");
         } else {
            logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
         }
      }

      INVOKE_ALLOCATOR = method;
   }

   private static final class CleanableDirectBufferImpl implements CleanableDirectBuffer {
      private final AutoCloseable closeable;
      private final ByteBuffer buffer;
      private final long memoryAddress;

      CleanableDirectBufferImpl(AutoCloseable closeable, ByteBuffer buffer, long memoryAddress) {
         this.closeable = closeable;
         this.buffer = buffer;
         this.memoryAddress = memoryAddress;
      }

      @Override
      public ByteBuffer buffer() {
         return this.buffer;
      }

      @Override
      public void clean() {
         try {
            this.closeable.close();
         } catch (RuntimeException var2) {
            throw var2;
         } catch (Exception var3) {
            throw new IllegalStateException("Unexpected close exception", var3);
         }
      }

      @Override
      public boolean hasMemoryAddress() {
         return true;
      }

      @Override
      public long memoryAddress() {
         return this.memoryAddress;
      }
   }
}
