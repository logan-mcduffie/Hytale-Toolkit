package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

public class CMCEKEMExtractor implements EncapsulatedSecretExtractor {
   private CMCEEngine engine;
   private CMCEKeyParameters key;

   public CMCEKEMExtractor(CMCEPrivateKeyParameters var1) {
      this.key = var1;
      this.initCipher(this.key.getParameters());
   }

   private void initCipher(CMCEParameters var1) {
      this.engine = var1.getEngine();
      CMCEPrivateKeyParameters var2 = (CMCEPrivateKeyParameters)this.key;
      if (var2.getPrivateKey().length < this.engine.getPrivateKeySize()) {
         this.key = new CMCEPrivateKeyParameters(var2.getParameters(), this.engine.decompress_private_key(var2.getPrivateKey()));
      }
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      return this.extractSecret(var1, this.engine.getDefaultSessionKeySize());
   }

   public byte[] extractSecret(byte[] var1, int var2) {
      byte[] var3 = new byte[var2 / 8];
      this.engine.kem_dec(var3, var1, ((CMCEPrivateKeyParameters)this.key).getPrivateKey());
      return var3;
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCipherTextSize();
   }
}
