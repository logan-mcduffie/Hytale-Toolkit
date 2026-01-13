package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v1CertificateBuilder {
   private V1TBSCertificateGenerator tbsGen;

   public X509v1CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, SubjectPublicKeyInfo var6) {
      this(var1, var2, new Time(var3), new Time(var4), var5, var6);
   }

   public X509v1CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, Locale var5, X500Name var6, SubjectPublicKeyInfo var7) {
      this(var1, var2, new Time(var3, var5), new Time(var4, var5), var6, var7);
   }

   public X509v1CertificateBuilder(X500Name var1, BigInteger var2, Time var3, Time var4, X500Name var5, SubjectPublicKeyInfo var6) {
      if (var1 == null) {
         throw new IllegalArgumentException("issuer must not be null");
      } else if (var6 == null) {
         throw new IllegalArgumentException("publicKeyInfo must not be null");
      } else {
         this.tbsGen = new V1TBSCertificateGenerator();
         this.tbsGen.setSerialNumber(new ASN1Integer(var2));
         this.tbsGen.setIssuer(var1);
         this.tbsGen.setStartDate(var3);
         this.tbsGen.setEndDate(var4);
         this.tbsGen.setSubject(var5);
         this.tbsGen.setSubjectPublicKeyInfo(var6);
      }
   }

   public X509CertificateHolder build(ContentSigner var1) {
      this.tbsGen.setSignature(var1.getAlgorithmIdentifier());
      return CertUtils.generateFullCert(var1, this.tbsGen.generateTBSCertificate());
   }
}
