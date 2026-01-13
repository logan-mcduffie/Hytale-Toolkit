package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Hkdf {
   public static byte[] computeHkdf(String macAlgorithm, final byte[] ikm, final byte[] salt, final byte[] info, int size) throws GeneralSecurityException {
      Mac mac = EngineFactory.MAC.getInstance(macAlgorithm);
      if (size > 255 * mac.getMacLength()) {
         throw new GeneralSecurityException("size too large");
      } else {
         if (salt != null && salt.length != 0) {
            mac.init(new SecretKeySpec(salt, macAlgorithm));
         } else {
            mac.init(new SecretKeySpec(new byte[mac.getMacLength()], macAlgorithm));
         }

         byte[] prk = mac.doFinal(ikm);
         byte[] result = new byte[size];
         int ctr = 1;
         int pos = 0;
         mac.init(new SecretKeySpec(prk, macAlgorithm));
         byte[] digest = new byte[0];

         while (true) {
            mac.update(digest);
            mac.update(info);
            mac.update((byte)ctr);
            digest = mac.doFinal();
            if (pos + digest.length >= size) {
               System.arraycopy(digest, 0, result, pos, size - pos);
               return result;
            }

            System.arraycopy(digest, 0, result, pos, digest.length);
            pos += digest.length;
            ctr++;
         }
      }
   }

   public static byte[] computeEciesHkdfSymmetricKey(
      final byte[] ephemeralPublicKeyBytes, final byte[] sharedSecret, String hmacAlgo, final byte[] hkdfSalt, final byte[] hkdfInfo, int keySizeInBytes
   ) throws GeneralSecurityException {
      byte[] hkdfInput = Bytes.concat(ephemeralPublicKeyBytes, sharedSecret);
      return computeHkdf(hmacAlgo, hkdfInput, hkdfSalt, hkdfInfo, keySizeInBytes);
   }

   private Hkdf() {
   }
}
