package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;

/** @deprecated */
public class TBSCertificateStructure extends ASN1Object implements X509ObjectIdentifiers, PKCSObjectIdentifiers {
   ASN1Sequence seq;
   ASN1Integer version;
   ASN1Integer serialNumber;
   AlgorithmIdentifier signature;
   X500Name issuer;
   Validity validity;
   X500Name subject;
   SubjectPublicKeyInfo subjectPublicKeyInfo;
   ASN1BitString issuerUniqueId;
   ASN1BitString subjectUniqueId;
   X509Extensions extensions;

   public static TBSCertificateStructure getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static TBSCertificateStructure getInstance(Object var0) {
      if (var0 instanceof TBSCertificateStructure) {
         return (TBSCertificateStructure)var0;
      } else {
         return var0 != null ? new TBSCertificateStructure(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TBSCertificateStructure(ASN1Sequence var1) {
      byte var2 = 0;
      this.seq = var1;
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         this.version = ASN1Integer.getInstance((ASN1TaggedObject)var1.getObjectAt(0), true);
      } else {
         var2 = -1;
         this.version = new ASN1Integer(0L);
      }

      this.serialNumber = ASN1Integer.getInstance(var1.getObjectAt(var2 + 1));
      this.signature = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2 + 2));
      this.issuer = X500Name.getInstance(var1.getObjectAt(var2 + 3));
      this.validity = Validity.getInstance(var1.getObjectAt(var2 + 4));
      this.subject = X500Name.getInstance(var1.getObjectAt(var2 + 5));
      this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(var2 + 6));

      for (int var3 = var1.size() - (var2 + 6) - 1; var3 > 0; var3--) {
         ASN1TaggedObject var4 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2 + 6 + var3));
         switch (var4.getTagNo()) {
            case 1:
               this.issuerUniqueId = ASN1BitString.getInstance(var4, false);
               break;
            case 2:
               this.subjectUniqueId = ASN1BitString.getInstance(var4, false);
               break;
            case 3:
               this.extensions = X509Extensions.getInstance(var4);
         }
      }
   }

   public int getVersion() {
      return this.version.intValueExact() + 1;
   }

   public ASN1Integer getVersionNumber() {
      return this.version;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   public AlgorithmIdentifier getSignature() {
      return this.signature;
   }

   public X500Name getIssuer() {
      return this.issuer;
   }

   public Validity getValidity() {
      return this.validity;
   }

   public Time getStartDate() {
      return this.validity.getNotBefore();
   }

   public Time getEndDate() {
      return this.validity.getNotAfter();
   }

   public X500Name getSubject() {
      return this.subject;
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.subjectPublicKeyInfo;
   }

   public ASN1BitString getIssuerUniqueId() {
      return this.issuerUniqueId;
   }

   public ASN1BitString getSubjectUniqueId() {
      return this.subjectUniqueId;
   }

   public X509Extensions getExtensions() {
      return this.extensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
