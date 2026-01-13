package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Properties;

public class TBSCertificate extends ASN1Object {
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
   Extensions extensions;

   public static TBSCertificate getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static TBSCertificate getInstance(Object var0) {
      if (var0 instanceof TBSCertificate) {
         return (TBSCertificate)var0;
      } else {
         return var0 != null ? new TBSCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private TBSCertificate(ASN1Sequence var1) {
      byte var2 = 0;
      this.seq = var1;
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         this.version = ASN1Integer.getInstance((ASN1TaggedObject)var1.getObjectAt(0), true);
      } else {
         var2 = -1;
         this.version = new ASN1Integer(0L);
      }

      boolean var3 = false;
      boolean var4 = false;
      if (this.version.hasValue(0)) {
         var3 = true;
      } else if (this.version.hasValue(1)) {
         var4 = true;
      } else if (!this.version.hasValue(2)) {
         throw new IllegalArgumentException("version number not recognised");
      }

      this.serialNumber = ASN1Integer.getInstance(var1.getObjectAt(var2 + 1));
      this.signature = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2 + 2));
      this.issuer = X500Name.getInstance(var1.getObjectAt(var2 + 3));
      this.validity = Validity.getInstance(var1.getObjectAt(var2 + 4));
      this.subject = X500Name.getInstance(var1.getObjectAt(var2 + 5));
      this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(var2 + 6));
      int var5 = var1.size() - (var2 + 6) - 1;
      if (var5 != 0 && var3) {
         throw new IllegalArgumentException("version 1 certificate contains extra data");
      } else {
         for (; var5 > 0; var5--) {
            ASN1TaggedObject var6 = (ASN1TaggedObject)var1.getObjectAt(var2 + 6 + var5);
            switch (var6.getTagNo()) {
               case 1:
                  this.issuerUniqueId = ASN1BitString.getInstance(var6, false);
                  break;
               case 2:
                  this.subjectUniqueId = ASN1BitString.getInstance(var6, false);
                  break;
               case 3:
                  if (var4) {
                     throw new IllegalArgumentException("version 2 certificate cannot contain extensions");
                  }

                  this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(var6, true));
                  break;
               default:
                  throw new IllegalArgumentException("Unknown tag encountered in structure: " + var6.getTagNo());
            }
         }
      }
   }

   public TBSCertificate(
      ASN1Integer var1,
      ASN1Integer var2,
      AlgorithmIdentifier var3,
      X500Name var4,
      Validity var5,
      X500Name var6,
      SubjectPublicKeyInfo var7,
      ASN1BitString var8,
      ASN1BitString var9,
      Extensions var10
   ) {
      if (var2 == null) {
         throw new NullPointerException("'serialNumber' cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("'signature' cannot be null");
      } else if (var4 == null) {
         throw new NullPointerException("'issuer' cannot be null");
      } else if (var5 == null) {
         throw new NullPointerException("'validity' cannot be null");
      } else if (var6 == null) {
         throw new NullPointerException("'subject' cannot be null");
      } else if (var7 == null) {
         throw new NullPointerException("'subjectPublicKeyInfo' cannot be null");
      } else {
         this.version = var1 != null ? var1 : new ASN1Integer(0L);
         this.serialNumber = var2;
         this.signature = var3;
         this.issuer = var4;
         this.validity = var5;
         this.subject = var6;
         this.subjectPublicKeyInfo = var7;
         this.issuerUniqueId = var8;
         this.subjectUniqueId = var9;
         this.extensions = var10;
         this.seq = null;
      }
   }

   public int getVersionNumber() {
      return this.version.intValueExact() + 1;
   }

   public ASN1Integer getVersion() {
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

   public Extensions getExtensions() {
      return this.extensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.seq != null) {
         if (Properties.getPropertyValue("org.bouncycastle.x509.allow_non-der_tbscert") == null) {
            return this.seq;
         }

         if (Properties.isOverrideSet("org.bouncycastle.x509.allow_non-der_tbscert")) {
            return this.seq;
         }
      }

      ASN1EncodableVector var1 = new ASN1EncodableVector(10);
      if (!this.version.hasValue(0)) {
         var1.add(new DERTaggedObject(true, 0, this.version));
      }

      var1.add(this.serialNumber);
      var1.add(this.signature);
      var1.add(this.issuer);
      var1.add(this.validity);
      var1.add(this.subject);
      var1.add(this.subjectPublicKeyInfo);
      if (this.issuerUniqueId != null) {
         var1.add(new DERTaggedObject(false, 1, this.issuerUniqueId));
      }

      if (this.subjectUniqueId != null) {
         var1.add(new DERTaggedObject(false, 2, this.subjectUniqueId));
      }

      if (this.extensions != null) {
         var1.add(new DERTaggedObject(true, 3, this.extensions));
      }

      return new DERSequence(var1);
   }
}
