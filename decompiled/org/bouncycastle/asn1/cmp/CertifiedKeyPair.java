package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;

public class CertifiedKeyPair extends ASN1Object {
   private final CertOrEncCert certOrEncCert;
   private EncryptedKey privateKey;
   private PKIPublicationInfo publicationInfo;

   private CertifiedKeyPair(ASN1Sequence var1) {
      this.certOrEncCert = CertOrEncCert.getInstance(var1.getObjectAt(0));
      if (var1.size() >= 2) {
         if (var1.size() == 2) {
            ASN1TaggedObject var2 = ASN1TaggedObject.getInstance(var1.getObjectAt(1), 128);
            if (var2.getTagNo() == 0) {
               this.privateKey = EncryptedKey.getInstance(var2.getExplicitBaseObject());
            } else {
               this.publicationInfo = PKIPublicationInfo.getInstance(var2.getExplicitBaseObject());
            }
         } else {
            this.privateKey = EncryptedKey.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(1), 128).getExplicitBaseObject());
            this.publicationInfo = PKIPublicationInfo.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(2), 128).getExplicitBaseObject());
         }
      }
   }

   public CertifiedKeyPair(CertOrEncCert var1) {
      this(var1, (EncryptedKey)null, null);
   }

   public CertifiedKeyPair(CertOrEncCert var1, EncryptedKey var2, PKIPublicationInfo var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("'certOrEncCert' cannot be null");
      } else {
         this.certOrEncCert = var1;
         this.privateKey = var2;
         this.publicationInfo = var3;
      }
   }

   public CertifiedKeyPair(CertOrEncCert var1, EncryptedValue var2, PKIPublicationInfo var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("'certOrEncCert' cannot be null");
      } else {
         this.certOrEncCert = var1;
         this.privateKey = var2 != null ? new EncryptedKey(var2) : null;
         this.publicationInfo = var3;
      }
   }

   public static CertifiedKeyPair getInstance(Object var0) {
      if (var0 instanceof CertifiedKeyPair) {
         return (CertifiedKeyPair)var0;
      } else {
         return var0 != null ? new CertifiedKeyPair(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertOrEncCert getCertOrEncCert() {
      return this.certOrEncCert;
   }

   public EncryptedKey getPrivateKey() {
      return this.privateKey;
   }

   public PKIPublicationInfo getPublicationInfo() {
      return this.publicationInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.certOrEncCert);
      if (this.privateKey != null) {
         var1.add(new DERTaggedObject(true, 0, this.privateKey));
      }

      if (this.publicationInfo != null) {
         var1.add(new DERTaggedObject(true, 1, this.publicationInfo));
      }

      return new DERSequence(var1);
   }
}
