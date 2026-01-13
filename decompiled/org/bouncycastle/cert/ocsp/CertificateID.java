package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CertificateID {
   public static final AlgorithmIdentifier HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
   private final CertID id;

   public CertificateID(CertID var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("'id' cannot be null");
      } else {
         this.id = var1;
      }
   }

   public CertificateID(DigestCalculator var1, X509CertificateHolder var2, BigInteger var3) throws OCSPException {
      this.id = createCertID(var1, var2, new ASN1Integer(var3));
   }

   public ASN1ObjectIdentifier getHashAlgOID() {
      return this.id.getHashAlgorithm().getAlgorithm();
   }

   public byte[] getIssuerNameHash() {
      return this.id.getIssuerNameHash().getOctets();
   }

   public byte[] getIssuerKeyHash() {
      return this.id.getIssuerKeyHash().getOctets();
   }

   public BigInteger getSerialNumber() {
      return this.id.getSerialNumber().getValue();
   }

   public boolean matchesIssuer(X509CertificateHolder var1, DigestCalculatorProvider var2) throws OCSPException {
      try {
         return createCertID(var2.get(this.id.getHashAlgorithm()), var1, this.id.getSerialNumber()).equals(this.id);
      } catch (OperatorCreationException var4) {
         throw new OCSPException("unable to create digest calculator: " + var4.getMessage(), var4);
      }
   }

   public CertID toASN1Primitive() {
      return this.id;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof CertificateID)) {
         return false;
      } else {
         CertificateID var2 = (CertificateID)var1;
         return this.id.toASN1Primitive().equals(var2.id.toASN1Primitive());
      }
   }

   @Override
   public int hashCode() {
      return this.id.toASN1Primitive().hashCode();
   }

   public static CertificateID deriveCertificateID(CertificateID var0, BigInteger var1) {
      return new CertificateID(new CertID(var0.id.getHashAlgorithm(), var0.id.getIssuerNameHash(), var0.id.getIssuerKeyHash(), new ASN1Integer(var1)));
   }

   private static CertID createCertID(DigestCalculator var0, X509CertificateHolder var1, ASN1Integer var2) throws OCSPException {
      try {
         OutputStream var3 = var0.getOutputStream();
         var3.write(var1.toASN1Structure().getSubject().getEncoded("DER"));
         var3.close();
         DEROctetString var4 = new DEROctetString(var0.getDigest());
         SubjectPublicKeyInfo var5 = var1.getSubjectPublicKeyInfo();
         var3 = var0.getOutputStream();
         var3.write(var5.getPublicKeyData().getBytes());
         var3.close();
         DEROctetString var6 = new DEROctetString(var0.getDigest());
         return new CertID(var0.getAlgorithmIdentifier(), var4, var6, var2);
      } catch (Exception var7) {
         throw new OCSPException("problem creating ID: " + var7, var7);
      }
   }
}
