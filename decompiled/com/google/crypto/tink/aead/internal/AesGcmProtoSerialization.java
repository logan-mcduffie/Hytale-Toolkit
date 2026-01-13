package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.AesGcmKey;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.SerializationRegistry;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.AesGcmKeyFormat;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class AesGcmProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesGcmKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesGcmKey");
   private static final ParametersSerializer<AesGcmParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesGcmProtoSerialization::serializeParameters, AesGcmParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesGcmProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesGcmKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesGcmProtoSerialization::serializeKey, AesGcmKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesGcmProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(AesGcmParameters.Variant variant) throws GeneralSecurityException {
      if (AesGcmParameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (AesGcmParameters.Variant.CRUNCHY.equals(variant)) {
         return OutputPrefixType.CRUNCHY;
      } else if (AesGcmParameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static AesGcmParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return AesGcmParameters.Variant.TINK;
         case CRUNCHY:
         case LEGACY:
            return AesGcmParameters.Variant.CRUNCHY;
         case RAW:
            return AesGcmParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static void validateParameters(AesGcmParameters parameters) throws GeneralSecurityException {
      if (parameters.getTagSizeBytes() != 16) {
         throw new GeneralSecurityException(
            String.format(
               "Invalid tag size in bytes %d. Currently Tink only supports serialization of AES GCM keys with tag size equal to 16 bytes.",
               parameters.getTagSizeBytes()
            )
         );
      } else if (parameters.getIvSizeBytes() != 12) {
         throw new GeneralSecurityException(
            String.format(
               "Invalid IV size in bytes %d. Currently Tink only supports serialization of AES GCM keys with IV size equal to 12 bytes.",
               parameters.getIvSizeBytes()
            )
         );
      }
   }

   private static ProtoParametersSerialization serializeParameters(AesGcmParameters parameters) throws GeneralSecurityException {
      validateParameters(parameters);
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesGcmKey")
            .setValue(AesGcmKeyFormat.newBuilder().setKeySize(parameters.getKeySizeBytes()).build().toByteString())
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesGcmKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      validateParameters(key.getParameters());
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesGcmKey",
         com.google.crypto.tink.proto.AesGcmKey.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static AesGcmParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesGcmKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to AesGcmProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         AesGcmKeyFormat format;
         try {
            format = AesGcmKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesGcmParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 parameters are accepted");
         } else {
            return AesGcmParameters.builder()
               .setKeySizeBytes(format.getKeySize())
               .setIvSizeBytes(12)
               .setTagSizeBytes(16)
               .setVariant(toVariant(serialization.getKeyTemplate().getOutputPrefixType()))
               .build();
         }
      }
   }

   private static AesGcmKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesGcmKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesGcmProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.AesGcmKey protoKey = com.google.crypto.tink.proto.AesGcmKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               AesGcmParameters parameters = AesGcmParameters.builder()
                  .setKeySizeBytes(protoKey.getKeyValue().size())
                  .setIvSizeBytes(12)
                  .setTagSizeBytes(16)
                  .setVariant(toVariant(serialization.getOutputPrefixType()))
                  .build();
               return AesGcmKey.builder()
                  .setParameters(parameters)
                  .setKeyBytes(SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setIdRequirement(serialization.getIdRequirementOrNull())
                  .build();
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesGcmKey failed");
         }
      }
   }

   public static void register() throws GeneralSecurityException {
      register(MutableSerializationRegistry.globalInstance());
   }

   public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
      registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
      registry.registerParametersParser(PARAMETERS_PARSER);
      registry.registerKeySerializer(KEY_SERIALIZER);
      registry.registerKeyParser(KEY_PARSER);
   }

   public static void register(SerializationRegistry.Builder registryBuilder) throws GeneralSecurityException {
      registryBuilder.registerParametersSerializer(PARAMETERS_SERIALIZER);
      registryBuilder.registerParametersParser(PARAMETERS_PARSER);
      registryBuilder.registerKeySerializer(KEY_SERIALIZER);
      registryBuilder.registerKeyParser(KEY_PARSER);
   }

   private AesGcmProtoSerialization() {
   }
}
