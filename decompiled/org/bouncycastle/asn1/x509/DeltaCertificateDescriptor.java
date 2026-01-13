package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class DeltaCertificateDescriptor extends ASN1Object {
   private final ASN1Integer serialNumber;
   private final AlgorithmIdentifier signature;
   private final X500Name issuer;
   private final Validity validity;
   private final X500Name subject;
   private final SubjectPublicKeyInfo subjectPublicKeyInfo;
   private final Extensions extensions;
   private final ASN1BitString signatureValue;

   public static DeltaCertificateDescriptor getInstance(Object var0) {
      if (var0 instanceof DeltaCertificateDescriptor) {
         return (DeltaCertificateDescriptor)var0;
      } else {
         return var0 != null ? new DeltaCertificateDescriptor(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static DeltaCertificateDescriptor fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.deltaCertificateDescriptor));
   }

   private DeltaCertificateDescriptor(ASN1Sequence var1) {
      ASN1Integer var2 = ASN1Integer.getInstance(var1.getObjectAt(0));
      int var3 = 1;
      ASN1Encodable var4 = var1.getObjectAt(var3++);
      AlgorithmIdentifier var5 = null;
      X500Name var6 = null;
      Validity var7 = null;

      X500Name var8;
      for (var8 = null; var4 instanceof ASN1TaggedObject; var4 = var1.getObjectAt(var3++)) {
         ASN1TaggedObject var9 = ASN1TaggedObject.getInstance(var4);
         switch (var9.getTagNo()) {
            case 0:
               var5 = AlgorithmIdentifier.getInstance(var9, true);
               break;
            case 1:
               var6 = X500Name.getInstance(var9, true);
               break;
            case 2:
               var7 = Validity.getInstance(var9, true);
               break;
            case 3:
               var8 = X500Name.getInstance(var9, true);
         }
      }

      SubjectPublicKeyInfo var14 = SubjectPublicKeyInfo.getInstance(var4);
      var4 = var1.getObjectAt(var3);

      Extensions var10;
      for (var10 = null; var4 instanceof ASN1TaggedObject; var4 = var1.getObjectAt(var3++)) {
         ASN1TaggedObject var11 = ASN1TaggedObject.getInstance(var4);
         switch (var11.getTagNo()) {
            case 4:
               var10 = Extensions.getInstance(var11, true);
               break;
         }
      }

      ASN1BitString var15 = ASN1BitString.getInstance(var4);
      this.serialNumber = var2;
      this.signature = var5;
      this.issuer = var6;
      this.validity = var7;
      this.subject = var8;
      this.subjectPublicKeyInfo = var14;
      this.extensions = var10;
      this.signatureValue = var15;
   }

   public DeltaCertificateDescriptor(
      ASN1Integer var1, AlgorithmIdentifier var2, X500Name var3, Validity var4, X500Name var5, SubjectPublicKeyInfo var6, Extensions var7, ASN1BitString var8
   ) {
      if (var1 == null) {
         throw new NullPointerException("'serialNumber' cannot be null");
      } else if (var6 == null) {
         throw new NullPointerException("'subjectPublicKeyInfo' cannot be null");
      } else if (var8 == null) {
         throw new NullPointerException("'signatureValue' cannot be null");
      } else {
         this.serialNumber = var1;
         this.signature = var2;
         this.issuer = var3;
         this.validity = var4;
         this.subject = var5;
         this.subjectPublicKeyInfo = var6;
         this.extensions = var7;
         this.signatureValue = var8;
      }
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

   /** @deprecated */
   public ASN1Sequence getValidity() {
      return (ASN1Sequence)this.validity.toASN1Primitive();
   }

   public Validity getValidityObject() {
      return this.validity;
   }

   public X500Name getSubject() {
      return this.subject;
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.subjectPublicKeyInfo;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   public ASN1BitString getSignatureValue() {
      return this.signatureValue;
   }

   /** @deprecated */
   public DeltaCertificateDescriptor trimTo(TBSCertificate var1, Extensions var2) {
      return trimDeltaCertificateDescriptor(this, var1, var2);
   }

   private static DeltaCertificateDescriptor trimDeltaCertificateDescriptor(DeltaCertificateDescriptor var0, TBSCertificate var1, Extensions var2) {
      ASN1Integer var3 = var0.getSerialNumber();
      AlgorithmIdentifier var4 = var0.getSignature();
      if (var4 != null && var4.equals(var1.getSignature())) {
         var4 = null;
      }

      X500Name var5 = var0.getIssuer();
      if (var5 != null && var5.equals(var1.getIssuer())) {
         var5 = null;
      }

      Validity var6 = var0.getValidityObject();
      if (var6 != null && var6.equals(var1.getValidity())) {
         var6 = null;
      }

      X500Name var7 = var0.getSubject();
      if (var7 != null && var7.equals(var1.getSubject())) {
         var7 = null;
      }

      SubjectPublicKeyInfo var8 = var0.getSubjectPublicKeyInfo();
      Extensions var9 = var0.getExtensions();
      if (var9 != null) {
         ExtensionsGenerator var10 = new ExtensionsGenerator();
         Enumeration var11 = var2.oids();

         while (var11.hasMoreElements()) {
            ASN1ObjectIdentifier var12 = (ASN1ObjectIdentifier)var11.nextElement();
            if (!Extension.deltaCertificateDescriptor.equals(var12)) {
               Extension var13 = var9.getExtension(var12);
               if (var13 != null && !var13.equals(var2.getExtension(var12))) {
                  var10.addExtension(var13);
               }
            }
         }

         var9 = var10.isEmpty() ? null : var10.generate();
      }

      ASN1BitString var14 = var0.getSignatureValue();
      return new DeltaCertificateDescriptor(var3, var4, var5, var6, var7, var8, var9, var14);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, boolean var3, ASN1Object var4) {
      if (var4 != null) {
         var1.add(new DERTaggedObject(var3, var2, var4));
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(8);
      var1.add(this.serialNumber);
      this.addOptional(var1, 0, true, this.signature);
      this.addOptional(var1, 1, true, this.issuer);
      this.addOptional(var1, 2, true, this.validity);
      this.addOptional(var1, 3, true, this.subject);
      var1.add(this.subjectPublicKeyInfo);
      this.addOptional(var1, 4, true, this.extensions);
      var1.add(this.signatureValue);
      return new DERSequence(var1);
   }
}
