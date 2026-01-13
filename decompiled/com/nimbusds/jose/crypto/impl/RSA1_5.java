package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class RSA1_5 {
   public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, Provider provider) throws JOSEException {
      try {
         Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
         cipher.init(1, pub);
         return cipher.doFinal(cek.getEncoded());
      } catch (IllegalBlockSizeException var4) {
         throw new JOSEException("RSA block size exception: The RSA key is too short, use a longer one", var4);
      } catch (Exception var5) {
         throw new JOSEException("Couldn't encrypt Content Encryption Key (CEK): " + var5.getMessage(), var5);
      }
   }

   public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, int keyLength, Provider provider) throws JOSEException {
      try {
         Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
         cipher.init(2, priv);
         byte[] secretKeyBytes = cipher.doFinal(encryptedCEK);
         return ByteUtils.safeBitLength(secretKeyBytes) != keyLength ? null : new SecretKeySpec(secretKeyBytes, "AES");
      } catch (Exception var6) {
         throw new JOSEException("Couldn't decrypt Content Encryption Key (CEK): " + var6.getMessage(), var6);
      }
   }

   private RSA1_5() {
   }
}
