package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;

public class SignedDataPayload extends ASN1Object {
   private final Ieee1609Dot2Data data;
   private final HashedData extDataHash;

   public SignedDataPayload(Ieee1609Dot2Data var1, HashedData var2) {
      this.data = var1;
      this.extDataHash = var2;
   }

   private SignedDataPayload(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.data = OEROptional.getValue(Ieee1609Dot2Data.class, var1.getObjectAt(0));
         this.extDataHash = OEROptional.getValue(HashedData.class, var1.getObjectAt(1));
      }
   }

   public static SignedDataPayload getInstance(Object var0) {
      if (var0 instanceof SignedDataPayload) {
         return (SignedDataPayload)var0;
      } else {
         return var0 != null ? new SignedDataPayload(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SignedDataPayload.Builder builder() {
      return new SignedDataPayload.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{OEROptional.getInstance(this.data), OEROptional.getInstance(this.extDataHash)});
   }

   public Ieee1609Dot2Data getData() {
      return this.data;
   }

   public HashedData getExtDataHash() {
      return this.extDataHash;
   }

   public static class Builder {
      private Ieee1609Dot2Data data;
      private HashedData extDataHash;

      public SignedDataPayload.Builder setData(Ieee1609Dot2Data var1) {
         this.data = var1;
         return this;
      }

      public SignedDataPayload.Builder setExtDataHash(HashedData var1) {
         this.extDataHash = var1;
         return this;
      }

      public SignedDataPayload createSignedDataPayload() {
         return new SignedDataPayload(this.data, this.extDataHash);
      }
   }
}
