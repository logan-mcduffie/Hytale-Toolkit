package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;

public class CertOrEncCert extends ASN1Object implements ASN1Choice {
   private CMPCertificate certificate;
   private EncryptedKey encryptedCert;

   private CertOrEncCert(ASN1TaggedObject var1) {
      if (var1.hasContextTag(0)) {
         this.certificate = CMPCertificate.getInstance(var1.getExplicitBaseObject());
      } else {
         if (!var1.hasContextTag(1)) {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(var1));
         }

         this.encryptedCert = EncryptedKey.getInstance(var1.getExplicitBaseObject());
      }
   }

   public CertOrEncCert(CMPCertificate var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("'certificate' cannot be null");
      } else {
         this.certificate = var1;
      }
   }

   public CertOrEncCert(EncryptedValue var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("'encryptedCert' cannot be null");
      } else {
         this.encryptedCert = new EncryptedKey(var1);
      }
   }

   public CertOrEncCert(EncryptedKey var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("'encryptedCert' cannot be null");
      } else {
         this.encryptedCert = var1;
      }
   }

   public static CertOrEncCert getInstance(Object var0) {
      if (var0 instanceof CertOrEncCert) {
         return (CertOrEncCert)var0;
      } else {
         return var0 instanceof ASN1TaggedObject ? new CertOrEncCert(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public boolean hasEncryptedCertificate() {
      return this.encryptedCert != null;
   }

   public CMPCertificate getCertificate() {
      return this.certificate;
   }

   public EncryptedKey getEncryptedCert() {
      return this.encryptedCert;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.certificate != null ? new DERTaggedObject(true, 0, this.certificate) : new DERTaggedObject(true, 1, this.encryptedCert);
   }
}
