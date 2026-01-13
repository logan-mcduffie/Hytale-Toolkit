package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.hybrid.internal.EciesProtoSerialization;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.proto.EciesAeadHkdfPrivateKey;
import com.google.crypto.tink.proto.EciesAeadHkdfPublicKey;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.EciesAeadHkdfHybridDecrypt;
import com.google.crypto.tink.subtle.EciesAeadHkdfHybridEncrypt;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.util.SecretBigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class EciesAeadHkdfPrivateKeyManager {
   private static final PrimitiveConstructor<EciesPrivateKey, HybridDecrypt> HYBRID_DECRYPT_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      EciesAeadHkdfHybridDecrypt::create, EciesPrivateKey.class, HybridDecrypt.class
   );
   private static final PrimitiveConstructor<EciesPublicKey, HybridEncrypt> HYBRID_ENCRYPT_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      EciesAeadHkdfHybridEncrypt::create, EciesPublicKey.class, HybridEncrypt.class
   );
   private static final PrivateKeyManager<HybridDecrypt> legacyPrivateKeyManager = LegacyKeyManagerImpl.createPrivateKeyManager(
      getKeyType(), HybridDecrypt.class, EciesAeadHkdfPrivateKey.parser()
   );
   private static final KeyManager<HybridEncrypt> legacyPublicKeyManager = LegacyKeyManagerImpl.create(
      EciesAeadHkdfPublicKeyManager.getKeyType(), HybridEncrypt.class, KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC, EciesAeadHkdfPublicKey.parser()
   );
   private static final KeyCreator<EciesParameters> KEY_CREATOR = EciesAeadHkdfPrivateKeyManager::createKey;

   private static final ECParameterSpec toParameterSpec(EciesParameters.CurveType curveType) throws GeneralSecurityException {
      if (curveType == EciesParameters.CurveType.NIST_P256) {
         return EllipticCurvesUtil.NIST_P256_PARAMS;
      } else if (curveType == EciesParameters.CurveType.NIST_P384) {
         return EllipticCurvesUtil.NIST_P384_PARAMS;
      } else if (curveType == EciesParameters.CurveType.NIST_P521) {
         return EllipticCurvesUtil.NIST_P521_PARAMS;
      } else {
         throw new GeneralSecurityException("Unsupported curve type: " + curveType);
      }
   }

   @AccessesPartialKey
   private static EciesPrivateKey createKey(EciesParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      KeyPair keyPair = EllipticCurves.generateKeyPair(toParameterSpec(parameters.getCurveType()));
      ECPublicKey ecPubKey = (ECPublicKey)keyPair.getPublic();
      ECPrivateKey ecPrivKey = (ECPrivateKey)keyPair.getPrivate();
      EciesPublicKey publicKey = EciesPublicKey.createForNistCurve(parameters, ecPubKey.getW(), idRequirement);
      return EciesPrivateKey.createForNistCurve(publicKey, SecretBigInteger.fromBigInteger(ecPrivKey.getS(), InsecureSecretKeyAccess.get()));
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPrivateKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put(
         "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
            .setVariant(EciesParameters.Variant.TINK)
            .setDemParameters(
               AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM_RAW",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
            .setVariant(EciesParameters.Variant.NO_PREFIX)
            .setDemParameters(
               AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
            .setVariant(EciesParameters.Variant.TINK)
            .setDemParameters(
               AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM_RAW",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
            .setVariant(EciesParameters.Variant.NO_PREFIX)
            .setDemParameters(
               AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM_COMPRESSED_WITHOUT_PREFIX",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
            .setVariant(EciesParameters.Variant.NO_PREFIX)
            .setDemParameters(
               AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
            .setVariant(EciesParameters.Variant.TINK)
            .setDemParameters(
               AesCtrHmacAeadParameters.builder()
                  .setAesKeySizeBytes(16)
                  .setHmacKeySizeBytes(32)
                  .setTagSizeBytes(16)
                  .setIvSizeBytes(16)
                  .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                  .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                  .build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256_RAW",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
            .setVariant(EciesParameters.Variant.NO_PREFIX)
            .setDemParameters(
               AesCtrHmacAeadParameters.builder()
                  .setAesKeySizeBytes(16)
                  .setHmacKeySizeBytes(32)
                  .setTagSizeBytes(16)
                  .setIvSizeBytes(16)
                  .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                  .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                  .build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
            .setVariant(EciesParameters.Variant.TINK)
            .setDemParameters(
               AesCtrHmacAeadParameters.builder()
                  .setAesKeySizeBytes(16)
                  .setHmacKeySizeBytes(32)
                  .setTagSizeBytes(16)
                  .setIvSizeBytes(16)
                  .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                  .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                  .build()
            )
            .build()
      );
      result.put(
         "ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256_RAW",
         EciesParameters.builder()
            .setCurveType(EciesParameters.CurveType.NIST_P256)
            .setHashType(EciesParameters.HashType.SHA256)
            .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
            .setVariant(EciesParameters.Variant.NO_PREFIX)
            .setDemParameters(
               AesCtrHmacAeadParameters.builder()
                  .setAesKeySizeBytes(16)
                  .setHmacKeySizeBytes(32)
                  .setTagSizeBytes(16)
                  .setIvSizeBytes(16)
                  .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                  .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                  .build()
            )
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   public static void registerPair(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering ECIES Hybrid Encryption is not supported in FIPS mode");
      } else {
         EciesProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(HYBRID_DECRYPT_PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(HYBRID_ENCRYPT_PRIMITIVE_CONSTRUCTOR);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, EciesParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyPrivateKeyManager, newKeyAllowed);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyPublicKeyManager, false);
      }
   }

   public static final KeyTemplate eciesP256HkdfHmacSha256Aes128GcmTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            EciesParameters.builder()
               .setCurveType(EciesParameters.CurveType.NIST_P256)
               .setHashType(EciesParameters.HashType.SHA256)
               .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
               .setVariant(EciesParameters.Variant.TINK)
               .setDemParameters(
                  AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
               )
               .build()
         )
      );
   }

   public static final KeyTemplate rawEciesP256HkdfHmacSha256Aes128GcmCompressedTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            EciesParameters.builder()
               .setCurveType(EciesParameters.CurveType.NIST_P256)
               .setHashType(EciesParameters.HashType.SHA256)
               .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
               .setVariant(EciesParameters.Variant.NO_PREFIX)
               .setDemParameters(
                  AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
               )
               .build()
         )
      );
   }

   public static final KeyTemplate eciesP256HkdfHmacSha256Aes128CtrHmacSha256Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            EciesParameters.builder()
               .setCurveType(EciesParameters.CurveType.NIST_P256)
               .setHashType(EciesParameters.HashType.SHA256)
               .setNistCurvePointFormat(EciesParameters.PointFormat.UNCOMPRESSED)
               .setVariant(EciesParameters.Variant.TINK)
               .setDemParameters(
                  AesCtrHmacAeadParameters.builder()
                     .setAesKeySizeBytes(16)
                     .setHmacKeySizeBytes(32)
                     .setTagSizeBytes(16)
                     .setIvSizeBytes(16)
                     .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                     .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                     .build()
               )
               .build()
         )
      );
   }

   public static final KeyTemplate rawEciesP256HkdfHmacSha256Aes128CtrHmacSha256CompressedTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            EciesParameters.builder()
               .setCurveType(EciesParameters.CurveType.NIST_P256)
               .setHashType(EciesParameters.HashType.SHA256)
               .setNistCurvePointFormat(EciesParameters.PointFormat.COMPRESSED)
               .setVariant(EciesParameters.Variant.NO_PREFIX)
               .setDemParameters(
                  AesCtrHmacAeadParameters.builder()
                     .setAesKeySizeBytes(16)
                     .setHmacKeySizeBytes(32)
                     .setTagSizeBytes(16)
                     .setIvSizeBytes(16)
                     .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
                     .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
                     .build()
               )
               .build()
         )
      );
   }

   private EciesAeadHkdfPrivateKeyManager() {
   }
}
