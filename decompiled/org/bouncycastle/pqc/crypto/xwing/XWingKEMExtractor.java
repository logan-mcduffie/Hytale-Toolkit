package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.util.Arrays;

public class XWingKEMExtractor implements EncapsulatedSecretExtractor {
   private static final int MLKEM_CIPHERTEXT_SIZE = 1088;
   private final XWingPrivateKeyParameters key;
   private final MLKEMExtractor mlkemExtractor;

   public XWingKEMExtractor(XWingPrivateKeyParameters var1) {
      this.key = var1;
      this.mlkemExtractor = new MLKEMExtractor(this.key.getKyberPrivateKey());
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = Arrays.copyOfRange(var1, 0, 1088);
      byte[] var3 = Arrays.copyOfRange(var1, 1088, var1.length);
      byte[] var4 = XWingKEMGenerator.computeSSX(new X25519PublicKeyParameters(var3, 0), this.key.getXDHPrivateKey());
      byte[] var5 = XWingKEMGenerator.computeSharedSecret(this.key.getXDHPublicKey().getEncoded(), this.mlkemExtractor.extractSecret(var2), var3, var4);
      Arrays.clear(var4);
      return var5;
   }

   @Override
   public int getEncapsulationLength() {
      return this.mlkemExtractor.getEncapsulationLength() + 32;
   }
}
