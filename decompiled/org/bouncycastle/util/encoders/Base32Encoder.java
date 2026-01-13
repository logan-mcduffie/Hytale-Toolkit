package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class Base32Encoder implements Encoder {
   private static final byte[] DEAULT_ENCODING_TABLE = new byte[]{
      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55
   };
   private static final byte DEFAULT_PADDING = 61;
   private final byte[] encodingTable;
   private final byte padding;
   private final byte[] decodingTable = new byte[128];

   protected void initialiseDecodingTable() {
      for (int var1 = 0; var1 < this.decodingTable.length; var1++) {
         this.decodingTable[var1] = -1;
      }

      for (int var2 = 0; var2 < this.encodingTable.length; var2++) {
         this.decodingTable[this.encodingTable[var2]] = (byte)var2;
      }
   }

   public Base32Encoder() {
      this.encodingTable = DEAULT_ENCODING_TABLE;
      this.padding = 61;
      this.initialiseDecodingTable();
   }

   public Base32Encoder(byte[] var1, byte var2) {
      if (var1.length != 32) {
         throw new IllegalArgumentException("encoding table needs to be length 32");
      } else {
         this.encodingTable = Arrays.clone(var1);
         this.padding = var2;
         this.initialiseDecodingTable();
      }
   }

   public int encode(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IOException {
      int var6 = var2;
      int var7 = var2 + var3 - 4;

      int var8;
      for (var8 = var5; var6 < var7; var8 += 8) {
         this.encodeBlock(var1, var6, var4, var8);
         var6 += 5;
      }

      int var9 = var3 - (var6 - var2);
      if (var9 > 0) {
         byte[] var10 = new byte[5];
         System.arraycopy(var1, var6, var10, 0, var9);
         this.encodeBlock(var10, 0, var4, var8);
         switch (var9) {
            case 1:
               var4[var8 + 2] = this.padding;
               var4[var8 + 3] = this.padding;
               var4[var8 + 4] = this.padding;
               var4[var8 + 5] = this.padding;
               var4[var8 + 6] = this.padding;
               var4[var8 + 7] = this.padding;
               break;
            case 2:
               var4[var8 + 4] = this.padding;
               var4[var8 + 5] = this.padding;
               var4[var8 + 6] = this.padding;
               var4[var8 + 7] = this.padding;
               break;
            case 3:
               var4[var8 + 5] = this.padding;
               var4[var8 + 6] = this.padding;
               var4[var8 + 7] = this.padding;
               break;
            case 4:
               var4[var8 + 7] = this.padding;
         }

         var8 += 8;
      }

      return var8 - var5;
   }

   private void encodeBlock(byte[] var1, int var2, byte[] var3, int var4) {
      byte var5 = var1[var2++];
      int var6 = var1[var2++] & 255;
      int var7 = var1[var2++] & 255;
      int var8 = var1[var2++] & 255;
      int var9 = var1[var2] & 255;
      var3[var4++] = this.encodingTable[var5 >>> 3 & 31];
      var3[var4++] = this.encodingTable[(var5 << 2 | var6 >>> 6) & 31];
      var3[var4++] = this.encodingTable[var6 >>> 1 & 31];
      var3[var4++] = this.encodingTable[(var6 << 4 | var7 >>> 4) & 31];
      var3[var4++] = this.encodingTable[(var7 << 1 | var8 >>> 7) & 31];
      var3[var4++] = this.encodingTable[var8 >>> 2 & 31];
      var3[var4++] = this.encodingTable[(var8 << 3 | var9 >>> 5) & 31];
      var3[var4] = this.encodingTable[var9 & 31];
   }

   @Override
   public int getEncodedLength(int var1) {
      return (var1 + 4) / 5 * 8;
   }

   @Override
   public int getMaxDecodedLength(int var1) {
      return var1 / 8 * 5;
   }

   @Override
   public int encode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException {
      if (var3 < 0) {
         return 0;
      } else {
         byte[] var5 = new byte[72];
         int var6 = var3;

         while (var6 > 0) {
            int var7 = Math.min(45, var6);
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
      byte[] var13 = new byte[55];
      int var14 = 0;
      byte var15 = 0;
      int var16 = var2 + var3;

      while (var16 > var2 && this.ignore((char)var1[var16 - 1])) {
         var16--;
      }

      if (var16 == 0) {
         return 0;
      } else {
         int var17 = 0;

         int var18;
         for (var18 = var16; var18 > var2 && var17 != 8; var18--) {
            if (!this.ignore((char)var1[var18 - 1])) {
               var17++;
            }
         }

         var17 = this.nextI(var1, var2, var18);

         while (var17 < var18) {
            byte var5 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var6 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var7 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var8 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var9 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var10 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var11 = this.decodingTable[var1[var17++]];
            var17 = this.nextI(var1, var17, var18);
            byte var12 = this.decodingTable[var1[var17++]];
            if ((var5 | var6 | var7 | var8 | var9 | var10 | var11 | var12) < 0) {
               throw new IOException("invalid characters encountered in base32 data");
            }

            var13[var14++] = (byte)(var5 << 3 | var6 >> 2);
            var13[var14++] = (byte)(var6 << 6 | var7 << 1 | var8 >> 4);
            var13[var14++] = (byte)(var8 << 4 | var9 >> 1);
            var13[var14++] = (byte)(var9 << 7 | var10 << 2 | var11 >> 3);
            var13[var14++] = (byte)(var11 << 5 | var12);
            if (var14 == var13.length) {
               var4.write(var13);
               var14 = 0;
            }

            var15 += 5;
            var17 = this.nextI(var1, var17, var18);
         }

         if (var14 > 0) {
            var4.write(var13, 0, var14);
         }

         int var19 = this.nextI(var1, var17, var16);
         int var20 = this.nextI(var1, var19 + 1, var16);
         int var21 = this.nextI(var1, var20 + 1, var16);
         int var22 = this.nextI(var1, var21 + 1, var16);
         int var23 = this.nextI(var1, var22 + 1, var16);
         int var24 = this.nextI(var1, var23 + 1, var16);
         int var25 = this.nextI(var1, var24 + 1, var16);
         int var26 = this.nextI(var1, var25 + 1, var16);
         return var15
            + this.decodeLastBlock(
               var4,
               (char)var1[var19],
               (char)var1[var20],
               (char)var1[var21],
               (char)var1[var22],
               (char)var1[var23],
               (char)var1[var24],
               (char)var1[var25],
               (char)var1[var26]
            );
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
      byte[] var3 = Strings.toByteArray(var1);
      return this.decode(var3, 0, var3.length, var2);
   }

   private int decodeLastBlock(OutputStream var1, char var2, char var3, char var4, char var5, char var6, char var7, char var8, char var9) throws IOException {
      if (var9 == this.padding) {
         if (var8 != this.padding) {
            byte var21 = this.decodingTable[var2];
            byte var25 = this.decodingTable[var3];
            byte var28 = this.decodingTable[var4];
            byte var31 = this.decodingTable[var5];
            byte var33 = this.decodingTable[var6];
            byte var34 = this.decodingTable[var7];
            byte var35 = this.decodingTable[var8];
            if ((var21 | var25 | var28 | var31 | var33 | var34 | var35) < 0) {
               throw new IOException("invalid characters encountered at end of base32 data");
            } else {
               var1.write(var21 << 3 | var25 >> 2);
               var1.write(var25 << 6 | var28 << 1 | var31 >> 4);
               var1.write(var31 << 4 | var33 >> 1);
               var1.write(var33 << 7 | var34 << 2 | var35 >> 3);
               return 4;
            }
         } else if (var7 != this.padding) {
            throw new IOException("invalid characters encountered at end of base32 data");
         } else if (var6 != this.padding) {
            byte var20 = this.decodingTable[var2];
            byte var24 = this.decodingTable[var3];
            byte var27 = this.decodingTable[var4];
            byte var30 = this.decodingTable[var5];
            byte var32 = this.decodingTable[var6];
            if ((var20 | var24 | var27 | var30 | var32) < 0) {
               throw new IOException("invalid characters encountered at end of base32 data");
            } else {
               var1.write(var20 << 3 | var24 >> 2);
               var1.write(var24 << 6 | var27 << 1 | var30 >> 4);
               var1.write(var30 << 4 | var32 >> 1);
               return 3;
            }
         } else if (var5 != this.padding) {
            byte var19 = this.decodingTable[var2];
            byte var23 = this.decodingTable[var3];
            byte var26 = this.decodingTable[var4];
            byte var29 = this.decodingTable[var5];
            if ((var19 | var23 | var26 | var29) < 0) {
               throw new IOException("invalid characters encountered at end of base32 data");
            } else {
               var1.write(var19 << 3 | var23 >> 2);
               var1.write(var23 << 6 | var26 << 1 | var29 >> 4);
               return 2;
            }
         } else if (var4 != this.padding) {
            throw new IOException("invalid characters encountered at end of base32 data");
         } else {
            byte var18 = this.decodingTable[var2];
            byte var22 = this.decodingTable[var3];
            if ((var18 | var22) < 0) {
               throw new IOException("invalid characters encountered at end of base32 data");
            } else {
               var1.write(var18 << 3 | var22 >> 2);
               return 1;
            }
         }
      } else {
         byte var10 = this.decodingTable[var2];
         byte var11 = this.decodingTable[var3];
         byte var12 = this.decodingTable[var4];
         byte var13 = this.decodingTable[var5];
         byte var14 = this.decodingTable[var6];
         byte var15 = this.decodingTable[var7];
         byte var16 = this.decodingTable[var8];
         byte var17 = this.decodingTable[var9];
         if ((var10 | var11 | var12 | var13 | var14 | var15 | var16 | var17) < 0) {
            throw new IOException("invalid characters encountered at end of base32 data");
         } else {
            var1.write(var10 << 3 | var11 >> 2);
            var1.write(var11 << 6 | var12 << 1 | var13 >> 4);
            var1.write(var13 << 4 | var14 >> 1);
            var1.write(var14 << 7 | var15 << 2 | var16 >> 3);
            var1.write(var16 << 5 | var17);
            return 5;
         }
      }
   }
}
