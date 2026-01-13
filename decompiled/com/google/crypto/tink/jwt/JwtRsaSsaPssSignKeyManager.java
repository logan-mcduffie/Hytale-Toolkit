package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.jwt.internal.JwtRsaSsaPssProtoSerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.signature.RsaSsaPssPrivateKey;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.RsaSsaPssSignJce;
import com.google.crypto.tink.util.SecretBigInteger;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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

public final class JwtRsaSsaPssSignKeyManager {
   private static final PrivateKeyManager<Void> legacyPrivateKeyManager = LegacyKeyManagerImpl.createPrivateKeyManager(
      getKeyType(), Void.class, com.google.crypto.tink.proto.JwtRsaSsaPssPrivateKey.parser()
   );
   private static final KeyManager<Void> legacyPublicKeyManager = LegacyKeyManagerImpl.create(
      JwtRsaSsaPssVerifyKeyManager.getKeyType(),
      Void.class,
      KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
      com.google.crypto.tink.proto.JwtRsaSsaPssPublicKey.parser()
   );
   private static final KeyCreator<JwtRsaSsaPssParameters> KEY_CREATOR = JwtRsaSsaPssSignKeyManager::createKey;
   private static final PrimitiveConstructor<JwtRsaSsaPssPrivateKey, JwtPublicKeySign> PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      JwtRsaSsaPssSignKeyManager::createFullPrimitive, JwtRsaSsaPssPrivateKey.class, JwtPublicKeySign.class
   );
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   @AccessesPartialKey
   private static JwtRsaSsaPssPrivateKey createKey(JwtRsaSsaPssParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      KeyPairGenerator keyGen = EngineFactory.KEY_PAIR_GENERATOR.getInstance("RSA");
      RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(parameters.getModulusSizeBits(), new BigInteger(1, parameters.getPublicExponent().toByteArray()));
      keyGen.initialize(spec);
      KeyPair keyPair = keyGen.generateKeyPair();
      RSAPublicKey pubKey = (RSAPublicKey)keyPair.getPublic();
      RSAPrivateCrtKey privKey = (RSAPrivateCrtKey)keyPair.getPrivate();
      JwtRsaSsaPssPublicKey.Builder jwtRsaSsaPssPublicKeyBuilder = JwtRsaSsaPssPublicKey.builder().setParameters(parameters).setModulus(pubKey.getModulus());
      if (idRequirement != null) {
         jwtRsaSsaPssPublicKeyBuilder.setIdRequirement(idRequirement);
      }

      JwtRsaSsaPssPublicKey jwtRsaSsaPssPublicKey = jwtRsaSsaPssPublicKeyBuilder.build();
      return JwtRsaSsaPssPrivateKey.builder()
         .setPublicKey(jwtRsaSsaPssPublicKey)
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

   @AccessesPartialKey
   private static RsaSsaPssPrivateKey toRsaSsaPssPrivateKey(JwtRsaSsaPssPrivateKey privateKey) {
      return privateKey.getRsaSsaPssPrivateKey();
   }

   static JwtPublicKeySign createFullPrimitive(JwtRsaSsaPssPrivateKey privateKey) throws GeneralSecurityException {
      RsaSsaPssPrivateKey rsaSsaPssPrivateKey = toRsaSsaPssPrivateKey(privateKey);
      final PublicKeySign signer = RsaSsaPssSignJce.create(rsaSsaPssPrivateKey);
      final String algorithm = privateKey.getParameters().getAlgorithm().getStandardName();
      return new JwtPublicKeySign() {
         @Override
         public String signAndEncode(RawJwt rawJwt) throws GeneralSecurityException {
            String unsignedCompact = JwtFormat.createUnsignedCompact(algorithm, privateKey.getPublicKey().getKid(), rawJwt);
            return JwtFormat.createSignedCompact(unsignedCompact, signer.sign(unsignedCompact.getBytes(StandardCharsets.US_ASCII)));
         }
      };
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.JwtRsaSsaPssPrivateKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put(
         "JWT_PS256_2048_F4_RAW",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(2048)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS256)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED)
            .build()
      );
      result.put(
         "JWT_PS256_2048_F4",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(2048)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS256)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      result.put(
         "JWT_PS256_3072_F4_RAW",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(3072)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS256)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED)
            .build()
      );
      result.put(
         "JWT_PS256_3072_F4",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(3072)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS256)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      result.put(
         "JWT_PS384_3072_F4_RAW",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(3072)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS384)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED)
            .build()
      );
      result.put(
         "JWT_PS384_3072_F4",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(3072)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS384)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      result.put(
         "JWT_PS512_4096_F4_RAW",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(4096)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS512)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED)
            .build()
      );
      result.put(
         "JWT_PS512_4096_F4",
         JwtRsaSsaPssParameters.builder()
            .setModulusSizeBits(4096)
            .setPublicExponent(JwtRsaSsaPssParameters.F4)
            .setAlgorithm(JwtRsaSsaPssParameters.Algorithm.PS512)
            .setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   public static void registerPair(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use RSA SSA PSS in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         JwtRsaSsaPssProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(JwtRsaSsaPssVerifyKeyManager.PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, JwtRsaSsaPssParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPrivateKeyManager, FIPS, newKeyAllowed);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPublicKeyManager, FIPS, false);
      }
   }

   private JwtRsaSsaPssSignKeyManager() {
   }
}
