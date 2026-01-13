package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

public class SP800SecureRandom extends SecureRandom {
   private final DRBGProvider drbgProvider;
   private final boolean predictionResistant;
   private final SecureRandom randomSource;
   private final EntropySource entropySource;
   private SP80090DRBG drbg;

   SP800SecureRandom(SecureRandom var1, EntropySource var2, DRBGProvider var3, boolean var4) {
      this.randomSource = var1;
      this.entropySource = var2;
      this.drbgProvider = var3;
      this.predictionResistant = var4;
   }

   @Override
   public void setSeed(byte[] var1) {
      synchronized (this) {
         if (this.randomSource != null) {
            this.randomSource.setSeed(var1);
         }
      }
   }

   @Override
   public void setSeed(long var1) {
      synchronized (this) {
         if (this.randomSource != null) {
            this.randomSource.setSeed(var1);
         }
      }
   }

   @Override
   public String getAlgorithm() {
      return this.drbgProvider.getAlgorithm();
   }

   @Override
   public void nextBytes(byte[] var1) {
      synchronized (this) {
         if (this.drbg == null) {
            this.drbg = this.drbgProvider.get(this.entropySource);
         }

         if (this.drbg.generate(var1, null, this.predictionResistant) < 0) {
            this.drbg.reseed(null);
            this.drbg.generate(var1, null, this.predictionResistant);
         }
      }
   }

   @Override
   public byte[] generateSeed(int var1) {
      return EntropyUtil.generateSeed(this.entropySource, var1);
   }

   public void reseed(byte[] var1) {
      synchronized (this) {
         if (this.drbg == null) {
            this.drbg = this.drbgProvider.get(this.entropySource);
         }

         this.drbg.reseed(var1);
      }
   }
}
