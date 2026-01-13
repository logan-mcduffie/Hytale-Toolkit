package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ToBeSignedData extends ASN1Object {
   private final SignedDataPayload payload;
   private final HeaderInfo headerInfo;

   public ToBeSignedData(SignedDataPayload var1, HeaderInfo var2) {
      this.payload = var1;
      this.headerInfo = var2;
   }

   private ToBeSignedData(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.payload = SignedDataPayload.getInstance(var1.getObjectAt(0));
         this.headerInfo = HeaderInfo.getInstance(var1.getObjectAt(1));
      }
   }

   public static ToBeSignedData getInstance(Object var0) {
      if (var0 instanceof ToBeSignedData) {
         return (ToBeSignedData)var0;
      } else {
         return var0 != null ? new ToBeSignedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SignedDataPayload getPayload() {
      return this.payload;
   }

   public HeaderInfo getHeaderInfo() {
      return this.headerInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.payload, this.headerInfo});
   }

   public static ToBeSignedData.Builder builder() {
      return new ToBeSignedData.Builder();
   }

   public static class Builder {
      private SignedDataPayload payload;
      private HeaderInfo headerInfo;

      public ToBeSignedData.Builder setPayload(SignedDataPayload var1) {
         this.payload = var1;
         return this;
      }

      public ToBeSignedData.Builder setHeaderInfo(HeaderInfo var1) {
         this.headerInfo = var1;
         return this;
      }

      public ToBeSignedData createToBeSignedData() {
         return new ToBeSignedData(this.payload, this.headerInfo);
      }
   }
}
