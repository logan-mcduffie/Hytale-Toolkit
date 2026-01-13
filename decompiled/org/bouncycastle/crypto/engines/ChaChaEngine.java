package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class ChaChaEngine extends Salsa20Engine {
   public ChaChaEngine() {
   }

   public ChaChaEngine(int var1) {
      super(var1);
   }

   @Override
   public String getAlgorithmName() {
      return "ChaCha" + this.rounds;
   }

   @Override
   protected void advanceCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 > 0) {
         this.engineState[13] = this.engineState[13] + var3;
      }

      int var5 = this.engineState[12];
      this.engineState[12] = this.engineState[12] + var4;
      if (var5 != 0 && this.engineState[12] < var5) {
         this.engineState[13]++;
      }
   }

   @Override
   protected void advanceCounter() {
      if (++this.engineState[12] == 0) {
         this.engineState[13]++;
      }
   }

   @Override
   protected void retreatCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 != 0) {
         if ((this.engineState[13] & 4294967295L) < (var3 & 4294967295L)) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
         }

         this.engineState[13] = this.engineState[13] - var3;
      }

      if ((this.engineState[12] & 4294967295L) >= (var4 & 4294967295L)) {
         this.engineState[12] = this.engineState[12] - var4;
      } else {
         if (this.engineState[13] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
         }

         this.engineState[13]--;
         this.engineState[12] = this.engineState[12] - var4;
      }
   }

   @Override
   protected void retreatCounter() {
      if (this.engineState[12] == 0 && this.engineState[13] == 0) {
         throw new IllegalStateException("attempt to reduce counter past zero.");
      } else {
         if (--this.engineState[12] == -1) {
            this.engineState[13]--;
         }
      }
   }

   @Override
   protected long getCounter() {
      return (long)this.engineState[13] << 32 | this.engineState[12] & 4294967295L;
   }

   @Override
   protected void resetCounter() {
      this.engineState[12] = this.engineState[13] = 0;
   }

   @Override
   protected void setKey(byte[] var1, byte[] var2) {
      if (var1 != null) {
         if (var1.length != 16 && var1.length != 32) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
         }

         this.packTauOrSigma(var1.length, this.engineState, 0);
         Pack.littleEndianToInt(var1, 0, this.engineState, 4, 4);
         Pack.littleEndianToInt(var1, var1.length - 16, this.engineState, 8, 4);
      }

      Pack.littleEndianToInt(var2, 0, this.engineState, 14, 2);
   }

   @Override
   protected void generateKeyStream(byte[] var1) {
      chachaCore(this.rounds, this.engineState, this.x);
      Pack.intToLittleEndian(this.x, var1, 0);
   }

   public static void chachaCore(int var0, int[] var1, int[] var2) {
      if (var1.length != 16) {
         throw new IllegalArgumentException();
      } else if (var2.length != 16) {
         throw new IllegalArgumentException();
      } else if (var0 % 2 != 0) {
         throw new IllegalArgumentException("Number of rounds must be even");
      } else {
         int var3 = var1[0];
         int var4 = var1[1];
         int var5 = var1[2];
         int var6 = var1[3];
         int var7 = var1[4];
         int var8 = var1[5];
         int var9 = var1[6];
         int var10 = var1[7];
         int var11 = var1[8];
         int var12 = var1[9];
         int var13 = var1[10];
         int var14 = var1[11];
         int var15 = var1[12];
         int var16 = var1[13];
         int var17 = var1[14];
         int var18 = var1[15];

         for (int var19 = var0; var19 > 0; var19 -= 2) {
            int var20 = var3 + var7;
            int var56 = Integers.rotateLeft(var15 ^ var20, 16);
            int var44 = var11 + var56;
            var7 = Integers.rotateLeft(var7 ^ var44, 12);
            int var21 = var20 + var7;
            int var57 = Integers.rotateLeft(var56 ^ var21, 8);
            int var45 = var44 + var57;
            var7 = Integers.rotateLeft(var7 ^ var45, 7);
            int var23 = var4 + var8;
            int var59 = Integers.rotateLeft(var16 ^ var23, 16);
            int var47 = var12 + var59;
            var8 = Integers.rotateLeft(var8 ^ var47, 12);
            int var24 = var23 + var8;
            int var60 = Integers.rotateLeft(var59 ^ var24, 8);
            int var48 = var47 + var60;
            var8 = Integers.rotateLeft(var8 ^ var48, 7);
            int var26 = var5 + var9;
            int var62 = Integers.rotateLeft(var17 ^ var26, 16);
            int var50 = var13 + var62;
            var9 = Integers.rotateLeft(var9 ^ var50, 12);
            int var27 = var26 + var9;
            int var63 = Integers.rotateLeft(var62 ^ var27, 8);
            int var51 = var50 + var63;
            var9 = Integers.rotateLeft(var9 ^ var51, 7);
            int var29 = var6 + var10;
            int var65 = Integers.rotateLeft(var18 ^ var29, 16);
            int var53 = var14 + var65;
            var10 = Integers.rotateLeft(var10 ^ var53, 12);
            int var30 = var29 + var10;
            int var66 = Integers.rotateLeft(var65 ^ var30, 8);
            int var54 = var53 + var66;
            var10 = Integers.rotateLeft(var10 ^ var54, 7);
            int var22 = var21 + var8;
            int var67 = Integers.rotateLeft(var66 ^ var22, 16);
            int var52 = var51 + var67;
            var8 = Integers.rotateLeft(var8 ^ var52, 12);
            var3 = var22 + var8;
            var18 = Integers.rotateLeft(var67 ^ var3, 8);
            var13 = var52 + var18;
            var8 = Integers.rotateLeft(var8 ^ var13, 7);
            int var25 = var24 + var9;
            int var58 = Integers.rotateLeft(var57 ^ var25, 16);
            int var55 = var54 + var58;
            var9 = Integers.rotateLeft(var9 ^ var55, 12);
            var4 = var25 + var9;
            var15 = Integers.rotateLeft(var58 ^ var4, 8);
            var14 = var55 + var15;
            var9 = Integers.rotateLeft(var9 ^ var14, 7);
            int var28 = var27 + var10;
            int var61 = Integers.rotateLeft(var60 ^ var28, 16);
            int var46 = var45 + var61;
            var10 = Integers.rotateLeft(var10 ^ var46, 12);
            var5 = var28 + var10;
            var16 = Integers.rotateLeft(var61 ^ var5, 8);
            var11 = var46 + var16;
            var10 = Integers.rotateLeft(var10 ^ var11, 7);
            int var31 = var30 + var7;
            int var64 = Integers.rotateLeft(var63 ^ var31, 16);
            int var49 = var48 + var64;
            var7 = Integers.rotateLeft(var7 ^ var49, 12);
            var6 = var31 + var7;
            var17 = Integers.rotateLeft(var64 ^ var6, 8);
            var12 = var49 + var17;
            var7 = Integers.rotateLeft(var7 ^ var12, 7);
         }

         var2[0] = var3 + var1[0];
         var2[1] = var4 + var1[1];
         var2[2] = var5 + var1[2];
         var2[3] = var6 + var1[3];
         var2[4] = var7 + var1[4];
         var2[5] = var8 + var1[5];
         var2[6] = var9 + var1[6];
         var2[7] = var10 + var1[7];
         var2[8] = var11 + var1[8];
         var2[9] = var12 + var1[9];
         var2[10] = var13 + var1[10];
         var2[11] = var14 + var1[11];
         var2[12] = var15 + var1[12];
         var2[13] = var16 + var1[13];
         var2[14] = var17 + var1[14];
         var2[15] = var18 + var1[15];
      }
   }
}
