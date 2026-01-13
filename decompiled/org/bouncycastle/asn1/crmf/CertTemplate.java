package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class CertTemplate extends ASN1Object {
   private ASN1Sequence seq;
   private ASN1Integer version;
   private ASN1Integer serialNumber;
   private AlgorithmIdentifier signingAlg;
   private X500Name issuer;
   private OptionalValidity validity;
   private X500Name subject;
   private SubjectPublicKeyInfo publicKey;
   private ASN1BitString issuerUID;
   private ASN1BitString subjectUID;
   private Extensions extensions;

   private CertTemplate(ASN1Sequence var1) {
      this.seq = var1;
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var2.nextElement();
         switch (var3.getTagNo()) {
            case 0:
               this.version = ASN1Integer.getInstance(var3, false);
               break;
            case 1:
               this.serialNumber = ASN1Integer.getInstance(var3, false);
               break;
            case 2:
               this.signingAlg = AlgorithmIdentifier.getInstance(var3, false);
               break;
            case 3:
               this.issuer = X500Name.getInstance(var3, true);
               break;
            case 4:
               this.validity = OptionalValidity.getInstance(ASN1Sequence.getInstance(var3, false));
               break;
            case 5:
               this.subject = X500Name.getInstance(var3, true);
               break;
            case 6:
               this.publicKey = SubjectPublicKeyInfo.getInstance(var3, false);
               break;
            case 7:
               this.issuerUID = ASN1BitString.getInstance(var3, false);
               break;
            case 8:
               this.subjectUID = ASN1BitString.getInstance(var3, false);
               break;
            case 9:
               this.extensions = Extensions.getInstance(var3, false);
               break;
            default:
               throw new IllegalArgumentException("unknown tag: " + var3.getTagNo());
         }
      }
   }

   public static CertTemplate getInstance(Object var0) {
      if (var0 instanceof CertTemplate) {
         return (CertTemplate)var0;
      } else {
         return var0 != null ? new CertTemplate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int getVersion() {
      return this.version != null ? this.version.intValueExact() : -1;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   public AlgorithmIdentifier getSigningAlg() {
      return this.signingAlg;
   }

   public X500Name getIssuer() {
      return this.issuer;
   }

   public OptionalValidity getValidity() {
      return this.validity;
   }

   public X500Name getSubject() {
      return this.subject;
   }

   public SubjectPublicKeyInfo getPublicKey() {
      return this.publicKey;
   }

   public ASN1BitString getIssuerUID() {
      return this.issuerUID;
   }

   public ASN1BitString getSubjectUID() {
      return this.subjectUID;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
