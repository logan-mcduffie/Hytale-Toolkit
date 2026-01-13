package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KXTSBlockCipher extends DefaultBufferedBlockCipher {
   private static final long RED_POLY_128 = 135L;
   private static final long RED_POLY_256 = 1061L;
   private static final long RED_POLY_512 = 293L;
   private final int blockSize;
   private final long reductionPolynomial;
   private final long[] tw_init;
   private final long[] tw_current;
   private int counter;

   protected static long getReductionPolynomial(int var0) {
      switch (var0) {
         case 16:
            return 135L;
         case 32:
            return 1061L;
         case 64:
            return 293L;
         default:
            throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
      }
   }

   public KXTSBlockCipher(BlockCipher var1) {
      this.cipher = var1;
      this.blockSize = var1.getBlockSize();
      this.reductionPolynomial = getReductionPolynomial(this.blockSize);
      this.tw_init = new long[this.blockSize >>> 3];
      this.tw_current = new long[this.blockSize >>> 3];
      this.counter = -1;
   }

   @Override
   public int getOutputSize(int var1) {
      return var1;
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      return var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException("Invalid parameters passed");
      } else {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         var2 = var3.getParameters();
         byte[] var4 = var3.getIV();
         if (var4.length != this.blockSize) {
            throw new IllegalArgumentException("Currently only support IVs of exactly one block");
         } else {
            byte[] var5 = new byte[this.blockSize];
            System.arraycopy(var4, 0, var5, 0, this.blockSize);
            this.cipher.init(true, var2);
            this.cipher.processBlock(var5, 0, var5, 0);
            this.cipher.init(var1, var2);
            Pack.littleEndianToLong(var5, 0, this.tw_init);
            System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
            this.counter = 0;
         }
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) {
      throw new IllegalStateException("unsupported operation");
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (var1.length - var2 < var3) {
         throw new DataLengthException("Input buffer too short");
      } else if (var4.length - var2 < var3) {
         throw new OutputLengthException("Output buffer too short");
      } else if (var3 % this.blockSize != 0) {
         throw new IllegalArgumentException("Partial blocks not supported");
      } else {
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, var3)) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         for (int var6 = 0; var6 < var3; var6 += this.blockSize) {
            this.processBlock(var1, var2 + var6, var4, var5 + var6);
         }

         return var3;
      }
   }

   private void processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.counter == -1) {
         throw new IllegalStateException("Attempt to process too many blocks");
      } else {
         this.counter++;
         GF_double(this.reductionPolynomial, this.tw_current);
         byte[] var5 = new byte[this.blockSize];
         Pack.longToLittleEndian(this.tw_current, var5, 0);
         byte[] var6 = new byte[this.blockSize];
         System.arraycopy(var5, 0, var6, 0, this.blockSize);

         for (int var7 = 0; var7 < this.blockSize; var7++) {
            var6[var7] ^= var1[var2 + var7];
         }

         this.cipher.processBlock(var6, 0, var6, 0);

         for (int var8 = 0; var8 < this.blockSize; var8++) {
            var3[var4 + var8] = (byte)(var6[var8] ^ var5[var8]);
         }
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.reset();
      return 0;
   }

   @Override
   public void reset() {
      this.cipher.reset();
      System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
      this.counter = 0;
   }

   private static void GF_double(long var0, long[] var2) {
      long var3 = 0L;

      for (int var5 = 0; var5 < var2.length; var5++) {
         long var6 = var2[var5];
         long var8 = var6 >>> 63;
         var2[var5] = var6 << 1 ^ var3;
         var3 = var8;
      }

      var2[0] ^= var0 & -var3;
   }
}
