package org.bouncycastle.jce;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;

public class PrincipalUtil {
   public static X509Principal getIssuerX509Principal(X509Certificate var0) throws CertificateEncodingException {
      try {
         TBSCertificateStructure var1 = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(var0.getTBSCertificate()));
         return new X509Principal(X509Name.getInstance(var1.getIssuer()));
      } catch (IOException var2) {
         throw new CertificateEncodingException(var2.toString());
      }
   }

   public static X509Principal getSubjectX509Principal(X509Certificate var0) throws CertificateEncodingException {
      try {
         TBSCertificateStructure var1 = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(var0.getTBSCertificate()));
         return new X509Principal(X509Name.getInstance(var1.getSubject()));
      } catch (IOException var2) {
         throw new CertificateEncodingException(var2.toString());
      }
   }

   public static X509Principal getIssuerX509Principal(X509CRL var0) throws CRLException {
      try {
         TBSCertList var1 = TBSCertList.getInstance(ASN1Primitive.fromByteArray(var0.getTBSCertList()));
         return new X509Principal(X509Name.getInstance(var1.getIssuer()));
      } catch (IOException var2) {
         throw new CRLException(var2.toString());
      }
   }
}
