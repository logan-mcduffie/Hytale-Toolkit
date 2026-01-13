package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.XChaCha20Poly1305Key;
import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20Poly1305;
import com.google.crypto.tink.internal.Util;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class XChaCha20Poly1305 implements Aead {
   private final InsecureNonceXChaCha20Poly1305 cipher;
   private final byte[] outputPrefix;

   private XChaCha20Poly1305(final byte[] key, final byte[] outputPrefix) throws GeneralSecurityException {
      this.cipher = new InsecureNonceXChaCha20Poly1305(key);
      this.outputPrefix = outputPrefix;
   }

   public XChaCha20Poly1305(final byte[] key) throws GeneralSecurityException {
      this(key, new byte[0]);
   }

   @AccessesPartialKey
   public static Aead create(XChaCha20Poly1305Key key) throws GeneralSecurityException {
      return new XChaCha20Poly1305(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray());
   }

   private byte[] rawEncrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      ByteBuffer output = ByteBuffer.allocate(24 + plaintext.length + 16);
      byte[] nonce = Random.randBytes(24);
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
      if (ciphertext.length < 40) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         byte[] nonce = Arrays.copyOf(ciphertext, 24);
         ByteBuffer rawCiphertext = ByteBuffer.wrap(ciphertext, 24, ciphertext.length - 24);
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
