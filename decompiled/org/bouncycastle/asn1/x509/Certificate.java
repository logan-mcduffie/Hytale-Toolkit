package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class Certificate extends ASN1Object {
   ASN1Sequence seq;
   TBSCertificate tbsCert;
   AlgorithmIdentifier sigAlgId;
   ASN1BitString sig;

   public static Certificate getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static Certificate getInstance(Object var0) {
      if (var0 instanceof Certificate) {
         return (Certificate)var0;
      } else {
         return var0 != null ? new Certificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private Certificate(ASN1Sequence var1) {
      this.seq = var1;
      if (var1.size() == 3) {
         this.tbsCert = TBSCertificate.getInstance(var1.getObjectAt(0));
         this.sigAlgId = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.sig = ASN1BitString.getInstance(var1.getObjectAt(2));
      } else {
         throw new IllegalArgumentException("sequence wrong size for a certificate");
      }
   }

   public Certificate(TBSCertificate var1, AlgorithmIdentifier var2, ASN1BitString var3) {
      if (var1 == null) {
         throw new NullPointerException("'tbsCertificate' cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("'signatureAlgorithm' cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("'signature' cannot be null");
      } else {
         this.tbsCert = var1;
         this.sigAlgId = var2;
         this.sig = var3;
         ASN1EncodableVector var4 = new ASN1EncodableVector(3);
         var4.add(var1);
         var4.add(var2);
         var4.add(var3);
         this.seq = new DERSequence(var4);
      }
   }

   public TBSCertificate getTBSCertificate() {
      return this.tbsCert;
   }

   public ASN1Integer getVersion() {
      return this.tbsCert.getVersion();
   }

   public int getVersionNumber() {
      return this.tbsCert.getVersionNumber();
   }

   public ASN1Integer getSerialNumber() {
      return this.tbsCert.getSerialNumber();
   }

   public X500Name getIssuer() {
      return this.tbsCert.getIssuer();
   }

   public Validity getValidity() {
      return this.tbsCert.getValidity();
   }

   public Time getStartDate() {
      return this.tbsCert.getStartDate();
   }

   public Time getEndDate() {
      return this.tbsCert.getEndDate();
   }

   public X500Name getSubject() {
      return this.tbsCert.getSubject();
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.tbsCert.getSubjectPublicKeyInfo();
   }

   public ASN1BitString getIssuerUniqueID() {
      return this.tbsCert.getIssuerUniqueId();
   }

   public ASN1BitString getSubjectUniqueID() {
      return this.tbsCert.getSubjectUniqueId();
   }

   public Extensions getExtensions() {
      return this.tbsCert.getExtensions();
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.sigAlgId;
   }

   public ASN1BitString getSignature() {
      return this.sig;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
