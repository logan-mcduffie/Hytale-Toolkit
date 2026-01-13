package org.bouncycastle.util;

import java.io.IOException;

public class Exceptions {
   public static IllegalArgumentException illegalArgumentException(String var0, Throwable var1) {
      return new IllegalArgumentException(var0, var1);
   }

   public static IllegalStateException illegalStateException(String var0, Throwable var1) {
      return new IllegalStateException(var0, var1);
   }

   public static IOException ioException(String var0, Throwable var1) {
      return new IOException(var0, var1);
   }
}
