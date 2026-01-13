package org.bouncycastle.x509;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Strings;

class X509Util {
   private static Hashtable algorithms = new Hashtable();
   private static Hashtable params = new Hashtable();
   private static Set noParams = new HashSet();

   private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier var0, int var1) {
      return new RSASSAPSSparams(
         var0, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, var0), new ASN1Integer(var1), RSASSAPSSparams.DEFAULT_TRAILER_FIELD
      );
   }

   static ASN1ObjectIdentifier getAlgorithmOID(String var0) {
      var0 = Strings.toUpperCase(var0);
      return algorithms.containsKey(var0) ? (ASN1ObjectIdentifier)algorithms.get(var0) : new ASN1ObjectIdentifier(var0);
   }

   static AlgorithmIdentifier getSigAlgID(ASN1ObjectIdentifier var0, String var1) {
      if (noParams.contains(var0)) {
         return new AlgorithmIdentifier(var0);
      } else {
         var1 = Strings.toUpperCase(var1);
         return params.containsKey(var1) ? new AlgorithmIdentifier(var0, (ASN1Encodable)params.get(var1)) : new AlgorithmIdentifier(var0, DERNull.INSTANCE);
      }
   }

   static Iterator getAlgNames() {
      Enumeration var0 = algorithms.keys();
      ArrayList var1 = new ArrayList();

      while (var0.hasMoreElements()) {
         var1.add(var0.nextElement());
      }

      return var1.iterator();
   }

   static Signature getSignatureInstance(String var0) throws NoSuchAlgorithmException {
      return Signature.getInstance(var0);
   }

   static Signature getSignatureInstance(String var0, String var1) throws NoSuchProviderException, NoSuchAlgorithmException {
      return var1 != null ? Signature.getInstance(var0, var1) : Signature.getInstance(var0);
   }

   static byte[] calculateSignature(ASN1ObjectIdentifier var0, String var1, PrivateKey var2, SecureRandom var3, ASN1Encodable var4) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      if (var0 == null) {
         throw new IllegalStateException("no signature algorithm specified");
      } else {
         Signature var5 = getSignatureInstance(var1);
         if (var3 != null) {
            var5.initSign(var2, var3);
         } else {
            var5.initSign(var2);
         }

         var5.update(var4.toASN1Primitive().getEncoded("DER"));
         return var5.sign();
      }
   }

   static byte[] calculateSignature(ASN1ObjectIdentifier var0, String var1, String var2, PrivateKey var3, SecureRandom var4, ASN1Encodable var5) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      if (var0 == null) {
         throw new IllegalStateException("no signature algorithm specified");
      } else {
         Signature var6 = getSignatureInstance(var1, var2);
         if (var4 != null) {
            var6.initSign(var3, var4);
         } else {
            var6.initSign(var3);
         }

         var6.update(var5.toASN1Primitive().getEncoded("DER"));
         return var6.sign();
      }
   }

   static X509Principal convertPrincipal(X500Principal var0) {
      try {
         return new X509Principal(var0.getEncoded());
      } catch (IOException var2) {
         throw new IllegalArgumentException("cannot convert principal");
      }
   }

   static X509Util.Implementation getImplementation(String var0, String var1, Provider var2) throws NoSuchAlgorithmException {
      var1 = Strings.toUpperCase(var1);

      String var3;
      while ((var3 = var2.getProperty("Alg.Alias." + var0 + "." + var1)) != null) {
         var1 = var3;
      }

      String var4 = var2.getProperty(var0 + "." + var1);
      if (var4 != null) {
         try {
            ClassLoader var6 = var2.getClass().getClassLoader();
            Class var5;
            if (var6 != null) {
               var5 = var6.loadClass(var4);
            } else {
               var5 = Class.forName(var4);
            }

            return new X509Util.Implementation(var5.newInstance(), var2);
         } catch (ClassNotFoundException var7) {
            throw new IllegalStateException("algorithm " + var1 + " in provider " + var2.getName() + " but no class \"" + var4 + "\" found!");
         } catch (Exception var8) {
            throw new IllegalStateException("algorithm " + var1 + " in provider " + var2.getName() + " but class \"" + var4 + "\" inaccessible!");
         }
      } else {
         throw new NoSuchAlgorithmException("cannot find implementation " + var1 + " for provider " + var2.getName());
      }
   }

   static X509Util.Implementation getImplementation(String var0, String var1) throws NoSuchAlgorithmException {
      Provider[] var2 = Security.getProviders();

      for (int var3 = 0; var3 != var2.length; var3++) {
         X509Util.Implementation var4 = getImplementation(var0, Strings.toUpperCase(var1), var2[var3]);
         if (var4 != null) {
            return var4;
         }

         try {
            var4 = getImplementation(var0, var1, var2[var3]);
         } catch (NoSuchAlgorithmException var6) {
         }
      }

      throw new NoSuchAlgorithmException("cannot find implementation " + var1);
   }

   static Provider getProvider(String var0) throws NoSuchProviderException {
      Provider var1 = Security.getProvider(var0);
      if (var1 == null) {
         throw new NoSuchProviderException("Provider " + var0 + " not found");
      } else {
         return var1;
      }
   }

   static {
      algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
      algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
      algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
      algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
      algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
      algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
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
      algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
      algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
      algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
      algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
      algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
      algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
      algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
      algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
      algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
      algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
      algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
      algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
      algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
      algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
      algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
      algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
      algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
      algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
      algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
      algorithms.put("GOST3411WITHGOST3410-94", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
      algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
      noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
      noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
      noParams.add(OIWObjectIdentifiers.dsaWithSHA1);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha384);
      noParams.add(NISTObjectIdentifiers.dsa_with_sha512);
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

   static class Implementation {
      Object engine;
      Provider provider;

      Implementation(Object var1, Provider var2) {
         this.engine = var1;
         this.provider = var2;
      }

      Object getEngine() {
         return this.engine;
      }

      Provider getProvider() {
         return this.provider;
      }
   }
}
