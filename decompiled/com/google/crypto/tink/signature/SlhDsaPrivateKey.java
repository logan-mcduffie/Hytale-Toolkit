package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;

public class SlhDsaPrivateKey extends SignaturePrivateKey {
   private static final int SLH_DSA_SHA2_128S_PRIVATE_KEY_BYTES = 64;
   private final SlhDsaPublicKey publicKey;
   private final SecretBytes privateKeyBytes;

   private SlhDsaPrivateKey(SlhDsaPublicKey publicKey, SecretBytes privateSeed) {
      this.publicKey = publicKey;
      this.privateKeyBytes = privateSeed;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public static SlhDsaPrivateKey createWithoutVerification(SlhDsaPublicKey slhDsaPublicKey, SecretBytes privateKeyBytes) throws GeneralSecurityException {
      if (privateKeyBytes.size() != 64) {
         throw new GeneralSecurityException("Incorrect private key size for SLH-DSA");
      } else if (slhDsaPublicKey.getParameters().getHashType() == SlhDsaParameters.HashType.SHA2
         && slhDsaPublicKey.getParameters().getPrivateKeySize() == 64
         && slhDsaPublicKey.getParameters().getSignatureType() == SlhDsaParameters.SignatureType.SMALL_SIGNATURE) {
         return new SlhDsaPrivateKey(slhDsaPublicKey, privateKeyBytes);
      } else {
         throw new GeneralSecurityException("Unknown SKH-DSA instance; only SLH-DSA-SHA2-128S is currently supported");
      }
   }

   public SlhDsaPublicKey getPublicKey() {
      return this.publicKey;
   }

   public SlhDsaParameters getParameters() {
      return this.publicKey.getParameters();
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
      if (!(o instanceof SlhDsaPrivateKey)) {
         return false;
      } else {
         SlhDsaPrivateKey that = (SlhDsaPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && this.privateKeyBytes.equalsSecretBytes(that.privateKeyBytes);
      }
   }
}
