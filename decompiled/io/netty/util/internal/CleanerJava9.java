package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class CleanerJava9 implements Cleaner {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
   private static final MethodHandle INVOKE_CLEANER;

   static boolean isSupported() {
      return INVOKE_CLEANER != null;
   }

   @Override
   public CleanableDirectBuffer allocate(int capacity) {
      return new CleanerJava9.CleanableDirectBufferImpl(ByteBuffer.allocateDirect(capacity));
   }

   @Deprecated
   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      freeDirectBufferStatic(buffer);
   }

   private static void freeDirectBufferStatic(ByteBuffer buffer) {
      if (System.getSecurityManager() == null) {
         try {
            INVOKE_CLEANER.invokeExact((ByteBuffer)buffer);
         } catch (Throwable var2) {
            PlatformDependent0.throwException(var2);
         }
      } else {
         freeDirectBufferPrivileged(buffer);
      }
   }

   private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
      Throwable error = AccessController.doPrivileged(new PrivilegedAction<Throwable>() {
         public Throwable run() {
            try {
               CleanerJava9.INVOKE_CLEANER.invokeExact((ByteBuffer)buffer);
               return null;
            } catch (Throwable var2) {
               return var2;
            }
         }
      });
      if (error != null) {
         PlatformDependent0.throwException(error);
      }
   }

   static {
      MethodHandle method;
      Throwable error;
      if (PlatformDependent0.hasUnsafe()) {
         final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
         Object maybeInvokeMethod = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Class<? extends Unsafe> unsafeClass = (Class<? extends Unsafe>)PlatformDependent0.UNSAFE.getClass();
                  Lookup lookup = MethodHandles.lookup();
                  MethodHandle invokeCleaner = lookup.findVirtual(unsafeClass, "invokeCleaner", MethodType.methodType(void.class, ByteBuffer.class));
                  invokeCleaner = invokeCleaner.bindTo(PlatformDependent0.UNSAFE);
                  invokeCleaner.invokeExact((ByteBuffer)buffer);
                  return invokeCleaner;
               } catch (Throwable var4) {
                  return var4;
               }
            }
         });
         if (maybeInvokeMethod instanceof Throwable) {
            method = null;
            error = (Throwable)maybeInvokeMethod;
         } else {
            method = (MethodHandle)maybeInvokeMethod;
            error = null;
         }
      } else {
         method = null;
         error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
      }

      if (error == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
      }

      INVOKE_CLEANER = method;
   }

   private static final class CleanableDirectBufferImpl implements CleanableDirectBuffer {
      private final ByteBuffer buffer;

      private CleanableDirectBufferImpl(ByteBuffer buffer) {
         this.buffer = buffer;
      }

      @Override
      public ByteBuffer buffer() {
         return this.buffer;
      }

      @Override
      public void clean() {
         CleanerJava9.freeDirectBufferStatic(this.buffer);
      }
   }
}
