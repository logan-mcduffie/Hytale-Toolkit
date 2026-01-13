package com.google.crypto.tink.subtle;

import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;

class XChaCha20 implements IndCpaCipher {
   static final int NONCE_LENGTH_IN_BYTES = 24;
   private final InsecureNonceXChaCha20 cipher;

   XChaCha20(byte[] key, int initialCounter) throws InvalidKeyException {
      this.cipher = new InsecureNonceXChaCha20(key, initialCounter);
   }

   @Override
   public byte[] encrypt(final byte[] plaintext) throws GeneralSecurityException {
      ByteBuffer output = ByteBuffer.allocate(24 + plaintext.length);
      byte[] nonce = Random.randBytes(24);
      output.put(nonce);
      this.cipher.encrypt(output, nonce, plaintext);
      return output.array();
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext) throws GeneralSecurityException {
      if (ciphertext.length < 24) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         byte[] nonce = Arrays.copyOf(ciphertext, 24);
         ByteBuffer rawCiphertext = ByteBuffer.wrap(ciphertext, 24, ciphertext.length - 24);
         return this.cipher.decrypt(nonce, rawCiphertext);
      }
   }
}
