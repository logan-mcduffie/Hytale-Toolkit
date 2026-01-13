package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.BlockCipher;

public class X931RNG {
   private static final long BLOCK64_RESEED_MAX = 32768L;
   private static final long BLOCK128_RESEED_MAX = 8388608L;
   private static final int BLOCK64_MAX_BITS_REQUEST = 4096;
   private static final int BLOCK128_MAX_BITS_REQUEST = 262144;
   private final BlockCipher engine;
   private final EntropySource entropySource;
   private final byte[] DT;
   private final byte[] I;
   private final byte[] R;
   private byte[] V;
   private long reseedCounter = 1L;

   public X931RNG(BlockCipher var1, byte[] var2, EntropySource var3) {
      this.engine = var1;
      this.entropySource = var3;
      this.DT = new byte[var1.getBlockSize()];
      System.arraycopy(var2, 0, this.DT, 0, this.DT.length);
      this.I = new byte[var1.getBlockSize()];
      this.R = new byte[var1.getBlockSize()];
   }

   int generate(byte[] var1, boolean var2) {
      if (this.R.length == 8) {
         if (this.reseedCounter > 32768L) {
            return -1;
         }

         if (isTooLarge(var1, 512)) {
            throw new IllegalArgumentException("Number of bits per request limited to 4096");
         }
      } else {
         if (this.reseedCounter > 8388608L) {
            return -1;
         }

         if (isTooLarge(var1, 32768)) {
            throw new IllegalArgumentException("Number of bits per request limited to 262144");
         }
      }

      if (var2 || this.V == null) {
         this.V = this.entropySource.getEntropy();
         if (this.V.length != this.engine.getBlockSize()) {
            throw new IllegalStateException("Insufficient entropy returned");
         }
      }

      int var3 = var1.length / this.R.length;

      for (int var4 = 0; var4 < var3; var4++) {
         this.engine.processBlock(this.DT, 0, this.I, 0);
         this.process(this.R, this.I, this.V);
         this.process(this.V, this.R, this.I);
         System.arraycopy(this.R, 0, var1, var4 * this.R.length, this.R.length);
         this.increment(this.DT);
      }

      int var5 = var1.length - var3 * this.R.length;
      if (var5 > 0) {
         this.engine.processBlock(this.DT, 0, this.I, 0);
         this.process(this.R, this.I, this.V);
         this.process(this.V, this.R, this.I);
         System.arraycopy(this.R, 0, var1, var3 * this.R.length, var5);
         this.increment(this.DT);
      }

      this.reseedCounter++;
      return var1.length * 8;
   }

   void reseed() {
      this.V = this.entropySource.getEntropy();
      if (this.V.length != this.engine.getBlockSize()) {
         throw new IllegalStateException("Insufficient entropy returned");
      } else {
         this.reseedCounter = 1L;
      }
   }

   EntropySource getEntropySource() {
      return this.entropySource;
   }

   private void process(byte[] var1, byte[] var2, byte[] var3) {
      for (int var4 = 0; var4 != var1.length; var4++) {
         var1[var4] = (byte)(var2[var4] ^ var3[var4]);
      }

      this.engine.processBlock(var1, 0, var1, 0);
   }

   private void increment(byte[] var1) {
      int var2 = var1.length - 1;

      while (var2 >= 0 && ++var1[var2] == 0) {
         var2--;
      }
   }

   private static boolean isTooLarge(byte[] var0, int var1) {
      return var0 != null && var0.length > var1;
   }
}
