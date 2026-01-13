package com.nimbusds.jose.jca;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.crypto.impl.RSASSA;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

public final class JCASupport {
   public static boolean isUnlimitedStrength() {
      try {
         return Cipher.getMaxAllowedKeyLength("AES") >= 256;
      } catch (NoSuchAlgorithmException var1) {
         return false;
      }
   }

   public static boolean isSupported(Algorithm alg) {
      if (alg instanceof JWSAlgorithm) {
         return isSupported((JWSAlgorithm)alg);
      } else if (alg instanceof JWEAlgorithm) {
         return isSupported((JWEAlgorithm)alg);
      } else if (alg instanceof EncryptionMethod) {
         return isSupported((EncryptionMethod)alg);
      } else {
         throw new IllegalArgumentException("Unexpected algorithm class: " + alg.getClass().getCanonicalName());
      }
   }

   public static boolean isSupported(Algorithm alg, Provider provider) {
      if (alg instanceof JWSAlgorithm) {
         return isSupported((JWSAlgorithm)alg, provider);
      } else if (alg instanceof JWEAlgorithm) {
         return isSupported((JWEAlgorithm)alg, provider);
      } else if (alg instanceof EncryptionMethod) {
         return isSupported((EncryptionMethod)alg, provider);
      } else {
         throw new IllegalArgumentException("Unexpected algorithm class: " + alg.getClass().getCanonicalName());
      }
   }

   public static boolean isSupported(JWSAlgorithm alg) {
      if (alg.getName().equals(Algorithm.NONE.getName())) {
         return true;
      } else {
         for (Provider p : Security.getProviders()) {
            if (isSupported(alg, p)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isSupported(JWSAlgorithm alg, Provider provider) {
      if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
         String jcaName;
         if (alg.equals(JWSAlgorithm.HS256)) {
            jcaName = "HMACSHA256";
         } else if (alg.equals(JWSAlgorithm.HS384)) {
            jcaName = "HMACSHA384";
         } else {
            if (!alg.equals(JWSAlgorithm.HS512)) {
               return false;
            }

            jcaName = "HMACSHA512";
         }

         try {
            Mac.getInstance(jcaName, provider);
            return true;
         } catch (NoSuchAlgorithmException var4) {
            return false;
         }
      } else if (JWSAlgorithm.Family.RSA.contains(alg)) {
         try {
            RSASSA.getSignerAndVerifier(alg, provider);
            return true;
         } catch (JOSEException var5) {
            return false;
         }
      } else if (JWSAlgorithm.Family.EC.contains(alg)) {
         try {
            ECDSA.getSignerAndVerifier(alg, provider);
            return true;
         } catch (JOSEException var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isSupported(JWEAlgorithm alg) {
      for (Provider p : Security.getProviders()) {
         if (isSupported(alg, p)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isSupported(JWEAlgorithm alg, Provider provider) {
      if (JWEAlgorithm.Family.RSA.contains(alg)) {
         String jcaName;
         if (alg.equals(JWEAlgorithm.RSA1_5)) {
            jcaName = "RSA/ECB/PKCS1Padding";
         } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
            jcaName = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
         } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
            jcaName = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
         } else {
            if (!alg.equals(JWEAlgorithm.RSA_OAEP_512)) {
               return false;
            }

            jcaName = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
         }

         try {
            Cipher.getInstance(jcaName, provider);
            return true;
         } catch (NoSuchPaddingException | NoSuchAlgorithmException var4) {
            return false;
         }
      } else if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
         return provider.getService("Cipher", "AESWrap") != null;
      } else if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
         return provider.getService("KeyAgreement", "ECDH") != null;
      } else if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
         try {
            Cipher.getInstance("AES/GCM/NoPadding", provider);
            return true;
         } catch (NoSuchPaddingException | NoSuchAlgorithmException var5) {
            return false;
         }
      } else if (JWEAlgorithm.Family.PBES2.contains(alg)) {
         String hmac;
         if (alg.equals(JWEAlgorithm.PBES2_HS256_A128KW)) {
            hmac = "HmacSHA256";
         } else if (alg.equals(JWEAlgorithm.PBES2_HS384_A192KW)) {
            hmac = "HmacSHA384";
         } else {
            hmac = "HmacSHA512";
         }

         return provider.getService("KeyGenerator", hmac) != null;
      } else {
         return JWEAlgorithm.DIR.equals(alg);
      }
   }

   public static boolean isSupported(EncryptionMethod enc) {
      for (Provider p : Security.getProviders()) {
         if (isSupported(enc, p)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isSupported(EncryptionMethod enc, Provider provider) {
      if (EncryptionMethod.Family.AES_CBC_HMAC_SHA.contains(enc)) {
         try {
            Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
         } catch (NoSuchPaddingException | NoSuchAlgorithmException var3) {
            return false;
         }

         String hmac;
         if (enc.equals(EncryptionMethod.A128CBC_HS256)) {
            hmac = "HmacSHA256";
         } else if (enc.equals(EncryptionMethod.A192CBC_HS384)) {
            hmac = "HmacSHA384";
         } else {
            hmac = "HmacSHA512";
         }

         return provider.getService("KeyGenerator", hmac) != null;
      } else if (EncryptionMethod.Family.AES_GCM.contains(enc)) {
         try {
            Cipher.getInstance("AES/GCM/NoPadding", provider);
            return true;
         } catch (NoSuchPaddingException | NoSuchAlgorithmException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   private JCASupport() {
   }
}
