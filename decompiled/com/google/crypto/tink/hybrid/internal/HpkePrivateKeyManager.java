package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePrivateKey;
import com.google.crypto.tink.hybrid.HpkeProtoSerialization;
import com.google.crypto.tink.hybrid.HpkePublicKey;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class HpkePrivateKeyManager {
   private static final PrimitiveConstructor<HpkePrivateKey, HybridDecrypt> HYBRID_DECRYPT_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      HpkeDecrypt::create, HpkePrivateKey.class, HybridDecrypt.class
   );
   private static final PrimitiveConstructor<HpkePublicKey, HybridEncrypt> HYBRID_ENCRYPT_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      HpkeEncrypt::create, HpkePublicKey.class, HybridEncrypt.class
   );
   private static final PrivateKeyManager<HybridDecrypt> legacyPrivateKeyManager = LegacyKeyManagerImpl.createPrivateKeyManager(
      getKeyType(), HybridDecrypt.class, com.google.crypto.tink.proto.HpkePrivateKey.parser()
   );
   private static final KeyManager<HybridEncrypt> legacyPublicKeyManager = LegacyKeyManagerImpl.create(
      HpkePublicKeyManager.getKeyType(), HybridEncrypt.class, KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC, com.google.crypto.tink.proto.HpkePublicKey.parser()
   );
   private static final KeyCreator<HpkeParameters> KEY_CREATOR = HpkePrivateKeyManager::createKey;

   @AccessesPartialKey
   private static HpkePrivateKey createKey(HpkeParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      SecretBytes privateKeyBytes;
      Bytes publicKeyBytes;
      if (parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)) {
         byte[] privateKeyByteArray = com.google.crypto.tink.subtle.X25519.generatePrivateKey();
         privateKeyBytes = SecretBytes.copyFrom(privateKeyByteArray, InsecureSecretKeyAccess.get());
         publicKeyBytes = Bytes.copyFrom(com.google.crypto.tink.subtle.X25519.publicFromPrivate(privateKeyByteArray));
      } else {
         if (!parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
            && !parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
            && !parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)) {
            throw new GeneralSecurityException("Unknown KEM ID");
         }

         EllipticCurves.CurveType curveType = HpkeUtil.nistHpkeKemToCurve(parameters.getKemId());
         KeyPair keyPair = EllipticCurves.generateKeyPair(curveType);
         publicKeyBytes = Bytes.copyFrom(
            EllipticCurves.pointEncode(curveType, EllipticCurves.PointFormatType.UNCOMPRESSED, ((ECPublicKey)keyPair.getPublic()).getW())
         );
         privateKeyBytes = SecretBytes.copyFrom(
            BigIntegerEncoding.toBigEndianBytesOfFixedLength(
               ((ECPrivateKey)keyPair.getPrivate()).getS(), HpkeUtil.getEncodedPrivateKeyLength(parameters.getKemId())
            ),
            InsecureSecretKeyAccess.get()
         );
      }

      HpkePublicKey publicKey = HpkePublicKey.create(parameters, publicKeyBytes, idRequirement);
      return HpkePrivateKey.create(publicKey, privateKeyBytes);
   }

   public static void registerPair(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering HPKE Hybrid Encryption is not supported in FIPS mode");
      } else {
         HpkeProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(HYBRID_DECRYPT_PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(HYBRID_ENCRYPT_PRIMITIVE_CONSTRUCTOR);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, HpkeParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyPrivateKeyManager, newKeyAllowed);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyPublicKeyManager, false);
      }
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.HpkePrivateKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_128_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_128_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_CHACHA20_POLY1305",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.CHACHA20_POLY1305)
            .build()
      );
      result.put(
         "DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_CHACHA20_POLY1305_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.CHACHA20_POLY1305)
            .build()
      );
      result.put(
         "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_128_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_128_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_256_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_P256_HKDF_SHA256_HKDF_SHA256_AES_256_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA256)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_128_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA384)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_128_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA384)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_256_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA384)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_P384_HKDF_SHA384_HKDF_SHA384_AES_256_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA384)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_128_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA512)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_128_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA512)
            .setAeadId(HpkeParameters.AeadId.AES_128_GCM)
            .build()
      );
      result.put(
         "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_256_GCM",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.TINK)
            .setKemId(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA512)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      result.put(
         "DHKEM_P521_HKDF_SHA512_HKDF_SHA512_AES_256_GCM_RAW",
         HpkeParameters.builder()
            .setVariant(HpkeParameters.Variant.NO_PREFIX)
            .setKemId(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)
            .setKdfId(HpkeParameters.KdfId.HKDF_SHA512)
            .setAeadId(HpkeParameters.AeadId.AES_256_GCM)
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   private HpkePrivateKeyManager() {
   }
}
