package org.bouncycastle.crypto.params;

public class DESedeParameters extends DESParameters {
   public static final int DES_EDE_KEY_LENGTH = 24;

   public DESedeParameters(byte[] var1) {
      super(var1);
      if (isWeakKey(var1, 0, var1.length)) {
         throw new IllegalArgumentException("attempt to create weak DESede key");
      }
   }

   public static boolean isWeakKey(byte[] var0, int var1, int var2) {
      for (int var3 = var1; var3 < var2; var3 += 8) {
         if (DESParameters.isWeakKey(var0, var3)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isWeakKey(byte[] var0, int var1) {
      return isWeakKey(var0, var1, var0.length - var1);
   }

   public static boolean isRealEDEKey(byte[] var0, int var1) {
      return var0.length == 16 ? isReal2Key(var0, var1) : isReal3Key(var0, var1);
   }

   public static boolean isReal2Key(byte[] var0, int var1) {
      boolean var2 = false;

      for (int var3 = var1; var3 != var1 + 8; var3++) {
         if (var0[var3] != var0[var3 + 8]) {
            var2 = true;
         }
      }

      return var2;
   }

   public static boolean isReal3Key(byte[] var0, int var1) {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;

      for (int var5 = var1; var5 != var1 + 8; var5++) {
         var2 |= var0[var5] != var0[var5 + 8];
         var3 |= var0[var5] != var0[var5 + 16];
         var4 |= var0[var5 + 8] != var0[var5 + 16];
      }

      return var2 && var3 && var4;
   }
}
