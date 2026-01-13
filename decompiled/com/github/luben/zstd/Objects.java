package com.github.luben.zstd;

final class Objects {
   static void checkFromIndexSize(int var0, int var1, int var2) {
      if ((var2 | var0 | var1) < 0 || var1 > var2 - var0) {
         throw new IndexOutOfBoundsException(String.format("Range [%s, %<s + %s) out of bounds for length %s", var0, var1, var2));
      }
   }
}
