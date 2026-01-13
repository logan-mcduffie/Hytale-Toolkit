package org.bouncycastle.pqc.crypto.falcon;

class FalconCodec {
   static final byte[] max_fg_bits = new byte[]{0, 8, 8, 8, 8, 8, 7, 7, 6, 6, 5};
   static final byte[] max_FG_bits = new byte[]{0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

   static int modq_encode(byte[] var0, int var1, short[] var2, int var3) {
      int var4 = 1 << var3;

      for (int var6 = 0; var6 < var4; var6++) {
         if ((var2[var6] & '\uffff') >= 12289) {
            return 0;
         }
      }

      int var5 = var4 * 14 + 7 >> 3;
      if (var0 == null) {
         return var5;
      } else if (var5 > var1) {
         return 0;
      } else {
         int var7 = 1;
         int var8 = 0;
         byte var9 = 0;

         for (int var10 = 0; var10 < var4; var10++) {
            var8 = var8 << 14 | var2[var10] & '\uffff';

            for (var9 += 14; var9 >= 8; var0[var7++] = (byte)(var8 >> var9)) {
               var9 -= 8;
            }
         }

         if (var9 > 0) {
            var0[var7] = (byte)(var8 << 8 - var9);
         }

         return var5;
      }
   }

   static int modq_decode(short[] var0, int var1, byte[] var2, int var3) {
      int var4 = 1 << var1;
      int var5 = var4 * 14 + 7 >> 3;
      if (var5 > var3) {
         return 0;
      } else {
         int var7 = 0;
         int var8 = 0;
         byte var9 = 0;
         int var6 = 0;

         while (var6 < var4) {
            var8 = var8 << 8 | var2[var7++] & 255;
            var9 += 8;
            if (var9 >= 14) {
               var9 -= 14;
               int var10 = var8 >>> var9 & 16383;
               if (var10 >= 12289) {
                  return 0;
               }

               var0[var6] = (short)var10;
               var6++;
            }
         }

         return (var8 & (1 << var9) - 1) != 0 ? 0 : var5;
      }
   }

   static int trim_i8_encode(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5) {
      int var6 = 1 << var4;
      int var10 = (1 << var5 - 1) - 1;
      int var9 = -var10;

      for (int var7 = 0; var7 < var6; var7++) {
         if (var3[var7] < var9 || var3[var7] > var10) {
            return 0;
         }
      }

      int var8 = var6 * var5 + 7 >> 3;
      if (var0 == null) {
         return var8;
      } else if (var8 > var2) {
         return 0;
      } else {
         int var11 = var1;
         int var12 = 0;
         int var14 = 0;
         int var13 = (1 << var5) - 1;

         for (int var15 = 0; var15 < var6; var15++) {
            var12 = var12 << var5 | var3[var15] & '\uffff' & var13;

            for (var14 += var5; var14 >= 8; var0[var11++] = (byte)(var12 >>> var14)) {
               var14 -= 8;
            }
         }

         if (var14 > 0) {
            var0[var11] = (byte)(var12 << 8 - var14);
         }

         return var8;
      }
   }

   static int trim_i8_decode(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5) {
      int var6 = 1 << var1;
      int var7 = var6 * var2 + 7 >> 3;
      if (var7 > var5) {
         return 0;
      } else {
         int var8 = var4;
         int var9 = 0;
         int var10 = 0;
         int var13 = 0;
         int var11 = (1 << var2) - 1;
         int var12 = 1 << var2 - 1;

         while (var9 < var6) {
            var10 = var10 << 8 | var3[var8++] & 255;

            for (var13 += 8; var13 >= var2 && var9 < var6; var9++) {
               var13 -= var2;
               int var14 = var10 >>> var13 & var11;
               var14 |= -(var14 & var12);
               if (var14 == -var12) {
                  return 0;
               }

               var0[var9] = (byte)var14;
            }
         }

         return (var10 & (1 << var13) - 1) != 0 ? 0 : var7;
      }
   }

   static int comp_encode(byte[] var0, int var1, short[] var2, int var3) {
      int var5 = 1 << var3;
      byte var4 = 0;

      for (int var6 = 0; var6 < var5; var6++) {
         if (var2[var6] < -2047 || var2[var6] > 2047) {
            return 0;
         }
      }

      int var8 = 0;
      int var9 = 0;
      int var7 = 0;

      for (int var12 = 0; var12 < var5; var12++) {
         var8 <<= 1;
         int var10 = var2[var12];
         if (var10 < 0) {
            var10 = -var10;
            var8 |= 1;
         }

         var8 <<= 7;
         var8 |= var10 & 127;
         int var11 = var10 >>> 7;
         var9 += 8;
         var8 <<= var11 + 1;
         var8 |= 1;

         for (var9 += var11 + 1; var9 >= 8; var7++) {
            var9 -= 8;
            if (var0 != null) {
               if (var7 >= var1) {
                  return 0;
               }

               var0[var4 + var7] = (byte)(var8 >>> var9);
            }
         }
      }

      if (var9 > 0) {
         if (var0 != null) {
            if (var7 >= var1) {
               return 0;
            }

            var0[var4 + var7] = (byte)(var8 << 8 - var9);
         }

         var7++;
      }

      return var7;
   }

   static int comp_decode(short[] var0, int var1, byte[] var2, int var3) {
      int var5 = 1 << var1;
      byte var4 = 0;
      int var8 = 0;
      int var9 = 0;
      int var7 = 0;

      label49:
      for (int var6 = 0; var6 < var5; var6++) {
         if (var7 >= var3) {
            return 0;
         }

         var8 = var8 << 8 | var2[var4 + var7] & 255;
         var7++;
         int var10 = var8 >>> var9;
         int var11 = var10 & 128;
         int var12 = var10 & 127;

         do {
            if (var9 == 0) {
               if (var7 >= var3) {
                  return 0;
               }

               var8 = var8 << 8 | var2[var4 + var7] & 255;
               var7++;
               var9 = 8;
            }

            if ((var8 >>> --var9 & 1) != 0) {
               if (var11 != 0 && var12 == 0) {
                  return 0;
               }

               var0[var6] = (short)(var11 != 0 ? -var12 : var12);
               continue label49;
            }

            var12 += 128;
         } while (var12 <= 2047);

         return 0;
      }

      return (var8 & (1 << var9) - 1) != 0 ? 0 : var7;
   }
}
