package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class Brotli {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Brotli.class);
   private static final ClassNotFoundException CNFE;
   private static Throwable cause;

   public static boolean isAvailable() {
      return CNFE == null && Brotli4jLoader.isAvailable();
   }

   public static void ensureAvailability() throws Throwable {
      if (CNFE != null) {
         throw CNFE;
      } else {
         Brotli4jLoader.ensureAvailability();
      }
   }

   public static Throwable cause() {
      return cause;
   }

   private Brotli() {
   }

   static {
      ClassNotFoundException cnfe = null;

      try {
         Class.forName("com.aayushatharva.brotli4j.Brotli4jLoader", false, PlatformDependent.getClassLoader(Brotli.class));
      } catch (ClassNotFoundException var2) {
         cnfe = var2;
         logger.debug("brotli4j not in the classpath; Brotli support will be unavailable.");
      }

      CNFE = cnfe;
      if (cnfe == null) {
         cause = Brotli4jLoader.getUnavailabilityCause();
         if (cause != null) {
            logger.debug("Failed to load brotli4j; Brotli support will be unavailable.", cause);
         }
      }
   }
}
