package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class CleanerJava24Linker implements Cleaner {
   private static final InternalLogger logger;
   private static final MethodHandle INVOKE_MALLOC;
   private static final MethodHandle INVOKE_CREATE_BYTEBUFFER;
   private static final MethodHandle INVOKE_FREE;

   static boolean isSupported() {
      return INVOKE_MALLOC != null;
   }

   @Override
   public CleanableDirectBuffer allocate(int capacity) {
      return new CleanerJava24Linker.CleanableDirectBufferImpl(capacity);
   }

   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      throw new UnsupportedOperationException("Cannot clean arbitrary ByteBuffer instances");
   }

   static long malloc(int capacity) {
      long addr;
      try {
         addr = (long)INVOKE_MALLOC.invokeExact((long)capacity);
      } catch (Throwable var4) {
         throw new Error(var4);
      }

      if (addr == 0L) {
         throw new OutOfMemoryError("malloc(2) failed to allocate " + capacity + " bytes");
      } else {
         return addr;
      }
   }

   static void free(long memoryAddress) {
      try {
         INVOKE_FREE.invokeExact((long)memoryAddress);
      } catch (Throwable var3) {
         throw new Error(var3);
      }
   }

   static {
      boolean suitableJavaVersion;
      if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
         String v = System.getProperty("java.specification.version");

         try {
            suitableJavaVersion = Integer.parseInt(v) >= 25;
         } catch (NumberFormatException var41) {
            suitableJavaVersion = false;
         }

         logger = null;
      } else {
         suitableJavaVersion = PlatformDependent0.javaVersion() >= 24;
         logger = InternalLoggerFactory.getInstance(CleanerJava24Linker.class);
      }

      MethodHandle wrapMethod;
      MethodHandle freeMethod;
      Throwable error;
      MethodHandle mallocMethod;
      if (suitableJavaVersion) {
         try {
            Lookup lookup = MethodHandles.lookup();
            Class<?> moduleCls = Class.forName("java.lang.Module");
            MethodHandle getModule = lookup.findVirtual(Class.class, "getModule", MethodType.methodType(moduleCls));
            MethodHandle isNativeAccessEnabledModule = lookup.findVirtual(moduleCls, "isNativeAccessEnabled", MethodType.methodType(boolean.class));
            MethodHandle isNativeAccessEnabledForClass = MethodHandles.filterArguments(isNativeAccessEnabledModule, 0, getModule);
            boolean isNativeAccessEnabled = (boolean)isNativeAccessEnabledForClass.invokeExact((Class)CleanerJava24Linker.class);
            if (!isNativeAccessEnabled) {
               throw new UnsupportedOperationException("Native access (restricted methods) is not enabled for the io.netty.common module.");
            }

            Class<?> memoryLayoutCls = Class.forName("java.lang.foreign.MemoryLayout");
            Class<?> memoryLayoutArrayCls = Class.forName("[Ljava.lang.foreign.MemoryLayout;");
            Class<?> valueLayoutCls = Class.forName("java.lang.foreign.ValueLayout");
            Class<?> valueLayoutAddressCls = Class.forName("java.lang.foreign.AddressLayout");
            MethodHandle addressLayoutGetter = lookup.findStaticGetter(valueLayoutCls, "ADDRESS", valueLayoutAddressCls);
            MethodHandle byteSize = lookup.findVirtual(valueLayoutAddressCls, "byteSize", MethodType.methodType(long.class));
            MethodHandle byteSizeOfAddress = MethodHandles.foldArguments(byteSize, addressLayoutGetter);
            long addressSize = (long)byteSizeOfAddress.invokeExact();
            if (addressSize != 8L) {
               throw new UnsupportedOperationException("Linking to malloc and free is only supported on 64-bit platforms.");
            }

            Class<?> ofLongValueLayoutCls = Class.forName("java.lang.foreign.ValueLayout$OfLong");
            Class<?> linkerCls = Class.forName("java.lang.foreign.Linker");
            Class<?> linkerOptionCls = Class.forName("java.lang.foreign.Linker$Option");
            Class<?> linkerOptionArrayCls = Class.forName("[Ljava.lang.foreign.Linker$Option;");
            Class<?> symbolLookupCls = Class.forName("java.lang.foreign.SymbolLookup");
            Class<?> memSegCls = Class.forName("java.lang.foreign.MemorySegment");
            Class<?> funcDescCls = Class.forName("java.lang.foreign.FunctionDescriptor");
            MethodHandle nativeLinker = lookup.findStatic(linkerCls, "nativeLinker", MethodType.methodType(linkerCls));
            MethodHandle defaultLookupStatic = MethodHandles.foldArguments(
               lookup.findVirtual(linkerCls, "defaultLookup", MethodType.methodType(symbolLookupCls)), nativeLinker
            );
            MethodHandle downcallHandleStatic = MethodHandles.foldArguments(
               lookup.findVirtual(linkerCls, "downcallHandle", MethodType.methodType(MethodHandle.class, memSegCls, funcDescCls, linkerOptionArrayCls)),
               nativeLinker
            );
            MethodHandle findSymbol = MethodHandles.foldArguments(
               lookup.findVirtual(symbolLookupCls, "findOrThrow", MethodType.methodType(memSegCls, String.class)), defaultLookupStatic
            );
            Object longLayout = (Object)lookup.findStaticGetter(valueLayoutCls, "JAVA_LONG", ofLongValueLayoutCls).invoke();
            Object layoutArray = Array.newInstance(memoryLayoutCls, 1);
            Array.set(layoutArray, 0, longLayout);
            MethodHandle mallocFuncDesc = MethodHandles.insertArguments(
               lookup.findStatic(funcDescCls, "of", MethodType.methodType(funcDescCls, memoryLayoutCls, memoryLayoutArrayCls)), 0, longLayout, layoutArray
            );
            MethodHandle mallocLinker = MethodHandles.foldArguments(
               MethodHandles.foldArguments(downcallHandleStatic, MethodHandles.foldArguments(findSymbol, MethodHandles.constant(String.class, "malloc"))),
               mallocFuncDesc
            );
            mallocMethod = (MethodHandle)mallocLinker.invoke((Object)Array.newInstance(linkerOptionCls, 0));
            MethodHandle freeFuncDesc = MethodHandles.insertArguments(
               lookup.findStatic(funcDescCls, "ofVoid", MethodType.methodType(funcDescCls, memoryLayoutArrayCls)), 0, layoutArray
            );
            MethodHandle freeLinker = MethodHandles.foldArguments(
               MethodHandles.foldArguments(downcallHandleStatic, MethodHandles.foldArguments(findSymbol, MethodHandles.constant(String.class, "free"))),
               freeFuncDesc
            );
            freeMethod = (MethodHandle)freeLinker.invoke((Object)Array.newInstance(linkerOptionCls, 0));
            MethodHandle ofAddress = lookup.findStatic(memSegCls, "ofAddress", MethodType.methodType(memSegCls, long.class));
            MethodHandle reinterpret = lookup.findVirtual(memSegCls, "reinterpret", MethodType.methodType(memSegCls, long.class));
            MethodHandle asByteBuffer = lookup.findVirtual(memSegCls, "asByteBuffer", MethodType.methodType(ByteBuffer.class));
            wrapMethod = MethodHandles.filterReturnValue(MethodHandles.filterArguments(reinterpret, 0, ofAddress), asByteBuffer);
            error = null;
         } catch (Throwable var40) {
            mallocMethod = null;
            wrapMethod = null;
            freeMethod = null;
            error = var40;
         }
      } else {
         mallocMethod = null;
         wrapMethod = null;
         freeMethod = null;
         error = new UnsupportedOperationException("java.lang.foreign.MemorySegment unavailable");
      }

      if (logger != null) {
         if (error == null) {
            logger.debug("java.nio.ByteBuffer.cleaner(): available");
         } else {
            logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
         }
      }

      INVOKE_MALLOC = mallocMethod;
      INVOKE_CREATE_BYTEBUFFER = wrapMethod;
      INVOKE_FREE = freeMethod;
   }

   private static final class CleanableDirectBufferImpl implements CleanableDirectBuffer {
      private final ByteBuffer buffer;
      private final long memoryAddress;

      private CleanableDirectBufferImpl(int capacity) {
         long addr = CleanerJava24Linker.malloc(capacity);

         try {
            this.memoryAddress = addr;
            this.buffer = (ByteBuffer)CleanerJava24Linker.INVOKE_CREATE_BYTEBUFFER.invokeExact((long)addr, (long)capacity);
         } catch (Throwable var8) {
            Error error = new Error(var8);

            try {
               CleanerJava24Linker.free(addr);
            } catch (Throwable var7) {
               error.addSuppressed(var7);
            }

            throw error;
         }
      }

      @Override
      public ByteBuffer buffer() {
         return this.buffer;
      }

      @Override
      public void clean() {
         CleanerJava24Linker.free(this.memoryAddress);
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
