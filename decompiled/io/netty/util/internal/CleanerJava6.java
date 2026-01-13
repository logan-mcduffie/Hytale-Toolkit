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
import java.util.Objects;

final class CleanerJava6 implements Cleaner {
   private static final MethodHandle CLEAN_METHOD;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava6.class);

   static boolean isSupported() {
      return CLEAN_METHOD != null;
   }

   @Override
   public CleanableDirectBuffer allocate(int capacity) {
      return new CleanerJava6.CleanableDirectBufferImpl(ByteBuffer.allocateDirect(capacity));
   }

   @Deprecated
   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      freeDirectBufferStatic(buffer);
   }

   private static void freeDirectBufferStatic(ByteBuffer buffer) {
      if (buffer.isDirect()) {
         if (System.getSecurityManager() == null) {
            try {
               freeDirectBuffer0(buffer);
            } catch (Throwable var2) {
               PlatformDependent0.throwException(var2);
            }
         } else {
            freeDirectBufferPrivileged(buffer);
         }
      }
   }

   private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
      Throwable cause = AccessController.doPrivileged(new PrivilegedAction<Throwable>() {
         public Throwable run() {
            try {
               CleanerJava6.freeDirectBuffer0(buffer);
               return null;
            } catch (Throwable var2) {
               return var2;
            }
         }
      });
      if (cause != null) {
         PlatformDependent0.throwException(cause);
      }
   }

   private static void freeDirectBuffer0(ByteBuffer buffer) throws Throwable {
      CLEAN_METHOD.invokeExact((ByteBuffer)buffer);
   }

   static {
      Throwable error = null;
      ByteBuffer direct = ByteBuffer.allocateDirect(1);

      MethodHandle clean;
      try {
         Object mayBeCleanerField = AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Class<?> cleanerClass = Class.forName("sun.misc.Cleaner");
                     Class<?> directBufClass = Class.forName("sun.nio.ch.DirectBuffer");
                     Lookup lookup = MethodHandles.lookup();
                     MethodHandle cleanx = lookup.findVirtual(cleanerClass, "clean", MethodType.methodType(void.class));
                     MethodHandle nullTest = lookup.findStatic(Objects.class, "nonNull", MethodType.methodType(boolean.class, Object.class));
                     cleanx = MethodHandles.guardWithTest(
                        nullTest.asType(MethodType.methodType(boolean.class, cleanerClass)),
                        cleanx,
                        nullTest.asType(MethodType.methodType(void.class, cleanerClass))
                     );
                     cleanx = MethodHandles.filterArguments(cleanx, 0, lookup.findVirtual(directBufClass, "cleaner", MethodType.methodType(cleanerClass)));
                     return MethodHandles.explicitCastArguments(cleanx, MethodType.methodType(void.class, ByteBuffer.class));
                  } catch (Throwable var6) {
                     return var6;
                  }
               }
            }
         );
         if (mayBeCleanerField instanceof Throwable) {
            throw (Throwable)mayBeCleanerField;
         }

         clean = (MethodHandle)mayBeCleanerField;
         clean.invokeExact((ByteBuffer)direct);
      } catch (Throwable var4) {
         clean = null;
         error = var4;
      }

      if (error == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
      }

      CLEAN_METHOD = clean;
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
         CleanerJava6.freeDirectBufferStatic(this.buffer);
      }
   }
}
