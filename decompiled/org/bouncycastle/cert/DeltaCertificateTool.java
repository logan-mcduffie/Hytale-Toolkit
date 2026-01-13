package org.bouncycastle.cert;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Validity;

public class DeltaCertificateTool {
   public static Extension makeDeltaCertificateExtension(boolean var0, Certificate var1) throws IOException {
      DeltaCertificateDescriptor var2 = new DeltaCertificateDescriptor(
         var1.getSerialNumber(),
         var1.getSignatureAlgorithm(),
         var1.getIssuer(),
         var1.getValidity(),
         var1.getSubject(),
         var1.getSubjectPublicKeyInfo(),
         var1.getExtensions(),
         var1.getSignature()
      );
      DEROctetString var3 = new DEROctetString(var2.getEncoded("DER"));
      return new Extension(Extension.deltaCertificateDescriptor, var0, var3);
   }

   public static Extension makeDeltaCertificateExtension(boolean var0, X509CertificateHolder var1) throws IOException {
      return makeDeltaCertificateExtension(var0, var1.toASN1Structure());
   }

   public static Certificate extractDeltaCertificate(TBSCertificate var0) {
      Extensions var1 = var0.getExtensions();
      Extension var2 = var1.getExtension(Extension.deltaCertificateDescriptor);
      if (var2 == null) {
         throw new IllegalStateException("no deltaCertificateDescriptor present");
      } else {
         DeltaCertificateDescriptor var3 = DeltaCertificateDescriptor.getInstance(var2.getParsedValue());
         ASN1Integer var4 = var0.getVersion();
         ASN1Integer var5 = var3.getSerialNumber();
         AlgorithmIdentifier var6 = var3.getSignature();
         if (var6 == null) {
            var6 = var0.getSignature();
         }

         X500Name var7 = var3.getIssuer();
         if (var7 == null) {
            var7 = var0.getIssuer();
         }

         Validity var8 = var3.getValidityObject();
         if (var8 == null) {
            var8 = var0.getValidity();
         }

         X500Name var9 = var3.getSubject();
         if (var9 == null) {
            var9 = var0.getSubject();
         }

         SubjectPublicKeyInfo var10 = var3.getSubjectPublicKeyInfo();
         Extensions var11 = extractDeltaExtensions(var3.getExtensions(), var1);
         TBSCertificate var12 = new TBSCertificate(var4, var5, var6, var7, var8, var9, var10, null, null, var11);
         return new Certificate(var12, var6, var3.getSignatureValue());
      }
   }

   public static X509CertificateHolder extractDeltaCertificate(X509CertificateHolder var0) {
      return new X509CertificateHolder(extractDeltaCertificate(var0.getTBSCertificate()));
   }

   public static DeltaCertificateDescriptor trimDeltaCertificateDescriptor(DeltaCertificateDescriptor var0, TBSCertificate var1, Extensions var2) {
      return var0.trimTo(var1, var2);
   }

   private static Extensions extractDeltaExtensions(Extensions var0, Extensions var1) {
      ExtensionsGenerator var2 = new ExtensionsGenerator();
      Enumeration var3 = var1.oids();

      while (var3.hasMoreElements()) {
         ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var3.nextElement();
         if (!Extension.deltaCertificateDescriptor.equals(var4)) {
            var2.addExtension(var1.getExtension(var4));
         }
      }

      if (var0 != null) {
         Enumeration var6 = var0.oids();

         while (var6.hasMoreElements()) {
            ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var6.nextElement();
            var2.replaceExtension(var0.getExtension(var5));
         }
      }

      return var2.isEmpty() ? null : var2.generate();
   }
}
