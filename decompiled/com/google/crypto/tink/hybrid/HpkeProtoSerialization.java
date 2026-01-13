package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.hybrid.internal.HpkeUtil;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.HpkeAead;
import com.google.crypto.tink.proto.HpkeKdf;
import com.google.crypto.tink.proto.HpkeKem;
import com.google.crypto.tink.proto.HpkeKeyFormat;
import com.google.crypto.tink.proto.HpkeParams;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class HpkeProtoSerialization {
   private static final int VERSION = 0;
   private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.HpkePrivateKey";
   private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.HpkePrivateKey");
   private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.HpkePublicKey";
   private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.HpkePublicKey");
   private static final ParametersSerializer<HpkeParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      HpkeProtoSerialization::serializeParameters, HpkeParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      HpkeProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<HpkePublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(
      HpkeProtoSerialization::serializePublicKey, HpkePublicKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(
      HpkeProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final KeySerializer<HpkePrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(
      HpkeProtoSerialization::serializePrivateKey, HpkePrivateKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(
      HpkeProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final EnumTypeProtoConverter<OutputPrefixType, HpkeParameters.Variant> VARIANT_TYPE_CONVERTER = EnumTypeProtoConverter.<OutputPrefixType, HpkeParameters.Variant>builder()
      .add(OutputPrefixType.RAW, HpkeParameters.Variant.NO_PREFIX)
      .add(OutputPrefixType.TINK, HpkeParameters.Variant.TINK)
      .add(OutputPrefixType.LEGACY, HpkeParameters.Variant.CRUNCHY)
      .add(OutputPrefixType.CRUNCHY, HpkeParameters.Variant.CRUNCHY)
      .build();
   private static final EnumTypeProtoConverter<HpkeKem, HpkeParameters.KemId> KEM_TYPE_CONVERTER = EnumTypeProtoConverter.<HpkeKem, HpkeParameters.KemId>builder()
      .add(HpkeKem.DHKEM_P256_HKDF_SHA256, HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)
      .add(HpkeKem.DHKEM_P384_HKDF_SHA384, HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)
      .add(HpkeKem.DHKEM_P521_HKDF_SHA512, HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)
      .add(HpkeKem.DHKEM_X25519_HKDF_SHA256, HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)
      .build();
   private static final EnumTypeProtoConverter<HpkeKdf, HpkeParameters.KdfId> KDF_TYPE_CONVERTER = EnumTypeProtoConverter.<HpkeKdf, HpkeParameters.KdfId>builder()
      .add(HpkeKdf.HKDF_SHA256, HpkeParameters.KdfId.HKDF_SHA256)
      .add(HpkeKdf.HKDF_SHA384, HpkeParameters.KdfId.HKDF_SHA384)
      .add(HpkeKdf.HKDF_SHA512, HpkeParameters.KdfId.HKDF_SHA512)
      .build();
   private static final EnumTypeProtoConverter<HpkeAead, HpkeParameters.AeadId> AEAD_TYPE_CONVERTER = EnumTypeProtoConverter.<HpkeAead, HpkeParameters.AeadId>builder()
      .add(HpkeAead.AES_128_GCM, HpkeParameters.AeadId.AES_128_GCM)
      .add(HpkeAead.AES_256_GCM, HpkeParameters.AeadId.AES_256_GCM)
      .add(HpkeAead.CHACHA20_POLY1305, HpkeParameters.AeadId.CHACHA20_POLY1305)
      .build();

   public static void register() throws GeneralSecurityException {
      register(MutableSerializationRegistry.globalInstance());
   }

   public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
      registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
      registry.registerParametersParser(PARAMETERS_PARSER);
      registry.registerKeySerializer(PUBLIC_KEY_SERIALIZER);
      registry.registerKeyParser(PUBLIC_KEY_PARSER);
      registry.registerKeySerializer(PRIVATE_KEY_SERIALIZER);
      registry.registerKeyParser(PRIVATE_KEY_PARSER);
   }

   private static HpkeParams toProtoParameters(HpkeParameters params) throws GeneralSecurityException {
      return HpkeParams.newBuilder()
         .setKem(KEM_TYPE_CONVERTER.toProtoEnum(params.getKemId()))
         .setKdf(KDF_TYPE_CONVERTER.toProtoEnum(params.getKdfId()))
         .setAead(AEAD_TYPE_CONVERTER.toProtoEnum(params.getAeadId()))
         .build();
   }

   private static com.google.crypto.tink.proto.HpkePublicKey toProtoPublicKey(HpkePublicKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.proto.HpkePublicKey.newBuilder()
         .setVersion(0)
         .setParams(toProtoParameters(key.getParameters()))
         .setPublicKey(ByteString.copyFrom(key.getPublicKeyBytes().toByteArray()))
         .build();
   }

   private static com.google.crypto.tink.proto.HpkePrivateKey toProtoPrivateKey(HpkePrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return com.google.crypto.tink.proto.HpkePrivateKey.newBuilder()
         .setVersion(0)
         .setPublicKey(toProtoPublicKey(key.getPublicKey()))
         .setPrivateKey(ByteString.copyFrom(key.getPrivateKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
         .build();
   }

   private static HpkeParameters fromProtoParameters(OutputPrefixType outputPrefixType, HpkeParams protoParams) throws GeneralSecurityException {
      return HpkeParameters.builder()
         .setVariant(VARIANT_TYPE_CONVERTER.fromProtoEnum(outputPrefixType))
         .setKemId(KEM_TYPE_CONVERTER.fromProtoEnum(protoParams.getKem()))
         .setKdfId(KDF_TYPE_CONVERTER.fromProtoEnum(protoParams.getKdf()))
         .setAeadId(AEAD_TYPE_CONVERTER.fromProtoEnum(protoParams.getAead()))
         .build();
   }

   private static ProtoParametersSerialization serializeParameters(HpkeParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.HpkePrivateKey")
            .setValue(HpkeKeyFormat.newBuilder().setParams(toProtoParameters(parameters)).build().toByteString())
            .setOutputPrefixType(VARIANT_TYPE_CONVERTER.toProtoEnum(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializePublicKey(HpkePublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.HpkePublicKey",
         toProtoPublicKey(key).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
         VARIANT_TYPE_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static ProtoKeySerialization serializePrivateKey(HpkePrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.HpkePrivateKey",
         toProtoPrivateKey(key, access).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE,
         VARIANT_TYPE_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static HpkeParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.HpkePrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to HpkeProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
      } else {
         HpkeKeyFormat format;
         try {
            format = HpkeKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing HpkeParameters failed: ", var3);
         }

         return fromProtoParameters(serialization.getKeyTemplate().getOutputPrefixType(), format.getParams());
      }
   }

   private static Bytes encodePublicKeyBytes(HpkeParameters.KemId kemId, byte[] publicKeyBytes) throws GeneralSecurityException {
      BigInteger n = BigIntegerEncoding.fromUnsignedBigEndianBytes(publicKeyBytes);
      byte[] encodedPublicKeyBytes = BigIntegerEncoding.toBigEndianBytesOfFixedLength(n, HpkeUtil.getEncodedPublicKeyLength(kemId));
      return Bytes.copyFrom(encodedPublicKeyBytes);
   }

   private static HpkePublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.HpkePublicKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to HpkeProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.HpkePublicKey protoKey = com.google.crypto.tink.proto.HpkePublicKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               HpkeParameters params = fromProtoParameters(serialization.getOutputPrefixType(), protoKey.getParams());
               return HpkePublicKey.create(
                  params, encodePublicKeyBytes(params.getKemId(), protoKey.getPublicKey().toByteArray()), serialization.getIdRequirementOrNull()
               );
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing HpkePublicKey failed");
         }
      }
   }

   private static SecretBytes encodePrivateKeyBytes(HpkeParameters.KemId kemId, byte[] privateKeyBytes, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      BigInteger n = BigIntegerEncoding.fromUnsignedBigEndianBytes(privateKeyBytes);
      byte[] encodedPrivateKeyBytes = BigIntegerEncoding.toBigEndianBytesOfFixedLength(n, HpkeUtil.getEncodedPrivateKeyLength(kemId));
      return SecretBytes.copyFrom(encodedPrivateKeyBytes, SecretKeyAccess.requireAccess(access));
   }

   private static HpkePrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.HpkePrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to HpkeProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.HpkePrivateKey protoKey = com.google.crypto.tink.proto.HpkePrivateKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               com.google.crypto.tink.proto.HpkePublicKey protoPublicKey = protoKey.getPublicKey();
               if (protoPublicKey.getVersion() != 0) {
                  throw new GeneralSecurityException("Only version 0 keys are accepted");
               } else {
                  HpkeParameters params = fromProtoParameters(serialization.getOutputPrefixType(), protoPublicKey.getParams());
                  HpkePublicKey publicKey = HpkePublicKey.create(
                     params, encodePublicKeyBytes(params.getKemId(), protoPublicKey.getPublicKey().toByteArray()), serialization.getIdRequirementOrNull()
                  );
                  return HpkePrivateKey.create(publicKey, encodePrivateKeyBytes(params.getKemId(), protoKey.getPrivateKey().toByteArray(), access));
               }
            }
         } catch (InvalidProtocolBufferException var6) {
            throw new GeneralSecurityException("Parsing HpkePrivateKey failed");
         }
      }
   }

   private HpkeProtoSerialization() {
   }
}
