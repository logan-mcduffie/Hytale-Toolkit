package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.AesCtrHmacAeadKey;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.AesCtrHmacAeadKeyFormat;
import com.google.crypto.tink.proto.AesCtrKey;
import com.google.crypto.tink.proto.AesCtrKeyFormat;
import com.google.crypto.tink.proto.AesCtrParams;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacKey;
import com.google.crypto.tink.proto.HmacKeyFormat;
import com.google.crypto.tink.proto.HmacParams;
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
public final class AesCtrHmacAeadProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey");
   private static final ParametersSerializer<AesCtrHmacAeadParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      AesCtrHmacAeadProtoSerialization::serializeParameters, AesCtrHmacAeadParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      AesCtrHmacAeadProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<AesCtrHmacAeadKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      AesCtrHmacAeadProtoSerialization::serializeKey, AesCtrHmacAeadKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      AesCtrHmacAeadProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(AesCtrHmacAeadParameters.Variant variant) throws GeneralSecurityException {
      if (AesCtrHmacAeadParameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (AesCtrHmacAeadParameters.Variant.CRUNCHY.equals(variant)) {
         return OutputPrefixType.CRUNCHY;
      } else if (AesCtrHmacAeadParameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static AesCtrHmacAeadParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return AesCtrHmacAeadParameters.Variant.TINK;
         case CRUNCHY:
         case LEGACY:
            return AesCtrHmacAeadParameters.Variant.CRUNCHY;
         case RAW:
            return AesCtrHmacAeadParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static HashType toProtoHashType(AesCtrHmacAeadParameters.HashType hashType) throws GeneralSecurityException {
      if (AesCtrHmacAeadParameters.HashType.SHA1.equals(hashType)) {
         return HashType.SHA1;
      } else if (AesCtrHmacAeadParameters.HashType.SHA224.equals(hashType)) {
         return HashType.SHA224;
      } else if (AesCtrHmacAeadParameters.HashType.SHA256.equals(hashType)) {
         return HashType.SHA256;
      } else if (AesCtrHmacAeadParameters.HashType.SHA384.equals(hashType)) {
         return HashType.SHA384;
      } else if (AesCtrHmacAeadParameters.HashType.SHA512.equals(hashType)) {
         return HashType.SHA512;
      } else {
         throw new GeneralSecurityException("Unable to serialize HashType " + hashType);
      }
   }

   private static AesCtrHmacAeadParameters.HashType toHashType(HashType hashType) throws GeneralSecurityException {
      switch (hashType) {
         case SHA1:
            return AesCtrHmacAeadParameters.HashType.SHA1;
         case SHA224:
            return AesCtrHmacAeadParameters.HashType.SHA224;
         case SHA256:
            return AesCtrHmacAeadParameters.HashType.SHA256;
         case SHA384:
            return AesCtrHmacAeadParameters.HashType.SHA384;
         case SHA512:
            return AesCtrHmacAeadParameters.HashType.SHA512;
         default:
            throw new GeneralSecurityException("Unable to parse HashType: " + hashType.getNumber());
      }
   }

   private static HmacParams getHmacProtoParams(AesCtrHmacAeadParameters parameters) throws GeneralSecurityException {
      return HmacParams.newBuilder().setTagSize(parameters.getTagSizeBytes()).setHash(toProtoHashType(parameters.getHashType())).build();
   }

   private static ProtoParametersSerialization serializeParameters(AesCtrHmacAeadParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey")
            .setValue(
               AesCtrHmacAeadKeyFormat.newBuilder()
                  .setAesCtrKeyFormat(
                     AesCtrKeyFormat.newBuilder()
                        .setParams(AesCtrParams.newBuilder().setIvSize(parameters.getIvSizeBytes()).build())
                        .setKeySize(parameters.getAesKeySizeBytes())
                        .build()
                  )
                  .setHmacKeyFormat(HmacKeyFormat.newBuilder().setParams(getHmacProtoParams(parameters)).setKeySize(parameters.getHmacKeySizeBytes()).build())
                  .build()
                  .toByteString()
            )
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(AesCtrHmacAeadKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey",
         com.google.crypto.tink.proto.AesCtrHmacAeadKey.newBuilder()
            .setAesCtrKey(
               AesCtrKey.newBuilder()
                  .setParams(AesCtrParams.newBuilder().setIvSize(key.getParameters().getIvSizeBytes()).build())
                  .setKeyValue(ByteString.copyFrom(key.getAesKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
                  .build()
            )
            .setHmacKey(
               HmacKey.newBuilder()
                  .setParams(getHmacProtoParams(key.getParameters()))
                  .setKeyValue(ByteString.copyFrom(key.getHmacKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
                  .build()
            )
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static AesCtrHmacAeadParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to AesCtrHmacAeadProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         AesCtrHmacAeadKeyFormat format;
         try {
            format = AesCtrHmacAeadKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing AesCtrHmacAeadParameters failed: ", var3);
         }

         if (format.getHmacKeyFormat().getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 keys are accepted");
         } else {
            return AesCtrHmacAeadParameters.builder()
               .setAesKeySizeBytes(format.getAesCtrKeyFormat().getKeySize())
               .setHmacKeySizeBytes(format.getHmacKeyFormat().getKeySize())
               .setIvSizeBytes(format.getAesCtrKeyFormat().getParams().getIvSize())
               .setTagSizeBytes(format.getHmacKeyFormat().getParams().getTagSize())
               .setHashType(toHashType(format.getHmacKeyFormat().getParams().getHash()))
               .setVariant(toVariant(serialization.getKeyTemplate().getOutputPrefixType()))
               .build();
         }
      }
   }

   private static AesCtrHmacAeadKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to AesCtrHmacAeadProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.AesCtrHmacAeadKey protoKey = com.google.crypto.tink.proto.AesCtrHmacAeadKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else if (protoKey.getAesCtrKey().getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys inner AES CTR keys are accepted");
            } else if (protoKey.getHmacKey().getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys inner HMAC keys are accepted");
            } else {
               AesCtrHmacAeadParameters parameters = AesCtrHmacAeadParameters.builder()
                  .setAesKeySizeBytes(protoKey.getAesCtrKey().getKeyValue().size())
                  .setHmacKeySizeBytes(protoKey.getHmacKey().getKeyValue().size())
                  .setIvSizeBytes(protoKey.getAesCtrKey().getParams().getIvSize())
                  .setTagSizeBytes(protoKey.getHmacKey().getParams().getTagSize())
                  .setHashType(toHashType(protoKey.getHmacKey().getParams().getHash()))
                  .setVariant(toVariant(serialization.getOutputPrefixType()))
                  .build();
               return AesCtrHmacAeadKey.builder()
                  .setParameters(parameters)
                  .setAesKeyBytes(SecretBytes.copyFrom(protoKey.getAesCtrKey().getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setHmacKeyBytes(SecretBytes.copyFrom(protoKey.getHmacKey().getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setIdRequirement(serialization.getIdRequirementOrNull())
                  .build();
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing AesCtrHmacAeadKey failed");
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

   private AesCtrHmacAeadProtoSerialization() {
   }
}
