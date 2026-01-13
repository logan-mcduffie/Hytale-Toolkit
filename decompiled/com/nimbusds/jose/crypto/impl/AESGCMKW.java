package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import java.security.Provider;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class AESGCMKW {
   public static AuthenticatedCipherText encryptCEK(SecretKey cek, Container<byte[]> iv, SecretKey kek, Provider provider) throws JOSEException {
      return AESGCM.encrypt(kek, iv, cek.getEncoded(), new byte[0], provider);
   }

   public static SecretKey decryptCEK(SecretKey kek, byte[] iv, AuthenticatedCipherText authEncrCEK, int keyLength, Provider provider) throws JOSEException {
      byte[] keyBytes = AESGCM.decrypt(kek, iv, authEncrCEK.getCipherText(), new byte[0], authEncrCEK.getAuthenticationTag(), provider);
      if (ByteUtils.safeBitLength(keyBytes) != keyLength) {
         throw new KeyLengthException("CEK key length mismatch: " + ByteUtils.safeBitLength(keyBytes) + " != " + keyLength);
      } else {
         return new SecretKeySpec(keyBytes, "AES");
      }
   }

   private AESGCMKW() {
   }
}
