package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.jwt.internal.JsonUtil;
import com.google.crypto.tink.signature.EcdsaPrivateKey;
import com.google.crypto.tink.signature.EcdsaPublicKey;
import com.google.crypto.tink.signature.RsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.signature.RsaSsaPssPrivateKey;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.subtle.EcdsaSignJce;
import com.google.crypto.tink.subtle.EcdsaVerifyJce;
import com.google.crypto.tink.subtle.RsaSsaPkcs1SignJce;
import com.google.crypto.tink.subtle.RsaSsaPkcs1VerifyJce;
import com.google.crypto.tink.subtle.RsaSsaPssSignJce;
import com.google.crypto.tink.subtle.RsaSsaPssVerifyJce;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

class JwtSignatureConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   private JwtSignatureConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         JwtPublicKeySignWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtEcdsaSign, JwtEcdsaPrivateKey.class, JwtPublicKeySign.class)
         );
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtRsaSsaPkcs1Sign, JwtRsaSsaPkcs1PrivateKey.class, JwtPublicKeySign.class)
         );
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtRsaSsaPssSign, JwtRsaSsaPssPrivateKey.class, JwtPublicKeySign.class)
         );
         JwtPublicKeyVerifyWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtEcdsaVerify, JwtEcdsaPublicKey.class, JwtPublicKeyVerify.class)
         );
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtRsaSsaPkcs1Verify, JwtRsaSsaPkcs1PublicKey.class, JwtPublicKeyVerify.class)
         );
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(JwtSignatureConfigurationV0::createJwtRsaSsaPssVerify, JwtRsaSsaPssPublicKey.class, JwtPublicKeyVerify.class)
         );
         return InternalConfiguration.createFromPrimitiveRegistry(builder.build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   @AccessesPartialKey
   private static EcdsaPublicKey toEcdsaPublicKey(JwtEcdsaPublicKey publicKey) {
      return publicKey.getEcdsaPublicKey();
   }

   @AccessesPartialKey
   private static EcdsaPrivateKey toEcdsaPrivateKey(JwtEcdsaPrivateKey privateKey) throws GeneralSecurityException {
      return privateKey.getEcdsaPrivateKey();
   }

   private static JwtPublicKeySign createJwtEcdsaSign(JwtEcdsaPrivateKey privateKey) throws GeneralSecurityException {
      EcdsaPrivateKey ecdsaPrivateKey = toEcdsaPrivateKey(privateKey);
      PublicKeySign signer = EcdsaSignJce.create(ecdsaPrivateKey);
      String algorithm = privateKey.getParameters().getAlgorithm().getStandardName();
      return rawJwt -> {
         String unsignedCompact = JwtFormat.createUnsignedCompact(algorithm, privateKey.getPublicKey().getKid(), rawJwt);
         return JwtFormat.createSignedCompact(unsignedCompact, signer.sign(unsignedCompact.getBytes(StandardCharsets.US_ASCII)));
      };
   }

   @AccessesPartialKey
   private static RsaSsaPkcs1PrivateKey toRsaSsaPkcs1PrivateKey(JwtRsaSsaPkcs1PrivateKey privateKey) {
      return privateKey.getRsaSsaPkcs1PrivateKey();
   }

   private static JwtPublicKeySign createJwtRsaSsaPkcs1Sign(JwtRsaSsaPkcs1PrivateKey privateKey) throws GeneralSecurityException {
      RsaSsaPkcs1PrivateKey rsaSsaPkcs1PrivateKey = toRsaSsaPkcs1PrivateKey(privateKey);
      PublicKeySign signer = RsaSsaPkcs1SignJce.create(rsaSsaPkcs1PrivateKey);
      String algorithm = privateKey.getParameters().getAlgorithm().getStandardName();
      return rawJwt -> {
         String unsignedCompact = JwtFormat.createUnsignedCompact(algorithm, privateKey.getPublicKey().getKid(), rawJwt);
         return JwtFormat.createSignedCompact(unsignedCompact, signer.sign(unsignedCompact.getBytes(StandardCharsets.US_ASCII)));
      };
   }

   @AccessesPartialKey
   private static RsaSsaPssPrivateKey toRsaSsaPssPrivateKey(JwtRsaSsaPssPrivateKey privateKey) {
      return privateKey.getRsaSsaPssPrivateKey();
   }

   private static JwtPublicKeySign createJwtRsaSsaPssSign(JwtRsaSsaPssPrivateKey privateKey) throws GeneralSecurityException {
      RsaSsaPssPrivateKey rsaSsaPssPrivateKey = toRsaSsaPssPrivateKey(privateKey);
      PublicKeySign signer = RsaSsaPssSignJce.create(rsaSsaPssPrivateKey);
      String algorithm = privateKey.getParameters().getAlgorithm().getStandardName();
      return rawJwt -> {
         String unsignedCompact = JwtFormat.createUnsignedCompact(algorithm, privateKey.getPublicKey().getKid(), rawJwt);
         return JwtFormat.createSignedCompact(unsignedCompact, signer.sign(unsignedCompact.getBytes(StandardCharsets.US_ASCII)));
      };
   }

   private static JwtPublicKeyVerify createJwtEcdsaVerify(JwtEcdsaPublicKey publicKey) throws GeneralSecurityException {
      EcdsaPublicKey ecdsaPublicKey = toEcdsaPublicKey(publicKey);
      PublicKeyVerify verifier = EcdsaVerifyJce.create(ecdsaPublicKey);
      return (compact, validator) -> {
         JwtFormat.Parts parts = JwtFormat.splitSignedCompact(compact);
         verifier.verify(parts.signatureOrMac, parts.unsignedCompact.getBytes(StandardCharsets.US_ASCII));
         JsonObject parsedHeader = JsonUtil.parseJson(parts.header);
         JwtFormat.validateHeader(
            parsedHeader, publicKey.getParameters().getAlgorithm().getStandardName(), publicKey.getKid(), publicKey.getParameters().allowKidAbsent()
         );
         RawJwt token = RawJwt.fromJsonPayload(JwtFormat.getTypeHeader(parsedHeader), parts.payload);
         return validator.validate(token);
      };
   }

   @AccessesPartialKey
   private static RsaSsaPkcs1PublicKey toRsaSsaPkcs1PublicKey(JwtRsaSsaPkcs1PublicKey publicKey) {
      return publicKey.getRsaSsaPkcs1PublicKey();
   }

   private static JwtPublicKeyVerify createJwtRsaSsaPkcs1Verify(JwtRsaSsaPkcs1PublicKey publicKey) throws GeneralSecurityException {
      RsaSsaPkcs1PublicKey rsaSsaPkcs1PublicKey = toRsaSsaPkcs1PublicKey(publicKey);
      PublicKeyVerify verifier = RsaSsaPkcs1VerifyJce.create(rsaSsaPkcs1PublicKey);
      return (compact, validator) -> {
         JwtFormat.Parts parts = JwtFormat.splitSignedCompact(compact);
         verifier.verify(parts.signatureOrMac, parts.unsignedCompact.getBytes(StandardCharsets.US_ASCII));
         JsonObject parsedHeader = JsonUtil.parseJson(parts.header);
         JwtFormat.validateHeader(
            parsedHeader, publicKey.getParameters().getAlgorithm().getStandardName(), publicKey.getKid(), publicKey.getParameters().allowKidAbsent()
         );
         RawJwt token = RawJwt.fromJsonPayload(JwtFormat.getTypeHeader(parsedHeader), parts.payload);
         return validator.validate(token);
      };
   }

   @AccessesPartialKey
   private static RsaSsaPssPublicKey toRsaSsaPssPublicKey(JwtRsaSsaPssPublicKey publicKey) {
      return publicKey.getRsaSsaPssPublicKey();
   }

   private static JwtPublicKeyVerify createJwtRsaSsaPssVerify(JwtRsaSsaPssPublicKey publicKey) throws GeneralSecurityException {
      RsaSsaPssPublicKey rsaSsaPssPublicKey = toRsaSsaPssPublicKey(publicKey);
      PublicKeyVerify verifier = RsaSsaPssVerifyJce.create(rsaSsaPssPublicKey);
      return (compact, validator) -> {
         JwtFormat.Parts parts = JwtFormat.splitSignedCompact(compact);
         verifier.verify(parts.signatureOrMac, parts.unsignedCompact.getBytes(StandardCharsets.US_ASCII));
         JsonObject parsedHeader = JsonUtil.parseJson(parts.header);
         JwtFormat.validateHeader(
            parsedHeader, publicKey.getParameters().getAlgorithm().getStandardName(), publicKey.getKid(), publicKey.getParameters().allowKidAbsent()
         );
         RawJwt token = RawJwt.fromJsonPayload(JwtFormat.getTypeHeader(parsedHeader), parts.payload);
         return validator.validate(token);
      };
   }

   public static Configuration get() throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Cannot use JwtSignatureConfigurationV0, as BoringCrypto module is needed for FIPS compatibility");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }
}
