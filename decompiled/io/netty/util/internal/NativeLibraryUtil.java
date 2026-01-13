package io.netty.util.internal;

final class NativeLibraryUtil {
   public static void loadLibrary(String libName, boolean absolute) {
      if (absolute) {
         System.load(libName);
      } else {
         System.loadLibrary(libName);
      }
   }

   private NativeLibraryUtil() {
   }
}
