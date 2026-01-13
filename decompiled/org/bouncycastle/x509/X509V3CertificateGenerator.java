package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509ExtensionsGenerator;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

/** @deprecated */
public class X509V3CertificateGenerator {
   private final JcaJceHelper bcHelper = new BCJcaJceHelper();
   private final CertificateFactory certificateFactory = new CertificateFactory();
   private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
   private ASN1ObjectIdentifier sigOID;
   private AlgorithmIdentifier sigAlgId;
   private String signatureAlgorithm;
   private X509ExtensionsGenerator extGenerator = new X509ExtensionsGenerator();

   public void reset() {
      this.tbsGen = new V3TBSCertificateGenerator();
      this.extGenerator.reset();
   }

   public void setSerialNumber(BigInteger var1) {
      if (var1.compareTo(BigInteger.ZERO) <= 0) {
         throw new IllegalArgumentException("serial number must be a positive integer");
      } else {
         this.tbsGen.setSerialNumber(new ASN1Integer(var1));
      }
   }

   public void setIssuerDN(X500Principal var1) {
      try {
         this.tbsGen.setIssuer(new X509Principal(var1.getEncoded()));
      } catch (IOException var3) {
         throw new IllegalArgumentException("can't process principal: " + var3);
      }
   }

   public void setIssuerDN(X509Name var1) {
      this.tbsGen.setIssuer(var1);
   }

   public void setNotBefore(Date var1) {
      this.tbsGen.setStartDate(new Time(var1));
   }

   public void setNotAfter(Date var1) {
      this.tbsGen.setEndDate(new Time(var1));
   }

   public void setSubjectDN(X500Principal var1) {
      try {
         this.tbsGen.setSubject(new X509Principal(var1.getEncoded()));
      } catch (IOException var3) {
         throw new IllegalArgumentException("can't process principal: " + var3);
      }
   }

   public void setSubjectDN(X509Name var1) {
      this.tbsGen.setSubject(var1);
   }

