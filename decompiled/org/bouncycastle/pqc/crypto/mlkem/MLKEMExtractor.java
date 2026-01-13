package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

public class MLKEMExtractor implements EncapsulatedSecretExtractor {
   private final MLKEMPrivateKeyParameters privateKey;
   private final MLKEMEngine engine;

   public MLKEMExtractor(MLKEMPrivateKeyParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("'privateKey' cannot be null");
      } else {
         this.privateKey = var1;
         this.engine = var1.getParameters().getEngine();
      }
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      return this.engine.kemDecrypt(this.privateKey, var1);
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCryptoCipherTextBytes();
   }
}
