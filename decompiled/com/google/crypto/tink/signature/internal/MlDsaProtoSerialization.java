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
import com.google.crypto.tink.proto.MlDsaInstance;
import com.google.crypto.tink.proto.MlDsaKeyFormat;
import com.google.crypto.tink.proto.MlDsaParams;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.signature.MlDsaParameters;
import com.google.crypto.tink.signature.MlDsaPrivateKey;
import com.google.crypto.tink.signature.MlDsaPublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class MlDsaProtoSerialization {
   private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.MlDsaPrivateKey";
   private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.MlDsaPrivateKey");
   private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.MlDsaPublicKey";
   private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.MlDsaPublicKey");
   private static final ParametersSerializer<MlDsaParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      MlDsaProtoSerialization::serializeParameters, MlDsaParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      MlDsaProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<MlDsaPublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(
      MlDsaProtoSerialization::serializePublicKey, MlDsaPublicKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(
      MlDsaProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final KeySerializer<MlDsaPrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(
      MlDsaProtoSerialization::serializePrivateKey, MlDsaPrivateKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(
      MlDsaProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final EnumTypeProtoConverter<OutputPrefixType, MlDsaParameters.Variant> VARIANT_CONVERTER = EnumTypeProtoConverter.<OutputPrefixType, MlDsaParameters.Variant>builder()
      .add(OutputPrefixType.RAW, MlDsaParameters.Variant.NO_PREFIX)
      .add(OutputPrefixType.TINK, MlDsaParameters.Variant.TINK)
      .build();
   private static final EnumTypeProtoConverter<MlDsaInstance, MlDsaParameters.MlDsaInstance> INSTANCE_CONVERTER = EnumTypeProtoConverter.<MlDsaInstance, MlDsaParameters.MlDsaInstance>builder()
      .add(MlDsaInstance.ML_DSA_65, MlDsaParameters.MlDsaInstance.ML_DSA_65)
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

   private static MlDsaParams getProtoParams(MlDsaParameters parameters) throws GeneralSecurityException {
      return MlDsaParams.newBuilder().setMlDsaInstance(INSTANCE_CONVERTER.toProtoEnum(parameters.getMlDsaInstance())).build();
   }

   private static com.google.crypto.tink.proto.MlDsaPublicKey getProtoPublicKey(MlDsaPublicKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.proto.MlDsaPublicKey.newBuilder()
         .setVersion(0)
         .setParams(getProtoParams(key.getParameters()))
         .setKeyValue(ByteString.copyFrom(key.getSerializedPublicKey().toByteArray()))
         .build();
   }

   private static ProtoParametersSerialization serializeParameters(MlDsaParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.MlDsaPrivateKey")
            .setValue(MlDsaKeyFormat.newBuilder().setParams(getProtoParams(parameters)).setVersion(0).build().toByteString())
            .setOutputPrefixType(VARIANT_CONVERTER.toProtoEnum(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializePublicKey(MlDsaPublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.MlDsaPublicKey",
         getProtoPublicKey(key).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static ProtoKeySerialization serializePrivateKey(MlDsaPrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.MlDsaPrivateKey",
         com.google.crypto.tink.proto.MlDsaPrivateKey.newBuilder()
            .setVersion(0)
            .setPublicKey(getProtoPublicKey(key.getPublicKey()))
            .setKeyValue(ByteString.copyFrom(key.getPrivateSeed().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static MlDsaParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.MlDsaPrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to MlDsaProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
      } else {
         MlDsaKeyFormat format;
         try {
            format = MlDsaKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing MlDsaParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 keys are accepted for ML-DSA.");
         } else {
            return MlDsaParameters.create(
               INSTANCE_CONVERTER.fromProtoEnum(format.getParams().getMlDsaInstance()),
               VARIANT_CONVERTER.fromProtoEnum(serialization.getKeyTemplate().getOutputPrefixType())
            );
         }
      }
   }

   private static MlDsaPublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.MlDsaPublicKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to MlDsaProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else if (serialization.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC) {
         throw new GeneralSecurityException("Wrong KeyMaterialType for MlDsaPublicKey: " + serialization.getKeyMaterialType());
      } else {
         try {
            com.google.crypto.tink.proto.MlDsaPublicKey protoKey = com.google.crypto.tink.proto.MlDsaPublicKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               MlDsaParameters parameters = MlDsaParameters.create(
                  INSTANCE_CONVERTER.fromProtoEnum(protoKey.getParams().getMlDsaInstance()),
                  VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType())
               );
               MlDsaPublicKey.Builder builder = MlDsaPublicKey.builder()
                  .setParameters(parameters)
                  .setSerializedPublicKey(Bytes.copyFrom(protoKey.getKeyValue().toByteArray()));
               if (serialization.getIdRequirementOrNull() != null) {
                  builder.setIdRequirement(serialization.getIdRequirementOrNull());
               }

               return builder.build();
            }
         } catch (InvalidProtocolBufferException var5) {
            throw new GeneralSecurityException("Parsing MlDsaPublicKey failed");
         }
      }
   }

   private static MlDsaPrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.MlDsaPrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to MlDsaProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
      } else if (serialization.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) {
         throw new GeneralSecurityException("Wrong KeyMaterialType for MlDsaPrivateKey: " + serialization.getKeyMaterialType());
      } else {
         try {
            com.google.crypto.tink.proto.MlDsaPrivateKey protoKey = com.google.crypto.tink.proto.MlDsaPrivateKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               com.google.crypto.tink.proto.MlDsaPublicKey protoPublicKey = protoKey.getPublicKey();
               if (protoPublicKey.getVersion() != 0) {
                  throw new GeneralSecurityException("Only version 0 keys are accepted");
               } else {
                  MlDsaParameters parameters = MlDsaParameters.create(
                     INSTANCE_CONVERTER.fromProtoEnum(protoPublicKey.getParams().getMlDsaInstance()),
                     VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType())
                  );
                  MlDsaPublicKey.Builder builder = MlDsaPublicKey.builder()
                     .setParameters(parameters)
                     .setSerializedPublicKey(Bytes.copyFrom(protoPublicKey.getKeyValue().toByteArray()));
                  if (serialization.getIdRequirementOrNull() != null) {
                     builder.setIdRequirement(serialization.getIdRequirementOrNull());
                  }

                  MlDsaPublicKey publicKey = builder.build();
                  return MlDsaPrivateKey.createWithoutVerification(
                     publicKey, SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access))
                  );
               }
            }
         } catch (InvalidProtocolBufferException var7) {
            throw new GeneralSecurityException("Parsing MlDsaPrivateKey failed");
         }
      }
   }

   private MlDsaProtoSerialization() {
   }
}