   public void setPublicKey(PublicKey var1) throws IllegalArgumentException {
      try {
         this.tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(var1.getEncoded()).readObject()));
      } catch (Exception var3) {
         throw new IllegalArgumentException("unable to process key - " + var3.toString());
      }
   }

   public void setSignatureAlgorithm(String var1) {
      this.signatureAlgorithm = var1;

      try {
         this.sigOID = X509Util.getAlgorithmOID(var1);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Unknown signature type requested: " + var1);
      }

      this.sigAlgId = X509Util.getSigAlgID(this.sigOID, var1);
      this.tbsGen.setSignature(this.sigAlgId);
   }

   public void setSubjectUniqueID(boolean[] var1) {
      this.tbsGen.setSubjectUniqueID(this.booleanToBitString(var1));
   }

   public void setIssuerUniqueID(boolean[] var1) {
      this.tbsGen.setIssuerUniqueID(this.booleanToBitString(var1));
   }

   private DERBitString booleanToBitString(boolean[] var1) {
      byte[] var2 = new byte[(var1.length + 7) / 8];

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2[var3 / 8] = (byte)(var2[var3 / 8] | (var1[var3] ? 1 << 7 - var3 % 8 : 0));
      }

      int var4 = var1.length % 8;
      return var4 == 0 ? new DERBitString(var2) : new DERBitString(var2, 8 - var4);
   }

   public void addExtension(String var1, boolean var2, ASN1Encodable var3) {
      this.addExtension(new ASN1ObjectIdentifier(var1), var2, var3);
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) {
      this.extGenerator.addExtension(new ASN1ObjectIdentifier(var1.getId()), var2, var3);
   }

   public void addExtension(String var1, boolean var2, byte[] var3) {
      this.addExtension(new ASN1ObjectIdentifier(var1), var2, var3);
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      this.extGenerator.addExtension(new ASN1ObjectIdentifier(var1.getId()), var2, var3);
   }

   public void copyAndAddExtension(String var1, boolean var2, X509Certificate var3) throws CertificateParsingException {
      byte[] var4 = var3.getExtensionValue(var1);
      if (var4 == null) {
         throw new CertificateParsingException("extension " + var1 + " not present");
      } else {
         try {
            ASN1Primitive var5 = X509ExtensionUtil.fromExtensionValue(var4);
            this.addExtension(var1, var2, var5);
         } catch (IOException var6) {
            throw new CertificateParsingException(var6.toString());
         }
      }
   }

   public void copyAndAddExtension(ASN1ObjectIdentifier var1, boolean var2, X509Certificate var3) throws CertificateParsingException {
      this.copyAndAddExtension(var1.getId(), var2, var3);
   }

   /** @deprecated */
   public X509Certificate generateX509Certificate(PrivateKey var1) throws SecurityException, SignatureException, InvalidKeyException {
      try {
         return this.generateX509Certificate(var1, "BC", null);
      } catch (NoSuchProviderException var3) {
         throw new SecurityException("BC provider not installed!");
      }
   }

   /** @deprecated */
   public X509Certificate generateX509Certificate(PrivateKey var1, SecureRandom var2) throws SecurityException, SignatureException, InvalidKeyException {
      try {
         return this.generateX509Certificate(var1, "BC", var2);
      } catch (NoSuchProviderException var4) {
         throw new SecurityException("BC provider not installed!");
      }
   }

   /** @deprecated */
   public X509Certificate generateX509Certificate(PrivateKey var1, String var2) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
      return this.generateX509Certificate(var1, var2, null);
   }

   /** @deprecated */
   public X509Certificate generateX509Certificate(PrivateKey var1, String var2, SecureRandom var3) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
      try {
         return this.generate(var1, var2, var3);
      } catch (NoSuchProviderException var5) {
         throw var5;
      } catch (SignatureException var6) {
         throw var6;
      } catch (InvalidKeyException var7) {
         throw var7;
      } catch (GeneralSecurityException var8) {
         throw new SecurityException("exception: " + var8);
      }
   }

   public X509Certificate generate(PrivateKey var1) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      return this.generate(var1, (SecureRandom)null);
   }

   public X509Certificate generate(PrivateKey var1, SecureRandom var2) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      TBSCertificate var3 = this.generateTbsCert();

      byte[] var4;
      try {
         var4 = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, var1, var2, var3);
      } catch (IOException var7) {
         throw new ExtCertificateEncodingException("exception encoding TBS cert", var7);
      }

      try {
         return this.generateJcaObject(var3, var4);
      } catch (Exception var6) {
         throw new ExtCertificateEncodingException("exception producing certificate object", var6);
      }
   }

   public X509Certificate generate(PrivateKey var1, String var2) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      return this.generate(var1, var2, null);
   }

   public X509Certificate generate(PrivateKey var1, String var2, SecureRandom var3) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      TBSCertificate var4 = this.generateTbsCert();

      byte[] var5;
      try {
         var5 = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, var2, var1, var3, var4);
      } catch (IOException var8) {
         throw new ExtCertificateEncodingException("exception encoding TBS cert", var8);
      }

      try {
         return this.generateJcaObject(var4, var5);
      } catch (Exception var7) {
         throw new ExtCertificateEncodingException("exception producing certificate object", var7);
      }
   }

   private TBSCertificate generateTbsCert() {
      if (!this.extGenerator.isEmpty()) {
         this.tbsGen.setExtensions(this.extGenerator.generate());
      }

      return this.tbsGen.generateTBSCertificate();
   }

   private X509Certificate generateJcaObject(TBSCertificate var1, byte[] var2) throws Exception {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var1);
      var3.add(this.sigAlgId);
      var3.add(new DERBitString(var2));
      return (X509Certificate)this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(new DERSequence(var3).getEncoded("DER")));
   }

   public Iterator getSignatureAlgNames() {
      return X509Util.getAlgNames();
   }
}
