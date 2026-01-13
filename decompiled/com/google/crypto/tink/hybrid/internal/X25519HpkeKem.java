package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
final class X25519HpkeKem implements HpkeKem {
   private final HkdfHpkeKdf hkdf;
   private final X25519 x25519;

   X25519HpkeKem(HkdfHpkeKdf hkdf) {
      this.hkdf = hkdf;
      X25519 x25519 = null;

      try {
         x25519 = X25519Conscrypt.create();
      } catch (GeneralSecurityException var4) {
         x25519 = new X25519HpkeKem.X25519Java();
      }

      this.x25519 = x25519;
   }

   private byte[] deriveKemSharedSecret(byte[] dhSharedSecret, byte[] senderEphemeralPublicKey, byte[] recipientPublicKey) throws GeneralSecurityException {
      byte[] kemContext = Bytes.concat(senderEphemeralPublicKey, recipientPublicKey);
      return this.extractAndExpand(dhSharedSecret, kemContext);
   }

   private byte[] deriveKemSharedSecret(byte[] dhSharedSecret, byte[] senderEphemeralPublicKey, byte[] recipientPublicKey, byte[] senderPublicKey) throws GeneralSecurityException {
      byte[] kemContext = Bytes.concat(senderEphemeralPublicKey, recipientPublicKey, senderPublicKey);
      return this.extractAndExpand(dhSharedSecret, kemContext);
   }

   private byte[] extractAndExpand(byte[] dhSharedSecret, byte[] kemContext) throws GeneralSecurityException {
      byte[] kemSuiteId = HpkeUtil.kemSuiteId(HpkeUtil.X25519_HKDF_SHA256_KEM_ID);
      return this.hkdf.extractAndExpand(null, dhSharedSecret, "eae_prk", kemContext, "shared_secret", kemSuiteId, this.hkdf.getMacLength());
   }

   HpkeKemEncapOutput encapsulateWithFixedEphemeralKey(byte[] recipientPublicKey, byte[] ephemeralPrivateKey, byte[] ephemeralPublicKey) throws GeneralSecurityException {
      byte[] dhSharedSecret = this.x25519.computeSharedSecret(ephemeralPrivateKey, recipientPublicKey);
      byte[] kemSharedSecret = this.deriveKemSharedSecret(dhSharedSecret, ephemeralPublicKey, recipientPublicKey);
      return new HpkeKemEncapOutput(kemSharedSecret, ephemeralPublicKey);
   }

   @Override
   public HpkeKemEncapOutput encapsulate(byte[] recipientPublicKey) throws GeneralSecurityException {
      X25519.KeyPair ephemeral = this.x25519.generateKeyPair();
      return this.encapsulateWithFixedEphemeralKey(recipientPublicKey, ephemeral.privateKey, ephemeral.publicKey);
   }

   HpkeKemEncapOutput authEncapsulateWithFixedEphemeralKey(
      byte[] recipientPublicKey, byte[] ephemeralPrivateKey, byte[] ephemeralPublicKey, HpkeKemPrivateKey senderPrivateKey
   ) throws GeneralSecurityException {
      byte[] dhSharedSecret = Bytes.concat(
         this.x25519.computeSharedSecret(ephemeralPrivateKey, recipientPublicKey),
         this.x25519.computeSharedSecret(senderPrivateKey.getSerializedPrivate().toByteArray(), recipientPublicKey)
      );
      byte[] senderPublicKey = senderPrivateKey.getSerializedPublic().toByteArray();
      byte[] kemSharedSecret = this.deriveKemSharedSecret(dhSharedSecret, ephemeralPublicKey, recipientPublicKey, senderPublicKey);
      return new HpkeKemEncapOutput(kemSharedSecret, ephemeralPublicKey);
   }

   @Override
   public HpkeKemEncapOutput authEncapsulate(byte[] recipientPublicKey, HpkeKemPrivateKey senderPrivateKey) throws GeneralSecurityException {
      X25519.KeyPair ephemeral = this.x25519.generateKeyPair();
      return this.authEncapsulateWithFixedEphemeralKey(recipientPublicKey, ephemeral.privateKey, ephemeral.publicKey, senderPrivateKey);
   }

   @Override
   public byte[] decapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey) throws GeneralSecurityException {
      byte[] dhSharedSecret = this.x25519.computeSharedSecret(recipientPrivateKey.getSerializedPrivate().toByteArray(), encapsulatedKey);
      return this.deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, recipientPrivateKey.getSerializedPublic().toByteArray());
   }

   @Override
   public byte[] authDecapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey, byte[] senderPublicKey) throws GeneralSecurityException {
      byte[] privateKey = recipientPrivateKey.getSerializedPrivate().toByteArray();
      byte[] dhSharedSecret = Bytes.concat(
         this.x25519.computeSharedSecret(privateKey, encapsulatedKey), this.x25519.computeSharedSecret(privateKey, senderPublicKey)
      );
      byte[] recipientPublicKey = recipientPrivateKey.getSerializedPublic().toByteArray();
      return this.deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, recipientPublicKey, senderPublicKey);
   }

   @Override
   public byte[] getKemId() throws GeneralSecurityException {
      if (Arrays.equals(this.hkdf.getKdfId(), HpkeUtil.HKDF_SHA256_KDF_ID)) {
         return HpkeUtil.X25519_HKDF_SHA256_KEM_ID;
      } else {
         throw new GeneralSecurityException("Could not determine HPKE KEM ID");
      }
   }

   @Immutable
   private static final class X25519Java implements X25519 {
      private X25519Java() {
      }

      @Override
      public X25519.KeyPair generateKeyPair() throws GeneralSecurityException {
         byte[] privateKey = com.google.crypto.tink.subtle.X25519.generatePrivateKey();
         byte[] publicKey = com.google.crypto.tink.subtle.X25519.publicFromPrivate(privateKey);
         return new X25519.KeyPair(privateKey, publicKey);
      }

      @Override
      public byte[] computeSharedSecret(byte[] privateKey, byte[] publicKey) throws GeneralSecurityException {
         return com.google.crypto.tink.subtle.X25519.computeSharedSecret(privateKey, publicKey);
      }
   }
}
