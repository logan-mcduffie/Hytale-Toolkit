package com.google.crypto.tink.daead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.daead.AesSivKey;
import com.google.crypto.tink.daead.AesSivParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.AesSivKeyFormat;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class AesSivProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesSivKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesSivKey");
   private static final ParametersSerializer<AesSivParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesSivProtoSerialization::serializeParameters, AesSivParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesSivProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesSivKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesSivProtoSerialization::serializeKey, AesSivKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesSivProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final Map<AesSivParameters.Variant, OutputPrefixType> variantsToOutputPrefixMap = createVariantToOutputPrefixMap();
   private static final Map<OutputPrefixType, AesSivParameters.Variant> outputPrefixToVariantMap = createOutputPrefixToVariantMap();

   private static Map<AesSivParameters.Variant, OutputPrefixType> createVariantToOutputPrefixMap() {
      Map<AesSivParameters.Variant, OutputPrefixType> result = new HashMap<>();
      result.put(AesSivParameters.Variant.NO_PREFIX, OutputPrefixType.RAW);
      result.put(AesSivParameters.Variant.TINK, OutputPrefixType.TINK);
      result.put(AesSivParameters.Variant.CRUNCHY, OutputPrefixType.CRUNCHY);
      return Collections.unmodifiableMap(result);
   }

   private static Map<OutputPrefixType, AesSivParameters.Variant> createOutputPrefixToVariantMap() {
      Map<OutputPrefixType, AesSivParameters.Variant> result = new EnumMap<>(OutputPrefixType.class);
      result.put(OutputPrefixType.RAW, AesSivParameters.Variant.NO_PREFIX);
      result.put(OutputPrefixType.TINK, AesSivParameters.Variant.TINK);
      result.put(OutputPrefixType.CRUNCHY, AesSivParameters.Variant.CRUNCHY);
      result.put(OutputPrefixType.LEGACY, AesSivParameters.Variant.CRUNCHY);
      return Collections.unmodifiableMap(result);
   }

   private static OutputPrefixType toProtoOutputPrefixType(AesSivParameters.Variant variant) throws GeneralSecurityException {
      if (variantsToOutputPrefixMap.containsKey(variant)) {
         return variantsToOutputPrefixMap.get(variant);
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static AesSivParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      if (outputPrefixToVariantMap.containsKey(outputPrefixType)) {
         return outputPrefixToVariantMap.get(outputPrefixType);
      } else {
         throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static ProtoParametersSerialization serializeParameters(AesSivParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesSivKey")
            .setValue(AesSivKeyFormat.newBuilder().setKeySize(parameters.getKeySizeBytes()).build().toByteString())
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesSivKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesSivKey",
         com.google.crypto.tink.proto.AesSivKey.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static AesSivParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesSivKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesSivParameters.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
      } else {
         AesSivKeyFormat format;
         try {
            format = AesSivKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (format.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesSivParameters failed: ", var3);
         }

         return AesSivParameters.builder()
            .setKeySizeBytes(format.getKeySize())
            .setVariant(toVariant(serialization.getKeyTemplate().getOutputPrefixType()))
            .build();
      }
   }

   private static AesSivKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesSivKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesSivParameters.parseParameters");
      } else {
         try {
            com.google.crypto.tink.proto.AesSivKey protoKey = com.google.crypto.tink.proto.AesSivKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               AesSivParameters parameters = AesSivParameters.builder()
                  .setKeySizeBytes(protoKey.getKeyValue().size())
                  .setVariant(toVariant(serialization.getOutputPrefixType()))
                  .build();
               return AesSivKey.builder()
                  .setParameters(parameters)
                  .setKeyBytes(SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setIdRequirement(serialization.getIdRequirementOrNull())
                  .build();
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesSivKey failed");
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

   private AesSivProtoSerialization() {
   }
}
