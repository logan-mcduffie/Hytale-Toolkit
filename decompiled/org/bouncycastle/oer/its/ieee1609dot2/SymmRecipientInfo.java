package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SymmRecipientInfo extends ASN1Object {
   private final HashedId8 recipientId;
   private final SymmetricCiphertext encKey;

   public SymmRecipientInfo(HashedId8 var1, SymmetricCiphertext var2) {
      this.recipientId = var1;
      this.encKey = var2;
   }

   private SymmRecipientInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.recipientId = HashedId8.getInstance(var1.getObjectAt(0));
         this.encKey = SymmetricCiphertext.getInstance(var1.getObjectAt(1));
      }
   }

   public static SymmRecipientInfo getInstance(Object var0) {
      if (var0 instanceof SymmRecipientInfo) {
         return (SymmRecipientInfo)var0;
      } else {
         return var0 != null ? new SymmRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public HashedId getRecipientId() {
      return this.recipientId;
   }

   public SymmetricCiphertext getEncKey() {
      return this.encKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.recipientId, this.encKey);
   }

   public static SymmRecipientInfo.Builder builder() {
      return new SymmRecipientInfo.Builder();
   }

   public static class Builder {
      private HashedId8 recipientId;
      private SymmetricCiphertext encKey;

      public SymmRecipientInfo.Builder setRecipientId(HashedId8 var1) {
         this.recipientId = var1;
         return this;
      }

      public SymmRecipientInfo.Builder setEncKey(SymmetricCiphertext var1) {
         this.encKey = var1;
         return this;
      }

      public SymmRecipientInfo createSymmRecipientInfo() {
         return new SymmRecipientInfo(this.recipientId, this.encKey);
      }
   }
}
