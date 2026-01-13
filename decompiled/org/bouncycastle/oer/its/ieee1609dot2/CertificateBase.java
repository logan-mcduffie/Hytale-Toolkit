package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CertificateBase extends ASN1Object {
   private final UINT8 version;
   private final CertificateType type;
   private final IssuerIdentifier issuer;
   private final ToBeSignedCertificate toBeSigned;
   private final Signature signature;

   public CertificateBase(UINT8 var1, CertificateType var2, IssuerIdentifier var3, ToBeSignedCertificate var4, Signature var5) {
      this.version = var1;
      this.type = var2;
      this.issuer = var3;
      this.toBeSigned = var4;
      this.signature = var5;
   }

   protected CertificateBase(ASN1Sequence var1) {
      if (var1.size() != 5) {
         throw new IllegalArgumentException("expected sequence size of 5");
      } else {
         this.version = UINT8.getInstance(var1.getObjectAt(0));
         this.type = CertificateType.getInstance(var1.getObjectAt(1));
         this.issuer = IssuerIdentifier.getInstance(var1.getObjectAt(2));
         this.toBeSigned = ToBeSignedCertificate.getInstance(var1.getObjectAt(3));
         this.signature = OEROptional.getValue(Signature.class, var1.getObjectAt(4));
      }
   }

   public static CertificateBase getInstance(Object var0) {
      if (var0 instanceof CertificateBase) {
         return (CertificateBase)var0;
      } else {
         return var0 != null ? new CertificateBase(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CertificateBase.Builder builder() {
      return new CertificateBase.Builder();
   }

   public UINT8 getVersion() {
      return this.version;
   }

   public CertificateType getType() {
      return this.type;
   }

   public IssuerIdentifier getIssuer() {
      return this.issuer;
   }

   public ToBeSignedCertificate getToBeSigned() {
      return this.toBeSigned;
   }

   public Signature getSignature() {
      return this.signature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.version, this.type, this.issuer, this.toBeSigned, OEROptional.getInstance(this.signature));
   }

   public static class Builder {
      private UINT8 version;
      private CertificateType type;
      private IssuerIdentifier issuer;
      private ToBeSignedCertificate toBeSigned;
      private Signature signature;

      public CertificateBase.Builder setVersion(UINT8 var1) {
         this.version = var1;
         return this;
      }

      public CertificateBase.Builder setType(CertificateType var1) {
         this.type = var1;
         return this;
      }

      public CertificateBase.Builder setIssuer(IssuerIdentifier var1) {
         this.issuer = var1;
         return this;
      }

      public CertificateBase.Builder setToBeSigned(ToBeSignedCertificate var1) {
         this.toBeSigned = var1;
         return this;
      }

      public CertificateBase.Builder setSignature(Signature var1) {
         this.signature = var1;
         return this;
      }

      public Certificate createCertificate() {
         return new Certificate(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
      }

      public ExplicitCertificate createExplicitCertificate() {
         return new ExplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
      }

      public ImplicitCertificate createImplicitCertificate() {
         return new ImplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
      }

      public CertificateBase createCertificateBase() {
         return new CertificateBase(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
      }

      public CertificateBase createEtsiTs103097Certificate() {
         return new EtsiTs103097Certificate(this.version, this.issuer, this.toBeSigned, this.signature);
      }
   }
}
