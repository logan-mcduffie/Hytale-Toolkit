package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.JsonParser;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.util.Optional;

public final class JwkSetConverter {
   public static String fromPublicKeysetHandle(KeysetHandle handle) throws IOException, GeneralSecurityException {
      handle = KeysetHandle.newBuilder(handle).build();
      JsonArray keys = new JsonArray();

      for (int i = 0; i < handle.size(); i++) {
         KeysetHandle.Entry entry = handle.getAt(i);
         if (entry.getStatus() == KeyStatus.ENABLED) {
            Key key = entry.getKey();
            if (key instanceof JwtEcdsaPublicKey) {
               keys.add(convertJwtEcdsaKey((JwtEcdsaPublicKey)key));
            } else if (key instanceof JwtRsaSsaPkcs1PublicKey) {
               keys.add(convertJwtRsaSsaPkcs1Key((JwtRsaSsaPkcs1PublicKey)key));
            } else {
               if (!(key instanceof JwtRsaSsaPssPublicKey)) {
                  throw new GeneralSecurityException("unsupported key with parameters " + key.getParameters());
               }

               keys.add(convertJwtRsaSsaPssKey((JwtRsaSsaPssPublicKey)key));
            }
         }
      }

      JsonObject jwkSet = new JsonObject();
      jwkSet.add("keys", keys);
      return jwkSet.toString();
   }

   public static KeysetHandle toPublicKeysetHandle(String jwkSet) throws IOException, GeneralSecurityException {
      JsonObject jsonKeyset;
      try {
         jsonKeyset = JsonParser.parse(jwkSet).getAsJsonObject();
      } catch (IOException | IllegalStateException var10) {
         throw new GeneralSecurityException("JWK set is invalid JSON", var10);
      }

      KeysetHandle.Builder builder = KeysetHandle.newBuilder();

      for (JsonElement element : jsonKeyset.get("keys").getAsJsonArray()) {
         JsonObject jsonKey = element.getAsJsonObject();
         String algPrefix = getStringItem(jsonKey, "alg").substring(0, 2);
         switch (algPrefix) {
            case "RS":
               builder.addEntry(KeysetHandle.importKey(convertToRsaSsaPkcs1Key(jsonKey)).withRandomId());
               break;
            case "PS":
               builder.addEntry(KeysetHandle.importKey(convertToRsaSsaPssKey(jsonKey)).withRandomId());
               break;
            case "ES":
               builder.addEntry(KeysetHandle.importKey(convertToEcdsaKey(jsonKey)).withRandomId());
               break;
            default:
               throw new GeneralSecurityException("unexpected alg value: " + getStringItem(jsonKey, "alg"));
         }
      }

      if (builder.size() <= 0) {
         throw new GeneralSecurityException("empty keyset");
      } else {
         builder.getAt(0).makePrimary();
         return builder.build();
      }
   }

