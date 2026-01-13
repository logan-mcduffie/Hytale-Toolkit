package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class HexEncoder implements Encoder {
   protected final byte[] encodingTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
   protected final byte[] decodingTable = new byte[128];

   protected void initialiseDecodingTable() {
      for (int var1 = 0; var1 < this.decodingTable.length; var1++) {
         this.decodingTable[var1] = -1;
      }

      for (int var2 = 0; var2 < this.encodingTable.length; var2++) {
         this.decodingTable[this.encodingTable[var2]] = (byte)var2;
      }

      this.decodingTable[65] = this.decodingTable[97];
      this.decodingTable[66] = this.decodingTable[98];
      this.decodingTable[67] = this.decodingTable[99];
      this.decodingTable[68] = this.decodingTable[100];
      this.decodingTable[69] = this.decodingTable[101];
      this.decodingTable[70] = this.decodingTable[102];
   }

   public HexEncoder() {
      this.initialiseDecodingTable();
   }

   public int encode(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IOException {
      int var6 = var2;
      int var7 = var2 + var3;
      int var8 = var5;

      while (var6 < var7) {
         int var9 = var1[var6++] & 255;
         var4[var8++] = this.encodingTable[var9 >>> 4];
         var4[var8++] = this.encodingTable[var9 & 15];
      }

      return var8 - var5;
   }

   @Override
   public int getEncodedLength(int var1) {
      return var1 * 2;
   }

   @Override
   public int getMaxDecodedLength(int var1) {
      return var1 / 2;
   }

   @Override
   public int encode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException {
      if (var3 < 0) {
         return 0;
      } else {
         byte[] var5 = new byte[72];
         int var6 = var3;

         while (var6 > 0) {
            int var7 = Math.min(36, var6);
            int var8 = this.encode(var1, var2, var7, var5, 0);
            var4.write(var5, 0, var8);
            var2 += var7;
            var6 -= var7;
         }

         return var3 * 2;
      }
   }

   private static boolean ignore(char var0) {
      return var0 == '\n' || var0 == '\r' || var0 == '\t' || var0 == ' ';
   }

   @Override
   public int decode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException {
      int var7 = 0;
      byte[] var8 = new byte[36];
      int var9 = 0;
      int var10 = var2 + var3;

      while (var10 > var2 && ignore((char)var1[var10 - 1])) {
         var10--;
      }

      for (int var11 = var2; var11 < var10; var7++) {
         while (var11 < var10 && ignore((char)var1[var11])) {
            var11++;
         }

         byte var5 = this.decodingTable[var1[var11++]];

         while (var11 < var10 && ignore((char)var1[var11])) {
            var11++;
         }

         byte var6 = this.decodingTable[var1[var11++]];
         if ((var5 | var6) < 0) {
            throw new IOException("invalid characters encountered in Hex data");
         }

         var8[var9++] = (byte)(var5 << 4 | var6);
         if (var9 == var8.length) {
            var4.write(var8);
            var9 = 0;
         }
      }

      if (var9 > 0) {
         var4.write(var8, 0, var9);
      }

      return var7;
   }

   @Override
   public int decode(String var1, OutputStream var2) throws IOException {
      int var5 = 0;
      byte[] var6 = new byte[36];
      int var7 = 0;
      int var8 = var1.length();

      while (var8 > 0 && ignore(var1.charAt(var8 - 1))) {
         var8--;
      }

      for (int var9 = 0; var9 < var8; var5++) {
         while (var9 < var8 && ignore(var1.charAt(var9))) {
            var9++;
         }

         byte var3 = this.decodingTable[var1.charAt(var9++)];

         while (var9 < var8 && ignore(var1.charAt(var9))) {
            var9++;
         }

         byte var4 = this.decodingTable[var1.charAt(var9++)];
         if ((var3 | var4) < 0) {
            throw new IOException("invalid characters encountered in Hex string");
         }

         var6[var7++] = (byte)(var3 << 4 | var4);
         if (var7 == var6.length) {
            var2.write(var6);
            var7 = 0;
         }
      }

      if (var7 > 0) {
         var2.write(var6, 0, var7);
      }

      return var5;
   }

   byte[] decodeStrict(String var1, int var2, int var3) throws IOException {
      if (null == var1) {
         throw new NullPointerException("'str' cannot be null");
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length() - var3) {
         if (0 != (var3 & 1)) {
            throw new IOException("a hexadecimal encoding must have an even number of characters");
         } else {
            int var4 = var3 >>> 1;
            byte[] var5 = new byte[var4];
            int var6 = var2;

            for (int var7 = 0; var7 < var4; var7++) {
               byte var8 = this.decodingTable[var1.charAt(var6++)];
               byte var9 = this.decodingTable[var1.charAt(var6++)];
               int var10 = var8 << 4 | var9;
               if (var10 < 0) {
                  throw new IOException("invalid characters encountered in Hex string");
               }

               var5[var7] = (byte)var10;
            }

            return var5;
         }
      } else {
         throw new IndexOutOfBoundsException("invalid offset and/or length specified");
      }
   }
}
