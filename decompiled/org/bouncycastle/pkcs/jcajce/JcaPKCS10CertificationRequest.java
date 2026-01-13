package org.bouncycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class JcaPKCS10CertificationRequest extends PKCS10CertificationRequest {
   private static Hashtable keyAlgorithms = new Hashtable();
   private JcaJceHelper helper = new DefaultJcaJceHelper();

   public JcaPKCS10CertificationRequest(CertificationRequest var1) {
      super(var1);
   }

   public JcaPKCS10CertificationRequest(byte[] var1) throws IOException {
      super(var1);
   }

   public JcaPKCS10CertificationRequest(PKCS10CertificationRequest var1) {
      super(var1.toASN1Structure());
   }

   public JcaPKCS10CertificationRequest setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JcaPKCS10CertificationRequest setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public PublicKey getPublicKey() throws InvalidKeyException, NoSuchAlgorithmException {
      try {
         SubjectPublicKeyInfo var1 = this.getSubjectPublicKeyInfo();
         X509EncodedKeySpec var2 = new X509EncodedKeySpec(var1.getEncoded());

         KeyFactory var3;
         try {
            var3 = this.helper.createKeyFactory(var1.getAlgorithm().getAlgorithm().getId());
         } catch (NoSuchAlgorithmException var6) {
            if (keyAlgorithms.get(var1.getAlgorithm().getAlgorithm()) == null) {
               throw var6;
            }

            String var5 = (String)keyAlgorithms.get(var1.getAlgorithm().getAlgorithm());
            var3 = this.helper.createKeyFactory(var5);
         }

         return var3.generatePublic(var2);
      } catch (InvalidKeySpecException var7) {
         throw new InvalidKeyException("error decoding public key");
      } catch (IOException var8) {
         throw new InvalidKeyException("error extracting key encoding");
      } catch (NoSuchProviderException var9) {
         throw new NoSuchAlgorithmException("cannot find provider: " + var9.getMessage());
      }
   }

   static {
      keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
      keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
      keyAlgorithms.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
   }
}
