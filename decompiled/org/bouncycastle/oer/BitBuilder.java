package org.bouncycastle.oer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class BitBuilder {
   private static final byte[] bits = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};
   byte[] buf = new byte[1];
   int pos = 0;

   public BitBuilder writeBit(int var1) {
      if (this.pos / 8 >= this.buf.length) {
         byte[] var2 = new byte[this.buf.length + 4];
         System.arraycopy(this.buf, 0, var2, 0, this.pos / 8);
         Arrays.clear(this.buf);
         this.buf = var2;
      }

      if (var1 == 0) {
         this.buf[this.pos / 8] = (byte)(this.buf[this.pos / 8] & ~bits[this.pos % 8]);
      } else {
         this.buf[this.pos / 8] = (byte)(this.buf[this.pos / 8] | bits[this.pos % 8]);
      }

      this.pos++;
      return this;
   }

   public BitBuilder writeBits(long var1, int var3) {
      for (int var4 = var3 - 1; var4 >= 0; var4--) {
         int var5 = (var1 & 1L << var4) > 0L ? 1 : 0;
         this.writeBit(var5);
      }

      return this;
   }

   public BitBuilder writeBits(long var1, int var3, int var4) {
      for (int var5 = var3 - 1; var5 >= var3 - var4; var5--) {
         int var6 = (var1 & 1L << var5) != 0L ? 1 : 0;
         this.writeBit(var6);
      }

      return this;
   }

   public int write(OutputStream var1) throws IOException {
      int var2 = (this.pos + this.pos % 8) / 8;
      var1.write(this.buf, 0, var2);
      var1.flush();
      return var2;
   }

   public int writeAndClear(OutputStream var1) throws IOException {
      int var2 = (this.pos + this.pos % 8) / 8;
      var1.write(this.buf, 0, var2);
      var1.flush();
      this.zero();
      return var2;
   }

   public void pad() {
      this.pos = this.pos + this.pos % 8;
   }

   public void write7BitBytes(int var1) {
      boolean var2 = false;

      for (int var3 = 4; var3 >= 0; var3--) {
         if (!var2 && (var1 & -33554432) != 0) {
            var2 = true;
         }

         if (var2) {
            this.writeBit(var3).writeBits(var1, 32, 7);
         }

         var1 <<= 7;
      }
   }

   public void write7BitBytes(BigInteger var1) {
      int var2 = (var1.bitLength() + var1.bitLength() % 8) / 8;
      BigInteger var3 = BigInteger.valueOf(254L).shiftLeft(var2 * 8);
      boolean var4 = false;

      for (int var5 = var2; var5 >= 0; var5--) {
         if (!var4 && var1.and(var3).compareTo(BigInteger.ZERO) != 0) {
            var4 = true;
         }

         if (var4) {
            BigInteger var6 = var1.and(var3).shiftRight(8 * var2 - 8);
            this.writeBit(var5).writeBits(var6.intValue(), 8, 7);
         }

         var1 = var1.shiftLeft(7);
      }
   }

   public void zero() {
      Arrays.clear(this.buf);
      this.pos = 0;
   }
}
