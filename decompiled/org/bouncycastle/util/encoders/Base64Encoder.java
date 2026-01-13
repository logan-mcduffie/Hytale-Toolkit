package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class Base64Encoder implements Encoder {
   protected final byte[] encodingTable = new byte[]{
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      43,
      47
   };
   protected byte padding = 61;
   protected final byte[] decodingTable = new byte[128];

   protected void initialiseDecodingTable() {
      for (int var1 = 0; var1 < this.decodingTable.length; var1++) {
         this.decodingTable[var1] = -1;
      }

      for (int var2 = 0; var2 < this.encodingTable.length; var2++) {
         this.decodingTable[this.encodingTable[var2]] = (byte)var2;
      }
   }

   public Base64Encoder() {
      this.initialiseDecodingTable();
   }

   public int encode(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IOException {
      int var6 = var2;
      int var7 = var2 + var3 - 2;
      int var8 = var5;

      while (var6 < var7) {
         byte var9 = var1[var6++];
         int var10 = var1[var6++] & 255;
         int var11 = var1[var6++] & 255;
         var4[var8++] = this.encodingTable[var9 >>> 2 & 63];
         var4[var8++] = this.encodingTable[(var9 << 4 | var10 >>> 4) & 63];
         var4[var8++] = this.encodingTable[(var10 << 2 | var11 >>> 6) & 63];
         var4[var8++] = this.encodingTable[var11 & 63];
      }

      switch (var3 - (var6 - var2)) {
         case 1:
            int var27 = var1[var6++] & 255;
            var4[var8++] = this.encodingTable[var27 >>> 2 & 63];
            var4[var8++] = this.encodingTable[var27 << 4 & 63];
            var4[var8++] = this.padding;
            var4[var8++] = this.padding;
            break;
         case 2:
            int var26 = var1[var6++] & 255;
            int var28 = var1[var6++] & 255;
            var4[var8++] = this.encodingTable[var26 >>> 2 & 63];
            var4[var8++] = this.encodingTable[(var26 << 4 | var28 >>> 4) & 63];
            var4[var8++] = this.encodingTable[var28 << 2 & 63];
            var4[var8++] = this.padding;
      }

      return var8 - var5;
   }

   @Override
   public int getEncodedLength(int var1) {
      return (var1 + 2) / 3 * 4;
   }

   @Override
   public int getMaxDecodedLength(int var1) {
      return var1 / 4 * 3;
   }

   @Override
   public int encode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException {
      if (var3 < 0) {
         return 0;
      } else {
         byte[] var5 = new byte[72];
         int var6 = var3;

         while (var6 > 0) {
            int var7 = Math.min(54, var6);
            int var8 = this.encode(var1, var2, var7, var5, 0);
            var4.write(var5, 0, var8);
            var2 += var7;
            var6 -= var7;
         }

         return (var3 + 2) / 3 * 4;
      }
   }

   private boolean ignore(char var1) {
      return var1 == '\n' || var1 == '\r' || var1 == '\t' || var1 == ' ';
   }

   @Override
   public int decode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException {
      byte[] var9 = new byte[54];
      int var10 = 0;
      byte var11 = 0;
      int var12 = var2 + var3;

      while (var12 > var2 && this.ignore((char)var1[var12 - 1])) {
         var12--;
      }

      if (var12 == 0) {
         return 0;
      } else {
         int var13 = 0;

         int var14;
         for (var14 = var12; var14 > var2 && var13 != 4; var14--) {
            if (!this.ignore((char)var1[var14 - 1])) {
               var13++;
            }
         }

         var13 = this.nextI(var1, var2, var14);

         while (var13 < var14) {
            byte var5 = this.decodingTable[var1[var13++]];
            var13 = this.nextI(var1, var13, var14);
            byte var6 = this.decodingTable[var1[var13++]];
            var13 = this.nextI(var1, var13, var14);
            byte var7 = this.decodingTable[var1[var13++]];
            var13 = this.nextI(var1, var13, var14);
            byte var8 = this.decodingTable[var1[var13++]];
            if ((var5 | var6 | var7 | var8) < 0) {
               throw new IOException("invalid characters encountered in base64 data");
            }

            var9[var10++] = (byte)(var5 << 2 | var6 >> 4);
            var9[var10++] = (byte)(var6 << 4 | var7 >> 2);
            var9[var10++] = (byte)(var7 << 6 | var8);
            if (var10 == var9.length) {
               var4.write(var9);
               var10 = 0;
            }

            var11 += 3;
            var13 = this.nextI(var1, var13, var14);
         }

         if (var10 > 0) {
            var4.write(var9, 0, var10);
         }

         int var15 = this.nextI(var1, var13, var12);
         int var16 = this.nextI(var1, var15 + 1, var12);
         int var17 = this.nextI(var1, var16 + 1, var12);
         int var18 = this.nextI(var1, var17 + 1, var12);
         return var11 + this.decodeLastBlock(var4, (char)var1[var15], (char)var1[var16], (char)var1[var17], (char)var1[var18]);
      }
   }

   private int nextI(byte[] var1, int var2, int var3) {
      while (var2 < var3 && this.ignore((char)var1[var2])) {
         var2++;
      }

      return var2;
   }

   @Override
   public int decode(String var1, OutputStream var2) throws IOException {
      byte[] var7 = new byte[54];
      int var8 = 0;
      byte var9 = 0;
      int var10 = var1.length();

      while (var10 > 0 && this.ignore(var1.charAt(var10 - 1))) {
         var10--;
      }

      if (var10 == 0) {
         return 0;
      } else {
         int var11 = 0;

         int var12;
         for (var12 = var10; var12 > 0 && var11 != 4; var12--) {
            if (!this.ignore(var1.charAt(var12 - 1))) {
               var11++;
            }
         }

         var11 = this.nextI(var1, 0, var12);

         while (var11 < var12) {
            byte var3 = this.decodingTable[var1.charAt(var11++)];
            var11 = this.nextI(var1, var11, var12);
            byte var4 = this.decodingTable[var1.charAt(var11++)];
            var11 = this.nextI(var1, var11, var12);
            byte var5 = this.decodingTable[var1.charAt(var11++)];
            var11 = this.nextI(var1, var11, var12);
            byte var6 = this.decodingTable[var1.charAt(var11++)];
            if ((var3 | var4 | var5 | var6) < 0) {
               throw new IOException("invalid characters encountered in base64 data");
            }

            var7[var8++] = (byte)(var3 << 2 | var4 >> 4);
            var7[var8++] = (byte)(var4 << 4 | var5 >> 2);
            var7[var8++] = (byte)(var5 << 6 | var6);
            var9 += 3;
            if (var8 == var7.length) {
               var2.write(var7);
               var8 = 0;
            }

            var11 = this.nextI(var1, var11, var12);
         }

         if (var8 > 0) {
            var2.write(var7, 0, var8);
         }

         int var13 = this.nextI(var1, var11, var10);
         int var14 = this.nextI(var1, var13 + 1, var10);
         int var15 = this.nextI(var1, var14 + 1, var10);
         int var16 = this.nextI(var1, var15 + 1, var10);
         return var9 + this.decodeLastBlock(var2, var1.charAt(var13), var1.charAt(var14), var1.charAt(var15), var1.charAt(var16));
      }
   }

   private int decodeLastBlock(OutputStream var1, char var2, char var3, char var4, char var5) throws IOException {
      if (var4 == this.padding) {
         if (var5 != this.padding) {
            throw new IOException("invalid characters encountered at end of base64 data");
         } else {
            byte var11 = this.decodingTable[var2];
            byte var13 = this.decodingTable[var3];
            if ((var11 | var13) < 0) {
               throw new IOException("invalid characters encountered at end of base64 data");
            } else {
               var1.write(var11 << 2 | var13 >> 4);
               return 1;
            }
         }
      } else if (var5 == this.padding) {
         byte var10 = this.decodingTable[var2];
         byte var12 = this.decodingTable[var3];
         byte var14 = this.decodingTable[var4];
         if ((var10 | var12 | var14) < 0) {
            throw new IOException("invalid characters encountered at end of base64 data");
         } else {
            var1.write(var10 << 2 | var12 >> 4);
            var1.write(var12 << 4 | var14 >> 2);
            return 2;
         }
      } else {
         byte var6 = this.decodingTable[var2];
         byte var7 = this.decodingTable[var3];
         byte var8 = this.decodingTable[var4];
         byte var9 = this.decodingTable[var5];
         if ((var6 | var7 | var8 | var9) < 0) {
            throw new IOException("invalid characters encountered at end of base64 data");
         } else {
            var1.write(var6 << 2 | var7 >> 4);
            var1.write(var7 << 4 | var8 >> 2);
            var1.write(var8 << 6 | var9);
            return 3;
         }
      }
   }

   private int nextI(String var1, int var2, int var3) {
      while (var2 < var3 && this.ignore(var1.charAt(var2))) {
         var2++;
      }

      return var2;
   }
}
