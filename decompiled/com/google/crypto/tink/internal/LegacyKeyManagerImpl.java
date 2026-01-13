package com.google.crypto.tink.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKey;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import java.security.GeneralSecurityException;

public class LegacyKeyManagerImpl<P> implements KeyManager<P> {
   final String typeUrl;
   final Class<P> primitiveClass;
   final KeyData.KeyMaterialType keyMaterialType;
   final Parser<? extends MessageLite> protobufKeyParser;

   public static <P> KeyManager<P> create(
      String typeUrl, Class<P> primitiveClass, KeyData.KeyMaterialType keyMaterialType, Parser<? extends MessageLite> protobufKeyParser
   ) {
      return new LegacyKeyManagerImpl<>(typeUrl, primitiveClass, keyMaterialType, protobufKeyParser);
   }

   LegacyKeyManagerImpl(String typeUrl, Class<P> primitiveClass, KeyData.KeyMaterialType keyMaterialType, Parser<? extends MessageLite> protobufKeyParser) {
      this.protobufKeyParser = protobufKeyParser;
      this.typeUrl = typeUrl;
      this.primitiveClass = primitiveClass;
      this.keyMaterialType = keyMaterialType;
   }

   @Override
   public P getPrimitive(ByteString serializedKey) throws GeneralSecurityException {
      ProtoKeySerialization serialization = ProtoKeySerialization.create(this.typeUrl, serializedKey, this.keyMaterialType, OutputPrefixType.RAW, null);
      Key key = MutableSerializationRegistry.globalInstance().parseKey(serialization, InsecureSecretKeyAccess.get());
      return MutablePrimitiveRegistry.globalInstance().getPrimitive(key, this.primitiveClass);
   }

   @Override
   public final P getPrimitive(MessageLite key) throws GeneralSecurityException {
      return this.getPrimitive(key.toByteString());
   }

   @Override
   public final MessageLite newKey(ByteString serializedKeyFormat) throws GeneralSecurityException {
      KeyData keyData = this.newKeyData(serializedKeyFormat);

      try {
         return this.protobufKeyParser.parseFrom(keyData.getValue(), ExtensionRegistryLite.getEmptyRegistry());
      } catch (InvalidProtocolBufferException var4) {
         throw new GeneralSecurityException("Unexpectedly failed to parse key");
      }
   }

   @Override
   public final MessageLite newKey(MessageLite keyFormat) throws GeneralSecurityException {
      return this.newKey(keyFormat.toByteString());
   }

   @Override
   public final boolean doesSupport(String typeUrl) {
      return typeUrl.equals(this.getKeyType());
   }

   @Override
   public final String getKeyType() {
      return this.typeUrl;
   }

   @Override
   public int getVersion() {
      return 0;
   }

   @Override
   public final KeyData newKeyData(ByteString serializedKeyFormat) throws GeneralSecurityException {
      ProtoParametersSerialization parametersSerialization = ProtoParametersSerialization.checkedCreate(
         KeyTemplate.newBuilder().setTypeUrl(this.typeUrl).setValue(serializedKeyFormat).setOutputPrefixType(OutputPrefixType.RAW).build()
      );
      Parameters parameters = MutableSerializationRegistry.globalInstance().parseParameters(parametersSerialization);
      Key key = MutableKeyCreationRegistry.globalInstance().createKey(parameters, null);
      ProtoKeySerialization keySerialization = MutableSerializationRegistry.globalInstance()
         .serializeKey(key, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
      return KeyData.newBuilder()
         .setTypeUrl(keySerialization.getTypeUrl())
         .setValue(keySerialization.getValue())
         .setKeyMaterialType(keySerialization.getKeyMaterialType())
         .build();
   }

   @Override
   public final Class<P> getPrimitiveClass() {
      return this.primitiveClass;
   }

   public static <P> PrivateKeyManager<P> createPrivateKeyManager(String typeUrl, Class<P> primitiveClass, Parser<? extends MessageLite> protobufKeyParser) {
      return new LegacyKeyManagerImpl.LegacyPrivateKeyManagerImpl<>(typeUrl, primitiveClass, protobufKeyParser);
   }

   private static class LegacyPrivateKeyManagerImpl<P> extends LegacyKeyManagerImpl<P> implements PrivateKeyManager<P> {
      protected LegacyPrivateKeyManagerImpl(String typeUrl, Class<P> primitiveClass, Parser<? extends MessageLite> protobufKeyParser) {
         super(typeUrl, primitiveClass, KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE, protobufKeyParser);
      }

      @Override
      public KeyData getPublicKeyData(ByteString serializedKey) throws GeneralSecurityException {
         ProtoKeySerialization serialization = ProtoKeySerialization.create(this.typeUrl, serializedKey, this.keyMaterialType, OutputPrefixType.RAW, null);
         Key key = MutableSerializationRegistry.globalInstance().parseKey(serialization, InsecureSecretKeyAccess.get());
         if (!(key instanceof PrivateKey)) {
            throw new GeneralSecurityException("Key not private key");
         } else {
            Key publicKey = ((PrivateKey)key).getPublicKey();
            ProtoKeySerialization publicKeySerialization = MutableSerializationRegistry.globalInstance()
               .serializeKey(publicKey, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
            return KeyData.newBuilder()
               .setTypeUrl(publicKeySerialization.getTypeUrl())
               .setValue(publicKeySerialization.getValue())
               .setKeyMaterialType(publicKeySerialization.getKeyMaterialType())
               .build();
         }
      }
   }
}
