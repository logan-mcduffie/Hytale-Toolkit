package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class ITSPublicVerificationKey {
   protected final PublicVerificationKey verificationKey;

   public ITSPublicVerificationKey(PublicVerificationKey var1) {
      this.verificationKey = var1;
   }

   public PublicVerificationKey toASN1Structure() {
      return this.verificationKey;
   }
}
