package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class DeltaCertificateRequestAttributeValue implements ASN1Encodable {
   private final X500Name subject;
   private final SubjectPublicKeyInfo subjectPKInfo;
   private final Extensions extensions;
   private final AlgorithmIdentifier signatureAlgorithm;
   private final ASN1Sequence attrSeq;

   public DeltaCertificateRequestAttributeValue(Attribute var1) {
      this(ASN1Sequence.getInstance(var1.getAttributeValues()[0]));
   }

   public static DeltaCertificateRequestAttributeValue getInstance(Object var0) {
      if (var0 instanceof DeltaCertificateDescriptor) {
         return (DeltaCertificateRequestAttributeValue)var0;
      } else {
         if (var0 != null) {
            new DeltaCertificateRequestAttributeValue(ASN1Sequence.getInstance(var0));
         }

         return null;
      }
   }

   DeltaCertificateRequestAttributeValue(ASN1Sequence var1) {
      this.attrSeq = var1;
      int var2 = 0;
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         this.subject = X500Name.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(0)), true);
         var2++;
      } else {
         this.subject = null;
      }

      this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(var2));
      var2++;
      Extensions var3 = null;
      AlgorithmIdentifier var4 = null;
      if (var2 != var1.size()) {
         for (; var2 < var1.size(); var2++) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
            if (var5.getTagNo() == 1) {
               var3 = Extensions.getInstance(var5, true);
            } else {
               if (var5.getTagNo() != 2) {
                  throw new IllegalArgumentException("unknown tag");
               }

               var4 = AlgorithmIdentifier.getInstance(var5, true);
            }
         }
      }

      this.extensions = var3;
      this.signatureAlgorithm = var4;
   }

   public X500Name getSubject() {
      return this.subject;
   }

   public SubjectPublicKeyInfo getSubjectPKInfo() {
      return this.subjectPKInfo;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.attrSeq;
   }
}
