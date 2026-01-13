package com.google.crypto.tink.jwt.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.jwt.JwtEcdsaParameters;
import com.google.crypto.tink.jwt.JwtEcdsaPrivateKey;
import com.google.crypto.tink.jwt.JwtEcdsaPublicKey;
import com.google.crypto.tink.proto.JwtEcdsaAlgorithm;
import com.google.crypto.tink.proto.JwtEcdsaKeyFormat;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class JwtEcdsaProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey");
   private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.JwtEcdsaPublicKey";
   private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.JwtEcdsaPublicKey");
   private static final ParametersSerializer<JwtEcdsaParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      JwtEcdsaProtoSerialization::serializeParameters, JwtEcdsaParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      JwtEcdsaProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<JwtEcdsaPublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(
      JwtEcdsaProtoSerialization::serializePublicKey, JwtEcdsaPublicKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(
      JwtEcdsaProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final KeySerializer<JwtEcdsaPrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(
      JwtEcdsaProtoSerialization::serializePrivateKey, JwtEcdsaPrivateKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(
      JwtEcdsaProtoSerialization::parsePrivateKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static JwtEcdsaAlgorithm toProtoAlgorithm(JwtEcdsaParameters.Algorithm algorithm) throws GeneralSecurityException {
      if (JwtEcdsaParameters.Algorithm.ES256.equals(algorithm)) {
         return JwtEcdsaAlgorithm.ES256;
      } else if (JwtEcdsaParameters.Algorithm.ES384.equals(algorithm)) {
         return JwtEcdsaAlgorithm.ES384;
      } else if (JwtEcdsaParameters.Algorithm.ES512.equals(algorithm)) {
         return JwtEcdsaAlgorithm.ES512;
      } else {
         throw new GeneralSecurityException("Unable to serialize algorithm: " + algorithm);
      }
   }

   private static JwtEcdsaParameters.Algorithm toAlgorithm(JwtEcdsaAlgorithm algorithm) throws GeneralSecurityException {
      switch (algorithm) {
         case ES256:
            return JwtEcdsaParameters.Algorithm.ES256;
         case ES384:
            return JwtEcdsaParameters.Algorithm.ES384;
         case ES512:
            return JwtEcdsaParameters.Algorithm.ES512;
         default:
            throw new GeneralSecurityException("Unable to parse algorithm: " + algorithm.getNumber());
      }
   }

   private static JwtEcdsaKeyFormat serializeToJwtEcdsaKeyFormat(JwtEcdsaParameters parameters) throws GeneralSecurityException {
      if (!parameters.getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.IGNORED)
         && !parameters.getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
         throw new GeneralSecurityException("Unable to serialize Parameters object with KidStrategy " + parameters.getKidStrategy());
      } else {
         return JwtEcdsaKeyFormat.newBuilder().setVersion(0).setAlgorithm(toProtoAlgorithm(parameters.getAlgorithm())).build();
      }
   }

   private static ProtoParametersSerialization serializeParameters(JwtEcdsaParameters parameters) throws GeneralSecurityException {
      OutputPrefixType outputPrefixType = OutputPrefixType.TINK;
      if (parameters.getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.IGNORED)) {
         outputPrefixType = OutputPrefixType.RAW;
      }

      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey")
            .setValue(serializeToJwtEcdsaKeyFormat(parameters).toByteString())
            .setOutputPrefixType(outputPrefixType)
            .build()
      );
   }

   private static JwtEcdsaParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to JwtEcdsaParameters.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
      } else {
         JwtEcdsaKeyFormat format;
         try {
            format = JwtEcdsaKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing JwtEcdsaKeyFormat failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Parsing HmacParameters failed: unknown Version " + format.getVersion());
         } else {
            JwtEcdsaParameters.KidStrategy kidStrategy = null;
            if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.TINK)) {
               kidStrategy = JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID;
            }

            if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.RAW)) {
               kidStrategy = JwtEcdsaParameters.KidStrategy.IGNORED;
            }

            if (kidStrategy == null) {
               throw new GeneralSecurityException("Invalid OutputPrefixType for JwtHmacKeyFormat");
            } else {
               return JwtEcdsaParameters.builder().setAlgorithm(toAlgorithm(format.getAlgorithm())).setKidStrategy(kidStrategy).build();
            }
         }
      }
   }

   private static int getEncodingLength(JwtEcdsaParameters.Algorithm algorithm) throws GeneralSecurityException {
      if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES256)) {
         return 33;
      } else if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES384)) {
         return 49;
      } else if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES512)) {
         return 67;
      } else {
         throw new GeneralSecurityException("Unknown algorithm: " + algorithm);
      }
   }

   private static OutputPrefixType toProtoOutputPrefixType(JwtEcdsaParameters parameters) {
      return parameters.getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID) ? OutputPrefixType.TINK : OutputPrefixType.RAW;
   }

   private static com.google.crypto.tink.proto.JwtEcdsaPublicKey serializePublicKey(JwtEcdsaPublicKey key) throws GeneralSecurityException {
      int encLength = getEncodingLength(key.getParameters().getAlgorithm());
      ECPoint publicPoint = key.getPublicPoint();
      com.google.crypto.tink.proto.JwtEcdsaPublicKey.Builder builder = com.google.crypto.tink.proto.JwtEcdsaPublicKey.newBuilder()
         .setVersion(0)
         .setAlgorithm(toProtoAlgorithm(key.getParameters().getAlgorithm()))
         .setX(ByteString.copyFrom(BigIntegerEncoding.toBigEndianBytesOfFixedLength(publicPoint.getAffineX(), encLength)))
         .setY(ByteString.copyFrom(BigIntegerEncoding.toBigEndianBytesOfFixedLength(publicPoint.getAffineY(), encLength)));
      if (key.getParameters().getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.CUSTOM)) {
         builder.setCustomKid(com.google.crypto.tink.proto.JwtEcdsaPublicKey.CustomKid.newBuilder().setValue(key.getKid().get()).build());
      }

      return builder.build();
   }

   private static ProtoKeySerialization serializePublicKey(JwtEcdsaPublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.JwtEcdsaPublicKey",
         serializePublicKey(key).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
         toProtoOutputPrefixType(key.getParameters()),
         key.getIdRequirementOrNull()
      );
   }

   private static JwtEcdsaPublicKey parsePublicKeyFromProto(
      com.google.crypto.tink.proto.JwtEcdsaPublicKey protoKey, OutputPrefixType outputPrefixType, @Nullable Integer idRequirement
   ) throws GeneralSecurityException {
      if (protoKey.getVersion() != 0) {
         throw new GeneralSecurityException("Only version 0 keys are accepted");
      } else {
         JwtEcdsaParameters.Builder parametersBuilder = JwtEcdsaParameters.builder();
         JwtEcdsaPublicKey.Builder keyBuilder = JwtEcdsaPublicKey.builder();
         if (outputPrefixType.equals(OutputPrefixType.TINK)) {
            if (protoKey.hasCustomKid()) {
               throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK should not have a custom kid");
            }

            if (idRequirement == null) {
               throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK need an ID Requirement");
            }

            parametersBuilder.setKidStrategy(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID);
            keyBuilder.setIdRequirement(idRequirement);
         } else if (outputPrefixType.equals(OutputPrefixType.RAW)) {
            if (protoKey.hasCustomKid()) {
               parametersBuilder.setKidStrategy(JwtEcdsaParameters.KidStrategy.CUSTOM);
               keyBuilder.setCustomKid(protoKey.getCustomKid().getValue());
            } else {
               parametersBuilder.setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED);
            }
         }

         parametersBuilder.setAlgorithm(toAlgorithm(protoKey.getAlgorithm()));
         keyBuilder.setPublicPoint(
            new ECPoint(
               BigIntegerEncoding.fromUnsignedBigEndianBytes(protoKey.getX().toByteArray()),
               BigIntegerEncoding.fromUnsignedBigEndianBytes(protoKey.getY().toByteArray())
            )
         );
         return keyBuilder.setParameters(parametersBuilder.build()).build();
      }
   }

   private static JwtEcdsaPublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.JwtEcdsaPublicKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to EcdsaProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.JwtEcdsaPublicKey protoKey = com.google.crypto.tink.proto.JwtEcdsaPublicKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            return parsePublicKeyFromProto(protoKey, serialization.getOutputPrefixType(), serialization.getIdRequirementOrNull());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing EcdsaPublicKey failed");
         }
      }
   }

   private static com.google.crypto.tink.proto.JwtEcdsaPrivateKey serializePrivateKeyToProto(JwtEcdsaPrivateKey key, SecretKeyAccess access) throws GeneralSecurityException {
      int encLength = getEncodingLength(key.getParameters().getAlgorithm());
      return com.google.crypto.tink.proto.JwtEcdsaPrivateKey.newBuilder()
         .setPublicKey(serializePublicKey(key.getPublicKey()))
         .setKeyValue(ByteString.copyFrom(BigIntegerEncoding.toBigEndianBytesOfFixedLength(key.getPrivateValue().getBigInteger(access), encLength)))
         .build();
   }

   private static ProtoKeySerialization serializePrivateKey(JwtEcdsaPrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey",
         serializePrivateKeyToProto(key, SecretKeyAccess.requireAccess(access)).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE,
         toProtoOutputPrefixType(key.getParameters()),
         key.getIdRequirementOrNull()
      );
   }

   private static JwtEcdsaPrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.JwtEcdsaPrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to EcdsaProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.JwtEcdsaPrivateKey protoKey = com.google.crypto.tink.proto.JwtEcdsaPrivateKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               JwtEcdsaPublicKey publicKey = parsePublicKeyFromProto(
                  protoKey.getPublicKey(), serialization.getOutputPrefixType(), serialization.getIdRequirementOrNull()
               );
               return JwtEcdsaPrivateKey.create(
                  publicKey,
                  SecretBigInteger.fromBigInteger(
                     BigIntegerEncoding.fromUnsignedBigEndianBytes(protoKey.getKeyValue().toByteArray()), SecretKeyAccess.requireAccess(access)
                  )
               );
            }
         } catch (InvalidProtocolBufferException var4) {
            throw new GeneralSecurityException("Parsing EcdsaPrivateKey failed");
         }
      }
   }

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

   private JwtEcdsaProtoSerialization() {
   }
}
