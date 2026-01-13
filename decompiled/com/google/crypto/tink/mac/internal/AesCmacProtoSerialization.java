package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.mac.AesCmacKey;
import com.google.crypto.tink.mac.AesCmacParameters;
import com.google.crypto.tink.proto.AesCmacKeyFormat;
import com.google.crypto.tink.proto.AesCmacParams;
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
public final class AesCmacProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesCmacKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesCmacKey");
   private static final ParametersSerializer<AesCmacParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesCmacProtoSerialization::serializeParameters, AesCmacParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesCmacProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesCmacKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesCmacProtoSerialization::serializeKey, AesCmacKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesCmacProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toOutputPrefixType(AesCmacParameters.Variant variant) throws GeneralSecurityException {
      if (AesCmacParameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (AesCmacParameters.Variant.CRUNCHY.equals(variant)) {
         return OutputPrefixType.CRUNCHY;
      } else if (AesCmacParameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else if (AesCmacParameters.Variant.LEGACY.equals(variant)) {
         return OutputPrefixType.LEGACY;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static AesCmacParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return AesCmacParameters.Variant.TINK;
         case CRUNCHY:
            return AesCmacParameters.Variant.CRUNCHY;
         case LEGACY:
            return AesCmacParameters.Variant.LEGACY;
         case RAW:
            return AesCmacParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static AesCmacParams getProtoParams(AesCmacParameters parameters) {
      return AesCmacParams.newBuilder().setTagSize(parameters.getCryptographicTagSizeBytes()).build();
   }

   private static ProtoParametersSerialization serializeParameters(AesCmacParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesCmacKey")
            .setValue(AesCmacKeyFormat.newBuilder().setParams(getProtoParams(parameters)).setKeySize(parameters.getKeySizeBytes()).build().toByteString())
            .setOutputPrefixType(toOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesCmacKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesCmacKey",
         com.google.crypto.tink.proto.AesCmacKey.newBuilder()
            .setParams(getProtoParams(key.getParameters()))
            .setKeyValue(ByteString.copyFrom(key.getAesKey().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static AesCmacParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCmacKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to AesCmacProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         AesCmacKeyFormat format;
         try {
            format = AesCmacKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesCmacParameters failed: ", var3);
         }

         return AesCmacParameters.builder()
            .setKeySizeBytes(format.getKeySize())
            .setTagSizeBytes(format.getParams().getTagSize())
            .setVariant(toVariant(serialization.getKeyTemplate().getOutputPrefixType()))
            .build();
      }
   }

   private static AesCmacKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCmacKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesCmacProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.AesCmacKey protoKey = com.google.crypto.tink.proto.AesCmacKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               AesCmacParameters parameters = AesCmacParameters.builder()
                  .setKeySizeBytes(protoKey.getKeyValue().size())
                  .setTagSizeBytes(protoKey.getParams().getTagSize())
                  .setVariant(toVariant(serialization.getOutputPrefixType()))
                  .build();
               return AesCmacKey.builder()
                  .setParameters(parameters)
                  .setAesKeyBytes(SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setIdRequirement(serialization.getIdRequirementOrNull())
                  .build();
            }
         } catch (IllegalArgumentException | InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesCmacKey failed");
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

   private AesCmacProtoSerialization() {
   }
}
