package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import javax.crypto.AEADBadTagException;

abstract class InsecureNonceChaCha20Poly1305Base {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private final InsecureNonceChaCha20Base chacha20;
   private final InsecureNonceChaCha20Base macKeyChaCha20;

   public InsecureNonceChaCha20Poly1305Base(final byte[] key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
      } else {
         this.chacha20 = this.newChaCha20Instance(key, 1);
         this.macKeyChaCha20 = this.newChaCha20Instance(key, 0);
      }
   }

   abstract InsecureNonceChaCha20Base newChaCha20Instance(final byte[] key, int initialCounter) throws InvalidKeyException;

   public byte[] encrypt(final byte[] nonce, final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext.length > 2147483631) {
         throw new GeneralSecurityException("plaintext too long");
      } else {
         ByteBuffer ciphertext = ByteBuffer.allocate(plaintext.length + 16);
         this.encrypt(ciphertext, nonce, plaintext, associatedData);
         return ciphertext.array();
      }
   }

   public void encrypt(ByteBuffer output, final byte[] nonce, final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (output.remaining() < plaintext.length + 16) {
         throw new IllegalArgumentException("Given ByteBuffer output is too small");
      } else {
         int firstPosition = output.position();
         this.chacha20.encrypt(output, nonce, plaintext);
         output.position(firstPosition);
         output.limit(output.limit() - 16);
         byte[] aad = associatedData;
         if (associatedData == null) {
            aad = new byte[0];
         }

         byte[] tag = Poly1305.computeMac(this.getMacKey(nonce), macDataRfc8439(aad, output));
         output.limit(output.limit() + 16);
         output.put(tag);
      }
   }

   public byte[] decrypt(final byte[] nonce, final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      return this.decrypt(ByteBuffer.wrap(ciphertext), nonce, associatedData);
   }

   public byte[] decrypt(ByteBuffer ciphertext, final byte[] nonce, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext.remaining() < 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         int firstPosition = ciphertext.position();
         byte[] tag = new byte[16];
         ciphertext.position(ciphertext.limit() - 16);
         ciphertext.get(tag);
         ciphertext.position(firstPosition);
         ciphertext.limit(ciphertext.limit() - 16);
         byte[] aad = associatedData;
         if (associatedData == null) {
            aad = new byte[0];
         }

         try {
            Poly1305.verifyMac(this.getMacKey(nonce), macDataRfc8439(aad, ciphertext), tag);
         } catch (GeneralSecurityException var8) {
            throw new AEADBadTagException(var8.toString());
         }

         ciphertext.position(firstPosition);
         return this.chacha20.decrypt(nonce, ciphertext);
      }
   }

   private byte[] getMacKey(final byte[] nonce) throws GeneralSecurityException {
      ByteBuffer firstBlock = this.macKeyChaCha20.chacha20Block(nonce, 0);
      byte[] result = new byte[32];
      firstBlock.get(result);
      return result;
   }

   private static byte[] macDataRfc8439(final byte[] aad, ByteBuffer ciphertext) {
      int aadPaddedLen = aad.length % 16 == 0 ? aad.length : aad.length + 16 - aad.length % 16;
      int ciphertextLen = ciphertext.remaining();
      int ciphertextPaddedLen = ciphertextLen % 16 == 0 ? ciphertextLen : ciphertextLen + 16 - ciphertextLen % 16;
      ByteBuffer macData = ByteBuffer.allocate(aadPaddedLen + ciphertextPaddedLen + 16).order(ByteOrder.LITTLE_ENDIAN);
      macData.put(aad);
      macData.position(aadPaddedLen);
      macData.put(ciphertext);
      macData.position(aadPaddedLen + ciphertextPaddedLen);
      macData.putLong(aad.length);
      macData.putLong(ciphertextLen);
      return macData.array();
   }
}
