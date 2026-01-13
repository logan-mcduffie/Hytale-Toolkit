package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateFormat;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

public class InnerEcRequest extends ASN1Object {
   private final ASN1OctetString itsId;
   private final CertificateFormat certificateFormat;
   private final PublicKeys publicKeys;
   private final CertificateSubjectAttributes requestedSubjectAttributes;

   public InnerEcRequest(ASN1OctetString var1, CertificateFormat var2, PublicKeys var3, CertificateSubjectAttributes var4) {
      this.itsId = var1;
      this.certificateFormat = var2;
      this.publicKeys = var3;
      this.requestedSubjectAttributes = var4;
   }

   private InnerEcRequest(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.itsId = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.certificateFormat = CertificateFormat.getInstance(var1.getObjectAt(1));
         this.publicKeys = PublicKeys.getInstance(var1.getObjectAt(2));
         this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(var1.getObjectAt(3));
      }
   }

   public static InnerEcRequest getInstance(Object var0) {
      if (var0 instanceof InnerEcRequest) {
         return (InnerEcRequest)var0;
      } else {
         return var0 != null ? new InnerEcRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getItsId() {
      return this.itsId;
   }

   public CertificateFormat getCertificateFormat() {
      return this.certificateFormat;
   }

   public PublicKeys getPublicKeys() {
      return this.publicKeys;
   }

   public CertificateSubjectAttributes getRequestedSubjectAttributes() {
      return this.requestedSubjectAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes});
   }

   public static InnerEcRequest.Builder builder() {
      return new InnerEcRequest.Builder();
   }

   public static class Builder {
      private ASN1OctetString itsId;
      private CertificateFormat certificateFormat;
      private PublicKeys publicKeys;
      private CertificateSubjectAttributes requestedSubjectAttributes;

      public InnerEcRequest.Builder setItsId(ASN1OctetString var1) {
         this.itsId = var1;
         return this;
      }

      public InnerEcRequest.Builder setItsId(byte[] var1) {
         this.itsId = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public InnerEcRequest.Builder setCertificateFormat(CertificateFormat var1) {
         this.certificateFormat = var1;
         return this;
      }

      public InnerEcRequest.Builder setPublicKeys(PublicKeys var1) {
         this.publicKeys = var1;
         return this;
      }

      public InnerEcRequest.Builder setRequestedSubjectAttributes(CertificateSubjectAttributes var1) {
         this.requestedSubjectAttributes = var1;
         return this;
      }

      public InnerEcRequest createInnerEcRequest() {
         return new InnerEcRequest(this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes);
      }
   }
}
