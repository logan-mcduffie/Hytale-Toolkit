package org.bouncycastle.util.encoders;

public class HexTranslator implements Translator {
   private static final byte[] hexTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

   @Override
   public int getEncodedBlockSize() {
      return 2;
   }

   @Override
   public int encode(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      int var6 = 0;

      for (byte var7 = 0; var6 < var3; var7 += 2) {
         var4[var5 + var7] = hexTable[var1[var2] >> 4 & 15];
         var4[var5 + var7 + 1] = hexTable[var1[var2] & 15];
         var2++;
         var6++;
      }

      return var3 * 2;
   }

   @Override
   public int getDecodedBlockSize() {
      return 1;
   }

   @Override
   public int decode(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      int var6 = var3 / 2;

      for (int var9 = 0; var9 < var6; var9++) {
         byte var7 = var1[var2 + var9 * 2];
         byte var8 = var1[var2 + var9 * 2 + 1];
         if (var7 < 97) {
            var4[var5] = (byte)(var7 - 48 << 4);
         } else {
            var4[var5] = (byte)(var7 - 97 + 10 << 4);
         }

         if (var8 < 97) {
            var4[var5] += (byte)(var8 - 48);
         } else {
            var4[var5] += (byte)(var8 - 97 + 10);
         }

         var5++;
      }

      return var6;
   }
}
