package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class HMAC {
   public static Mac getInitMac(SecretKey secretKey, Provider provider) throws JOSEException {
      return getInitMac(secretKey.getAlgorithm(), secretKey, provider);
   }

   public static Mac getInitMac(String alg, SecretKey secretKey, Provider provider) throws JOSEException {
      try {
         Mac mac;
         if (provider != null) {
            mac = Mac.getInstance(alg, provider);
         } else {
            mac = Mac.getInstance(alg);
         }

         mac.init(secretKey);
         return mac;
      } catch (NoSuchAlgorithmException var5) {
         throw new JOSEException("Unsupported HMAC algorithm: " + var5.getMessage(), var5);
      } catch (InvalidKeyException var6) {
         throw new JOSEException("Invalid HMAC key: " + var6.getMessage(), var6);
      }
   }

   @Deprecated
   public static byte[] compute(String alg, byte[] secret, byte[] message, Provider provider) throws JOSEException {
      return compute(alg, new SecretKeySpec(secret, alg), message, provider);
   }

   public static byte[] compute(String alg, SecretKey secretKey, byte[] message, Provider provider) throws JOSEException {
      Mac mac = getInitMac(alg, secretKey, provider);
      mac.update(message);
      return mac.doFinal();
   }

   public static byte[] compute(SecretKey secretKey, byte[] message, Provider provider) throws JOSEException {
      return compute(secretKey.getAlgorithm(), secretKey, message, provider);
   }
}
