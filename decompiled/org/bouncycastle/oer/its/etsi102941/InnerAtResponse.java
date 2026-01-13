package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.util.Arrays;

public class InnerAtResponse extends ASN1Object {
   private final ASN1OctetString requestHash;
   private final AuthorizationResponseCode responseCode;
   private final EtsiTs103097Certificate certificate;

   public InnerAtResponse(ASN1OctetString var1, AuthorizationResponseCode var2, EtsiTs103097Certificate var3) {
      this.requestHash = var1;
      this.responseCode = var2;
      this.certificate = var3;
   }

   private InnerAtResponse(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.requestHash = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.responseCode = AuthorizationResponseCode.getInstance(var1.getObjectAt(1));
         this.certificate = OEROptional.getValue(EtsiTs103097Certificate.class, var1.getObjectAt(2));
      }
   }

   public static InnerAtResponse getInstance(Object var0) {
      if (var0 instanceof InnerAtResponse) {
         return (InnerAtResponse)var0;
      } else {
         return var0 != null ? new InnerAtResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getRequestHash() {
      return this.requestHash;
   }

   public AuthorizationResponseCode getResponseCode() {
      return this.responseCode;
   }

   public EtsiTs103097Certificate getCertificate() {
      return this.certificate;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.certificate)});
   }

   public static InnerAtResponse.Builder builder() {
      return new InnerAtResponse.Builder();
   }

   public static class Builder {
      private ASN1OctetString requestHash;
      private AuthorizationResponseCode responseCode;
      private EtsiTs103097Certificate certificate;

      public InnerAtResponse.Builder setRequestHash(ASN1OctetString var1) {
         this.requestHash = var1;
         return this;
      }

      public InnerAtResponse.Builder setRequestHash(byte[] var1) {
         this.requestHash = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public InnerAtResponse.Builder setResponseCode(AuthorizationResponseCode var1) {
         this.responseCode = var1;
         return this;
      }

      public InnerAtResponse.Builder setCertificate(EtsiTs103097Certificate var1) {
         this.certificate = var1;
         return this;
      }

      public InnerAtResponse createInnerAtResponse() {
         return new InnerAtResponse(this.requestHash, this.responseCode, this.certificate);
      }
   }
}
