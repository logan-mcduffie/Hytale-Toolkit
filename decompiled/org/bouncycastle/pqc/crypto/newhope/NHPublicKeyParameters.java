package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public class NHPublicKeyParameters extends AsymmetricKeyParameter {
   final byte[] pubData;

   public NHPublicKeyParameters(byte[] var1) {
      super(false);
      this.pubData = Arrays.clone(var1);
   }

   public byte[] getPubData() {
      return Arrays.clone(this.pubData);
   }
}
