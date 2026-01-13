package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EeRaCertRequest extends ASN1Object {
   private final UINT8 version;
   private final Time32 generationTime;
   private final CertificateType type;
   private final ToBeSignedCertificate tbsCert;
   private final AdditionalParams additionalParams;

   public EeRaCertRequest(UINT8 var1, Time32 var2, CertificateType var3, ToBeSignedCertificate var4, AdditionalParams var5) {
      this.version = var1;
      this.generationTime = var2;
      this.type = var3;
      this.tbsCert = var4;
      this.additionalParams = var5;
   }

   private EeRaCertRequest(ASN1Sequence var1) {
      if (var1.size() != 5) {
         throw new IllegalArgumentException("expected sequence size of 5");
      } else {
         this.version = UINT8.getInstance(var1.getObjectAt(0));
         this.generationTime = Time32.getInstance(var1.getObjectAt(1));
         this.type = CertificateType.getInstance(var1.getObjectAt(2));
         this.tbsCert = ToBeSignedCertificate.getInstance(var1.getObjectAt(3));
         this.additionalParams = OEROptional.getInstance(var1.getObjectAt(4)).getObject(AdditionalParams.class);
      }
   }

   public static EeRaCertRequest getInstance(Object var0) {
      if (var0 instanceof EeRaCertRequest) {
         return (EeRaCertRequest)var0;
      } else {
         return var0 != null ? new EeRaCertRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static EeRaCertRequest.Builder builder() {
      return new EeRaCertRequest.Builder();
   }

   public UINT8 getVersion() {
      return this.version;
   }

   public Time32 getGenerationTime() {
      return this.generationTime;
   }

   public CertificateType getType() {
      return this.type;
   }

   public ToBeSignedCertificate getTbsCert() {
      return this.tbsCert;
   }

   public AdditionalParams getAdditionalParams() {
      return this.additionalParams;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.version, this.generationTime, this.type, this.tbsCert, OEROptional.getInstance(this.additionalParams));
   }

   public static class Builder {
      private UINT8 version;
      private Time32 generationTime;
      private CertificateType type;
      private ToBeSignedCertificate tbsCert;
      private AdditionalParams additionalParams;

      public EeRaCertRequest.Builder setVersion(UINT8 var1) {
         this.version = var1;
         return this;
      }

      public EeRaCertRequest.Builder setGenerationTime(Time32 var1) {
         this.generationTime = var1;
         return this;
      }

      public EeRaCertRequest.Builder setType(CertificateType var1) {
         this.type = var1;
         return this;
      }

      public EeRaCertRequest.Builder setTbsCert(ToBeSignedCertificate var1) {
         this.tbsCert = var1;
         return this;
      }

      public EeRaCertRequest.Builder setAdditionalParams(AdditionalParams var1) {
         this.additionalParams = var1;
         return this;
      }

      public EeRaCertRequest createEeRaCertRequest() {
         return new EeRaCertRequest(this.version, this.generationTime, this.type, this.tbsCert, this.additionalParams);
      }
   }
}
