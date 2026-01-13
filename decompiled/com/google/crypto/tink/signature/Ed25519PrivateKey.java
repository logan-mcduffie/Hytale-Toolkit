package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.Ed25519;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class Ed25519PrivateKey extends SignaturePrivateKey {
   private final Ed25519PublicKey publicKey;
   private final SecretBytes privateKeyBytes;

   private Ed25519PrivateKey(Ed25519PublicKey publicKey, SecretBytes privateKeyBytes) {
      this.publicKey = publicKey;
      this.privateKeyBytes = privateKeyBytes;
   }

   @AccessesPartialKey
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static Ed25519PrivateKey create(Ed25519PublicKey publicKey, SecretBytes privateKeyBytes) throws GeneralSecurityException {
      if (publicKey == null) {
         throw new GeneralSecurityException("Ed25519 key cannot be constructed without an Ed25519 public key");
      } else if (privateKeyBytes.size() != 32) {
         throw new GeneralSecurityException("Ed25519 key must be constructed with key of length 32 bytes, not " + privateKeyBytes.size());
      } else {
         byte[] publicKeyBytes = publicKey.getPublicKeyBytes().toByteArray();
         byte[] secretSeed = privateKeyBytes.toByteArray(InsecureSecretKeyAccess.get());
         byte[] expectedPublicKeyBytes = Ed25519.scalarMultWithBaseToBytes(Ed25519.getHashedScalar(secretSeed));
         if (!Arrays.equals(publicKeyBytes, expectedPublicKeyBytes)) {
            throw new GeneralSecurityException("Ed25519 keys mismatch");
         } else {
            return new Ed25519PrivateKey(publicKey, privateKeyBytes);
         }
      }
   }

   public Ed25519Parameters getParameters() {
      return this.publicKey.getParameters();
   }

   public Ed25519PublicKey getPublicKey() {
      return this.publicKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getPrivateKeyBytes() {
      return this.privateKeyBytes;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof Ed25519PrivateKey)) {
         return false;
      } else {
         Ed25519PrivateKey that = (Ed25519PrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && this.privateKeyBytes.equalsSecretBytes(that.privateKeyBytes);
      }
   }
}
