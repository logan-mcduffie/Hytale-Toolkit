package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.ChaCha20Poly1305Key;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Poly1305;
import com.google.crypto.tink.internal.Util;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class ChaCha20Poly1305 implements Aead {
   private final InsecureNonceChaCha20Poly1305 cipher;
   private final byte[] outputPrefix;

   private ChaCha20Poly1305(final byte[] key, final byte[] outputPrefix) throws GeneralSecurityException {
      this.cipher = new InsecureNonceChaCha20Poly1305(key);
      this.outputPrefix = outputPrefix;
   }

   public ChaCha20Poly1305(final byte[] key) throws GeneralSecurityException {
      this(key, new byte[0]);
   }

   @AccessesPartialKey
   public static Aead create(ChaCha20Poly1305Key key) throws GeneralSecurityException {
      return new ChaCha20Poly1305(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray());
   }

   private byte[] rawEncrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      ByteBuffer output = ByteBuffer.allocate(12 + plaintext.length + 16);
      byte[] nonce = Random.randBytes(12);
      output.put(nonce);
      this.cipher.encrypt(output, nonce, plaintext, associatedData);
      return output.array();
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      byte[] ciphertext = this.rawEncrypt(plaintext, associatedData);
      return this.outputPrefix.length == 0 ? ciphertext : Bytes.concat(this.outputPrefix, ciphertext);
   }

   private byte[] rawDecrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext.length < 28) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         byte[] nonce = Arrays.copyOf(ciphertext, 12);
         ByteBuffer rawCiphertext = ByteBuffer.wrap(ciphertext, 12, ciphertext.length - 12);
         return this.cipher.decrypt(rawCiphertext, nonce, associatedData);
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (this.outputPrefix.length == 0) {
         return this.rawDecrypt(ciphertext, associatedData);
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         byte[] copiedCiphertext = Arrays.copyOfRange(ciphertext, this.outputPrefix.length, ciphertext.length);
         return this.rawDecrypt(copiedCiphertext, associatedData);
      }
   }
}
