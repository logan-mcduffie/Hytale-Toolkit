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
import com.google.crypto.tink.proto.Ed25519KeyFormat;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.signature.Ed25519Parameters;
import com.google.crypto.tink.signature.Ed25519PrivateKey;
import com.google.crypto.tink.signature.Ed25519PublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class Ed25519ProtoSerialization {
   private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.Ed25519PrivateKey";
   private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.Ed25519PrivateKey");
   private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.Ed25519PublicKey";
   private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.Ed25519PublicKey");
   private static final ParametersSerializer<Ed25519Parameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      Ed25519ProtoSerialization::serializeParameters, Ed25519Parameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      Ed25519ProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<Ed25519PublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(
      Ed25519ProtoSerialization::serializePublicKey, Ed25519PublicKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(
      Ed25519ProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final KeySerializer<Ed25519PrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(
      Ed25519ProtoSerialization::serializePrivateKey, Ed25519PrivateKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(
      Ed25519ProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class
   );
   private static final EnumTypeProtoConverter<OutputPrefixType, Ed25519Parameters.Variant> VARIANT_CONVERTER = EnumTypeProtoConverter.<OutputPrefixType, Ed25519Parameters.Variant>builder()
      .add(OutputPrefixType.RAW, Ed25519Parameters.Variant.NO_PREFIX)
      .add(OutputPrefixType.TINK, Ed25519Parameters.Variant.TINK)
      .add(OutputPrefixType.CRUNCHY, Ed25519Parameters.Variant.CRUNCHY)
      .add(OutputPrefixType.LEGACY, Ed25519Parameters.Variant.LEGACY)
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

   private static com.google.crypto.tink.proto.Ed25519PublicKey getProtoPublicKey(Ed25519PublicKey key) {
      return com.google.crypto.tink.proto.Ed25519PublicKey.newBuilder().setKeyValue(ByteString.copyFrom(key.getPublicKeyBytes().toByteArray())).build();
   }

   private static ProtoParametersSerialization serializeParameters(Ed25519Parameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.Ed25519PrivateKey")
            .setValue(Ed25519KeyFormat.getDefaultInstance().toByteString())
            .setOutputPrefixType(VARIANT_CONVERTER.toProtoEnum(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializePublicKey(Ed25519PublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.Ed25519PublicKey",
         getProtoPublicKey(key).toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static ProtoKeySerialization serializePrivateKey(Ed25519PrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.Ed25519PrivateKey",
         com.google.crypto.tink.proto.Ed25519PrivateKey.newBuilder()
            .setPublicKey(getProtoPublicKey(key.getPublicKey()))
            .setKeyValue(ByteString.copyFrom(key.getPrivateKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE,
         VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static Ed25519Parameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.Ed25519PrivateKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to Ed25519ProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         try {
            Ed25519KeyFormat format = Ed25519KeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (format.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
         } catch (InvalidProtocolBufferException var2) {
            throw new GeneralSecurityException("Parsing Ed25519Parameters failed: ", var2);
         }

         return Ed25519Parameters.create(VARIANT_CONVERTER.fromProtoEnum(serialization.getKeyTemplate().getOutputPrefixType()));
      }
   }

   private static Ed25519PublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.Ed25519PublicKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to Ed25519ProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.Ed25519PublicKey protoKey = com.google.crypto.tink.proto.Ed25519PublicKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               return Ed25519PublicKey.create(
                  VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType()),
                  Bytes.copyFrom(protoKey.getKeyValue().toByteArray()),
                  serialization.getIdRequirementOrNull()
               );
            }
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing Ed25519PublicKey failed");
         }
      }
   }

   private static Ed25519PrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.Ed25519PrivateKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to Ed25519ProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
      } else {
         try {
            com.google.crypto.tink.proto.Ed25519PrivateKey protoKey = com.google.crypto.tink.proto.Ed25519PrivateKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               com.google.crypto.tink.proto.Ed25519PublicKey protoPublicKey = protoKey.getPublicKey();
               if (protoPublicKey.getVersion() != 0) {
                  throw new GeneralSecurityException("Only version 0 keys are accepted");
               } else {
                  Ed25519PublicKey publicKey = Ed25519PublicKey.create(
                     VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType()),
                     Bytes.copyFrom(protoPublicKey.getKeyValue().toByteArray()),
                     serialization.getIdRequirementOrNull()
                  );
                  return Ed25519PrivateKey.create(publicKey, SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)));
               }
            }
         } catch (InvalidProtocolBufferException var5) {
            throw new GeneralSecurityException("Parsing Ed25519PrivateKey failed");
         }
      }
   }

   private Ed25519ProtoSerialization() {
   }
}
