package com.google.crypto.tink.keyderivation.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.keyderivation.PrfBasedKeyDerivationKey;
import com.google.crypto.tink.keyderivation.PrfBasedKeyDerivationParameters;
import com.google.crypto.tink.prf.PrfKey;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.PrfBasedDeriverKey;
import com.google.crypto.tink.proto.PrfBasedDeriverKeyFormat;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public final class PrfBasedDeriverKeyManager implements KeyManager<Void> {
   private static final PrimitiveConstructor<PrfBasedKeyDerivationKey, KeyDeriver> PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      PrfBasedKeyDeriver::create, PrfBasedKeyDerivationKey.class, KeyDeriver.class
   );
   private static final KeyCreator<PrfBasedKeyDerivationParameters> KEY_CREATOR = PrfBasedDeriverKeyManager::createNewKey;
   private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.PrfBasedDeriverKey";

   @AccessesPartialKey
   private static final PrfBasedKeyDerivationKey createNewKey(PrfBasedKeyDerivationParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      Key prfKey = MutableKeyCreationRegistry.globalInstance().createKey(parameters.getPrfParameters(), null);
      if (!(prfKey instanceof PrfKey)) {
         throw new GeneralSecurityException("Failed to create PrfKey from parameters" + parameters.getPrfParameters() + ", instead got " + prfKey.getClass());
      } else {
         return PrfBasedKeyDerivationKey.create(parameters, (PrfKey)prfKey, idRequirement);
      }
   }

   PrfBasedDeriverKeyManager() {
   }

   public Void getPrimitive(ByteString serializedKey) throws GeneralSecurityException {
      throw new GeneralSecurityException("Cannot use the KeyManager to get a primitive for KeyDerivation");
   }

   public final Void getPrimitive(MessageLite key) throws GeneralSecurityException {
      throw new GeneralSecurityException("Cannot use the KeyManager to get a primitive for KeyDerivation");
   }

   @Override
   public final MessageLite newKey(ByteString serializedKeyFormat) throws GeneralSecurityException {
      KeyData keyData = this.newKeyData(serializedKeyFormat);

      try {
         return PrfBasedDeriverKey.parseFrom(keyData.getValue(), ExtensionRegistryLite.getEmptyRegistry());
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
      return "type.googleapis.com/google.crypto.tink.PrfBasedDeriverKey";
   }

   @Override
   public int getVersion() {
      return 0;
   }

   private static OutputPrefixType getOutputPrefixTypeFromSerializedKeyFormat(ByteString serializedKeyFormat) throws GeneralSecurityException {
      try {
         PrfBasedDeriverKeyFormat format = PrfBasedDeriverKeyFormat.parseFrom(serializedKeyFormat, ExtensionRegistryLite.getEmptyRegistry());
         return format.getParams().getDerivedKeyTemplate().getOutputPrefixType();
      } catch (InvalidProtocolBufferException var2) {
         throw new GeneralSecurityException("Unexpectedly failed to parse key format", var2);
      }
   }

   @Override
   public final KeyData newKeyData(ByteString serializedKeyFormat) throws GeneralSecurityException {
      OutputPrefixType outputPrefixType = getOutputPrefixTypeFromSerializedKeyFormat(serializedKeyFormat);
      ProtoParametersSerialization parametersSerialization = ProtoParametersSerialization.checkedCreate(
         KeyTemplate.newBuilder()
            .setTypeUrl("type.googleapis.com/google.crypto.tink.PrfBasedDeriverKey")
            .setValue(serializedKeyFormat)
            .setOutputPrefixType(outputPrefixType)
            .build()
      );
      Parameters parameters = MutableSerializationRegistry.globalInstance().parseParameters(parametersSerialization);
      Integer idRequirement = null;
      if (!outputPrefixType.equals(OutputPrefixType.RAW)) {
         idRequirement = 123;
      }

      Key key = MutableKeyCreationRegistry.globalInstance().createKey(parameters, idRequirement);
      ProtoKeySerialization keySerialization = MutableSerializationRegistry.globalInstance()
         .serializeKey(key, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
      return KeyData.newBuilder()
         .setTypeUrl(keySerialization.getTypeUrl())
         .setValue(keySerialization.getValue())
         .setKeyMaterialType(keySerialization.getKeyMaterialType())
         .build();
   }

   @Override
   public final Class<Void> getPrimitiveClass() {
      return Void.class;
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      KeyManagerRegistry.globalInstance().registerKeyManager(new PrfBasedDeriverKeyManager(), newKeyAllowed);
      MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, PrfBasedKeyDerivationParameters.class);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PRIMITIVE_CONSTRUCTOR);
      PrfBasedKeyDerivationKeyProtoSerialization.register();
   }
}
