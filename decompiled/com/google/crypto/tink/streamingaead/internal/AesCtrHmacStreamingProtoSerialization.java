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
import com.google.crypto.tink.proto.AesCtrHmacStreamingKeyFormat;
import com.google.crypto.tink.proto.AesCtrHmacStreamingParams;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingKey;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingParameters;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class AesCtrHmacStreamingProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey");
   private static final ParametersSerializer<AesCtrHmacStreamingParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesCtrHmacStreamingProtoSerialization::serializeParameters, AesCtrHmacStreamingParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesCtrHmacStreamingProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesCtrHmacStreamingKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesCtrHmacStreamingProtoSerialization::serializeKey, AesCtrHmacStreamingKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesCtrHmacStreamingProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static HashType toProtoHashType(AesCtrHmacStreamingParameters.HashType hashType) throws GeneralSecurityException {
      if (AesCtrHmacStreamingParameters.HashType.SHA1.equals(hashType)) {
         return HashType.SHA1;
      } else if (AesCtrHmacStreamingParameters.HashType.SHA256.equals(hashType)) {
         return HashType.SHA256;
      } else if (AesCtrHmacStreamingParameters.HashType.SHA512.equals(hashType)) {
         return HashType.SHA512;
      } else {
         throw new GeneralSecurityException("Unable to serialize HashType " + hashType);
      }
   }

   private static AesCtrHmacStreamingParameters.HashType toHashType(HashType hashType) throws GeneralSecurityException {
      switch (hashType) {
         case SHA1:
            return AesCtrHmacStreamingParameters.HashType.SHA1;
         case SHA256:
            return AesCtrHmacStreamingParameters.HashType.SHA256;
         case SHA512:
            return AesCtrHmacStreamingParameters.HashType.SHA512;
         default:
            throw new GeneralSecurityException("Unable to parse HashType: " + hashType.getNumber());
      }
   }

   private static AesCtrHmacStreamingParams toProtoParams(AesCtrHmacStreamingParameters parameters) throws GeneralSecurityException {
      return AesCtrHmacStreamingParams.newBuilder()
         .setCiphertextSegmentSize(parameters.getCiphertextSegmentSizeBytes())
         .setDerivedKeySize(parameters.getDerivedKeySizeBytes())
         .setHkdfHashType(toProtoHashType(parameters.getHkdfHashType()))
         .setHmacParams(HmacParams.newBuilder().setHash(toProtoHashType(parameters.getHmacHashType())).setTagSize(parameters.getHmacTagSizeBytes()))
         .build();
   }

   private static ProtoParametersSerialization serializeParameters(AesCtrHmacStreamingParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey")
            .setValue(
               AesCtrHmacStreamingKeyFormat.newBuilder().setKeySize(parameters.getKeySizeBytes()).setParams(toProtoParams(parameters)).build().toByteString()
            )
            .setOutputPrefixType(OutputPrefixType.RAW)
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesCtrHmacStreamingKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey",
         com.google.crypto.tink.proto.AesCtrHmacStreamingKey.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getInitialKeyMaterial().toByteArray(SecretKeyAccess.requireAccess(access))))
            .setParams(toProtoParams(key.getParameters()))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         OutputPrefixType.RAW,
         key.getIdRequirementOrNull()
      );
   }

   private static AesCtrHmacStreamingParameters toParametersObject(AesCtrHmacStreamingParams params, int keySize) throws GeneralSecurityException {
      return AesCtrHmacStreamingParameters.builder()
         .setKeySizeBytes(keySize)
         .setDerivedKeySizeBytes(params.getDerivedKeySize())
         .setCiphertextSegmentSizeBytes(params.getCiphertextSegmentSize())
         .setHkdfHashType(toHashType(params.getHkdfHashType()))
         .setHmacHashType(toHashType(params.getHmacParams().getHash()))
         .setHmacTagSizeBytes(params.getHmacParams().getTagSize())
         .build();
   }

   private static AesCtrHmacStreamingParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to AesCtrHmacStreamingParameters.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         AesCtrHmacStreamingKeyFormat format;
         try {
            format = AesCtrHmacStreamingKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesCtrHmacStreamingParameters failed: ", var3);
         }

         return toParametersObject(format.getParams(), format.getKeySize());
      }
   }

   private static AesCtrHmacStreamingKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesCtrHmacStreamingParameters.parseParameters");
      } else {
         try {
            com.google.crypto.tink.proto.AesCtrHmacStreamingKey protoKey = com.google.crypto.tink.proto.AesCtrHmacStreamingKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               AesCtrHmacStreamingParameters parameters = toParametersObject(protoKey.getParams(), protoKey.getKeyValue().size());
               return AesCtrHmacStreamingKey.create(
                  parameters, SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access))
               );
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesCtrHmacStreamingKey failed");
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

   private AesCtrHmacStreamingProtoSerialization() {
   }
}
