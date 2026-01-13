package io.netty.channel.kqueue;

import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class KQueue {
   private static final Throwable UNAVAILABILITY_CAUSE;

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   public static boolean isTcpFastOpenClientSideAvailable() {
      return isAvailable() && Native.IS_SUPPORTING_TCP_FASTOPEN_CLIENT;
   }

   public static boolean isTcpFastOpenServerSideAvailable() {
      return isAvailable() && Native.IS_SUPPORTING_TCP_FASTOPEN_SERVER;
   }

   private KQueue() {
   }

   static {
      Throwable cause = null;
      if (SystemPropertyUtil.getBoolean("io.netty.transport.noNative", false)) {
         cause = new UnsupportedOperationException("Native transport was explicit disabled with -Dio.netty.transport.noNative=true");
      } else {
         FileDescriptor kqueueFd = null;

         try {
            kqueueFd = Native.newKQueue();
         } catch (Throwable var11) {
            cause = var11;
         } finally {
            if (kqueueFd != null) {
               try {
                  kqueueFd.close();
               } catch (Exception var10) {
               }
            }
         }
      }

      if (cause != null) {
         InternalLogger logger = InternalLoggerFactory.getInstance(KQueue.class);
         if (logger.isTraceEnabled()) {
            logger.debug("KQueue support is not available", cause);
         } else if (logger.isDebugEnabled()) {
            logger.debug("KQueue support is not available: {}", cause.getMessage());
         }
      }

      UNAVAILABILITY_CAUSE = cause;
   }
}
