package org.bouncycastle.math.raw;

public abstract class Nat384 {
   public static void mul(int[] var0, int[] var1, int[] var2) {
      Nat192.mul(var0, var1, var2);
      Nat192.mul(var0, 6, var1, 6, var2, 12);
      int var3 = Nat192.addToEachOther(var2, 6, var2, 12);
      int var4 = var3 + Nat192.addTo(var2, 0, var2, 6, 0);
      var3 += Nat192.addTo(var2, 18, var2, 12, var4);
      int[] var5 = Nat192.create();
      int[] var6 = Nat192.create();
      boolean var7 = Nat192.diff(var0, 6, var0, 0, var5, 0) != Nat192.diff(var1, 6, var1, 0, var6, 0);
      int[] var8 = Nat192.createExt();
      Nat192.mul(var5, var6, var8);
      var3 += var7 ? Nat.addTo(12, var8, 0, var2, 6) : Nat.subFrom(12, var8, 0, var2, 6);
      Nat.addWordAt(24, var3, var2, 18);
   }

   public static void square(int[] var0, int[] var1) {
      Nat192.square(var0, var1);
      Nat192.square(var0, 6, var1, 12);
      int var2 = Nat192.addToEachOther(var1, 6, var1, 12);
      int var3 = var2 + Nat192.addTo(var1, 0, var1, 6, 0);
      var2 += Nat192.addTo(var1, 18, var1, 12, var3);
      int[] var4 = Nat192.create();
      Nat192.diff(var0, 6, var0, 0, var4, 0);
      int[] var5 = Nat192.createExt();
      Nat192.square(var4, var5);
      var2 += Nat.subFrom(12, var5, 0, var1, 6);
      Nat.addWordAt(24, var2, var1, 18);
   }
}
