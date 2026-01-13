package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.TinkProtoParametersFormat;
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
import com.google.crypto.tink.proto.KmsEnvelopeAeadKey;
import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormat;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public final class LegacyKmsEnvelopeAeadProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey");
   private static final ParametersSerializer<LegacyKmsEnvelopeAeadParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      LegacyKmsEnvelopeAeadProtoSerialization::serializeParameters, LegacyKmsEnvelopeAeadParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      LegacyKmsEnvelopeAeadProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<LegacyKmsEnvelopeAeadKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      LegacyKmsEnvelopeAeadProtoSerialization::serializeKey, LegacyKmsEnvelopeAeadKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      LegacyKmsEnvelopeAeadProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(LegacyKmsEnvelopeAeadParameters.Variant variant) throws GeneralSecurityException {
      if (LegacyKmsEnvelopeAeadParameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (LegacyKmsEnvelopeAeadParameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static LegacyKmsEnvelopeAeadParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return LegacyKmsEnvelopeAeadParameters.Variant.TINK;
         case RAW:
            return LegacyKmsEnvelopeAeadParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   @AccessesPartialKey
   private static ProtoParametersSerialization serializeParameters(LegacyKmsEnvelopeAeadParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey")
            .setValue(serializeParametersToKmsEnvelopeAeadKeyFormat(parameters).toByteString())
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   @AccessesPartialKey
   private static KmsEnvelopeAeadKeyFormat serializeParametersToKmsEnvelopeAeadKeyFormat(LegacyKmsEnvelopeAeadParameters parameters) throws GeneralSecurityException {
      byte[] serializedDekParameters = TinkProtoParametersFormat.serialize(parameters.getDekParametersForNewKeys());

      try {
         KeyTemplate dekKeyTemplate = KeyTemplate.parseFrom(serializedDekParameters, ExtensionRegistryLite.getEmptyRegistry());
         return KmsEnvelopeAeadKeyFormat.newBuilder().setKekUri(parameters.getKekUri()).setDekTemplate(dekKeyTemplate).build();
      } catch (InvalidProtocolBufferException var3) {
         throw new GeneralSecurityException("Parsing KmsEnvelopeAeadKeyFormat failed: ", var3);
      }
   }

   @AccessesPartialKey
   private static ProtoKeySerialization serializeKey(LegacyKmsEnvelopeAeadKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey",
         KmsEnvelopeAeadKey.newBuilder().setParams(serializeParametersToKmsEnvelopeAeadKeyFormat(key.getParameters())).build().toByteString(),
         KeyData.KeyMaterialType.REMOTE,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   @AccessesPartialKey
   private static LegacyKmsEnvelopeAeadParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to LegacyKmsEnvelopeAeadProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         KmsEnvelopeAeadKeyFormat format;
         try {
            format = KmsEnvelopeAeadKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing KmsEnvelopeAeadKeyFormat failed: ", var3);
         }

         return parseParameters(format, serialization.getKeyTemplate().getOutputPrefixType());
      }
   }

   @AccessesPartialKey
   private static LegacyKmsEnvelopeAeadParameters parseParameters(KmsEnvelopeAeadKeyFormat format, OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      Parameters aeadParameters = TinkProtoParametersFormat.parse(
         KeyTemplate.newBuilder()
            .setTypeUrl(format.getDekTemplate().getTypeUrl())
            .setValue(format.getDekTemplate().getValue())
            .setOutputPrefixType(OutputPrefixType.RAW)
            .build()
            .toByteArray()
      );
      LegacyKmsEnvelopeAeadParameters.DekParsingStrategy strategy;
      if (aeadParameters instanceof AesGcmParameters) {
         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_GCM;
      } else if (aeadParameters instanceof ChaCha20Poly1305Parameters) {
         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_CHACHA20POLY1305;
      } else if (aeadParameters instanceof XChaCha20Poly1305Parameters) {
         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_XCHACHA20POLY1305;
      } else if (aeadParameters instanceof AesCtrHmacAeadParameters) {
         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_CTR_HMAC;
      } else if (aeadParameters instanceof AesEaxParameters) {
         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_EAX;
      } else {
         if (!(aeadParameters instanceof AesGcmSivParameters)) {
            throw new GeneralSecurityException("Unsupported DEK parameters when parsing " + aeadParameters);
         }

         strategy = LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_GCM_SIV;
      }

      return LegacyKmsEnvelopeAeadParameters.builder()
         .setVariant(toVariant(outputPrefixType))
         .setKekUri(format.getKekUri())
         .setDekParametersForNewKeys((AeadParameters)aeadParameters)
         .setDekParsingStrategy(strategy)
         .build();
   }

   @AccessesPartialKey
   private static LegacyKmsEnvelopeAeadKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to LegacyKmsEnvelopeAeadProtoSerialization.parseKey");
      } else {
         try {
            KmsEnvelopeAeadKey protoKey = KmsEnvelopeAeadKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("KmsEnvelopeAeadKeys are only accepted with version 0, got " + protoKey);
            } else {
               LegacyKmsEnvelopeAeadParameters parameters = parseParameters(protoKey.getParams(), serialization.getOutputPrefixType());
               return LegacyKmsEnvelopeAeadKey.create(parameters, serialization.getIdRequirementOrNull());
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing KmsEnvelopeAeadKey failed: ", var4);
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

   private LegacyKmsEnvelopeAeadProtoSerialization() {
   }
}
