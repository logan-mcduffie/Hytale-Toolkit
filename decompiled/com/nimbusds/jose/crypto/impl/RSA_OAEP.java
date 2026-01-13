package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.opts.CipherMode;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class RSA_OAEP {
   private static final String RSA_OEAP_JCA_ALG = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

   public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, CipherMode mode, Provider provider) throws JOSEException {
      assert mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT;

      try {
         Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", provider);
         cipher.init(mode.getForJWEEncrypter(), pub, new SecureRandom());
         return mode == CipherMode.WRAP_UNWRAP ? cipher.wrap(cek) : cipher.doFinal(cek.getEncoded());
      } catch (InvalidKeyException var5) {
         throw new JOSEException("RSA block size exception: The RSA key is too short, try a longer one", var5);
      } catch (Exception var6) {
         throw new JOSEException(var6.getMessage(), var6);
      }
   }

   public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, CipherMode mode, Provider provider) throws JOSEException {
      assert mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT;

      try {
         Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", provider);
         cipher.init(mode.getForJWEDecrypter(), priv);
         return (SecretKey)(mode == CipherMode.WRAP_UNWRAP
            ? (SecretKey)cipher.unwrap(encryptedCEK, "AES", 3)
            : new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES"));
      } catch (Exception var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }
   }

   private RSA_OAEP() {
   }
}
