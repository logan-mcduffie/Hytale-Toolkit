package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class RainbowKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private RainbowKeyComputation rkc;
   private Version version;

   private void initialize(KeyGenerationParameters var1) {
      RainbowParameters var2 = ((RainbowKeyGenerationParameters)var1).getParameters();
      this.rkc = new RainbowKeyComputation(var2, var1.getRandom());
      this.version = var2.getVersion();
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.initialize(var1);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      switch (this.version) {
         case CLASSIC:
            return this.rkc.genKeyPairClassical();
         case CIRCUMZENITHAL:
            return this.rkc.genKeyPairCircumzenithal();
         case COMPRESSED:
            return this.rkc.genKeyPairCompressed();
         default:
            throw new IllegalArgumentException("No valid version. Please choose one of the following: classic, circumzenithal, compressed");
      }
   }
}
