package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.KeyUtils;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@ThreadSafe
public class AESKW {
   public static byte[] wrapCEK(SecretKey cek, SecretKey kek, Provider provider) throws JOSEException {
      try {
         Cipher cipher;
         if (provider != null) {
            cipher = Cipher.getInstance("AESWrap", provider);
         } else {
            cipher = Cipher.getInstance("AESWrap");
         }

         cipher.init(3, kek);
         return cipher.wrap(cek);
      } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException var4) {
         throw new JOSEException("Couldn't wrap AES key: " + var4.getMessage(), var4);
      }
   }

   public static SecretKey unwrapCEK(SecretKey kek, byte[] encryptedCEK, Provider provider) throws JOSEException {
      try {
         Cipher cipher;
         if (provider != null) {
            cipher = Cipher.getInstance("AESWrap", provider);
         } else {
            cipher = Cipher.getInstance("AESWrap");
         }

         cipher.init(4, KeyUtils.toAESKey(kek));
         return (SecretKey)cipher.unwrap(encryptedCEK, "AES", 3);
      } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException var4) {
         throw new JOSEException("Couldn't unwrap AES key: " + var4.getMessage(), var4);
      }
   }

   private AESKW() {
   }
}
