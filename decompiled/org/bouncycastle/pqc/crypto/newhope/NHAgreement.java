package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.CipherParameters;

public class NHAgreement {
   private NHPrivateKeyParameters privKey;

   public void init(CipherParameters var1) {
      this.privKey = (NHPrivateKeyParameters)var1;
   }

   public byte[] calculateAgreement(CipherParameters var1) {
      NHPublicKeyParameters var2 = (NHPublicKeyParameters)var1;
      byte[] var3 = new byte[32];
      NewHope.sharedA(var3, this.privKey.secData, var2.pubData);
      return var3;
   }
}
