package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.util.Arrays;

public class AuthorizationValidationResponse extends ASN1Object {
   private final ASN1OctetString requestHash;
   private final AuthorizationValidationResponseCode responseCode;
   private final CertificateSubjectAttributes confirmedSubjectAttributes;

   public AuthorizationValidationResponse(ASN1OctetString var1, AuthorizationValidationResponseCode var2, CertificateSubjectAttributes var3) {
      this.requestHash = var1;
      this.responseCode = var2;
      this.confirmedSubjectAttributes = var3;
   }

   private AuthorizationValidationResponse(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.requestHash = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.responseCode = AuthorizationValidationResponseCode.getInstance(var1.getObjectAt(1));
         this.confirmedSubjectAttributes = OEROptional.getValue(CertificateSubjectAttributes.class, var1.getObjectAt(2));
      }
   }

   public static AuthorizationValidationResponse getInstance(Object var0) {
      if (var0 instanceof AuthorizationValidationResponse) {
         return (AuthorizationValidationResponse)var0;
      } else {
         return var0 != null ? new AuthorizationValidationResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getRequestHash() {
      return this.requestHash;
   }

   public AuthorizationValidationResponseCode getResponseCode() {
      return this.responseCode;
   }

   public CertificateSubjectAttributes getConfirmedSubjectAttributes() {
      return this.confirmedSubjectAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.confirmedSubjectAttributes)});
   }

   public static AuthorizationValidationResponse.Builder builder() {
      return new AuthorizationValidationResponse.Builder();
   }

   public static class Builder {
      private ASN1OctetString requestHash;
      private AuthorizationValidationResponseCode responseCode;
      private CertificateSubjectAttributes confirmedSubjectAttributes;

      public AuthorizationValidationResponse.Builder setRequestHash(ASN1OctetString var1) {
         this.requestHash = var1;
         return this;
      }

      public AuthorizationValidationResponse.Builder setRequestHash(byte[] var1) {
         this.requestHash = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public AuthorizationValidationResponse.Builder setResponseCode(AuthorizationValidationResponseCode var1) {
         this.responseCode = var1;
         return this;
      }

      public AuthorizationValidationResponse.Builder setConfirmedSubjectAttributes(CertificateSubjectAttributes var1) {
         this.confirmedSubjectAttributes = var1;
         return this;
      }

      public AuthorizationValidationResponse createAuthorizationValidationResponse() {
         return new AuthorizationValidationResponse(this.requestHash, this.responseCode, this.confirmedSubjectAttributes);
      }
   }
}
