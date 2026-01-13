package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class ETSIRecipientID implements Selector<ETSIRecipientInfo> {
   private final HashedId8 id;

   public ETSIRecipientID(byte[] var1) {
      this(new HashedId8(var1));
   }

   public ETSIRecipientID(HashedId8 var1) {
      this.id = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ETSIRecipientID var2 = (ETSIRecipientID)var1;
         return this.id != null ? this.id.equals(var2.id) : var2.id == null;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public boolean match(ETSIRecipientInfo var1) {
      if (var1.getRecipientInfo().getChoice() == 2) {
         PKRecipientInfo var2 = PKRecipientInfo.getInstance(var1.getRecipientInfo().getRecipientInfo());
         return Arrays.areEqual(var2.getRecipientId().getHashBytes(), this.id.getHashBytes());
      } else {
         return false;
      }
   }

   @Override
   public Object clone() {
      return this;
   }
}
