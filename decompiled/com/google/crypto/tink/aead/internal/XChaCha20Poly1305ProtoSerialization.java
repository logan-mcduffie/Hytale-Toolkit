package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.XChaCha20Poly1305Key;
import com.google.crypto.tink.aead.XChaCha20Poly1305Parameters;
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
import com.google.crypto.tink.proto.XChaCha20Poly1305KeyFormat;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class XChaCha20Poly1305ProtoSerialization {
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key";
   private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key");
   private static final ParametersSerializer<XChaCha20Poly1305Parameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(
      XChaCha20Poly1305ProtoSerialization::serializeParameters, XChaCha20Poly1305Parameters.class, ProtoParametersSerialization.class
   );
   private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(
      XChaCha20Poly1305ProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class
   );
   private static final KeySerializer<XChaCha20Poly1305Key, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(
      XChaCha20Poly1305ProtoSerialization::serializeKey, XChaCha20Poly1305Key.class, ProtoKeySerialization.class
   );
   private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(
      XChaCha20Poly1305ProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class
   );

   private static OutputPrefixType toProtoOutputPrefixType(XChaCha20Poly1305Parameters.Variant variant) throws GeneralSecurityException {
      if (XChaCha20Poly1305Parameters.Variant.TINK.equals(variant)) {
         return OutputPrefixType.TINK;
      } else if (XChaCha20Poly1305Parameters.Variant.CRUNCHY.equals(variant)) {
         return OutputPrefixType.CRUNCHY;
      } else if (XChaCha20Poly1305Parameters.Variant.NO_PREFIX.equals(variant)) {
         return OutputPrefixType.RAW;
      } else {
         throw new GeneralSecurityException("Unable to serialize variant: " + variant);
      }
   }

   private static XChaCha20Poly1305Parameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
      switch (outputPrefixType) {
         case TINK:
            return XChaCha20Poly1305Parameters.Variant.TINK;
         case CRUNCHY:
         case LEGACY:
            return XChaCha20Poly1305Parameters.Variant.CRUNCHY;
         case RAW:
            return XChaCha20Poly1305Parameters.Variant.NO_PREFIX;
         default:
            throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
      }
   }

   private static ProtoParametersSerialization serializeParameters(XChaCha20Poly1305Parameters parameters) throws GeneralSecurityException {
      return ProtoParametersSerialization.create(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key")
            .setValue(XChaCha20Poly1305KeyFormat.getDefaultInstance().toByteString())
            .setOutputPrefixType(toProtoOutputPrefixType(parameters.getVariant()))
            .build()
      );
   }

   private static ProtoKeySerialization serializeKey(XChaCha20Poly1305Key key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      return ProtoKeySerialization.create(
         "type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key",
         com.google.crypto.tink.proto.XChaCha20Poly1305Key.newBuilder()
            .setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access))))
            .build()
            .toByteString(),
         KeyData.KeyMaterialType.SYMMETRIC,
         toProtoOutputPrefixType(key.getParameters().getVariant()),
         key.getIdRequirementOrNull()
      );
   }

   private static XChaCha20Poly1305Parameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
      if (!serialization.getKeyTemplate().getTypeUrl().equals("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key")) {
         throw new IllegalArgumentException(
            "Wrong type URL in call to XChaCha20Poly1305ProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl()
         );
      } else {
         XChaCha20Poly1305KeyFormat format;
         try {
            format = XChaCha20Poly1305KeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing XChaCha20Poly1305Parameters failed: ", var3);
         }

         if (format.getVersion() != 0) {
            throw new GeneralSecurityException("Only version 0 parameters are accepted");
         } else {
            return XChaCha20Poly1305Parameters.create(toVariant(serialization.getKeyTemplate().getOutputPrefixType()));
         }
      }
   }

   private static XChaCha20Poly1305Key parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (!serialization.getTypeUrl().equals("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key")) {
         throw new IllegalArgumentException("Wrong type URL in call to XChaCha20Poly1305ProtoSerialization.parseKey");
      } else {
         try {
            com.google.crypto.tink.proto.XChaCha20Poly1305Key protoKey = com.google.crypto.tink.proto.XChaCha20Poly1305Key.parseFrom(
               serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry()
            );
            if (protoKey.getVersion() != 0) {
               throw new GeneralSecurityException("Only version 0 keys are accepted");
            } else {
               return XChaCha20Poly1305Key.create(
                  toVariant(serialization.getOutputPrefixType()),
                  SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access)),
                  serialization.getIdRequirementOrNull()
               );
            }
         } catch (InvalidProtocolBufferException var3) {
            throw new GeneralSecurityException("Parsing XChaCha20Poly1305Key failed");
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

   private XChaCha20Poly1305ProtoSerialization() {
   }
}
