package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

public class HQCKEMExtractor implements EncapsulatedSecretExtractor {
   private HQCEngine engine;
   private final HQCKeyParameters key;

   public HQCKEMExtractor(HQCPrivateKeyParameters var1) {
      this.key = var1;
      this.initCipher(this.key.getParameters());
   }

   private void initCipher(HQCParameters var1) {
      this.engine = var1.getEngine();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[64];
      HQCPrivateKeyParameters var3 = (HQCPrivateKeyParameters)this.key;
      byte[] var4 = var3.getPrivateKey();
      this.engine.decaps(var2, var1, var4);
      return Arrays.copyOfRange(var2, 0, 32);
   }

   @Override
   public int getEncapsulationLength() {
      return this.key.getParameters().getN_BYTES() + this.key.getParameters().getN1N2_BYTES() + 16;
   }
}
