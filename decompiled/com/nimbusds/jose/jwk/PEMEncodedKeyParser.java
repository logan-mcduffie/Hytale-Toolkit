package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

class PEMEncodedKeyParser {
   private static final JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();

   private PEMEncodedKeyParser() {
   }

   static List<KeyPair> parseKeys(String pemEncodedKeys) throws JOSEException {
      Reader pemReader = new StringReader(pemEncodedKeys);
      PEMParser parser = new PEMParser(pemReader);
      List<KeyPair> keys = new ArrayList<>();

      try {
         Object pemObj;
         do {
            pemObj = parser.readObject();
            if (pemObj instanceof SubjectPublicKeyInfo) {
               keys.add(toKeyPair((SubjectPublicKeyInfo)pemObj));
            } else if (pemObj instanceof X509CertificateHolder) {
               keys.add(toKeyPair((X509CertificateHolder)pemObj));
            } else if (pemObj instanceof PEMKeyPair) {
               keys.add(toKeyPair((PEMKeyPair)pemObj));
            } else if (pemObj instanceof PrivateKeyInfo) {
               keys.add(toKeyPair((PrivateKeyInfo)pemObj));
            }
         } while (pemObj != null);

         return keys;
      } catch (Exception var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }
   }

   private static KeyPair toKeyPair(SubjectPublicKeyInfo spki) throws PEMException {
      return new KeyPair(pemConverter.getPublicKey(spki), null);
   }

   private static KeyPair toKeyPair(X509CertificateHolder pemObj) throws PEMException {
      SubjectPublicKeyInfo spki = pemObj.getSubjectPublicKeyInfo();
      return new KeyPair(pemConverter.getPublicKey(spki), null);
   }

   private static KeyPair toKeyPair(PEMKeyPair pair) throws PEMException {
      return pemConverter.getKeyPair(pair);
   }

   private static KeyPair toKeyPair(PrivateKeyInfo pki) throws PEMException, NoSuchAlgorithmException, InvalidKeySpecException {
      PrivateKey privateKey = pemConverter.getPrivateKey(pki);
      if (privateKey instanceof RSAPrivateCrtKey) {
         RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(((RSAPrivateCrtKey)privateKey).getModulus(), ((RSAPrivateCrtKey)privateKey).getPublicExponent());
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
         return new KeyPair(publicKey, privateKey);
      } else {
         return new KeyPair(null, privateKey);
      }
   }
}
