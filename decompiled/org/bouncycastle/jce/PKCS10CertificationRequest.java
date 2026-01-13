package org.bouncycastle.jce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Strings;

/** @deprecated */
public class PKCS10CertificationRequest extends CertificationRequest {
   private static Hashtable algorithms = new Hashtable();
   private static Hashtable params = new Hashtable();
   private static Hashtable keyAlgorithms = new Hashtable();
   private static Hashtable oids = new Hashtable();
   private static Set noParams = new HashSet();

   private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier var0, int var1) {
      return new RSASSAPSSparams(
         var0, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, var0), new ASN1Integer(var1), RSASSAPSSparams.DEFAULT_TRAILER_FIELD
      );
   }

   private static ASN1Sequence toDERSequence(byte[] var0) {
      try {
         ASN1InputStream var1 = new ASN1InputStream(var0);
         return (ASN1Sequence)var1.readObject();
      } catch (Exception var2) {
         throw new IllegalArgumentException("badly encoded request");
      }
   }

   public PKCS10CertificationRequest(byte[] var1) {
      super(toDERSequence(var1));
   }

   public PKCS10CertificationRequest(ASN1Sequence var1) {
      super(var1);
   }

   public PKCS10CertificationRequest(String var1, X509Name var2, PublicKey var3, ASN1Set var4, PrivateKey var5) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      this(var1, var2, var3, var4, var5, "BC");
   }

   private static X509Name convertName(X500Principal var0) {
      try {
         return new X509Principal(var0.getEncoded());
      } catch (IOException var2) {
         throw new IllegalArgumentException("can't convert name");
      }
   }

   public PKCS10CertificationRequest(String var1, X500Principal var2, PublicKey var3, ASN1Set var4, PrivateKey var5) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      this(var1, convertName(var2), var3, var4, var5, "BC");
   }

   public PKCS10CertificationRequest(String var1, X500Principal var2, PublicKey var3, ASN1Set var4, PrivateKey var5, String var6) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      this(var1, convertName(var2), var3, var4, var5, var6);
   }

   public PKCS10CertificationRequest(String var1, X509Name var2, PublicKey var3, ASN1Set var4, PrivateKey var5, String var6) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      String var7 = Strings.toUpperCase(var1);
      ASN1ObjectIdentifier var8 = (ASN1ObjectIdentifier)algorithms.get(var7);
      if (var8 == null) {
         try {
            var8 = new ASN1ObjectIdentifier(var7);
         } catch (Exception var13) {
            throw new IllegalArgumentException("Unknown signature type requested");
         }
      }

      if (var2 == null) {
         throw new IllegalArgumentException("subject must not be null");
      } else if (var3 == null) {
         throw new IllegalArgumentException("public key must not be null");
      } else {
         if (noParams.contains(var8)) {
            this.sigAlgId = new AlgorithmIdentifier(var8);
         } else if (params.containsKey(var7)) {
            this.sigAlgId = new AlgorithmIdentifier(var8, (ASN1Encodable)params.get(var7));
         } else {
            this.sigAlgId = new AlgorithmIdentifier(var8, DERNull.INSTANCE);
         }

         try {
            ASN1Sequence var9 = (ASN1Sequence)ASN1Primitive.fromByteArray(var3.getEncoded());
            this.reqInfo = new CertificationRequestInfo(var2, SubjectPublicKeyInfo.getInstance(var9), var4);
         } catch (IOException var12) {
            throw new IllegalArgumentException("can't encode public key");
         }

         Signature var14;
         if (var6 == null) {
            var14 = Signature.getInstance(var1);
         } else {
            var14 = Signature.getInstance(var1, var6);
         }

         var14.initSign(var5);

         try {
            var14.update(this.reqInfo.getEncoded("DER"));
         } catch (Exception var11) {
            throw new IllegalArgumentException("exception encoding TBS cert request - " + var11);
         }

         this.sigBits = new DERBitString(var14.sign());
      }
   }

   public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      return this.getPublicKey("BC");
   }

   public PublicKey getPublicKey(String var1) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      SubjectPublicKeyInfo var2 = this.reqInfo.getSubjectPublicKeyInfo();

      try {
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(new DERBitString(var2).getOctets());
         AlgorithmIdentifier var4 = var2.getAlgorithm();

         try {
            return var1 == null
               ? KeyFactory.getInstance(var4.getAlgorithm().getId()).generatePublic(var3)
               : KeyFactory.getInstance(var4.getAlgorithm().getId(), var1).generatePublic(var3);
         } catch (NoSuchAlgorithmException var7) {
            if (keyAlgorithms.get(var4.getAlgorithm()) != null) {
               String var6 = (String)keyAlgorithms.get(var4.getAlgorithm());
               return var1 == null ? KeyFactory.getInstance(var6).generatePublic(var3) : KeyFactory.getInstance(var6, var1).generatePublic(var3);
            } else {
               throw var7;
            }
         }
      } catch (InvalidKeySpecException var8) {
         throw new InvalidKeyException("error decoding public key");
      } catch (IOException var9) {
         throw new InvalidKeyException("error decoding public key");
      }
   }

   public boolean verify() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      return this.verify("BC");
   }

   public boolean verify(String var1) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      return this.verify(this.getPublicKey(var1), var1);
   }

   public boolean verify(PublicKey var1, String var2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
      Signature var3;
      try {
         if (var2 == null) {
            var3 = Signature.getInstance(getSignatureName(this.sigAlgId));
         } else {
            var3 = Signature.getInstance(getSignatureName(this.sigAlgId), var2);
         }
      } catch (NoSuchAlgorithmException var7) {
         if (oids.get(this.sigAlgId.getAlgorithm()) == null) {
            throw var7;
         }

         String var5 = (String)oids.get(this.sigAlgId.getAlgorithm());
         if (var2 == null) {
            var3 = Signature.getInstance(var5);
         } else {
            var3 = Signature.getInstance(var5, var2);
         }
      }

      this.setSignatureParameters(var3, this.sigAlgId.getParameters());
      var3.initVerify(var1);

      try {
         var3.update(this.reqInfo.getEncoded("DER"));
      } catch (Exception var6) {
         throw new SignatureException("exception encoding TBS cert request - " + var6);
      }

      return var3.verify(this.sigBits.getOctets());
   }

   @Override
   public byte[] getEncoded() {
      try {
         return this.getEncoded("DER");
      } catch (IOException var2) {
         throw new RuntimeException(var2.toString());
      }
   }

   private void setSignatureParameters(Signature var1, ASN1Encodable var2) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      if (var2 != null && !DERNull.INSTANCE.equals(var2)) {
         AlgorithmParameters var3 = AlgorithmParameters.getInstance(var1.getAlgorithm(), var1.getProvider());

         try {
            var3.init(var2.toASN1Primitive().getEncoded("DER"));
         } catch (IOException var6) {
            throw new SignatureException("IOException decoding parameters: " + var6.getMessage());
         }

         if (var1.getAlgorithm().endsWith("MGF1")) {
            try {
               var1.setParameter(var3.getParameterSpec(PSSParameterSpec.class));
            } catch (GeneralSecurityException var5) {
               throw new SignatureException("Exception extracting parameters: " + var5.getMessage());
            }
         }
      }
   }

   static String getSignatureName(AlgorithmIdentifier var0) {
      ASN1Encodable var1 = var0.getParameters();
      if (var1 != null && !DERNull.INSTANCE.equals(var1) && var0.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
         RSASSAPSSparams var2 = RSASSAPSSparams.getInstance(var1);
         return getDigestAlgName(var2.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
      } else {
         return var0.getAlgorithm().getId();
      }
   }

   private static String getDigestAlgName(ASN1ObjectIdentifier var0) {
      if (PKCSObjectIdentifiers.md5.equals(var0)) {
         return "MD5";
      } else if (OIWObjectIdentifiers.idSHA1.equals(var0)) {
         return "SHA1";
      } else if (NISTObjectIdentifiers.id_sha224.equals(var0)) {
         return "SHA224";
      } else if (NISTObjectIdentifiers.id_sha256.equals(var0)) {
         return "SHA256";
      } else if (NISTObjectIdentifiers.id_sha384.equals(var0)) {
         return "SHA384";
      } else if (NISTObjectIdentifiers.id_sha512.equals(var0)) {
         return "SHA512";
      } else if (TeleTrusTObjectIdentifiers.ripemd128.equals(var0)) {
         return "RIPEMD128";
      } else if (TeleTrusTObjectIdentifiers.ripemd160.equals(var0)) {
         return "RIPEMD160";
      } else if (TeleTrusTObjectIdentifiers.ripemd256.equals(var0)) {
         return "RIPEMD256";
      } else {
         return CryptoProObjectIdentifiers.gostR3411.equals(var0) ? "GOST3411" : var0.getId();
      }
   }

   static {
      algorithms.put("MD2WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
      algorithms.put("MD2WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
      algorithms.put("MD5WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
      algorithms.put("MD5WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
      algorithms.put("RSAWITHMD5", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
      algorithms.put("SHA1WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
      algorithms.put("SHA1WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
      algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
      algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
      algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
      algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
      algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
      algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
      algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
      algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
      algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
      algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
      algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
      algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
      algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
      algorithms.put("RSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
      algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
      algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
      algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
      algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
      algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
      algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
      algorithms.put("SHA1WITHDSA", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
      algorithms.put("DSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
      algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
      algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
      algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
      algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
      algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
      algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
      algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
      algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
      algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
      algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
      algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
      algorithms.put("GOST3410WITHGOST3411", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
      algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
      oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
      oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
      oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
      oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
      oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
      oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
      oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
      keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
      keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
      noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
      noParams.add(OIWObjectIdentifiers.dsaWithSHA1);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
      noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
      noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      AlgorithmIdentifier var0 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
      params.put("SHA1WITHRSAANDMGF1", creatPSSParams(var0, 20));
      AlgorithmIdentifier var1 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE);
      params.put("SHA224WITHRSAANDMGF1", creatPSSParams(var1, 28));
      AlgorithmIdentifier var2 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
      params.put("SHA256WITHRSAANDMGF1", creatPSSParams(var2, 32));
      AlgorithmIdentifier var3 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE);
      params.put("SHA384WITHRSAANDMGF1", creatPSSParams(var3, 48));
      AlgorithmIdentifier var4 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE);
      params.put("SHA512WITHRSAANDMGF1", creatPSSParams(var4, 64));
   }
}
