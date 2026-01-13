package org.bouncycastle.pqc.crypto.sphincs;

class Horst {
   static final int HORST_LOGT = 16;
   static final int HORST_T = 65536;
   static final int HORST_K = 32;
   static final int HORST_SKBYTES = 32;
   static final int HORST_SIGBYTES = 13312;
   static final int N_MASKS = 32;

   static void expand_seed(byte[] var0, byte[] var1) {
      Seed.prg(var0, 0, 2097152L, var1, 0);
   }

   static int horst_sign(HashFunctions var0, byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      byte[] var7 = new byte[2097152];
      int var12 = var2;
      byte[] var13 = new byte[4194272];
      expand_seed(var7, var4);

      for (int var9 = 0; var9 < 65536; var9++) {
         var0.hash_n_n(var13, (65535 + var9) * 32, var7, var9 * 32);
      }

      for (int var20 = 0; var20 < 16; var20++) {
         long var14 = (1 << 16 - var20) - 1;
         long var16 = (1 << 16 - var20 - 1) - 1;

         for (int var10 = 0; var10 < 1 << 16 - var20 - 1; var10++) {
            var0.hash_2n_n_mask(var13, (int)((var16 + var10) * 32L), var13, (int)((var14 + 2 * var10) * 32L), var5, 2 * var20 * 32);
         }
      }

      for (int var23 = 2016; var23 < 4064; var23++) {
         var1[var12++] = var13[var23];
      }

      for (int var21 = 0; var21 < 32; var21++) {
         int var8 = (var6[2 * var21] & 255) + ((var6[2 * var21 + 1] & 255) << 8);

         for (int var11 = 0; var11 < 32; var11++) {
            var1[var12++] = var7[var8 * 32 + var11];
         }

         var8 += 65535;

         for (int var24 = 0; var24 < 10; var24++) {
            var8 = (var8 & 1) != 0 ? var8 + 1 : var8 - 1;

            for (int var25 = 0; var25 < 32; var25++) {
               var1[var12++] = var13[var8 * 32 + var25];
            }

            var8 = (var8 - 1) / 2;
         }
      }

      for (int var22 = 0; var22 < 32; var22++) {
         var3[var22] = var13[var22];
      }

      return 13312;
   }

   static int horst_verify(HashFunctions var0, byte[] var1, byte[] var2, int var3, byte[] var4, byte[] var5) {
      byte[] var6 = new byte[1024];
      int var11 = var3 + 2048;

      for (int var8 = 0; var8 < 32; var8++) {
         int var7 = (var5[2 * var8] & 255) + ((var5[2 * var8 + 1] & 255) << 8);
         if ((var7 & 1) == 0) {
            var0.hash_n_n(var6, 0, var2, var11);

            for (int var18 = 0; var18 < 32; var18++) {
               var6[32 + var18] = var2[var11 + 32 + var18];
            }
         } else {
            var0.hash_n_n(var6, 32, var2, var11);

            for (int var10 = 0; var10 < 32; var10++) {
               var6[var10] = var2[var11 + 32 + var10];
            }
         }

         var11 += 64;

         for (int var9 = 1; var9 < 10; var9++) {
            var7 >>>= 1;
            if ((var7 & 1) == 0) {
               var0.hash_2n_n_mask(var6, 0, var6, 0, var4, 2 * (var9 - 1) * 32);

               for (int var20 = 0; var20 < 32; var20++) {
                  var6[32 + var20] = var2[var11 + var20];
               }
            } else {
               var0.hash_2n_n_mask(var6, 32, var6, 0, var4, 2 * (var9 - 1) * 32);

               for (int var19 = 0; var19 < 32; var19++) {
                  var6[var19] = var2[var11 + var19];
               }
            }

            var11 += 32;
         }

         var7 >>>= 1;
         var0.hash_2n_n_mask(var6, 0, var6, 0, var4, 576);

         for (int var21 = 0; var21 < 32; var21++) {
            if (var2[var3 + var7 * 32 + var21] != var6[var21]) {
               for (int var22 = 0; var22 < 32; var22++) {
                  var1[var22] = 0;
               }

               return -1;
            }
         }
      }

      for (int var13 = 0; var13 < 32; var13++) {
         var0.hash_2n_n_mask(var6, var13 * 32, var2, var3 + 2 * var13 * 32, var4, 640);
      }

      for (int var14 = 0; var14 < 16; var14++) {
         var0.hash_2n_n_mask(var6, var14 * 32, var6, 2 * var14 * 32, var4, 704);
      }

      for (int var15 = 0; var15 < 8; var15++) {
         var0.hash_2n_n_mask(var6, var15 * 32, var6, 2 * var15 * 32, var4, 768);
      }

      for (int var16 = 0; var16 < 4; var16++) {
         var0.hash_2n_n_mask(var6, var16 * 32, var6, 2 * var16 * 32, var4, 832);
      }

      for (int var17 = 0; var17 < 2; var17++) {
         var0.hash_2n_n_mask(var6, var17 * 32, var6, 2 * var17 * 32, var4, 896);
      }

      var0.hash_2n_n_mask(var1, 0, var6, 0, var4, 960);
      return 0;
   }
}
