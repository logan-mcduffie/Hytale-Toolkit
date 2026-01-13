package org.bouncycastle.pqc.crypto.bike;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class BIKERing {
   private static final int PERMUTATION_CUTOFF = 64;
   private final int bits;
   private final int size;
   private final int sizeExt;
   private final Map<Integer, Integer> halfPowers = new HashMap<>();

   BIKERing(int var1) {
      if ((var1 & -65535) != 1) {
         throw new IllegalArgumentException();
      } else {
         this.bits = var1;
         this.size = var1 + 63 >>> 6;
         this.sizeExt = this.size * 2;
         generateHalfPowersInv(this.halfPowers, var1);
      }
   }

   void add(long[] var1, long[] var2, long[] var3) {
      for (int var4 = 0; var4 < this.size; var4++) {
         var3[var4] = var1[var4] ^ var2[var4];
      }
   }

   void addTo(long[] var1, long[] var2) {
      for (int var3 = 0; var3 < this.size; var3++) {
         var2[var3] ^= var1[var3];
      }
   }

   void copy(long[] var1, long[] var2) {
      for (int var3 = 0; var3 < this.size; var3++) {
         var2[var3] = var1[var3];
      }
   }

   long[] create() {
      return new long[this.size];
   }

   long[] createExt() {
      return new long[this.sizeExt];
   }

   void decodeBytes(byte[] var1, long[] var2) {
      int var3 = this.bits & 63;
      Pack.littleEndianToLong(var1, 0, var2, 0, this.size - 1);
      byte[] var4 = new byte[8];
      System.arraycopy(var1, this.size - 1 << 3, var4, 0, var3 + 7 >>> 3);
      var2[this.size - 1] = Pack.littleEndianToLong(var4, 0);
   }

   byte[] encodeBitsTransposed(long[] var1) {
      byte[] var2 = new byte[this.bits];
      var2[0] = (byte)(var1[0] & 1L);

      for (int var3 = 1; var3 < this.bits; var3++) {
         var2[this.bits - var3] = (byte)(var1[var3 >>> 6] >>> (var3 & 63) & 1L);
      }

      return var2;
   }

   void encodeBytes(long[] var1, byte[] var2) {
      int var3 = this.bits & 63;
      Pack.longToLittleEndian(var1, 0, this.size - 1, var2, 0);
      byte[] var4 = new byte[8];
      Pack.longToLittleEndian(var1[this.size - 1], var4, 0);
      System.arraycopy(var4, 0, var2, this.size - 1 << 3, var3 + 7 >>> 3);
   }

   void inv(long[] var1, long[] var2) {
      long[] var3 = this.create();
      long[] var4 = this.create();
      long[] var5 = this.create();
      this.copy(var1, var3);
      this.copy(var1, var5);
      int var6 = this.bits - 2;
      int var7 = 32 - Integers.numberOfLeadingZeros(var6);

      for (int var8 = 1; var8 < var7; var8++) {
         this.squareN(var3, 1 << var8 - 1, var4);
         this.multiply(var3, var4, var3);
         if ((var6 & 1 << var8) != 0) {
            int var9 = var6 & (1 << var8) - 1;
            this.squareN(var3, var9, var4);
            this.multiply(var5, var4, var5);
         }
      }

      this.square(var5, var2);
   }

   void multiply(long[] var1, long[] var2, long[] var3) {
      long[] var4 = this.createExt();
      this.implMultiplyAcc(var1, var2, var4);
      this.reduce(var4, var3);
   }

   void reduce(long[] var1, long[] var2) {
      int var3 = this.bits & 63;
      int var4 = 64 - var3;
      long var5 = -1L >>> var4;
      Nat.shiftUpBits64(this.size, var1, this.size, var4, var1[this.size - 1], var2, 0);
      this.addTo(var1, var2);
      var2[this.size - 1] = var2[this.size - 1] & var5;
   }

   int getSize() {
      return this.size;
   }

   int getSizeExt() {
      return this.sizeExt;
   }

   void square(long[] var1, long[] var2) {
      long[] var3 = this.createExt();
      this.implSquare(var1, var3);
      this.reduce(var3, var2);
   }

   void squareN(long[] var1, int var2, long[] var3) {
      if (var2 >= 64) {
         this.implPermute(var1, var2, var3);
      } else {
         long[] var4 = this.createExt();
         this.implSquare(var1, var4);
         this.reduce(var4, var3);

         while (--var2 > 0) {
            this.implSquare(var3, var4);
            this.reduce(var4, var3);
         }
      }
   }

   private static int implModAdd(int var0, int var1, int var2) {
      int var3 = var1 + var2 - var0;
      return var3 + (var3 >> 31 & var0);
   }

   protected void implMultiplyAcc(long[] var1, long[] var2, long[] var3) {
      long[] var4 = new long[16];

      for (int var5 = 0; var5 < this.size; var5++) {
         implMulwAcc(var4, var1[var5], var2[var5], var3, var5 << 1);
      }

      long var15 = var3[0];
      long var7 = var3[1];

      for (int var9 = 1; var9 < this.size; var9++) {
         var15 ^= var3[var9 << 1];
         var3[var9] = var15 ^ var7;
         var7 ^= var3[(var9 << 1) + 1];
      }

      long var16 = var15 ^ var7;

      for (int var11 = 0; var11 < this.size; var11++) {
         var3[this.size + var11] = var3[var11] ^ var16;
      }

      int var17 = this.size - 1;

      for (int var12 = 1; var12 < var17 * 2; var12++) {
         int var13 = Math.min(var17, var12);

         for (int var14 = var12 - var13; var14 < var13; var13--) {
            implMulwAcc(var4, var1[var14] ^ var1[var13], var2[var14] ^ var2[var13], var3, var12);
            var14++;
         }
      }
   }

   private void implPermute(long[] var1, int var2, long[] var3) {
      int var4 = this.bits;
      int var5 = this.halfPowers.get(Integers.valueOf(var2));
      int var6 = implModAdd(var4, var5, var5);
      int var7 = implModAdd(var4, var6, var6);
      int var8 = implModAdd(var4, var7, var7);
      int var9 = var4 - var8;
      int var10 = implModAdd(var4, var9, var5);
      int var11 = implModAdd(var4, var9, var6);
      int var12 = implModAdd(var4, var10, var6);
      int var13 = implModAdd(var4, var9, var7);
      int var14 = implModAdd(var4, var10, var7);
      int var15 = implModAdd(var4, var11, var7);
      int var16 = implModAdd(var4, var12, var7);

      for (int var17 = 0; var17 < this.size; var17++) {
         long var18 = 0L;

         for (byte var20 = 0; var20 < 64; var20 += 8) {
            var9 = implModAdd(var4, var9, var8);
            var10 = implModAdd(var4, var10, var8);
            var11 = implModAdd(var4, var11, var8);
            var12 = implModAdd(var4, var12, var8);
            var13 = implModAdd(var4, var13, var8);
            var14 = implModAdd(var4, var14, var8);
            var15 = implModAdd(var4, var15, var8);
            var16 = implModAdd(var4, var16, var8);
            var18 |= (var1[var9 >>> 6] >>> var9 & 1L) << var20 + 0;
            var18 |= (var1[var10 >>> 6] >>> var10 & 1L) << var20 + 1;
            var18 |= (var1[var11 >>> 6] >>> var11 & 1L) << var20 + 2;
            var18 |= (var1[var12 >>> 6] >>> var12 & 1L) << var20 + 3;
            var18 |= (var1[var13 >>> 6] >>> var13 & 1L) << var20 + 4;
            var18 |= (var1[var14 >>> 6] >>> var14 & 1L) << var20 + 5;
            var18 |= (var1[var15 >>> 6] >>> var15 & 1L) << var20 + 6;
            var18 |= (var1[var16 >>> 6] >>> var16 & 1L) << var20 + 7;
         }

         var3[var17] = var18;
      }

      var3[this.size - 1] = var3[this.size - 1] & -1L >>> -var4;
   }

   private static int generateHalfPower(int var0, int var1, int var2) {
      int var3 = 1;

      int var4;
      for (var4 = var2; var4 >= 32; var4 -= 32) {
         int var5 = var1 * var3;
         long var6 = (var5 & 4294967295L) * var0;
         long var8 = var6 + var3;
         var3 = (int)(var8 >>> 32);
      }

      if (var4 > 0) {
         int var11 = -1 >>> -var4;
         int var12 = var1 * var3 & var11;
         long var7 = (var12 & 4294967295L) * var0;
         long var9 = var7 + var3;
         var3 = (int)(var9 >>> var4);
      }

      return var3;
   }

   private static void generateHalfPowersInv(Map<Integer, Integer> var0, int var1) {
      int var2 = var1 - 2;
      int var3 = 32 - Integers.numberOfLeadingZeros(var2);
      int var4 = Mod.inverse32(-var1);

      for (int var5 = 1; var5 < var3; var5++) {
         int var6 = 1 << var5 - 1;
         if (var6 >= 64 && !var0.containsKey(Integers.valueOf(var6))) {
            var0.put(Integers.valueOf(var6), Integers.valueOf(generateHalfPower(var1, var4, var6)));
         }

         if ((var2 & 1 << var5) != 0) {
            int var7 = var2 & (1 << var5) - 1;
            if (var7 >= 64 && !var0.containsKey(Integers.valueOf(var7))) {
               var0.put(Integers.valueOf(var7), Integers.valueOf(generateHalfPower(var1, var4, var7)));
            }
         }
      }
   }

   private static void implMulwAcc(long[] var0, long var1, long var3, long[] var5, int var6) {
      var0[1] = var3;

      for (byte var7 = 2; var7 < 16; var7 += 2) {
         var0[var7] = var0[var7 >>> 1] << 1;
         var0[var7 + 1] = var0[var7] ^ var3;
      }

      int var16 = (int)var1;
      long var10 = 0L;
      long var12 = var0[var16 & 15] ^ var0[var16 >>> 4 & 15] << 4;
      byte var14 = 56;

      do {
         var16 = (int)(var1 >>> var14);
         long var8 = var0[var16 & 15] ^ var0[var16 >>> 4 & 15] << 4;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 8;
      } while (var14 > 0);

      for (int var15 = 0; var15 < 7; var15++) {
         var1 = (var1 & -72340172838076674L) >>> 1;
         var10 ^= var1 & var3 << var15 >> 63;
      }

      var5[var6] ^= var12;
      var5[var6 + 1] = var5[var6 + 1] ^ var10;
   }

   private void implSquare(long[] var1, long[] var2) {
      Interleave.expand64To128(var1, 0, this.size, var2, 0);
   }
}
