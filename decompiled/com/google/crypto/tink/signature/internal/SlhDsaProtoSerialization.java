package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.SlhDsaHashType;
import com.google.crypto.tink.proto.SlhDsaKeyFormat;
import com.google.crypto.tink.proto.SlhDsaParams;
import com.google.crypto.tink.proto.SlhDsaSignatureType;
import com.google.crypto.tink.signature.SlhDsaParameters;
import com.google.crypto.tink.signature.SlhDsaPrivateKey;
import com.google.crypto.tink.signature.SlhDsaPublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class SlhDsaProtoSerialization {
   private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey";
   private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey");
   private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.SlhDsaPublicKey";
   private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.SlhDsaPublicKey");
   private static final ParametersSerializer<SlhDsaParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      SlhDsaProtoSerialization::serializeParameters, SlhDsaParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      SlhDsaProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<SlhDsaPublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(
      SlhDsaProtoSerialization::serializePublicKey, SlhDsaPublicKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(
      SlhDsaProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final KeySerializer<SlhDsaPrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(
      SlhDsaProtoSerialization::serializePrivateKey, SlhDsaPrivateKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(
      SlhDsaProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final EnumTypeProtoConverter<OutputPrefixType, SlhDsaParameters.Variant> VARIANT_CONVERTER = EnumTypeProtoConverter.<OutputPrefixType, SlhDsaParameters.Variant>builder()
      .add(OutputPrefixType.RAW, SlhDsaParameters.Variant.NO_PREFIX)
      .add(OutputPrefixType.TINK, SlhDsaParameters.Variant.TINK)
      .build();
   private static final EnumTypeProtoConverter<SlhDsaHashType, SlhDsaParameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.<SlhDsaHashType, SlhDsaParameters.HashType>builder()
      .add(SlhDsaHashType.SHA2, SlhDsaParameters.HashType.SHA2)
      .add(SlhDsaHashType.SHAKE, SlhDsaParameters.HashType.SHAKE)
      .build();
   private static final EnumTypeProtoConverter<SlhDsaSignatureType, SlhDsaParameters.SignatureType> SIGNATURE_TYPE_CONVERTER = EnumTypeProtoConverter.<SlhDsaSignatureType, SlhDsaParameters.SignatureType>builder()
      .add(SlhDsaSignatureType.FAST_SIGNING, SlhDsaParameters.SignatureType.FAST_SIGNING)
      .add(SlhDsaSignatureType.SMALL_SIGNATURE, SlhDsaParameters.SignatureType.SMALL_SIGNATURE)
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

   private static SlhDsaParams getProtoParams(SlhDsaParameters parameters) throws GeneralSecurityException {
      return SlhDsaParams.newBuilder()
         .setKeySize(parameters.getPrivateKeySize())
         .setHashType(HASH_TYPE_CONVERTER.toProtoEnum(parameters.getHashType()))
         .setSigType(SIGNATURE_TYPE_CONVERTER.toProtoEnum(parameters.getSignatureType()))
         .build();
   }

   private static com.google.crypto.tink.proto.SlhDsaPublicKey getProtoPublicKey(SlhDsaPublicKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.proto.SlhDsaPublicKey.newBuilder()
         .setVersion(0)
         .setParams(getProtoParams(key.getParameters()))
         .setKeyValue(ByteString.copyFrom(key.getSerializedPublicKey().toByteArray()))
         .build();
   }

   private static ProtoParametersSerialization serializeParameters(SlhDsaParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey")
            .setValue(SlhDsaKeyFormat.newBuilder().setParams(getProtoParams(parameters)).setVersion(0).build().toByteString())
            .setOutputPrefixType(VARIANT_CONVERTER.toProtoEnum(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializePublicKey(SlhDsaPublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.SlhDsaPublicKey",
         getProtoPublicKey(key).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static ProtoKeySerialization serializePrivateKey(SlhDsaPrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey",
         com.google.crypto.tink.proto.SlhDsaPrivateKey.newBuilder()
            .setVersion(0)
            .setPublicKey(getProtoPublicKey(key.getPublicKey()))
            .setKeyValue(ByteString.copyFrom(key.getPrivateKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static SlhDsaParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to SlhDsaProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         SlhDsaKeyFormat format;
         try {
            format = SlhDsaKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing SlhDsaParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 keys are accepted for SLH-DSA.");
         } else {
            return validateAndConvertToSlhDsaParameters(
               format.getParams(), VARIANT_CONVERTER.fromProtoEnum(serialization.getKeyTemplate().getOutputPrefixType())
            );
         }
      }
   }

   private static SlhDsaParameters validateAndConvertToSlhDsaParameters(SlhDsaParams params, SlhDsaParameters.Variant variant) throws GeneralSecurityException {
      if (params.getKeySize() == 64 && params.getHashType() == SlhDsaHashType.SHA2 && params.getSigType() == SlhDsaSignatureType.SMALL_SIGNATURE) {
         return SlhDsaParameters.createSlhDsaWithSha2And128S(variant);
      } else {
         throw new GeneralSecurityException("Unsupported SLH-DSA parameters");
      }
   }

   private static SlhDsaPublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.SlhDsaPublicKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to SlhDsaProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else if (serialization.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC) {
         throw new GeneralSecurityException("Wrong KeyMaterialType for SlhDsaPublicKey: " + serialization.getKeyMaterialType().name());
      } else {
         try {
            return convertToSlhDsaPublicKey(
               serialization, com.google.crypto.tink.proto.SlhDsaPublicKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry())
            );
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing SlhDsaPublicKey failed");
         }
      }
   }

   private static SlhDsaPublicKey convertToSlhDsaPublicKey(ProtoKeySerialization serialization, com.google.crypto.tink.proto.SlhDsaPublicKey protoKey) throws GeneralSecurityException {
      if (protoKey.getVersion() != 0) {
         throw new GeneralSecurityException("Only version 0 keys are accepted");
      } else {
         SlhDsaParameters parameters = validateAndConvertToSlhDsaParameters(
            protoKey.getParams(), VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType())
         );
         SlhDsaPublicKey.Builder builder = SlhDsaPublicKey.builder()
            .setParameters(parameters)
            .setSerializedPublicKey(Bytes.copyFrom(protoKey.getKeyValue().toByteArray()));
         if (serialization.getIdRequirementOrNull() != null) {
            builder.setIdRequirement(serialization.getIdRequirementOrNull());
         }

         return builder.build();
      }
   }

   private static SlhDsaPrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.SlhDsaPrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to SlhDsaProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
      } else if (serialization.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) {
         throw new GeneralSecurityException("Wrong KeyMaterialType for SlhDsaPrivateKey: " + serialization.getKeyMaterialType().name());
      } else {
         try {
            com.google.crypto.tink.proto.SlhDsaPrivateKey protoKey = com.google.crypto.tink.proto.SlhDsaPrivateKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               SlhDsaPublicKey publicKey = convertToSlhDsaPublicKey(serialization, protoKey.getPublicKey());
               return SlhDsaPrivateKey.createWithoutVerification(
                  publicKey, SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access))
               );
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing SlhDsaPrivateKey failed");
         }
      }
   }

   private SlhDsaProtoSerialization() {
   }
}
