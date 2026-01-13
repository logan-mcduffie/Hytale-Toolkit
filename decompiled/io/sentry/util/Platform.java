package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Platform {
   static boolean isAndroid;
   static boolean isJavaNinePlus;

   public static boolean isAndroid() {
      return isAndroid;
   }

   public static boolean isJvm() {
      return !isAndroid;
   }

   public static boolean isJavaNinePlus() {
      return isJavaNinePlus;
   }

   static {
      try {
         isAndroid = "The Android Project".equals(System.getProperty("java.vendor"));
      } catch (Throwable var4) {
         isAndroid = false;
      }

      try {
         String javaStringVersion = System.getProperty("java.specification.version");
         if (javaStringVersion != null) {
            double javaVersion = Double.valueOf(javaStringVersion);
            isJavaNinePlus = javaVersion >= 9.0;
         } else {
            isJavaNinePlus = false;
         }
      } catch (Throwable var3) {
         isJavaNinePlus = false;
      }
   }
}
