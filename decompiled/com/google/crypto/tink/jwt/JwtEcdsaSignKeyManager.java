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
import com.google.crypto.tink.jwt.internal.JwtEcdsaProtoSerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.signature.EcdsaPrivateKey;
import com.google.crypto.tink.subtle.EcdsaSignJce;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.util.SecretBigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class JwtEcdsaSignKeyManager {
   private static final PrivateKeyManager<Void> legacyPrivateKeyManager = LegacyKeyManagerImpl.createPrivateKeyManager(
      getKeyType(), Void.class, com.google.crypto.tink.proto.JwtEcdsaPrivateKey.parser()
   );
   private static final KeyManager<Void> legacyPublicKeyManager = LegacyKeyManagerImpl.create(
      JwtEcdsaVerifyKeyManager.getKeyType(), Void.class, KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC, com.google.crypto.tink.proto.JwtEcdsaPublicKey.parser()
   );
   private static final PrimitiveConstructor<JwtEcdsaPrivateKey, JwtPublicKeySign> PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      JwtEcdsaSignKeyManager::createFullPrimitive, JwtEcdsaPrivateKey.class, JwtPublicKeySign.class
   );
   private static final KeyCreator<JwtEcdsaParameters> KEY_CREATOR = JwtEcdsaSignKeyManager::createKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   @AccessesPartialKey
   private static EcdsaPrivateKey toEcdsaPrivateKey(JwtEcdsaPrivateKey privateKey) throws GeneralSecurityException {
      return privateKey.getEcdsaPrivateKey();
   }

   static JwtPublicKeySign createFullPrimitive(JwtEcdsaPrivateKey privateKey) throws GeneralSecurityException {
      EcdsaPrivateKey ecdsaPrivateKey = toEcdsaPrivateKey(privateKey);
      final PublicKeySign signer = EcdsaSignJce.create(ecdsaPrivateKey);
      final String algorithm = privateKey.getParameters().getAlgorithm().getStandardName();
      return new JwtPublicKeySign() {
         @Override
         public String signAndEncode(RawJwt rawJwt) throws GeneralSecurityException {
            String unsignedCompact = JwtFormat.createUnsignedCompact(algorithm, privateKey.getPublicKey().getKid(), rawJwt);
            return JwtFormat.createSignedCompact(unsignedCompact, signer.sign(unsignedCompact.getBytes(StandardCharsets.US_ASCII)));
         }
      };
   }

   @AccessesPartialKey
   private static JwtEcdsaPrivateKey createKey(JwtEcdsaParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      KeyPair keyPair = EllipticCurves.generateKeyPair(parameters.getAlgorithm().getEcParameterSpec());
      ECPublicKey pubKey = (ECPublicKey)keyPair.getPublic();
      ECPrivateKey privKey = (ECPrivateKey)keyPair.getPrivate();
      JwtEcdsaPublicKey.Builder publicKeyBuilder = JwtEcdsaPublicKey.builder().setParameters(parameters).setPublicPoint(pubKey.getW());
      if (idRequirement != null) {
         publicKeyBuilder.setIdRequirement(idRequirement);
      }

      return JwtEcdsaPrivateKey.create(publicKeyBuilder.build(), SecretBigInteger.fromBigInteger(privKey.getS(), InsecureSecretKeyAccess.get()));
   }

   private JwtEcdsaSignKeyManager() {
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put(
         "JWT_ES256_RAW",
         JwtEcdsaParameters.builder().setAlgorithm(JwtEcdsaParameters.Algorithm.ES256).setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED).build()
      );
      result.put(
         "JWT_ES256",
         JwtEcdsaParameters.builder()
            .setAlgorithm(JwtEcdsaParameters.Algorithm.ES256)
            .setKidStrategy(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      result.put(
         "JWT_ES384_RAW",
         JwtEcdsaParameters.builder().setAlgorithm(JwtEcdsaParameters.Algorithm.ES384).setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED).build()
      );
      result.put(
         "JWT_ES384",
         JwtEcdsaParameters.builder()
            .setAlgorithm(JwtEcdsaParameters.Algorithm.ES384)
            .setKidStrategy(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      result.put(
         "JWT_ES512_RAW",
         JwtEcdsaParameters.builder().setAlgorithm(JwtEcdsaParameters.Algorithm.ES512).setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED).build()
      );
      result.put(
         "JWT_ES512",
         JwtEcdsaParameters.builder()
            .setAlgorithm(JwtEcdsaParameters.Algorithm.ES512)
            .setKidStrategy(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID)
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   public static void registerPair(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ECDSA in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPrivateKeyManager, FIPS, newKeyAllowed);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyPublicKeyManager, FIPS, false);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, JwtEcdsaParameters.class);
         JwtEcdsaProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(JwtEcdsaVerifyKeyManager.PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
      }
   }
}
