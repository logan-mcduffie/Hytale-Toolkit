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

public class InnerEcResponse extends ASN1Object {
   private final ASN1OctetString requestHash;
   private final EnrolmentResponseCode responseCode;
   private final EtsiTs103097Certificate certificate;

   public InnerEcResponse(ASN1OctetString var1, EnrolmentResponseCode var2, EtsiTs103097Certificate var3) {
      this.requestHash = var1;
      this.responseCode = var2;
      this.certificate = var3;
   }

   private InnerEcResponse(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.requestHash = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.responseCode = EnrolmentResponseCode.getInstance(var1.getObjectAt(1));
         this.certificate = OEROptional.getValue(EtsiTs103097Certificate.class, var1.getObjectAt(2));
      }
   }

   public static InnerEcResponse getInstance(Object var0) {
      if (var0 instanceof InnerEcResponse) {
         return (InnerEcResponse)var0;
      } else {
         return var0 != null ? new InnerEcResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getRequestHash() {
      return this.requestHash;
   }

   public EnrolmentResponseCode getResponseCode() {
      return this.responseCode;
   }

   public EtsiTs103097Certificate getCertificate() {
      return this.certificate;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.certificate)});
   }

   public static InnerEcResponse.Builder builder() {
      return new InnerEcResponse.Builder();
   }

   public static class Builder {
      private ASN1OctetString requestHash;
      private EnrolmentResponseCode responseCode;
      private EtsiTs103097Certificate certificate;

      public InnerEcResponse.Builder setRequestHash(ASN1OctetString var1) {
         this.requestHash = var1;
         return this;
      }

      public InnerEcResponse.Builder setRequestHash(byte[] var1) {
         this.requestHash = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public InnerEcResponse.Builder setResponseCode(EnrolmentResponseCode var1) {
         this.responseCode = var1;
         return this;
      }

      public InnerEcResponse.Builder setCertificate(EtsiTs103097Certificate var1) {
         this.certificate = var1;
         return this;
      }

      public InnerEcResponse createInnerEcResponse() {
         return new InnerEcResponse(this.requestHash, this.responseCode, this.certificate);
      }
   }
}
