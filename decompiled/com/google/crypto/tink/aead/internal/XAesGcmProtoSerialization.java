package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.XAesGcmKey;
import com.google.crypto.tink.aead.XAesGcmParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.SerializationRegistry;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.XAesGcmKeyFormat;
import com.google.crypto.tink.proto.XAesGcmParams;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class XAesGcmProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.XAesGcmKey";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.XAesGcmKey");
   private static final int KEY_SIZE_BYTES = 32;
   private static final ParametersSerializer<XAesGcmParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      XAesGcmProtoSerialization::serializeParameters, XAesGcmParameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      XAesGcmProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<XAesGcmKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      XAesGcmProtoSerialization::serializeKey, XAesGcmKey.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      XAesGcmProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(XAesGcmParameters.Variant variant) throws GeneralSecurityException {
      if (Objects.equals(variant, XAesGcmParameters.Variant.TINK)) {
         return OutputPrefixType.TINK;
      } else if (Objects.equals(variant, XAesGcmParameters.Variant.NO_PREFIX)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static XAesGcmParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return XAesGcmParameters.Variant.TINK;
         case RAW:
            return XAesGcmParameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static ProtoParametersSerialization serializeParameters(XAesGcmParameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.XAesGcmKey")
            .setValue(
               XAesGcmKeyFormat.newBuilder().setParams(XAesGcmParams.newBuilder().setSaltSize(parameters.getSaltSizeBytes()).build()).build().toByteString()
            )
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(XAesGcmKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.XAesGcmKey",
         com.google.crypto.tink.proto.XAesGcmKey.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .setParams(XAesGcmParams.newBuilder().setSaltSize(key.getParameters().getSaltSizeBytes()).build())
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static XAesGcmParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.XAesGcmKey")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to XAesGcmProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         XAesGcmKeyFormat format;
         try {
            format = XAesGcmKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing XAesGcmParameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 parameters are accepted");
         } else {
            return XAesGcmParameters.create(toVariant(serialization.getKeyTemplate().getOutputPrefixType()), format.getParams().getSaltSize());
         }
      }
   }

   private static XAesGcmKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.XAesGcmKey")) {
         throw new IllegalArgumentException("Wrong type URL in call to XAesGcmProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.XAesGcmKey protoKey = com.google.crypto.tink.proto.XAesGcmKey.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else if (protoKey.getKeyValue().size() != 32) {
               throw new GeneralSecurityException("Only 32 byte key size is accepted");
            } else {
               return XAesGcmKey.create(
                  XAesGcmParameters.create(toVariant(serialization.getOutputPrefixType()), protoKey.getParams().getSaltSize()),
                  SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)),
                  serialization.getIdRequirementOrNull()
               );
            }
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing XAesGcmKey failed");
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

   public static void register(SerializationRegistry.Builder registryBuilder) throws GeneralSecurityException {
      registryBuilder.registerParametersSerializer(PARAMETERS_SERIALIZER);
      registryBuilder.registerParametersParser(PARAMETERS_PARSER);
      registryBuilder.registerKeySerializer(KEY_SERIALIZER);
      registryBuilder.registerKeyParser(KEY_PARSER);
   }

   private XAesGcmProtoSerialization() {
   }
}
