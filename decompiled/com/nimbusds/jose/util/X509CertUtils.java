package com.nimbusds.jose.util;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class X509CertUtils {
   public static final String PEM_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----";
   public static final String PEM_END_MARKER = "-----END CERTIFICATE-----";
   private static Provider jcaProvider;

   public static Provider getProvider() {
      return jcaProvider;
   }

   public static void setProvider(Provider provider) {
      jcaProvider = provider;
   }

   public static X509Certificate parse(byte[] derEncodedCert) {
      try {
         return parseWithException(derEncodedCert);
      } catch (CertificateException var2) {
         return null;
      }
   }

   public static X509Certificate parseWithException(byte[] derEncodedCert) throws CertificateException {
      if (derEncodedCert != null && derEncodedCert.length != 0) {
         CertificateFactory cf = jcaProvider != null ? CertificateFactory.getInstance("X.509", jcaProvider) : CertificateFactory.getInstance("X.509");
         Certificate cert = cf.generateCertificate(new ByteArrayInputStream(derEncodedCert));
         if (!(cert instanceof X509Certificate)) {
            throw new CertificateException("Not a X.509 certificate: " + cert.getType());
         } else {
            return (X509Certificate)cert;
         }
      } else {
         return null;
      }
   }

   public static X509Certificate parse(String pemEncodedCert) {
      if (pemEncodedCert != null && !pemEncodedCert.isEmpty()) {
         int markerStart = pemEncodedCert.indexOf("-----BEGIN CERTIFICATE-----");
         if (markerStart < 0) {
            return null;
         } else {
            String buf = pemEncodedCert.substring(markerStart + "-----BEGIN CERTIFICATE-----".length());
            int markerEnd = buf.indexOf("-----END CERTIFICATE-----");
            if (markerEnd < 0) {
               return null;
            } else {
               buf = buf.substring(0, markerEnd);
               buf = buf.replaceAll("\\s", "");
               return parse(new Base64(buf).decode());
            }
         }
      } else {
         return null;
      }
   }

   public static X509Certificate parseWithException(String pemEncodedCert) throws CertificateException {
      if (pemEncodedCert != null && !pemEncodedCert.isEmpty()) {
         int markerStart = pemEncodedCert.indexOf("-----BEGIN CERTIFICATE-----");
         if (markerStart < 0) {
            throw new CertificateException("PEM begin marker not found");
         } else {
            String buf = pemEncodedCert.substring(markerStart + "-----BEGIN CERTIFICATE-----".length());
            int markerEnd = buf.indexOf("-----END CERTIFICATE-----");
            if (markerEnd < 0) {
               throw new CertificateException("PEM end marker not found");
            } else {
               buf = buf.substring(0, markerEnd);
               buf = buf.replaceAll("\\s", "");
               return parseWithException(new Base64(buf).decode());
            }
         }
      } else {
         return null;
      }
   }

   public static String toPEMString(X509Certificate cert) {
      return toPEMString(cert, true);
   }

   public static String toPEMString(X509Certificate cert, boolean withLineBreaks) {
      StringBuilder sb = new StringBuilder();
      sb.append("-----BEGIN CERTIFICATE-----");
      if (withLineBreaks) {
         sb.append('\n');
      }

      try {
         sb.append(Base64.encode(cert.getEncoded()));
      } catch (CertificateEncodingException var4) {
         return null;
      }

      if (withLineBreaks) {
         sb.append('\n');
      }

      sb.append("-----END CERTIFICATE-----");
      return sb.toString();
   }

   public static Base64URL computeSHA256Thumbprint(X509Certificate cert) {
      try {
         byte[] derEncodedCert = cert.getEncoded();
         MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
         return Base64URL.encode(sha256.digest(derEncodedCert));
      } catch (CertificateEncodingException | NoSuchAlgorithmException var3) {
         return null;
      }
   }

   public static UUID store(KeyStore keyStore, PrivateKey privateKey, char[] keyPassword, X509Certificate cert) throws KeyStoreException {
      UUID alias = UUID.randomUUID();
      keyStore.setKeyEntry(alias.toString(), privateKey, keyPassword, new Certificate[]{cert});
      return alias;
   }
}
