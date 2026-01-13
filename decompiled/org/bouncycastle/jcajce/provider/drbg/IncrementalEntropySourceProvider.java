package org.bouncycastle.jcajce.provider.drbg;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

class IncrementalEntropySourceProvider implements EntropySourceProvider {
   private final SecureRandom random;
   private final boolean predictionResistant;

   public IncrementalEntropySourceProvider(SecureRandom var1, boolean var2) {
      this.random = var1;
      this.predictionResistant = var2;
   }

   @Override
   public EntropySource get(final int var1) {
      return new IncrementalEntropySource() {
         final int numBytes = (var1 + 7) / 8;

         @Override
         public boolean isPredictionResistant() {
            return IncrementalEntropySourceProvider.this.predictionResistant;
         }

         @Override
         public byte[] getEntropy() {
            try {
               return this.getEntropy(0L);
            } catch (InterruptedException var2) {
               Thread.currentThread().interrupt();
               throw new IllegalStateException("initial entropy fetch interrupted");
            }
         }

         @Override
         public byte[] getEntropy(long var1x) throws InterruptedException {
            byte[] var3 = new byte[this.numBytes];

            for (int var4 = 0; var4 < this.numBytes / 8; var4++) {
               IncrementalEntropySourceProvider.sleep(var1x);
               byte[] var5 = IncrementalEntropySourceProvider.this.random.generateSeed(8);
               System.arraycopy(var5, 0, var3, var4 * 8, var5.length);
            }

            int var6 = this.numBytes - this.numBytes / 8 * 8;
            if (var6 != 0) {
               IncrementalEntropySourceProvider.sleep(var1x);
               byte[] var7 = IncrementalEntropySourceProvider.this.random.generateSeed(var6);
               System.arraycopy(var7, 0, var3, var3.length - var7.length, var7.length);
            }

            return var3;
         }

         @Override
         public int entropySize() {
            return var1;
         }
      };
   }

   private static void sleep(long var0) throws InterruptedException {
      if (var0 != 0L) {
         Thread.sleep(var0);
      }
   }
}