   @AccessesPartialKey
   private static JsonObject convertJwtEcdsaKey(JwtEcdsaPublicKey key) throws GeneralSecurityException {
      JwtEcdsaParameters.Algorithm algorithm = key.getParameters().getAlgorithm();
      String alg;
      String crv;
      int encLength;
      if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES256)) {
         alg = "ES256";
         crv = "P-256";
         encLength = 32;
      } else if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES384)) {
         alg = "ES384";
         crv = "P-384";
         encLength = 48;
      } else {
         if (!algorithm.equals(JwtEcdsaParameters.Algorithm.ES512)) {
            throw new GeneralSecurityException("unknown algorithm");
         }

         alg = "ES512";
         crv = "P-521";
         encLength = 66;
      }

      JsonObject jsonKey = new JsonObject();
      jsonKey.addProperty("kty", "EC");
      jsonKey.addProperty("crv", crv);
      BigInteger x = key.getPublicPoint().getAffineX();
      BigInteger y = key.getPublicPoint().getAffineY();
      jsonKey.addProperty("x", Base64.urlSafeEncode(BigIntegerEncoding.toBigEndianBytesOfFixedLength(x, encLength)));
      jsonKey.addProperty("y", Base64.urlSafeEncode(BigIntegerEncoding.toBigEndianBytesOfFixedLength(y, encLength)));
      jsonKey.addProperty("use", "sig");
      jsonKey.addProperty("alg", alg);
      JsonArray keyOps = new JsonArray();
      keyOps.add("verify");
      jsonKey.add("key_ops", keyOps);
      Optional<String> kid = key.getKid();
      if (kid.isPresent()) {
         jsonKey.addProperty("kid", kid.get());
      }

      return jsonKey;
   }

   private static byte[] base64urlUInt(BigInteger n) {
      return n.equals(BigInteger.ZERO) ? new byte[]{0} : BigIntegerEncoding.toUnsignedBigEndianBytes(n);
   }

   @AccessesPartialKey
   private static JsonObject convertJwtRsaSsaPkcs1Key(JwtRsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
      String alg = key.getParameters().getAlgorithm().getStandardName();
      JsonObject jsonKey = new JsonObject();
      jsonKey.addProperty("kty", "RSA");
      jsonKey.addProperty("n", Base64.urlSafeEncode(base64urlUInt(key.getModulus())));
      jsonKey.addProperty("e", Base64.urlSafeEncode(base64urlUInt(key.getParameters().getPublicExponent())));
      jsonKey.addProperty("use", "sig");
      jsonKey.addProperty("alg", alg);
      JsonArray keyOps = new JsonArray();
      keyOps.add("verify");
      jsonKey.add("key_ops", keyOps);
      Optional<String> kid = key.getKid();
      if (kid.isPresent()) {
         jsonKey.addProperty("kid", kid.get());
      }

      return jsonKey;
   }

   @AccessesPartialKey
   private static JsonObject convertJwtRsaSsaPssKey(JwtRsaSsaPssPublicKey key) throws GeneralSecurityException {
      String alg = key.getParameters().getAlgorithm().getStandardName();
      JsonObject jsonKey = new JsonObject();
      jsonKey.addProperty("kty", "RSA");
      jsonKey.addProperty("n", Base64.urlSafeEncode(base64urlUInt(key.getModulus())));
      jsonKey.addProperty("e", Base64.urlSafeEncode(base64urlUInt(key.getParameters().getPublicExponent())));
      jsonKey.addProperty("use", "sig");
      jsonKey.addProperty("alg", alg);
      JsonArray keyOps = new JsonArray();
      keyOps.add("verify");
      jsonKey.add("key_ops", keyOps);
      Optional<String> kid = key.getKid();
      if (kid.isPresent()) {
         jsonKey.addProperty("kid", kid.get());
      }

      return jsonKey;
   }

   private static String getStringItem(JsonObject obj, String name) throws GeneralSecurityException {
      if (!obj.has(name)) {
         throw new GeneralSecurityException(name + " not found");
      } else if (obj.get(name).isJsonPrimitive() && obj.get(name).getAsJsonPrimitive().isString()) {
         return obj.get(name).getAsString();
      } else {
         throw new GeneralSecurityException(name + " is not a string");
      }
   }

   private static void expectStringItem(JsonObject obj, String name, String expectedValue) throws GeneralSecurityException {
      String value = getStringItem(obj, name);
      if (!value.equals(expectedValue)) {
         throw new GeneralSecurityException("unexpected " + name + " value: " + value);
      }
   }

   private static void validateUseIsSig(JsonObject jsonKey) throws GeneralSecurityException {
      if (jsonKey.has("use")) {
         expectStringItem(jsonKey, "use", "sig");
      }
   }

   private static void validateKeyOpsIsVerify(JsonObject jsonKey) throws GeneralSecurityException {
      if (jsonKey.has("key_ops")) {
         if (!jsonKey.get("key_ops").isJsonArray()) {
            throw new GeneralSecurityException("key_ops is not an array");
         } else {
            JsonArray keyOps = jsonKey.get("key_ops").getAsJsonArray();
            if (keyOps.size() != 1) {
               throw new GeneralSecurityException("key_ops must contain exactly one element");
            } else if (keyOps.get(0).isJsonPrimitive() && keyOps.get(0).getAsJsonPrimitive().isString()) {
               if (!keyOps.get(0).getAsString().equals("verify")) {
                  throw new GeneralSecurityException("unexpected keyOps value: " + keyOps.get(0).getAsString());
               }
            } else {
               throw new GeneralSecurityException("key_ops is not a string");
            }
         }
      }
   }

   @AccessesPartialKey
   private static JwtRsaSsaPkcs1PublicKey convertToRsaSsaPkcs1Key(JsonObject jsonKey) throws GeneralSecurityException {
      String publicExponent = getStringItem(jsonKey, "alg");
      JwtRsaSsaPkcs1Parameters.Algorithm algorithm;
      switch (publicExponent) {
         case "RS256":
            algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS256;
            break;
         case "RS384":
            algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS384;
            break;
         case "RS512":
            algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS512;
            break;
         default:
            throw new GeneralSecurityException("Unknown Rsa Algorithm: " + getStringItem(jsonKey, "alg"));
      }

      if (!jsonKey.has("p") && !jsonKey.has("q") && !jsonKey.has("dp") && !jsonKey.has("dq") && !jsonKey.has("d") && !jsonKey.has("qi")) {
         expectStringItem(jsonKey, "kty", "RSA");
         validateUseIsSig(jsonKey);
         validateKeyOpsIsVerify(jsonKey);
         BigInteger publicExponent = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "e")));
         BigInteger modulus = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "n")));
         return jsonKey.has("kid")
            ? JwtRsaSsaPkcs1PublicKey.builder()
               .setParameters(
                  JwtRsaSsaPkcs1Parameters.builder()
                     .setModulusSizeBits(modulus.bitLength())
                     .setPublicExponent(publicExponent)
                     .setAlgorithm(algorithm)
                     .setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.CUSTOM)
                     .build()
               )
               .setModulus(modulus)
               .setCustomKid(getStringItem(jsonKey, "kid"))
               .build()
            : JwtRsaSsaPkcs1PublicKey.builder()
               .setParameters(
                  JwtRsaSsaPkcs1Parameters.builder()
                     .setModulusSizeBits(modulus.bitLength())
                     .setPublicExponent(publicExponent)
                     .setAlgorithm(algorithm)
                     .setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED)
                     .build()
               )
               .setModulus(modulus)
               .build();
      } else {
         throw new UnsupportedOperationException("importing RSA private keys is not implemented");
      }
   }

   @AccessesPartialKey
   private static JwtRsaSsaPssPublicKey convertToRsaSsaPssKey(JsonObject jsonKey) throws GeneralSecurityException {
      String publicExponent = getStringItem(jsonKey, "alg");
      JwtRsaSsaPssParameters.Algorithm algorithm;
      switch (publicExponent) {
         case "PS256":
            algorithm = JwtRsaSsaPssParameters.Algorithm.PS256;
            break;
         case "PS384":
            algorithm = JwtRsaSsaPssParameters.Algorithm.PS384;
            break;
         case "PS512":
            algorithm = JwtRsaSsaPssParameters.Algorithm.PS512;
            break;
         default:
            throw new GeneralSecurityException("Unknown Rsa Algorithm: " + getStringItem(jsonKey, "alg"));
      }

      if (!jsonKey.has("p") && !jsonKey.has("q") && !jsonKey.has("dq") && !jsonKey.has("dq") && !jsonKey.has("d") && !jsonKey.has("qi")) {
         expectStringItem(jsonKey, "kty", "RSA");
         validateUseIsSig(jsonKey);
         validateKeyOpsIsVerify(jsonKey);
         BigInteger publicExponent = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "e")));
         BigInteger modulus = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "n")));
         return jsonKey.has("kid")
            ? JwtRsaSsaPssPublicKey.builder()
               .setParameters(
                  JwtRsaSsaPssParameters.builder()
                     .setModulusSizeBits(modulus.bitLength())
                     .setPublicExponent(publicExponent)
                     .setAlgorithm(algorithm)
                     .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.CUSTOM)
                     .build()
               )
               .setModulus(modulus)
               .setCustomKid(getStringItem(jsonKey, "kid"))
               .build()
            : JwtRsaSsaPssPublicKey.builder()
               .setParameters(
                  JwtRsaSsaPssParameters.builder()
                     .setModulusSizeBits(modulus.bitLength())
                     .setPublicExponent(publicExponent)
                     .setAlgorithm(algorithm)
                     .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED)
                     .build()
               )
               .setModulus(modulus)
               .build();
      } else {
         throw new UnsupportedOperationException("importing RSA private keys is not implemented");
      }
   }

   @AccessesPartialKey
   private static JwtEcdsaPublicKey convertToEcdsaKey(JsonObject jsonKey) throws GeneralSecurityException {
      String x = getStringItem(jsonKey, "alg");
      JwtEcdsaParameters.Algorithm algorithm;
      switch (x) {
         case "ES256":
            expectStringItem(jsonKey, "crv", "P-256");
            algorithm = JwtEcdsaParameters.Algorithm.ES256;
            break;
         case "ES384":
            expectStringItem(jsonKey, "crv", "P-384");
            algorithm = JwtEcdsaParameters.Algorithm.ES384;
            break;
         case "ES512":
            expectStringItem(jsonKey, "crv", "P-521");
            algorithm = JwtEcdsaParameters.Algorithm.ES512;
            break;
         default:
            throw new GeneralSecurityException("Unknown Ecdsa Algorithm: " + getStringItem(jsonKey, "alg"));
      }

      if (jsonKey.has("d")) {
         throw new UnsupportedOperationException("importing ECDSA private keys is not implemented");
      } else {
         expectStringItem(jsonKey, "kty", "EC");
         validateUseIsSig(jsonKey);
         validateKeyOpsIsVerify(jsonKey);
         BigInteger x = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "x")));
         BigInteger y = new BigInteger(1, Base64.urlSafeDecode(getStringItem(jsonKey, "y")));
         ECPoint publicPoint = new ECPoint(x, y);
         return jsonKey.has("kid")
            ? JwtEcdsaPublicKey.builder()
               .setParameters(JwtEcdsaParameters.builder().setKidStrategy(JwtEcdsaParameters.KidStrategy.CUSTOM).setAlgorithm(algorithm).build())
               .setPublicPoint(publicPoint)
               .setCustomKid(getStringItem(jsonKey, "kid"))
               .build()
            : JwtEcdsaPublicKey.builder()
               .setParameters(JwtEcdsaParameters.builder().setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED).setAlgorithm(algorithm).build())
               .setPublicPoint(publicPoint)
               .build();
      }
   }

   @Deprecated
   @InlineMe(replacement = "JwkSetConverter.fromPublicKeysetHandle(handle)", imports = "com.google.crypto.tink.jwt.JwkSetConverter")
   public static String fromKeysetHandle(KeysetHandle handle, KeyAccess keyAccess) throws IOException, GeneralSecurityException {
      return fromPublicKeysetHandle(handle);
   }

   @Deprecated
   @InlineMe(replacement = "JwkSetConverter.toPublicKeysetHandle(jwkSet)", imports = "com.google.crypto.tink.jwt.JwkSetConverter")
   public static KeysetHandle toKeysetHandle(String jwkSet, KeyAccess keyAccess) throws IOException, GeneralSecurityException {
      return toPublicKeysetHandle(jwkSet);
   }

   private JwkSetConverter() {
   }
}
