package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.EcdsaPrivateKey;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;

@Immutable
public final class JwtEcdsaPrivateKey extends JwtSignaturePrivateKey {
   public final JwtEcdsaPublicKey publicKey;
   private final EcdsaPrivateKey ecdsaPrivateKey;

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public static JwtEcdsaPrivateKey create(JwtEcdsaPublicKey publicKey, SecretBigInteger privateValue) throws GeneralSecurityException {
      EcdsaPrivateKey ecdsaPrivateKey = EcdsaPrivateKey.builder().setPublicKey(publicKey.getEcdsaPublicKey()).setPrivateValue(privateValue).build();
      return new JwtEcdsaPrivateKey(publicKey, ecdsaPrivateKey);
   }

   private JwtEcdsaPrivateKey(JwtEcdsaPublicKey publicKey, EcdsaPrivateKey ecdsaPrivateKey) {
      this.publicKey = publicKey;
      this.ecdsaPrivateKey = ecdsaPrivateKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public SecretBigInteger getPrivateValue() {
      return this.ecdsaPrivateKey.getPrivateValue();
   }

   public JwtEcdsaParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public JwtEcdsaPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof JwtEcdsaPrivateKey)) {
         return false;
      } else {
         JwtEcdsaPrivateKey that = (JwtEcdsaPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && this.ecdsaPrivateKey.equalsKey(that.ecdsaPrivateKey);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   EcdsaPrivateKey getEcdsaPrivateKey() {
      return this.ecdsaPrivateKey;
   }
}
