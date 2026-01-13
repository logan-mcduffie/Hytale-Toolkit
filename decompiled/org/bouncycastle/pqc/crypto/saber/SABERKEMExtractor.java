package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

public class SABERKEMExtractor implements EncapsulatedSecretExtractor {
   private SABEREngine engine;
   private SABERKeyParameters key;

   public SABERKEMExtractor(SABERKeyParameters var1) {
      this.key = var1;
      this.initCipher(this.key.getParameters());
   }

   private void initCipher(SABERParameters var1) {
      this.engine = var1.getEngine();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[this.engine.getSessionKeySize()];
      this.engine.crypto_kem_dec(var2, var1, ((SABERPrivateKeyParameters)this.key).getPrivateKey());
      return var2;
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCipherTextSize();
   }
}
