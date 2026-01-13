package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

public class BIKEKEMExtractor implements EncapsulatedSecretExtractor {
   private BIKEEngine engine;
   private BIKEKeyParameters key;

   public BIKEKEMExtractor(BIKEPrivateKeyParameters var1) {
      this.key = var1;
      this.initCipher(this.key.getParameters());
   }

   private void initCipher(BIKEParameters var1) {
      this.engine = var1.getEngine();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[this.engine.getSessionKeySize()];
      BIKEPrivateKeyParameters var3 = (BIKEPrivateKeyParameters)this.key;
      byte[] var4 = Arrays.copyOfRange(var1, 0, var3.getParameters().getRByte());
      byte[] var5 = Arrays.copyOfRange(var1, var3.getParameters().getRByte(), var1.length);
      byte[] var6 = var3.getH0();
      byte[] var7 = var3.getH1();
      byte[] var8 = var3.getSigma();
      this.engine.decaps(var2, var6, var7, var8, var4, var5);
      return Arrays.copyOfRange(var2, 0, this.key.getParameters().getSessionKeySize() / 8);
   }

   @Override
   public int getEncapsulationLength() {
      return this.key.getParameters().getRByte() + this.key.getParameters().getLByte();
   }
}
