package org.bouncycastle.math.raw;

public abstract class Nat512 {
   public static void mul(int[] var0, int[] var1, int[] var2) {
      Nat256.mul(var0, var1, var2);
      Nat256.mul(var0, 8, var1, 8, var2, 16);
      int var3 = Nat256.addToEachOther(var2, 8, var2, 16);
      int var4 = var3 + Nat256.addTo(var2, 0, var2, 8, 0);
      var3 += Nat256.addTo(var2, 24, var2, 16, var4);
      int[] var5 = Nat256.create();
      int[] var6 = Nat256.create();
      boolean var7 = Nat256.diff(var0, 8, var0, 0, var5, 0) != Nat256.diff(var1, 8, var1, 0, var6, 0);
      int[] var8 = Nat256.createExt();
      Nat256.mul(var5, var6, var8);
      var3 += var7 ? Nat.addTo(16, var8, 0, var2, 8) : Nat.subFrom(16, var8, 0, var2, 8);
      Nat.addWordAt(32, var3, var2, 24);
   }

   public static void square(int[] var0, int[] var1) {
      Nat256.square(var0, var1);
      Nat256.square(var0, 8, var1, 16);
      int var2 = Nat256.addToEachOther(var1, 8, var1, 16);
      int var3 = var2 + Nat256.addTo(var1, 0, var1, 8, 0);
      var2 += Nat256.addTo(var1, 24, var1, 16, var3);
      int[] var4 = Nat256.create();
      Nat256.diff(var0, 8, var0, 0, var4, 0);
      int[] var5 = Nat256.createExt();
      Nat256.square(var4, var5);
      var2 += Nat.subFrom(16, var5, 0, var1, 8);
      Nat.addWordAt(32, var2, var1, 24);
   }
}
