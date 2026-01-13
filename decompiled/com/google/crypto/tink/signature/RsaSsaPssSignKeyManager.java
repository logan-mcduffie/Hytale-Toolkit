package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.signature.internal.RsaSsaPssProtoSerialization;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.RsaSsaPssSignJce;
import com.google.crypto.tink.subtle.RsaSsaPssVerifyJce;
import com.google.crypto.tink.util.SecretBigInteger;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class RsaSsaPssSignKeyManager {
   private static final PrimitiveConstructor<RsaSsaPssPrivateKey, PublicKeySign> PUBLIC_KEY_SIGN_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      RsaSsaPssSignJce::create, RsaSsaPssPrivateKey.class, PublicKeySign.class
   );
   private static final PrimitiveConstructor<RsaSsaPssPublicKey, PublicKeyVerify> PUBLIC_KEY_VERIFY_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      RsaSsaPssVerifyJce::create, RsaSsaPssPublicKey.class, PublicKeyVerify.class
   );
   private static final PrivateKeyManager<PublicKeySign> legacyPrivateKeyManager = LegacyKeyManagerImpl.createPrivateKeyManager(
      getKeyType(), PublicKeySign.class, com.google.crypto.tink.proto.RsaSsaPssPrivateKey.parser()
   );
   private static final KeyManager<PublicKeyVerify> legacyPublicKeyManager = LegacyKeyManagerImpl.create(
      RsaSsaPssVerifyKeyManager.getKeyType(),
      PublicKeyVerify.class,
      KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
      com.google.crypto.tink.proto.RsaSsaPssPublicKey.parser()
   );
   private static final KeyCreator<RsaSsaPssParameters> KEY_CREATOR = RsaSsaPssSignKeyManager::createKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.RsaSsaPssPrivateKey";
   }

   @AccessesPartialKey
   private static RsaSsaPssPrivateKey createKey(RsaSsaPssParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      KeyPairGenerator keyGen = EngineFactory.KEY_PAIR_GENERATOR.getInstance("RSA");
      RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(parameters.getModulusSizeBits(), new BigInteger(1, parameters.getPublicExponent().toByteArray()));
      keyGen.initialize(spec);
      KeyPair keyPair = keyGen.generateKeyPair();
      RSAPublicKey pubKey = (RSAPublicKey)keyPair.getPublic();
      RSAPrivateCrtKey privKey = (RSAPrivateCrtKey)keyPair.getPrivate();
      RsaSsaPssPublicKey rsaSsaPssPublicKey = RsaSsaPssPublicKey.builder()
         .setParameters(parameters)
         .setModulus(pubKey.getModulus())
         .setIdRequirement(idRequirement)
         .build();
      return RsaSsaPssPrivateKey.builder()
         .setPublicKey(rsaSsaPssPublicKey)
         .setPrimes(
            SecretBigInteger.fromBigInteger(privKey.getPrimeP(), InsecureSecretKeyAccess.get()),
            SecretBigInteger.fromBigInteger(privKey.getPrimeQ(), InsecureSecretKeyAccess.get())
         )
         .setPrivateExponent(SecretBigInteger.fromBigInteger(privKey.getPrivateExponent(), InsecureSecretKeyAccess.get()))
         .setPrimeExponents(
            SecretBigInteger.fromBigInteger(privKey.getPrimeExponentP(), InsecureSecretKeyAccess.get()),
            SecretBigInteger.fromBigInteger(privKey.getPrimeExponentQ(), InsecureSecretKeyAccess.get())
         )
         .setCrtCoefficient(SecretBigInteger.fromBigInteger(privKey.getCrtCoefficient(), InsecureSecretKeyAccess.get()))
         .build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put(
         "RSA_SSA_PSS_3072_SHA256_F4",
         RsaSsaPssParameters.builder()
            .setSigHashType(RsaSsaPssParameters.HashType.SHA256)
            .setMgf1HashType(RsaSsaPssParameters.HashType.SHA256)
            .setSaltLengthBytes(32)
            .setModulusSizeBits(3072)
            .setPublicExponent(RsaSsaPssParameters.F4)
            .setVariant(RsaSsaPssParameters.Variant.TINK)
            .build()
      );
      result.put(
         "RSA_SSA_PSS_3072_SHA256_F4_RAW",
         RsaSsaPssParameters.builder()
            .setSigHashType(RsaSsaPssParameters.HashType.SHA256)
            .setMgf1HashType(RsaSsaPssParameters.HashType.SHA256)
            .setSaltLengthBytes(32)
            .setModulusSizeBits(3072)
            .setPublicExponent(RsaSsaPssParameters.F4)
            .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
            .build()
      );
      result.put("RSA_SSA_PSS_3072_SHA256_SHA256_32_F4", PredefinedSignatureParameters.RSA_SSA_PSS_3072_SHA256_SHA256_32_F4);
      result.put(
         "RSA_SSA_PSS_4096_SHA512_F4",
         RsaSsaPssParameters.builder()
            .setSigHashType(RsaSsaPssParameters.HashType.SHA512)
            .setMgf1HashType(RsaSsaPssParameters.HashType.SHA512)
            .setSaltLengthBytes(64)
            .setModulusSizeBits(4096)
            .setPublicExponent(RsaSsaPssParameters.F4)
            .setVariant(RsaSsaPssParameters.Variant.TINK)
            .build()
      );
      result.put(
         "RSA_SSA_PSS_4096_SHA512_F4_RAW",
         RsaSsaPssParameters.builder()
            .setSigHashType(RsaSsaPssParameters.HashType.SHA512)
            .setMgf1HashType(RsaSsaPssParameters.HashType.SHA512)
            .setSaltLengthBytes(64)
            .setModulusSizeBits(4096)
            .setPublicExponent(RsaSsaPssParameters.F4)
            .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
            .build()
      );
      result.put("RSA_SSA_PSS_4096_SHA512_SHA512_64_F4", PredefinedSignatureParameters.RSA_SSA_PSS_4096_SHA512_SHA512_64_F4);
      return Collections.unmodifiableMap(result);
   }

   public static void registerPair(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use RSA SSA PSS in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         RsaSsaPssProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PUBLIC_KEY_SIGN_PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PUBLIC_KEY_VERIFY_PRIMITIVE_CONSTRUCTOR);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, RsaSsaPssParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPrivateKeyManager, FIPS, newKeyAllowed);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPublicKeyManager, FIPS, false);
      }
   }

   public static final KeyTemplate rsa3072PssSha256F4Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            RsaSsaPssParameters.builder()
               .setSigHashType(RsaSsaPssParameters.HashType.SHA256)
               .setMgf1HashType(RsaSsaPssParameters.HashType.SHA256)
               .setSaltLengthBytes(32)
               .setModulusSizeBits(3072)
               .setPublicExponent(RsaSsaPssParameters.F4)
               .setVariant(RsaSsaPssParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate rawRsa3072PssSha256F4Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            RsaSsaPssParameters.builder()
               .setSigHashType(RsaSsaPssParameters.HashType.SHA256)
               .setMgf1HashType(RsaSsaPssParameters.HashType.SHA256)
               .setSaltLengthBytes(32)
               .setModulusSizeBits(3072)
               .setPublicExponent(RsaSsaPssParameters.F4)
               .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
               .build()
         )
      );
   }

   public static final KeyTemplate rsa4096PssSha512F4Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            RsaSsaPssParameters.builder()
               .setSigHashType(RsaSsaPssParameters.HashType.SHA512)
               .setMgf1HashType(RsaSsaPssParameters.HashType.SHA512)
               .setSaltLengthBytes(64)
               .setModulusSizeBits(4096)
               .setPublicExponent(RsaSsaPssParameters.F4)
               .setVariant(RsaSsaPssParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate rawRsa4096PssSha512F4Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            RsaSsaPssParameters.builder()
               .setSigHashType(RsaSsaPssParameters.HashType.SHA512)
               .setMgf1HashType(RsaSsaPssParameters.HashType.SHA512)
               .setSaltLengthBytes(64)
               .setModulusSizeBits(4096)
               .setPublicExponent(RsaSsaPssParameters.F4)
               .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
               .build()
         )
      );
   }

   private RsaSsaPssSignKeyManager() {
   }
}
