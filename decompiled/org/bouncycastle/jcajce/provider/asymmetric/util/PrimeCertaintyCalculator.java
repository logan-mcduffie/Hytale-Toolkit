package org.bouncycastle.jcajce.provider.asymmetric.util;

public class PrimeCertaintyCalculator {
   private PrimeCertaintyCalculator() {
   }

   public static int getDefaultCertainty(int var0) {
      return var0 <= 1024 ? 80 : 96 + 16 * ((var0 - 1) / 1024);
   }
}
