package org.bson.diagnostics;

import org.bson.assertions.Assertions;

public final class Loggers {
   private static final String PREFIX = "org.bson";
   private static final boolean USE_SLF4J = shouldUseSLF4J();

   public static Logger getLogger(String suffix) {
      Assertions.notNull("suffix", suffix);
      if (!suffix.startsWith(".") && !suffix.endsWith(".")) {
         String name = "org.bson." + suffix;
         return (Logger)(USE_SLF4J ? new SLF4JLogger(name) : new NoOpLogger(name));
      } else {
         throw new IllegalArgumentException("The suffix can not start or end with a '.'");
      }
   }

   private static boolean shouldUseSLF4J() {
      try {
         Class.forName("org.slf4j.Logger");
         return true;
      } catch (ClassNotFoundException var1) {
         java.util.logging.Logger.getLogger("org.bson")
            .warning(String.format("SLF4J not found on the classpath. Logging is disabled for the '%s' component", "org.bson"));
         return false;
      }
   }

   private Loggers() {
   }
}
