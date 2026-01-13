package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509ExtensionsGenerator;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.X509CRLObject;

/** @deprecated */
public class X509V2CRLGenerator {
   private final JcaJceHelper bcHelper = new BCJcaJceHelper();
   private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
   private ASN1ObjectIdentifier sigOID;
   private AlgorithmIdentifier sigAlgId;
   private String signatureAlgorithm;
   private X509ExtensionsGenerator extGenerator = new X509ExtensionsGenerator();

   public void reset() {
      this.tbsGen = new V2TBSCertListGenerator();
      this.extGenerator.reset();
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

   public void setThisUpdate(Date var1) {
      this.tbsGen.setThisUpdate(new Time(var1));
   }

   public void setNextUpdate(Date var1) {
      this.tbsGen.setNextUpdate(new Time(var1));
   }

   public void addCRLEntry(BigInteger var1, Date var2, int var3) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), var3);
   }

   public void addCRLEntry(BigInteger var1, Date var2, int var3, Date var4) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), var3, new ASN1GeneralizedTime(var4));
   }

   public void addCRLEntry(BigInteger var1, Date var2, X509Extensions var3) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), Extensions.getInstance(var3));
   }

   public void addCRL(X509CRL var1) throws CRLException {
      Set var2 = var1.getRevokedCertificates();
      if (var2 != null) {
         for (X509CRLEntry var4 : var2) {
            ASN1InputStream var5 = new ASN1InputStream(var4.getEncoded());

            try {
               this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(var5.readObject()));
            } catch (IOException var7) {
               throw new CRLException("exception processing encoding of CRL: " + var7.toString());
            }
         }
      }
   }

   public void setSignatureAlgorithm(String var1) {
      this.signatureAlgorithm = var1;

      try {
         this.sigOID = X509Util.getAlgorithmOID(var1);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Unknown signature type requested");
      }

      this.sigAlgId = X509Util.getSigAlgID(this.sigOID, var1);
      this.tbsGen.setSignature(this.sigAlgId);
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

   /** @deprecated */
   public X509CRL generateX509CRL(PrivateKey var1) throws SecurityException, SignatureException, InvalidKeyException {
      try {
         return this.generateX509CRL(var1, "BC", null);
      } catch (NoSuchProviderException var3) {
         throw new SecurityException("BC provider not installed!");
      }
   }

   /** @deprecated */
   public X509CRL generateX509CRL(PrivateKey var1, SecureRandom var2) throws SecurityException, SignatureException, InvalidKeyException {
      try {
         return this.generateX509CRL(var1, "BC", var2);
      } catch (NoSuchProviderException var4) {
         throw new SecurityException("BC provider not installed!");
      }
   }

   /** @deprecated */
   public X509CRL generateX509CRL(PrivateKey var1, String var2) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
      return this.generateX509CRL(var1, var2, null);
   }

   /** @deprecated */
   public X509CRL generateX509CRL(PrivateKey var1, String var2, SecureRandom var3) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
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

   public X509CRL generate(PrivateKey var1) throws CRLException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      return this.generate(var1, (SecureRandom)null);
   }

   public X509CRL generate(PrivateKey var1, SecureRandom var2) throws CRLException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      TBSCertList var3 = this.generateCertList();

      byte[] var4;
      try {
         var4 = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, var1, var2, var3);
      } catch (IOException var6) {
         throw new X509V2CRLGenerator.ExtCRLException("cannot generate CRL encoding", var6);
      }

      return this.generateJcaObject(var3, var4);
   }

   public X509CRL generate(PrivateKey var1, String var2) throws CRLException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      return this.generate(var1, var2, null);
   }

   public X509CRL generate(PrivateKey var1, String var2, SecureRandom var3) throws CRLException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      TBSCertList var4 = this.generateCertList();

      byte[] var5;
      try {
         var5 = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, var2, var1, var3, var4);
      } catch (IOException var7) {
         throw new X509V2CRLGenerator.ExtCRLException("cannot generate CRL encoding", var7);
      }

      return this.generateJcaObject(var4, var5);
   }

   private TBSCertList generateCertList() {
      if (!this.extGenerator.isEmpty()) {
         this.tbsGen.setExtensions(this.extGenerator.generate());
      }

      return this.tbsGen.generateTBSCertList();
   }

   private X509CRL generateJcaObject(TBSCertList var1, byte[] var2) throws CRLException {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var1);
      var3.add(this.sigAlgId);
      var3.add(new DERBitString(var2));
      return new X509CRLObject(CertificateList.getInstance(new DERSequence(var3)));
   }

   public Iterator getSignatureAlgNames() {
      return X509Util.getAlgNames();
   }

   private static class ExtCRLException extends CRLException {
      Throwable cause;

      ExtCRLException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
