package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;

public class CertificationRequestInfo extends ASN1Object {
   ASN1Integer version = new ASN1Integer(0L);
   X500Name subject;
   SubjectPublicKeyInfo subjectPKInfo;
   ASN1Set attributes = null;

   public static CertificationRequestInfo getInstance(Object var0) {
      if (var0 instanceof CertificationRequestInfo) {
         return (CertificationRequestInfo)var0;
      } else {
         return var0 != null ? new CertificationRequestInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertificationRequestInfo(X500Name var1, SubjectPublicKeyInfo var2, ASN1Set var3) {
      if (var1 != null && var2 != null) {
         validateAttributes(var3);
         this.subject = var1;
         this.subjectPKInfo = var2;
         this.attributes = var3;
      } else {
         throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
      }
   }

   /** @deprecated */
   public CertificationRequestInfo(X509Name var1, SubjectPublicKeyInfo var2, ASN1Set var3) {
      this(X500Name.getInstance(var1.toASN1Primitive()), var2, var3);
   }

   private CertificationRequestInfo(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      this.subject = X500Name.getInstance(var1.getObjectAt(1));
      this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(2));
      if (var1.size() > 3) {
         ASN1TaggedObject var2 = (ASN1TaggedObject)var1.getObjectAt(3);
         this.attributes = ASN1Set.getInstance(var2, false);
      }

      validateAttributes(this.attributes);
      if (this.subject == null || this.version == null || this.subjectPKInfo == null) {
         throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public X500Name getSubject() {
      return this.subject;
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.subjectPKInfo;
   }

   public ASN1Set getAttributes() {
      return this.attributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.version);
      var1.add(this.subject);
      var1.add(this.subjectPKInfo);
      if (this.attributes != null) {
         var1.add(new DERTaggedObject(false, 0, this.attributes));
      }

      return new DERSequence(var1);
   }

   private static void validateAttributes(ASN1Set var0) {
      if (var0 != null) {
         Enumeration var1 = var0.getObjects();

         while (var1.hasMoreElements()) {
            Attribute var2 = Attribute.getInstance(var1.nextElement());
            if (var2.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_challengePassword) && var2.getAttrValues().size() != 1) {
               throw new IllegalArgumentException("challengePassword attribute must have one value");
            }
         }
      }
   }
}
