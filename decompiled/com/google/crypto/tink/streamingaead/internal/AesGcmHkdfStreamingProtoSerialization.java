package com.google.crypto.tink.streamingaead.internal;

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
import com.google.crypto.tink.proto.AesGcmHkdfStreamingKeyFormat;
import com.google.crypto.tink.proto.AesGcmHkdfStreamingParams;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKey;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingParameters;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class AesGcmHkdfStreamingProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey");
   private static final ParametersSerializer<AesGcmHkdfStreamingParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesGcmHkdfStreamingProtoSerialization::serializeParameters, AesGcmHkdfStreamingParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesGcmHkdfStreamingProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesGcmHkdfStreamingKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesGcmHkdfStreamingProtoSerialization::serializeKey, AesGcmHkdfStreamingKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesGcmHkdfStreamingProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static HashType toProtoHashType(AesGcmHkdfStreamingParameters.HashType hashType) throws GeneralSecurityException {
      if (AesGcmHkdfStreamingParameters.HashType.SHA1.equals(hashType)) {
         return HashType.SHA1;
      } else if (AesGcmHkdfStreamingParameters.HashType.SHA256.equals(hashType)) {
         return HashType.SHA256;
      } else if (AesGcmHkdfStreamingParameters.HashType.SHA512.equals(hashType)) {
         return HashType.SHA512;
      } else {
         throw new GeneralSecurityException("Unable to serialize HashType " + hashType);
      }
   }

   private static AesGcmHkdfStreamingParameters.HashType toHashType(HashType hashType) throws GeneralSecurityException {
      switch (hashType) {
         case SHA1:
            return AesGcmHkdfStreamingParameters.HashType.SHA1;
         case SHA256:
            return AesGcmHkdfStreamingParameters.HashType.SHA256;
         case SHA512:
            return AesGcmHkdfStreamingParameters.HashType.SHA512;
         default:
            throw new GeneralSecurityException("Unable to parse HashType: " + hashType.getNumber());
      }
   }

   private static AesGcmHkdfStreamingParams toProtoParams(AesGcmHkdfStreamingParameters parameters) throws GeneralSecurityException {
      return AesGcmHkdfStreamingParams.newBuilder()
         .setCiphertextSegmentSize(parameters.getCiphertextSegmentSizeBytes())
         .setDerivedKeySize(parameters.getDerivedAesGcmKeySizeBytes())
         .setHkdfHashType(toProtoHashType(parameters.getHkdfHashType()))
         .build();
   }

   private static ProtoParametersSerialization serializeParameters(AesGcmHkdfStreamingParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey")
            .setValue(
               AesGcmHkdfStreamingKeyFormat.newBuilder().setKeySize(parameters.getKeySizeBytes()).setParams(toProtoParams(parameters)).build().toByteString()
            )
            .setOutputPrefixType(OutputPrefixType.RAW)
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesGcmHkdfStreamingKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey",
         com.google.crypto.tink.proto.AesGcmHkdfStreamingKey.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getInitialKeyMaterial().toByteArray(SecretKeyAccess.requireAccess(access))))
            .setParams(toProtoParams(key.getParameters()))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         OutputPrefixType.RAW,
         key.getIdRequirementOrNull()
      );
   }

   private static AesGcmHkdfStreamingParameters toParametersObject(AesGcmHkdfStreamingParams params, int keySize) throws GeneralSecurityException {
      return AesGcmHkdfStreamingParameters.builder()
         .setKeySizeBytes(keySize)
         .setDerivedAesGcmKeySizeBytes(params.getDerivedKeySize())
         .setCiphertextSegmentSizeBytes(params.getCiphertextSegmentSize())
         .setHkdfHashType(toHashType(params.getHkdfHashType()))
         .build();
   }

   private static AesGcmHkdfStreamingParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to AesGcmHkdfStreamingParameters.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         AesGcmHkdfStreamingKeyFormat format;
         try {
            format = AesGcmHkdfStreamingKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesGcmHkdfStreamingParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 parameters are accepted");
         } else {
            return toParametersObject(format.getParams(), format.getKeySize());
         }
      }
   }

   private static AesGcmHkdfStreamingKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesGcmHkdfStreamingParameters.parseParameters");
      } else {
         try {
            com.google.crypto.tink.proto.AesGcmHkdfStreamingKey protoKey = com.google.crypto.tink.proto.AesGcmHkdfStreamingKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               AesGcmHkdfStreamingParameters parameters = toParametersObject(protoKey.getParams(), protoKey.getKeyValue().size());
               return AesGcmHkdfStreamingKey.create(
                  parameters, SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access))
               );
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesGcmHkdfStreamingKey failed");
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

   private AesGcmHkdfStreamingProtoSerialization() {
   }
}
