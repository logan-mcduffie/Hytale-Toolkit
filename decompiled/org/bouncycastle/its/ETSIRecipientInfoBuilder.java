package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class ETSIRecipientInfoBuilder {
   private final ETSIKeyWrapper keyWrapper;
   private final byte[] recipientID;

   public ETSIRecipientInfoBuilder(ETSIKeyWrapper var1, byte[] var2) {
      this.keyWrapper = var1;
      this.recipientID = var2;
   }

   public RecipientInfo build(byte[] var1) {
      try {
         return RecipientInfo.certRecipInfo(
            PKRecipientInfo.builder().setRecipientId(new HashedId8(this.recipientID)).setEncKey(this.keyWrapper.wrap(var1)).createPKRecipientInfo()
         );
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }
}
