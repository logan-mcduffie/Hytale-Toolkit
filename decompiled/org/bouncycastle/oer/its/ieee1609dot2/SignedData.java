package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;

public class SignedData extends ASN1Object {
   private final HashAlgorithm hashId;
   private final ToBeSignedData tbsData;
   private final SignerIdentifier signer;
   private final Signature signature;

   public SignedData(HashAlgorithm var1, ToBeSignedData var2, SignerIdentifier var3, Signature var4) {
      this.hashId = var1;
      this.tbsData = var2;
      this.signer = var3;
      this.signature = var4;
   }

   private SignedData(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.hashId = HashAlgorithm.getInstance(var1.getObjectAt(0));
         this.tbsData = ToBeSignedData.getInstance(var1.getObjectAt(1));
         this.signer = SignerIdentifier.getInstance(var1.getObjectAt(2));
         this.signature = Signature.getInstance(var1.getObjectAt(3));
      }
   }

   public static SignedData getInstance(Object var0) {
      if (var0 instanceof SignedData) {
         return (SignedData)var0;
      } else {
         return var0 != null ? new SignedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.hashId, this.tbsData, this.signer, this.signature);
   }

   public HashAlgorithm getHashId() {
      return this.hashId;
   }

   public ToBeSignedData getTbsData() {
      return this.tbsData;
   }

   public SignerIdentifier getSigner() {
      return this.signer;
   }

   public Signature getSignature() {
      return this.signature;
   }

   public static SignedData.Builder builder() {
      return new SignedData.Builder();
   }

   public static class Builder {
      private HashAlgorithm hashId;
      private ToBeSignedData tbsData;
      private SignerIdentifier signer;
      private Signature signature;

      public SignedData.Builder setHashId(HashAlgorithm var1) {
         this.hashId = var1;
         return this;
      }

      public SignedData.Builder setTbsData(ToBeSignedData var1) {
         this.tbsData = var1;
         return this;
      }

      public SignedData.Builder setSigner(SignerIdentifier var1) {
         this.signer = var1;
         return this;
      }

      public SignedData.Builder setSignature(Signature var1) {
         this.signature = var1;
         return this;
      }

      public SignedData createSignedData() {
         return new SignedData(this.hashId, this.tbsData, this.signer, this.signature);
      }
   }
}
