package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class IDEAEngine implements BlockCipher {
   protected static final int BLOCK_SIZE = 8;
   private int[] workingKey = null;
   private boolean forEncryption;
   private static final int MASK = 65535;
   private static final int BASE = 65537;

   public IDEAEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         byte[] var3 = ((KeyParameter)var2).getKey();
         this.workingKey = this.generateWorkingKey(var1, var3);
         this.forEncryption = var1;
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var3.length * 8, var2, Utils.getPurpose(var1)));
      } else {
         throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "IDEA";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.workingKey == null) {
         throw new IllegalStateException("IDEA engine not initialised");
      } else if (var2 + 8 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 8 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.ideaFunc(this.workingKey, var1, var2, var3, var4);
         return 8;
      }
   }

   @Override
   public void reset() {
   }

   private int bytesToWord(byte[] var1, int var2) {
      return (var1[var2] << 8 & 0xFF00) + (var1[var2 + 1] & 0xFF);
   }

   private void wordToBytes(int var1, byte[] var2, int var3) {
      var2[var3] = (byte)(var1 >>> 8);
      var2[var3 + 1] = (byte)var1;
   }

   private int mul(int var1, int var2) {
      if (var1 == 0) {
         var1 = 65537 - var2;
      } else if (var2 == 0) {
         var1 = 65537 - var1;
      } else {
         int var3 = var1 * var2;
         var2 = var3 & 65535;
         var1 = var3 >>> 16;
         var1 = var2 - var1 + (var2 < var1 ? 1 : 0);
      }

      return var1 & 65535;
   }

   private void ideaFunc(int[] var1, byte[] var2, int var3, byte[] var4, int var5) {
      int var12 = 0;
      int var6 = this.bytesToWord(var2, var3);
      int var7 = this.bytesToWord(var2, var3 + 2);
      int var8 = this.bytesToWord(var2, var3 + 4);
      int var9 = this.bytesToWord(var2, var3 + 6);

      for (int var13 = 0; var13 < 8; var13++) {
         var6 = this.mul(var6, var1[var12++]);
         var7 += var1[var12++];
         var7 &= 65535;
         var8 += var1[var12++];
         var8 &= 65535;
         var9 = this.mul(var9, var1[var12++]);
         int var23 = var8 ^ var6;
         int var17 = var7 ^ var9;
         int var24 = this.mul(var23, var1[var12++]);
         int var18 = var17 + var24;
         int var19 = var18 & 65535;
         int var20 = this.mul(var19, var1[var12++]);
         int var25 = var24 + var20;
         int var26 = var25 & 65535;
         var6 ^= var20;
         var9 ^= var26;
         var7 = var20 ^ var8;
         var8 = var26 ^ var7;
      }

      this.wordToBytes(this.mul(var6, var1[var12++]), var4, var5);
      this.wordToBytes(var8 + var1[var12++], var4, var5 + 2);
      this.wordToBytes(var7 + var1[var12++], var4, var5 + 4);
      this.wordToBytes(this.mul(var9, var1[var12]), var4, var5 + 6);
   }

   private int[] expandKey(byte[] var1) {
      int[] var2 = new int[52];
      if (var1.length < 16) {
         byte[] var3 = new byte[16];
         System.arraycopy(var1, 0, var3, var3.length - var1.length, var1.length);
         var1 = var3;
      }

      for (int var4 = 0; var4 < 8; var4++) {
         var2[var4] = this.bytesToWord(var1, var4 * 2);
      }

      for (int var5 = 8; var5 < 52; var5++) {
         if ((var5 & 7) < 6) {
            var2[var5] = ((var2[var5 - 7] & 127) << 9 | var2[var5 - 6] >> 7) & 65535;
         } else if ((var5 & 7) == 6) {
            var2[var5] = ((var2[var5 - 7] & 127) << 9 | var2[var5 - 14] >> 7) & 65535;
         } else {
            var2[var5] = ((var2[var5 - 15] & 127) << 9 | var2[var5 - 14] >> 7) & 65535;
         }
      }

      return var2;
   }

   private int mulInv(int var1) {
      if (var1 < 2) {
         return var1;
      } else {
         int var2 = 1;
         int var3 = 65537 / var1;
         int var5 = 65537 % var1;

         while (var5 != 1) {
            int var4 = var1 / var5;
            var1 %= var5;
            var2 = var2 + var3 * var4 & 65535;
            if (var1 == 1) {
               return var2;
            }

            var4 = var5 / var1;
            var5 %= var1;
            var3 = var3 + var2 * var4 & 65535;
         }

         return 1 - var3 & 65535;
      }
   }

   int addInv(int var1) {
      return 0 - var1 & 65535;
   }

   private int[] invertKey(int[] var1) {
      int var6 = 52;
      int[] var7 = new int[52];
      int var8 = 0;
      int var2 = this.mulInv(var1[var8++]);
      int var3 = this.addInv(var1[var8++]);
      int var4 = this.addInv(var1[var8++]);
      int var5 = this.mulInv(var1[var8++]);
      var7[--var6] = var5;
      var7[--var6] = var4;
      var7[--var6] = var3;
      var7[--var6] = var2;

      for (int var9 = 1; var9 < 8; var9++) {
         var2 = var1[var8++];
         var3 = var1[var8++];
         var7[--var6] = var3;
         var7[--var6] = var2;
         var2 = this.mulInv(var1[var8++]);
         var3 = this.addInv(var1[var8++]);
         var4 = this.addInv(var1[var8++]);
         var5 = this.mulInv(var1[var8++]);
         var7[--var6] = var5;
         var7[--var6] = var3;
         var7[--var6] = var4;
         var7[--var6] = var2;
      }

      var2 = var1[var8++];
      var3 = var1[var8++];
      var7[--var6] = var3;
      var7[--var6] = var2;
      var2 = this.mulInv(var1[var8++]);
      var3 = this.addInv(var1[var8++]);
      var4 = this.addInv(var1[var8++]);
      var5 = this.mulInv(var1[var8]);
      var7[--var6] = var5;
      var7[--var6] = var4;
      var7[--var6] = var3;
      var7[--var6] = var2;
      return var7;
   }

   private int[] generateWorkingKey(boolean var1, byte[] var2) {
      return var1 ? this.expandKey(var2) : this.invertKey(this.expandKey(var2));
   }
}
