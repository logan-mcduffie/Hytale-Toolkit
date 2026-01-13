package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;

public class CaCertificateRequest extends ASN1Object {
   private final PublicKeys publicKeys;
   private final CertificateSubjectAttributes requestedSubjectAttributes;

   public CaCertificateRequest(PublicKeys var1, CertificateSubjectAttributes var2) {
      this.publicKeys = var1;
      this.requestedSubjectAttributes = var2;
   }

   private CaCertificateRequest(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.publicKeys = PublicKeys.getInstance(var1.getObjectAt(0));
         this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(var1.getObjectAt(1));
      }
   }

   public static CaCertificateRequest getInstance(Object var0) {
      if (var0 instanceof CaCertificateRequest) {
         return (CaCertificateRequest)var0;
      } else {
         return var0 != null ? new CaCertificateRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PublicKeys getPublicKeys() {
      return this.publicKeys;
   }

   public CertificateSubjectAttributes getRequestedSubjectAttributes() {
      return this.requestedSubjectAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.publicKeys, this.requestedSubjectAttributes});
   }

   public static CaCertificateRequest.Builder builder() {
      return new CaCertificateRequest.Builder();
   }

   public static class Builder {
      private PublicKeys publicKeys;
      private CertificateSubjectAttributes requestedSubjectAttributes;

      public CaCertificateRequest.Builder setPublicKeys(PublicKeys var1) {
         this.publicKeys = var1;
         return this;
      }

      public CaCertificateRequest.Builder setRequestedSubjectAttributes(CertificateSubjectAttributes var1) {
         this.requestedSubjectAttributes = var1;
         return this;
      }

      public CaCertificateRequest createCaCertificateRequest() {
         return new CaCertificateRequest(this.publicKeys, this.requestedSubjectAttributes);
      }
   }
}
