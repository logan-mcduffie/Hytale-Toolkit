package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;

public class MlDsaPrivateKey extends SignaturePrivateKey {
   private static final int MLDSA_SEED_BYTES = 32;
   private final MlDsaPublicKey publicKey;
   private final SecretBytes privateSeed;

   private MlDsaPrivateKey(MlDsaPublicKey publicKey, SecretBytes privateSeed) {
      this.publicKey = publicKey;
      this.privateSeed = privateSeed;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public static MlDsaPrivateKey createWithoutVerification(MlDsaPublicKey mlDsaPublicKey, SecretBytes privateSeed) throws GeneralSecurityException {
      if (privateSeed.size() != 32) {
         throw new GeneralSecurityException("Incorrect private seed size for ML-DSA");
      } else if (mlDsaPublicKey.getParameters().getMlDsaInstance() != MlDsaParameters.MlDsaInstance.ML_DSA_65) {
         throw new GeneralSecurityException("Unknown ML-DSA instance; only ML-DSA-65 is currently supported");
      } else {
         return new MlDsaPrivateKey(mlDsaPublicKey, privateSeed);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getPrivateSeed() {
      return this.privateSeed;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof MlDsaPrivateKey)) {
         return false;
      } else {
         MlDsaPrivateKey that = (MlDsaPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && this.privateSeed.equalsSecretBytes(that.privateSeed);
      }
   }

   public MlDsaParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public MlDsaPublicKey getPublicKey() {
      return this.publicKey;
   }
}
