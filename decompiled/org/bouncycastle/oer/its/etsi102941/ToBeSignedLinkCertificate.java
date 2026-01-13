package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificate extends ASN1Object {
   private final Time32 expiryTime;
   private final HashedData certificateHash;

   public ToBeSignedLinkCertificate(Time32 var1, HashedData var2) {
      this.expiryTime = var1;
      this.certificateHash = var2;
   }

   protected ToBeSignedLinkCertificate(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.expiryTime = Time32.getInstance(var1.getObjectAt(0));
         this.certificateHash = HashedData.getInstance(var1.getObjectAt(1));
      }
   }

   public static ToBeSignedLinkCertificate getInstance(Object var0) {
      if (var0 instanceof ToBeSignedLinkCertificate) {
         return (ToBeSignedLinkCertificate)var0;
      } else {
         return var0 != null ? new ToBeSignedLinkCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Time32 getExpiryTime() {
      return this.expiryTime;
   }

   public HashedData getCertificateHash() {
      return this.certificateHash;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.expiryTime, this.certificateHash});
   }

   public static ToBeSignedLinkCertificate.Builder builder() {
      return new ToBeSignedLinkCertificate.Builder();
   }

   public static class Builder {
      private Time32 expiryTime;
      private HashedData certificateHash;

      public ToBeSignedLinkCertificate.Builder setExpiryTime(Time32 var1) {
         this.expiryTime = var1;
         return this;
      }

      public ToBeSignedLinkCertificate.Builder setCertificateHash(HashedData var1) {
         this.certificateHash = var1;
         return this;
      }

      public ToBeSignedLinkCertificate createToBeSignedLinkCertificate() {
         return new ToBeSignedLinkCertificate(this.expiryTime, this.certificateHash);
      }

      public ToBeSignedLinkCertificateTlm createToBeSignedLinkCertificateTlm() {
         return new ToBeSignedLinkCertificateTlm(this.expiryTime, this.certificateHash);
      }

      public ToBeSignedLinkCertificateRca createToBeSignedLinkCertificateRca() {
         return new ToBeSignedLinkCertificateRca(this.expiryTime, this.certificateHash);
      }
   }
}
