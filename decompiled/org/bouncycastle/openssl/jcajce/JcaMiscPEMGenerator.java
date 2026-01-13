package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;

public class JcaMiscPEMGenerator extends MiscPEMGenerator {
   private Object obj;
   private String algorithm;
   private char[] password;
   private SecureRandom random;
   private Provider provider;

   public JcaMiscPEMGenerator(Object var1) throws IOException {
      super(convertObject(var1));
   }

   public JcaMiscPEMGenerator(Object var1, PEMEncryptor var2) throws IOException {
      super(convertObject(var1), var2);
   }

   private static Object convertObject(Object var0) throws IOException {
      if (var0 instanceof X509Certificate) {
         try {
            return new JcaX509CertificateHolder((X509Certificate)var0);
         } catch (CertificateEncodingException var2) {
            throw new IllegalArgumentException("Cannot encode object: " + var2.toString());
         }
      } else if (var0 instanceof X509CRL) {
         try {
            return new JcaX509CRLHolder((X509CRL)var0);
         } catch (CRLException var3) {
            throw new IllegalArgumentException("Cannot encode object: " + var3.toString());
         }
      } else if (var0 instanceof KeyPair) {
         return convertObject(((KeyPair)var0).getPrivate());
      } else if (var0 instanceof PrivateKey) {
         return PrivateKeyInfo.getInstance(((Key)var0).getEncoded());
      } else {
         return var0 instanceof PublicKey ? SubjectPublicKeyInfo.getInstance(((PublicKey)var0).getEncoded()) : var0;
      }
   }
}
