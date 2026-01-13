package org.bouncycastle.mime.encoding;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class Base64InputStream extends InputStream {
   private static final byte[] decodingTable = new byte[128];
   InputStream in;
   int[] outBuf = new int[3];
   int bufPtr = 3;

   private int decode(int var1, int var2, int var3, int var4, int[] var5) throws EOFException {
      if (var4 < 0) {
         throw new EOFException("unexpected end of file in armored stream.");
      } else if (var3 == 61) {
         int var11 = decodingTable[var1] & 255;
         int var13 = decodingTable[var2] & 255;
         var5[2] = (var11 << 2 | var13 >> 4) & 0xFF;
         return 2;
      } else if (var4 == 61) {
         byte var10 = decodingTable[var1];
         byte var12 = decodingTable[var2];
         byte var14 = decodingTable[var3];
         var5[1] = (var10 << 2 | var12 >> 4) & 0xFF;
         var5[2] = (var12 << 4 | var14 >> 2) & 0xFF;
         return 1;
      } else {
         byte var6 = decodingTable[var1];
         byte var7 = decodingTable[var2];
         byte var8 = decodingTable[var3];
         byte var9 = decodingTable[var4];
         var5[0] = (var6 << 2 | var7 >> 4) & 0xFF;
         var5[1] = (var7 << 4 | var8 >> 2) & 0xFF;
         var5[2] = (var8 << 6 | var9) & 0xFF;
         return 0;
      }
   }

   public Base64InputStream(InputStream var1) {
      this.in = var1;
   }

   @Override
   public int available() throws IOException {
      return 0;
   }

   @Override
   public int read() throws IOException {
      if (this.bufPtr > 2) {
         int var1 = this.readIgnoreSpaceFirst();
         if (var1 < 0) {
            return -1;
         }

         int var2 = this.readIgnoreSpace();
         int var3 = this.readIgnoreSpace();
         int var4 = this.readIgnoreSpace();
         this.bufPtr = this.decode(var1, var2, var3, var4, this.outBuf);
      }

      return this.outBuf[this.bufPtr++];
   }

   @Override
   public void close() throws IOException {
      this.in.close();
   }

   private int readIgnoreSpace() throws IOException {
      while (true) {
         int var1;
         switch (var1 = this.in.read()) {
            case 9:
            case 32:
               break;
            default:
               return var1;
         }
      }
   }

   private int readIgnoreSpaceFirst() throws IOException {
      while (true) {
         int var1;
         switch (var1 = this.in.read()) {
            case 9:
            case 10:
            case 13:
            case 32:
               break;
            default:
               return var1;
         }
      }
   }

   static {
      for (int var0 = 65; var0 <= 90; var0++) {
         decodingTable[var0] = (byte)(var0 - 65);
      }

      for (int var1 = 97; var1 <= 122; var1++) {
         decodingTable[var1] = (byte)(var1 - 97 + 26);
      }

      for (int var2 = 48; var2 <= 57; var2++) {
         decodingTable[var2] = (byte)(var2 - 48 + 52);
      }

      decodingTable[43] = 62;
      decodingTable[47] = 63;
   }
}
