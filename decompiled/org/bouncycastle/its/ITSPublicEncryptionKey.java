package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class ITSPublicEncryptionKey {
   protected final PublicEncryptionKey encryptionKey;

   public ITSPublicEncryptionKey(PublicEncryptionKey var1) {
      this.encryptionKey = var1;
   }

   public PublicEncryptionKey toASN1Structure() {
      return this.encryptionKey;
   }

   public static enum symmAlgorithm {
      aes128Ccm(SymmAlgorithm.aes128Ccm.intValueExact());

      private final int tagValue;

      private symmAlgorithm(int nullxx) {
         this.tagValue = nullxx;
      }
   }
}
