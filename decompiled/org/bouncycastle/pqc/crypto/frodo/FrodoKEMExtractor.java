package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

public class FrodoKEMExtractor implements EncapsulatedSecretExtractor {
   private FrodoEngine engine;
   private FrodoKeyParameters key;

   public FrodoKEMExtractor(FrodoKeyParameters var1) {
      this.key = var1;
      this.initCipher(this.key.getParameters());
   }

   private void initCipher(FrodoParameters var1) {
      this.engine = var1.getEngine();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[this.engine.getSessionKeySize()];
      this.engine.kem_dec(var2, var1, ((FrodoPrivateKeyParameters)this.key).getPrivateKey());
      return var2;
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCipherTextSize();
   }
}
