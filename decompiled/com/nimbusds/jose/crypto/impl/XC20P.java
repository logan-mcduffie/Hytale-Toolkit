package com.nimbusds.jose.crypto.impl;

import com.google.crypto.tink.subtle.XChaCha20Poly1305;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

@ThreadSafe
public class XC20P {
   public static final int AUTH_TAG_BIT_LENGTH = 128;
   public static final int IV_BIT_LENGTH = 192;

   public static AuthenticatedCipherText encryptAuthenticated(SecretKey secretKey, Container<byte[]> ivContainer, byte[] plainText, byte[] authData) throws JOSEException {
      XChaCha20Poly1305 aead;
      try {
         aead = new XChaCha20Poly1305(secretKey.getEncoded());
      } catch (GeneralSecurityException var12) {
         throw new JOSEException("Invalid XChaCha20Poly1305 key: " + var12.getMessage(), var12);
      }

      byte[] cipherOutput;
      try {
         cipherOutput = aead.encrypt(plainText, authData);
      } catch (GeneralSecurityException var11) {
         throw new JOSEException("Couldn't encrypt with XChaCha20Poly1305: " + var11.getMessage(), var11);
      }

      int tagPos = cipherOutput.length - ByteUtils.byteLength(128);
      int cipherTextPos = ByteUtils.byteLength(192);
      byte[] iv = ByteUtils.subArray(cipherOutput, 0, cipherTextPos);
      byte[] cipherText = ByteUtils.subArray(cipherOutput, cipherTextPos, tagPos - cipherTextPos);
      byte[] authTag = ByteUtils.subArray(cipherOutput, tagPos, ByteUtils.byteLength(128));
      ivContainer.set(iv);
      return new AuthenticatedCipherText(cipherText, authTag);
   }

   public static byte[] decryptAuthenticated(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] authData, byte[] authTag) throws JOSEException {
      XChaCha20Poly1305 aead;
      try {
         aead = new XChaCha20Poly1305(secretKey.getEncoded());
      } catch (GeneralSecurityException var9) {
         throw new JOSEException("Invalid XChaCha20Poly1305 key: " + var9.getMessage(), var9);
      }

      byte[] cipherInput = ByteUtils.concat(iv, cipherText, authTag);

      try {
         return aead.decrypt(cipherInput, authData);
      } catch (GeneralSecurityException var8) {
         throw new JOSEException("XChaCha20Poly1305 decryption failed: " + var8.getMessage(), var8);
      }
   }
}
