package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class PKRecipientInfo extends ASN1Object {
   private final HashedId8 recipientId;
   private final EncryptedDataEncryptionKey encKey;

   public PKRecipientInfo(HashedId8 var1, EncryptedDataEncryptionKey var2) {
      this.recipientId = var1;
      this.encKey = var2;
   }

   private PKRecipientInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.recipientId = HashedId8.getInstance(var1.getObjectAt(0));
         this.encKey = EncryptedDataEncryptionKey.getInstance(var1.getObjectAt(1));
      }
   }

   public static PKRecipientInfo getInstance(Object var0) {
      if (var0 instanceof PKRecipientInfo) {
         return (PKRecipientInfo)var0;
      } else {
         return var0 != null ? new PKRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public HashedId getRecipientId() {
      return this.recipientId;
   }

   public EncryptedDataEncryptionKey getEncKey() {
      return this.encKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.recipientId, this.encKey);
   }

   public static PKRecipientInfo.Builder builder() {
      return new PKRecipientInfo.Builder();
   }

   public static class Builder {
      private HashedId8 recipientId;
      private EncryptedDataEncryptionKey encKey;

      public PKRecipientInfo.Builder setRecipientId(HashedId8 var1) {
         this.recipientId = var1;
         return this;
      }

      public PKRecipientInfo.Builder setEncKey(EncryptedDataEncryptionKey var1) {
         this.encKey = var1;
         return this;
      }

      public PKRecipientInfo createPKRecipientInfo() {
         return new PKRecipientInfo(this.recipientId, this.encKey);
      }
   }
}
