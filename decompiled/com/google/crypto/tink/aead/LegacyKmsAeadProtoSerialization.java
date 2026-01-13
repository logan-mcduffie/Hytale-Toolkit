package com.google.crypto.tink.aead;

import com.google.crypto.tink.SecretKeyAccess;
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
import com.google.crypto.tink.proto.KmsAeadKey;
import com.google.crypto.tink.proto.KmsAeadKeyFormat;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

final class LegacyKmsAeadProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.KmsAeadKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.KmsAeadKey");
   private static final ParametersSerializer<LegacyKmsAeadParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      LegacyKmsAeadProtoSerialization::serializeParameters, LegacyKmsAeadParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      LegacyKmsAeadProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<LegacyKmsAeadKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      LegacyKmsAeadProtoSerialization::serializeKey, LegacyKmsAeadKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      LegacyKmsAeadProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(LegacyKmsAeadParameters.Variant variant) throws GeneralSecurityException {
      if (LegacyKmsAeadParameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (LegacyKmsAeadParameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static LegacyKmsAeadParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return LegacyKmsAeadParameters.Variant.TINK;
         case RAW:
            return LegacyKmsAeadParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static ProtoParametersSerialization serializeParameters(LegacyKmsAeadParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.KmsAeadKey")
            .setValue(KmsAeadKeyFormat.newBuilder().setKeyUri(parameters.keyUri()).build().toByteString())
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.variant()))
            .build()
      );
   }

   private static LegacyKmsAeadParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.KmsAeadKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to LegacyKmsAeadProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         KmsAeadKeyFormat format;
         try {
            format = KmsAeadKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing KmsAeadKeyFormat failed: ", var3);
         }

         return LegacyKmsAeadParameters.create(format.getKeyUri(), toVariant(serialization.getKeyTemplate().getOutputPrefixType()));
      }
   }

   private static ProtoKeySerialization serializeKey(LegacyKmsAeadKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.KmsAeadKey",
         KmsAeadKey.newBuilder().setParams(KmsAeadKeyFormat.newBuilder().setKeyUri(key.getParameters().keyUri()).build()).build().toByteString(),
         KeyData.KeyMaterialType.REMOTE,
         toProtoOutputPrefixType(key.getParameters().variant()),
         key.getIdRequirementOrNull()
      );
   }

   private static LegacyKmsAeadKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.KmsAeadKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to LegacyKmsAeadProtoSerialization.parseKey");
      } else {
         try {
            KmsAeadKey protoKey = KmsAeadKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("KmsAeadKey are only accepted with version 0, got " + protoKey);
            } else {
               LegacyKmsAeadParameters parameters = LegacyKmsAeadParameters.create(
                  protoKey.getParams().getKeyUri(), toVariant(serialization.getOutputPrefixType())
               );
               return LegacyKmsAeadKey.create(parameters, serialization.getIdRequirementOrNull());
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing KmsAeadKey failed: ", var4);
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

   private LegacyKmsAeadProtoSerialization() {
   }
}
