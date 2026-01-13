package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;

public class BasicEntropySourceProvider implements EntropySourceProvider {
   private final SecureRandom _sr;
   private final boolean _predictionResistant;

   public BasicEntropySourceProvider(SecureRandom var1, boolean var2) {
      this._sr = var1;
      this._predictionResistant = var2;
   }

   @Override
   public EntropySource get(final int var1) {
      return new EntropySource() {
         @Override
         public boolean isPredictionResistant() {
            return BasicEntropySourceProvider.this._predictionResistant;
         }

         @Override
         public byte[] getEntropy() {
            if (!(BasicEntropySourceProvider.this._sr instanceof SP800SecureRandom) && !(BasicEntropySourceProvider.this._sr instanceof X931SecureRandom)) {
               return BasicEntropySourceProvider.this._sr.generateSeed((var1 + 7) / 8);
            } else {
               byte[] var1x = new byte[(var1 + 7) / 8];
               BasicEntropySourceProvider.this._sr.nextBytes(var1x);
               return var1x;
            }
         }

         @Override
         public int entropySize() {
            return var1;
         }
      };
   }
}
