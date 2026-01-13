package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class CertificateList extends ASN1Object {
   TBSCertList tbsCertList;
   AlgorithmIdentifier sigAlgId;
   ASN1BitString sig;
   boolean isHashCodeSet = false;
   int hashCodeValue;

   public static CertificateList getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static CertificateList getInstance(Object var0) {
      if (var0 instanceof CertificateList) {
         return (CertificateList)var0;
      } else {
         return var0 != null ? new CertificateList(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CertificateList(ASN1Sequence var1) {
      if (var1.size() == 3) {
         this.tbsCertList = TBSCertList.getInstance(var1.getObjectAt(0));
         this.sigAlgId = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.sig = ASN1BitString.getInstance(var1.getObjectAt(2));
      } else {
         throw new IllegalArgumentException("sequence wrong size for CertificateList");
      }
   }

   public TBSCertList getTBSCertList() {
      return this.tbsCertList;
   }

   public TBSCertList.CRLEntry[] getRevokedCertificates() {
      return this.tbsCertList.getRevokedCertificates();
   }

   public Enumeration getRevokedCertificateEnumeration() {
      return this.tbsCertList.getRevokedCertificateEnumeration();
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.sigAlgId;
   }

   public ASN1BitString getSignature() {
      return this.sig;
   }

   public int getVersionNumber() {
      return this.tbsCertList.getVersionNumber();
   }

   public X500Name getIssuer() {
      return this.tbsCertList.getIssuer();
   }

   public Time getThisUpdate() {
      return this.tbsCertList.getThisUpdate();
   }

   public Time getNextUpdate() {
      return this.tbsCertList.getNextUpdate();
   }

   public Extensions getExtensions() {
      return this.tbsCertList.getExtensions();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.tbsCertList);
      var1.add(this.sigAlgId);
      var1.add(this.sig);
      return new DERSequence(var1);
   }

   @Override
   public int hashCode() {
      if (!this.isHashCodeSet) {
         this.hashCodeValue = super.hashCode();
         this.isHashCodeSet = true;
      }

      return this.hashCodeValue;
   }
}
