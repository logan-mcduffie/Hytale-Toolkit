package org.bouncycastle.util.test;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

public class TestRandomEntropySourceProvider implements EntropySourceProvider {
   private final SecureRandom _sr = new SecureRandom();
   private final boolean _predictionResistant;

   public TestRandomEntropySourceProvider(boolean var1) {
      this._predictionResistant = var1;
   }

   @Override
   public EntropySource get(final int var1) {
      return new EntropySource() {
         @Override
         public boolean isPredictionResistant() {
            return TestRandomEntropySourceProvider.this._predictionResistant;
         }

         @Override
         public byte[] getEntropy() {
            byte[] var1x = new byte[(var1 + 7) / 8];
            TestRandomEntropySourceProvider.this._sr.nextBytes(var1x);
            return var1x;
         }

         @Override
         public int entropySize() {
            return var1;
         }
      };
   }
}
