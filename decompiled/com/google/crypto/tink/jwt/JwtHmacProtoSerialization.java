package com.google.crypto.tink.jwt;

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
import com.google.crypto.tink.proto.JwtHmacAlgorithm;
import com.google.crypto.tink.proto.JwtHmacKeyFormat;
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
final class JwtHmacProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.JwtHmacKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.JwtHmacKey");
   private static final ParametersSerializer<JwtHmacParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      JwtHmacProtoSerialization::serializeParameters, JwtHmacParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      JwtHmacProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<JwtHmacKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      JwtHmacProtoSerialization::serializeKey, JwtHmacKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      JwtHmacProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static JwtHmacAlgorithm toProtoAlgorithm(JwtHmacParameters.Algorithm hashType) throws GeneralSecurityException {
      if (JwtHmacParameters.Algorithm.HS256.equals(hashType)) {
         return JwtHmacAlgorithm.HS256;
      } else if (JwtHmacParameters.Algorithm.HS384.equals(hashType)) {
         return JwtHmacAlgorithm.HS384;
      } else if (JwtHmacParameters.Algorithm.HS512.equals(hashType)) {
         return JwtHmacAlgorithm.HS512;
      } else {
         throw new GeneralSecurityException("Unable to serialize HashType " + hashType);
      }
   }

   private static JwtHmacParameters.Algorithm toAlgorithm(JwtHmacAlgorithm hashType) throws GeneralSecurityException {
      switch (hashType) {
         case HS256:
            return JwtHmacParameters.Algorithm.HS256;
         case HS384:
            return JwtHmacParameters.Algorithm.HS384;
         case HS512:
            return JwtHmacParameters.Algorithm.HS512;
         default:
            throw new GeneralSecurityException("Unable to parse HashType: " + hashType.getNumber());
      }
   }

   private static JwtHmacKeyFormat serializeToJwtHmacKeyFormat(JwtHmacParameters parameters) throws GeneralSecurityException {
      if (parameters.getKidStrategy().equals(JwtHmacParameters.KidStrategy.CUSTOM)) {
         throw new GeneralSecurityException("Unable to serialize Parameters object with KidStrategy CUSTOM");
      } else {
         return JwtHmacKeyFormat.newBuilder()
            .setVersion(0)
            .setAlgorithm(toProtoAlgorithm(parameters.getAlgorithm()))
            .setKeySize(parameters.getKeySizeBytes())
            .build();
      }
   }

   private static ProtoParametersSerialization serializeParameters(JwtHmacParameters parameters) throws GeneralSecurityException {
      OutputPrefixType outputPrefixType = OutputPrefixType.TINK;
      if (parameters.getKidStrategy().equals(JwtHmacParameters.KidStrategy.IGNORED)) {
         outputPrefixType = OutputPrefixType.RAW;
      }

      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.JwtHmacKey")
            .setValue(serializeToJwtHmacKeyFormat(parameters).toByteString())
            .setOutputPrefixType(outputPrefixType)
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(JwtHmacKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      com.google.crypto.tink.proto.JwtHmacKey.Builder protoKeyBuilder = com.google.crypto.tink.proto.JwtHmacKey.newBuilder();
      protoKeyBuilder.setVersion(0)
         .setAlgorithm(toProtoAlgorithm(key.getParameters().getAlgorithm()))
         .setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))));
      OutputPrefixType outputPrefixType = null;
      if (key.getParameters().getKidStrategy().equals(JwtHmacParameters.KidStrategy.CUSTOM)) {
         protoKeyBuilder.setCustomKid(com.google.crypto.tink.proto.JwtHmacKey.CustomKid.newBuilder().setValue(key.getKid().get()));
         outputPrefixType = OutputPrefixType.RAW;
      }

      if (key.getParameters().getKidStrategy().equals(JwtHmacParameters.KidStrategy.IGNORED)) {
         outputPrefixType = OutputPrefixType.RAW;
      }

      if (key.getParameters().getKidStrategy().equals(JwtHmacParameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
         outputPrefixType = OutputPrefixType.TINK;
      }

      if (outputPrefixType == null) {
         throw new GeneralSecurityException("Unknown KID Strategy in " + key.getParameters().getKidStrategy());
      } else {
         return ProtoKeySerialization.create(
            "type.googleapis.com/google.crypto.tink.JwtHmacKey",
            protoKeyBuilder.build().toByteString(),
            KeyData.KeyMaterialType.SYMMETRIC,
            outputPrefixType,
            key.getIdRequirementOrNull()
         );
      }
   }

   private static JwtHmacParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.JwtHmacKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to JwtHmacProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         JwtHmacKeyFormat format;
         try {
            format = JwtHmacKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing HmacParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Parsing HmacParameters failed: unknown Version " + format.getVersion());
         } else {
            JwtHmacParameters.KidStrategy kidStrategy = null;
            if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.TINK)) {
               kidStrategy = JwtHmacParameters.KidStrategy.BASE64_ENCODED_KEY_ID;
            }

            if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.RAW)) {
               kidStrategy = JwtHmacParameters.KidStrategy.IGNORED;
            }

            if (kidStrategy == null) {
               throw new GeneralSecurityException("Invalid OutputPrefixType for JwtHmacKeyFormat");
            } else {
               return JwtHmacParameters.builder()
                  .setAlgorithm(toAlgorithm(format.getAlgorithm()))
                  .setKeySizeBytes(format.getKeySize())
                  .setKidStrategy(kidStrategy)
                  .build();
            }
         }
      }
   }

   private static JwtHmacKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.JwtHmacKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to HmacProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.JwtHmacKey protoKey = com.google.crypto.tink.proto.JwtHmacKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               JwtHmacParameters.Builder parametersBuilder = JwtHmacParameters.builder();
               JwtHmacKey.Builder keyBuilder = JwtHmacKey.builder();
               if (serialization.getOutputPrefixType().equals(OutputPrefixType.TINK)) {
                  if (protoKey.hasCustomKid()) {
                     throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK should not have a custom kid");
                  }

                  Integer idRequirement = serialization.getIdRequirementOrNull();
                  if (idRequirement == null) {
                     throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK need an ID Requirement");
                  }

                  parametersBuilder.setKidStrategy(JwtHmacParameters.KidStrategy.BASE64_ENCODED_KEY_ID);
                  keyBuilder.setIdRequirement(idRequirement);
               } else if (serialization.getOutputPrefixType().equals(OutputPrefixType.RAW)) {
                  if (protoKey.hasCustomKid()) {
                     parametersBuilder.setKidStrategy(JwtHmacParameters.KidStrategy.CUSTOM);
                     keyBuilder.setCustomKid(protoKey.getCustomKid().getValue());
                  } else {
                     parametersBuilder.setKidStrategy(JwtHmacParameters.KidStrategy.IGNORED);
                  }
               }

               parametersBuilder.setAlgorithm(toAlgorithm(protoKey.getAlgorithm()));
               parametersBuilder.setKeySizeBytes(protoKey.getKeyValue().size());
               return keyBuilder.setKeyBytes(SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)))
                  .setParameters(parametersBuilder.build())
                  .build();
            }
         } catch (IllegalArgumentException | InvalidProtocolBufferException var6) {
            throw new GeneralSecurityException("Parsing HmacKey failed");
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

   private JwtHmacProtoSerialization() {
   }
}
