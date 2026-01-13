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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SharedAtRequest extends ASN1Object {
   private final HashedId8 eaId;
   private final ASN1OctetString keyTag;
   private final CertificateFormat certificateFormat;
   private final CertificateSubjectAttributes requestedSubjectAttributes;

   public SharedAtRequest(HashedId8 var1, ASN1OctetString var2, CertificateFormat var3, CertificateSubjectAttributes var4) {
      this.eaId = var1;
      this.keyTag = var2;
      this.certificateFormat = var3;
      this.requestedSubjectAttributes = var4;
   }

   private SharedAtRequest(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.eaId = HashedId8.getInstance(var1.getObjectAt(0));
         this.keyTag = ASN1OctetString.getInstance(var1.getObjectAt(1));
         this.certificateFormat = CertificateFormat.getInstance(var1.getObjectAt(2));
         this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(var1.getObjectAt(3));
      }
   }

   public static SharedAtRequest getInstance(Object var0) {
      if (var0 instanceof SharedAtRequest) {
         return (SharedAtRequest)var0;
      } else {
         return var0 != null ? new SharedAtRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public HashedId8 getEaId() {
      return this.eaId;
   }

   public ASN1OctetString getKeyTag() {
      return this.keyTag;
   }

   public CertificateFormat getCertificateFormat() {
      return this.certificateFormat;
   }

   public CertificateSubjectAttributes getRequestedSubjectAttributes() {
      return this.requestedSubjectAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.eaId, this.keyTag, this.certificateFormat, this.requestedSubjectAttributes});
   }

   public static SharedAtRequest.Builder builder() {
      return new SharedAtRequest.Builder();
   }

   public static class Builder {
      private HashedId8 eaId;
      private ASN1OctetString keyTag;
      private CertificateFormat certificateFormat;
      private CertificateSubjectAttributes requestedSubjectAttributes;

      public SharedAtRequest.Builder setEaId(HashedId8 var1) {
         this.eaId = var1;
         return this;
      }

      public SharedAtRequest.Builder setKeyTag(ASN1OctetString var1) {
         this.keyTag = var1;
         return this;
      }

      public SharedAtRequest.Builder setKeyTag(byte[] var1) {
         this.keyTag = new DEROctetString(var1);
         return this;
      }

      public SharedAtRequest.Builder setCertificateFormat(CertificateFormat var1) {
         this.certificateFormat = var1;
         return this;
      }

      public SharedAtRequest.Builder setRequestedSubjectAttributes(CertificateSubjectAttributes var1) {
         this.requestedSubjectAttributes = var1;
         return this;
      }

      public SharedAtRequest createSharedAtRequest() {
         return new SharedAtRequest(this.eaId, this.keyTag, this.certificateFormat, this.requestedSubjectAttributes);
      }
   }
}
