package org.bouncycastle.pqc.crypto.cmce;

abstract class BENES {
   private static final long[] TRANSPOSE_MASKS = new long[]{
      6148914691236517205L, 3689348814741910323L, 1085102592571150095L, 71777214294589695L, 281470681808895L, 4294967295L
   };
   protected final int SYS_N;
   protected final int SYS_T;
   protected final int GFBITS;

   public BENES(int var1, int var2, int var3) {
      this.SYS_N = var1;
      this.SYS_T = var2;
      this.GFBITS = var3;
   }

   static void transpose_64x64(long[] var0, long[] var1) {
      transpose_64x64(var0, var1, 0);
   }

   static void transpose_64x64(long[] var0, long[] var1, int var2) {
      System.arraycopy(var1, var2, var0, var2, 64);
      int var3 = 5;

      do {
         long var4 = TRANSPOSE_MASKS[var3];
         int var6 = 1 << var3;

         for (int var7 = var2; var7 < var2 + 64; var7 += var6 * 2) {
            for (int var8 = var7; var8 < var7 + var6; var8 += 4) {
               long var9 = var0[var8 + 0];
               long var11 = var0[var8 + 1];
               long var13 = var0[var8 + 2];
               long var15 = var0[var8 + 3];
               long var17 = var0[var8 + var6 + 0];
               long var19 = var0[var8 + var6 + 1];
               long var21 = var0[var8 + var6 + 2];
               long var23 = var0[var8 + var6 + 3];
               long var25 = (var9 >>> var6 ^ var17) & var4;
               long var27 = (var11 >>> var6 ^ var19) & var4;
               long var29 = (var13 >>> var6 ^ var21) & var4;
               long var31 = (var15 >>> var6 ^ var23) & var4;
               var0[var8 + 0] = var9 ^ var25 << var6;
               var0[var8 + 1] = var11 ^ var27 << var6;
               var0[var8 + 2] = var13 ^ var29 << var6;
               var0[var8 + 3] = var15 ^ var31 << var6;
               var0[var8 + var6 + 0] = var17 ^ var25;
               var0[var8 + var6 + 1] = var19 ^ var27;
               var0[var8 + var6 + 2] = var21 ^ var29;
               var0[var8 + var6 + 3] = var23 ^ var31;
            }
         }
      } while (--var3 >= 2);

      do {
         long var33 = TRANSPOSE_MASKS[var3];
         int var34 = 1 << var3;

         for (int var35 = var2; var35 < var2 + 64; var35 += var34 * 2) {
            for (int var36 = var35; var36 < var35 + var34; var36++) {
               long var37 = var0[var36 + 0];
               long var38 = var0[var36 + var34];
               long var39 = (var37 >>> var34 ^ var38) & var33;
               var0[var36 + 0] = var37 ^ var39 << var34;
               var0[var36 + var34] = var38 ^ var39;
            }
         }
      } while (--var3 >= 0);
   }

   protected abstract void support_gen(short[] var1, byte[] var2);
}
