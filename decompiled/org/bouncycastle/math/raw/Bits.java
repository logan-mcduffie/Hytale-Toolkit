package org.bouncycastle.math.raw;

public abstract class Bits {
   public static int bitPermuteStep(int var0, int var1, int var2) {
      int var3 = (var0 ^ var0 >>> var2) & var1;
      return var3 ^ var3 << var2 ^ var0;
   }

   public static long bitPermuteStep(long var0, long var2, int var4) {
      long var5 = (var0 ^ var0 >>> var4) & var2;
      return var5 ^ var5 << var4 ^ var0;
   }

   public static int bitPermuteStepSimple(int var0, int var1, int var2) {
      return (var0 & var1) << var2 | var0 >>> var2 & var1;
   }

   public static long bitPermuteStepSimple(long var0, long var2, int var4) {
      return (var0 & var2) << var4 | var0 >>> var4 & var2;
   }
}
