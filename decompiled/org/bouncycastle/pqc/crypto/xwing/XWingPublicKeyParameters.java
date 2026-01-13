package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingPublicKeyParameters extends XWingKeyParameters {
   private final MLKEMPublicKeyParameters kybPub;
   private final X25519PublicKeyParameters xdhPub;

   XWingPublicKeyParameters(AsymmetricKeyParameter var1, AsymmetricKeyParameter var2) {
      super(false);
      this.kybPub = (MLKEMPublicKeyParameters)var1;
      this.xdhPub = (X25519PublicKeyParameters)var2;
   }

   public XWingPublicKeyParameters(byte[] var1) {
      super(false);
      this.kybPub = new MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_768, Arrays.copyOfRange(var1, 0, var1.length - 32));
      this.xdhPub = new X25519PublicKeyParameters(var1, var1.length - 32);
   }

   MLKEMPublicKeyParameters getKyberPublicKey() {
      return this.kybPub;
   }

   X25519PublicKeyParameters getXDHPublicKey() {
      return this.xdhPub;
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(this.kybPub.getEncoded(), this.xdhPub.getEncoded());
   }
}
