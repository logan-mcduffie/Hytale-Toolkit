package com.google.crypto.tink.subtle;

import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;

class ChaCha20 implements IndCpaCipher {
   static final int NONCE_LENGTH_IN_BYTES = 12;
   private final InsecureNonceChaCha20 cipher;

   ChaCha20(final byte[] key, int initialCounter) throws InvalidKeyException {
      this.cipher = new InsecureNonceChaCha20(key, initialCounter);
   }

   @Override
   public byte[] encrypt(final byte[] plaintext) throws GeneralSecurityException {
      ByteBuffer output = ByteBuffer.allocate(12 + plaintext.length);
      byte[] nonce = Random.randBytes(12);
      output.put(nonce);
      this.cipher.encrypt(output, nonce, plaintext);
      return output.array();
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext) throws GeneralSecurityException {
      if (ciphertext.length < 12) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         byte[] nonce = Arrays.copyOf(ciphertext, 12);
         ByteBuffer rawCiphertext = ByteBuffer.wrap(ciphertext, 12, ciphertext.length - 12);
         return this.cipher.decrypt(nonce, rawCiphertext);
      }
   }
}
