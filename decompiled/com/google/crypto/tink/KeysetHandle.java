package com.google.crypto.tink;

import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.config.GlobalTinkFlags;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.crypto.tink.tinkkey.KeyHandle;
import com.google.crypto.tink.tinkkey.internal.InternalKeyHandle;
import com.google.crypto.tink.tinkkey.internal.ProtoKey;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.InlineMe;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class KeysetHandle implements KeysetHandleInterface {
   private final List<KeysetHandle.Entry> entries;
   private final MonitoringAnnotations annotations;
   @Nullable
   private final KeysetHandle unmonitoredHandle;

   private static KeyStatus parseStatusWithDisabledFallback(KeyStatusType in) {
      switch (in) {
         case ENABLED:
            return KeyStatus.ENABLED;
         case DESTROYED:
            return KeyStatus.DESTROYED;
         case DISABLED:
         default:
            return KeyStatus.DISABLED;
      }
   }

   private static boolean isValidKeyStatusType(KeyStatusType in) {
      switch (in) {
         case ENABLED:
         case DESTROYED:
         case DISABLED:
            return true;
         default:
            return false;
      }
   }

   private static KeyStatusType serializeStatus(KeyStatus in) {
      if (KeyStatus.ENABLED.equals(in)) {
         return KeyStatusType.ENABLED;
      } else if (KeyStatus.DISABLED.equals(in)) {
         return KeyStatusType.DISABLED;
      } else if (KeyStatus.DESTROYED.equals(in)) {
         return KeyStatusType.DESTROYED;
      } else {
         throw new IllegalStateException("Unknown key status");
      }
   }

   private static List<KeysetHandle.Entry> getEntriesFromKeyset(Keyset keyset) throws GeneralSecurityException {
      List<KeysetHandle.Entry> result = new ArrayList<>(keyset.getKeyCount());

      for (Keyset.Key protoKey : keyset.getKeyList()) {
         int id = protoKey.getKeyId();

         Key key;
         boolean keyParsingFailed;
         try {
            key = toKey(protoKey);
            keyParsingFailed = false;
         } catch (GeneralSecurityException var8) {
            if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
               throw var8;
            }

            key = new LegacyProtoKey(toProtoKeySerialization(protoKey), InsecureSecretKeyAccess.get());
            keyParsingFailed = true;
         }

         if (GlobalTinkFlags.validateKeysetsOnParsing.getValue() && !isValidKeyStatusType(protoKey.getStatus())) {
            throw new GeneralSecurityException(
               "Parsing of a single key failed (wrong status) and Tink is configured via validateKeysetsOnParsing to reject such keysets."
            );
         }

         result.add(new KeysetHandle.Entry(key, protoKey.getStatus(), id, id == keyset.getPrimaryKeyId(), keyParsingFailed, KeysetHandle.Entry.NO_LOGGING));
      }

      return Collections.unmodifiableList(result);
   }

   private KeysetHandle.Entry entryByIndex(int i) {
      KeysetHandle.Entry entry = this.entries.get(i);
      if (!isValidKeyStatusType(entry.keyStatusType)) {
         throw new IllegalStateException("Keyset-Entry at position " + i + " has wrong status");
      } else if (entry.keyParsingFailed) {
         throw new IllegalStateException("Keyset-Entry at position " + i + " didn't parse correctly");
      } else {
         return this.entries.get(i);
      }
   }

   public static KeysetHandle.Builder.Entry importKey(Key key) {
      KeysetHandle.Builder.Entry importedEntry = new KeysetHandle.Builder.Entry(key);
      Integer requirement = key.getIdRequirementOrNull();
      if (requirement != null) {
         importedEntry.withFixedId(requirement);
      }

      return importedEntry;
   }

   public static KeysetHandle.Builder.Entry generateEntryFromParametersName(String parametersName) throws GeneralSecurityException {
      Parameters parameters = MutableParametersRegistry.globalInstance().get(parametersName);
      return new KeysetHandle.Builder.Entry(parameters);
   }

   public static KeysetHandle.Builder.Entry generateEntryFromParameters(Parameters parameters) {
      return new KeysetHandle.Builder.Entry(parameters);
   }

   private KeysetHandle getUnmonitoredHandle() {
      return this.unmonitoredHandle == null ? this : this.unmonitoredHandle;
   }

   private static void validateNoDuplicateIds(List<KeysetHandle.Entry> entries) throws GeneralSecurityException {
      Set<Integer> idsSoFar = new HashSet<>();
      boolean foundPrimary = false;

      for (KeysetHandle.Entry e : entries) {
         if (idsSoFar.contains(e.getId())) {
            throw new GeneralSecurityException(
               "KeyID " + e.getId() + " is duplicated in the keyset, and Tink is configured to reject such keysets with the flag validateKeysetsOnParsing."
            );
         }

         idsSoFar.add(e.getId());
         if (e.isPrimary()) {
            foundPrimary = true;
         }
      }

      if (!foundPrimary) {
         throw new GeneralSecurityException(
            "Primary key id not found in keyset, and Tink is configured to reject such keysets with the flag validateKeysetsOnParsing."
         );
      }
   }

   private KeysetHandle(List<KeysetHandle.Entry> entries, MonitoringAnnotations annotations) throws GeneralSecurityException {
      this.entries = entries;
      this.annotations = annotations;
      if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
         validateNoDuplicateIds(entries);
      }

      this.unmonitoredHandle = null;
   }

   private KeysetHandle(List<KeysetHandle.Entry> entries, MonitoringAnnotations annotations, KeysetHandle unmonitoredHandle) {
      this.entries = entries;
      this.annotations = annotations;
      this.unmonitoredHandle = unmonitoredHandle;
   }

   private static KeysetHandle addMonitoringIfNeeded(KeysetHandle unmonitoredHandle) {
      MonitoringAnnotations annotations = unmonitoredHandle.annotations;
      if (annotations.isEmpty()) {
         return unmonitoredHandle;
      } else {
         KeysetHandle.Entry.EntryConsumer keyExportLogger = entryToLog -> {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            client.createLogger(unmonitoredHandle, annotations, "keyset_handle", "get_key").logKeyExport(entryToLog.getId());
         };
         List<KeysetHandle.Entry> monitoredEntries = new ArrayList<>(unmonitoredHandle.entries.size());

         for (KeysetHandle.Entry e : unmonitoredHandle.entries) {
            monitoredEntries.add(new KeysetHandle.Entry(e.key, e.keyStatusType, e.id, e.isPrimary, e.keyParsingFailed, keyExportLogger));
         }

         return new KeysetHandle(monitoredEntries, annotations, unmonitoredHandle);
      }
   }

   static final KeysetHandle fromKeyset(Keyset keyset) throws GeneralSecurityException {
      assertEnoughKeyMaterial(keyset);
      List<KeysetHandle.Entry> entries = getEntriesFromKeyset(keyset);
      return new KeysetHandle(entries, MonitoringAnnotations.EMPTY);
   }

   static final KeysetHandle fromKeysetAndAnnotations(Keyset keyset, MonitoringAnnotations annotations) throws GeneralSecurityException {
      assertEnoughKeyMaterial(keyset);
      List<KeysetHandle.Entry> entries = getEntriesFromKeyset(keyset);
      return addMonitoringIfNeeded(new KeysetHandle(entries, annotations));
   }

   Keyset getKeyset() {
      try {
         Keyset.Builder builder = Keyset.newBuilder();

         for (KeysetHandle.Entry entry : this.entries) {
            Keyset.Key protoKey = createKeysetKey(entry.getKey(), entry.keyStatusType, entry.getId());
            builder.addKey(protoKey);
            if (entry.isPrimary()) {
               builder.setPrimaryKeyId(entry.getId());
            }
         }

         return builder.build();
      } catch (GeneralSecurityException var5) {
         throw new TinkBugException(var5);
      }
   }

   public static KeysetHandle.Builder newBuilder() {
      return new KeysetHandle.Builder();
   }

   public static KeysetHandle.Builder newBuilder(KeysetHandle handle) {
      KeysetHandle.Builder builder = new KeysetHandle.Builder();

      for (int i = 0; i < handle.size(); i++) {
         KeysetHandle.Entry entry;
         try {
            entry = handle.getAt(i);
         } catch (IllegalStateException var5) {
            builder.setErrorToThrow(
               new GeneralSecurityException("Keyset-Entry in original keyset at position " + i + " has wrong status or key parsing failed", var5)
            );
            break;
         }

         KeysetHandle.Builder.Entry builderEntry = importKey(entry.getKey()).withFixedId(entry.getId());
         builderEntry.setStatus(entry.getStatus());
         if (entry.isPrimary()) {
            builderEntry.makePrimary();
         }

         builder.addEntry(builderEntry);
      }

      return builder;
   }

   public KeysetHandle.Entry getPrimary() {
      for (KeysetHandle.Entry entry : this.entries) {
         if (entry != null && entry.isPrimary()) {
            if (entry.getStatus() != KeyStatus.ENABLED) {
               throw new IllegalStateException("Keyset has primary which isn't enabled");
            }

            return entry;
         }
      }

      throw new IllegalStateException("Keyset has no valid primary");
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   public KeysetHandle.Entry getAt(int i) {
      if (i >= 0 && i < this.size()) {
         return this.entryByIndex(i);
      } else {
         throw new IndexOutOfBoundsException("Invalid index " + i + " for keyset of size " + this.size());
      }
   }

   @Deprecated
   public List<KeyHandle> getKeys() {
      ArrayList<KeyHandle> result = new ArrayList<>();
      Keyset keyset = this.getKeyset();

      for (Keyset.Key key : keyset.getKeyList()) {
         KeyData keyData = key.getKeyData();
         result.add(new InternalKeyHandle(new ProtoKey(keyData, KeyTemplate.fromProto(key.getOutputPrefixType())), key.getStatus(), key.getKeyId()));
      }

      return Collections.unmodifiableList(result);
   }

   @Deprecated
   public KeysetInfo getKeysetInfo() {
      Keyset keyset = this.getKeyset();
      return Util.getKeysetInfo(keyset);
   }

   public static final KeysetHandle generateNew(Parameters parameters) throws GeneralSecurityException {
      return newBuilder().addEntry(generateEntryFromParameters(parameters).withRandomId().makePrimary()).build();
   }

   @Deprecated
   public static final KeysetHandle generateNew(com.google.crypto.tink.proto.KeyTemplate keyTemplate) throws GeneralSecurityException {
      return generateNew(TinkProtoParametersFormat.parse(keyTemplate.toByteArray()));
   }

   public static final KeysetHandle generateNew(KeyTemplate keyTemplate) throws GeneralSecurityException {
      return generateNew(keyTemplate.toParameters());
   }

   @Deprecated
   public static final KeysetHandle createFromKey(KeyHandle keyHandle, KeyAccess access) throws GeneralSecurityException {
      KeysetManager km = KeysetManager.withEmptyKeyset().add(keyHandle);
      km.setPrimary(km.getKeysetHandle().getKeysetInfo().getKeyInfo(0).getKeyId());
      return km.getKeysetHandle();
   }

   @Deprecated
   public static final KeysetHandle read(KeysetReader reader, Aead masterKey) throws GeneralSecurityException, IOException {
      return readWithAssociatedData(reader, masterKey, new byte[0]);
   }

   @Deprecated
   public static final KeysetHandle readWithAssociatedData(KeysetReader reader, Aead masterKey, byte[] associatedData) throws GeneralSecurityException, IOException {
      EncryptedKeyset encryptedKeyset = reader.readEncrypted();
      assertEnoughEncryptedKeyMaterial(encryptedKeyset);
      return fromKeyset(decrypt(encryptedKeyset, masterKey, associatedData));
   }

   @Deprecated
   public static final KeysetHandle readNoSecret(KeysetReader reader) throws GeneralSecurityException, IOException {
      byte[] serializedKeyset;
      try {
         serializedKeyset = reader.read().toByteArray();
      } catch (InvalidProtocolBufferException var3) {
         throw new GeneralSecurityException("invalid keyset");
      }

      return readNoSecret(serializedKeyset);
   }

   @Deprecated
   public static final KeysetHandle readNoSecret(final byte[] serialized) throws GeneralSecurityException {
      try {
         Keyset keyset = Keyset.parseFrom(serialized, ExtensionRegistryLite.getEmptyRegistry());
         assertNoSecretKeyMaterial(keyset);
         return fromKeyset(keyset);
      } catch (InvalidProtocolBufferException var2) {
         throw new GeneralSecurityException("invalid keyset");
      }
   }

   @Deprecated
   public void write(KeysetWriter keysetWriter, Aead masterKey) throws GeneralSecurityException, IOException {
      this.writeWithAssociatedData(keysetWriter, masterKey, new byte[0]);
   }

   @Deprecated
   public void writeWithAssociatedData(KeysetWriter keysetWriter, Aead masterKey, byte[] associatedData) throws GeneralSecurityException, IOException {
      Keyset keyset = this.getKeyset();
      EncryptedKeyset encryptedKeyset = encrypt(keyset, masterKey, associatedData);
      keysetWriter.write(encryptedKeyset);
   }

   @Deprecated
   public void writeNoSecret(KeysetWriter writer) throws GeneralSecurityException, IOException {
      Keyset keyset = this.getKeyset();
      assertNoSecretKeyMaterial(keyset);
      writer.write(keyset);
   }

   private static EncryptedKeyset encrypt(Keyset keyset, Aead masterKey, byte[] associatedData) throws GeneralSecurityException {
      byte[] encryptedKeyset = masterKey.encrypt(keyset.toByteArray(), associatedData);
      return EncryptedKeyset.newBuilder().setEncryptedKeyset(ByteString.copyFrom(encryptedKeyset)).setKeysetInfo(Util.getKeysetInfo(keyset)).build();
   }

   private static Keyset decrypt(EncryptedKeyset encryptedKeyset, Aead masterKey, byte[] associatedData) throws GeneralSecurityException {
      try {
         Keyset keyset = Keyset.parseFrom(
            masterKey.decrypt(encryptedKeyset.getEncryptedKeyset().toByteArray(), associatedData), ExtensionRegistryLite.getEmptyRegistry()
         );
         assertEnoughKeyMaterial(keyset);
         return keyset;
      } catch (InvalidProtocolBufferException var4) {
         throw new GeneralSecurityException("invalid keyset, corrupted key material");
      }
   }

   public KeysetHandle getPublicKeysetHandle() throws GeneralSecurityException {
      Keyset keyset = this.getKeyset();
      List<KeysetHandle.Entry> publicEntries = new ArrayList<>(this.entries.size());
      int i = 0;

      for (KeysetHandle.Entry entry : this.entries) {
         KeysetHandle.Entry publicEntry;
         if (entry.getKey() instanceof PrivateKey) {
            Key publicKey = ((PrivateKey)entry.getKey()).getPublicKey();
            publicEntry = new KeysetHandle.Entry(publicKey, entry.keyStatusType, entry.getId(), entry.isPrimary(), false, KeysetHandle.Entry.NO_LOGGING);
            validateKeyId(publicKey, entry.getId());
         } else {
            Keyset.Key protoKey = keyset.getKey(i);
            KeyData keyData = getPublicKeyDataFromRegistry(protoKey.getKeyData());
            Keyset.Key publicProtoKey = protoKey.toBuilder().setKeyData(keyData).build();

            Key publicKey;
            boolean keyParsingFailed;
            try {
               publicKey = toKey(publicProtoKey);
               keyParsingFailed = false;
            } catch (GeneralSecurityException var13) {
               if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
                  throw var13;
               }

               publicKey = new LegacyProtoKey(toProtoKeySerialization(publicProtoKey), InsecureSecretKeyAccess.get());
               keyParsingFailed = true;
            }

            int id = publicProtoKey.getKeyId();
            publicEntry = new KeysetHandle.Entry(
               publicKey, entry.keyStatusType, id, id == keyset.getPrimaryKeyId(), keyParsingFailed, KeysetHandle.Entry.NO_LOGGING
            );
         }

         publicEntries.add(publicEntry);
         i++;
      }

      return addMonitoringIfNeeded(new KeysetHandle(publicEntries, this.annotations));
   }

   private static KeyData getPublicKeyDataFromRegistry(KeyData privateKeyData) throws GeneralSecurityException {
      if (privateKeyData.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) {
         throw new GeneralSecurityException("The keyset contains a non-private key");
      } else {
         return Registry.getPublicKeyData(privateKeyData.getTypeUrl(), privateKeyData.getValue());
      }
   }

   @Override
   public String toString() {
      return this.getKeysetInfo().toString();
   }

   private static void assertNoSecretKeyMaterial(Keyset keyset) throws GeneralSecurityException {
      for (Keyset.Key key : keyset.getKeyList()) {
         if (key.getKeyData().getKeyMaterialType() == KeyData.KeyMaterialType.UNKNOWN_KEYMATERIAL
            || key.getKeyData().getKeyMaterialType() == KeyData.KeyMaterialType.SYMMETRIC
            || key.getKeyData().getKeyMaterialType() == KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) {
            throw new GeneralSecurityException(
               String.format(
                  "keyset contains key material of type %s for type url %s", key.getKeyData().getKeyMaterialType().name(), key.getKeyData().getTypeUrl()
               )
            );
         }
      }
   }

   private static void assertEnoughKeyMaterial(Keyset keyset) throws GeneralSecurityException {
      if (keyset == null || keyset.getKeyCount() <= 0) {
         throw new GeneralSecurityException("empty keyset");
      }
   }

   private static void assertEnoughEncryptedKeyMaterial(EncryptedKeyset keyset) throws GeneralSecurityException {
      if (keyset == null || keyset.getEncryptedKeyset().size() == 0) {
         throw new GeneralSecurityException("empty keyset");
      }
   }

   private <P> P getPrimitiveInternal(InternalConfiguration config, Class<P> classObject) throws GeneralSecurityException {
      Keyset keyset = this.getUnmonitoredHandle().getKeyset();
      Util.validateKeyset(keyset);

      for (int i = 0; i < this.size(); i++) {
         if (this.entries.get(i).keyParsingFailed || !isValidKeyStatusType(this.entries.get(i).keyStatusType)) {
            Keyset.Key protoKey = keyset.getKey(i);
            throw new GeneralSecurityException(
               "Key parsing of key with index " + i + " and type_url " + protoKey.getKeyData().getTypeUrl() + " failed, unable to get primitive"
            );
         }
      }

      return config.wrap(this.getUnmonitoredHandle(), this.annotations, classObject);
   }

   public <P> P getPrimitive(Configuration configuration, Class<P> targetClassObject) throws GeneralSecurityException {
      if (!(configuration instanceof InternalConfiguration)) {
         throw new GeneralSecurityException("Currently only subclasses of InternalConfiguration are accepted");
      } else {
         InternalConfiguration internalConfig = (InternalConfiguration)configuration;
         return this.getPrimitiveInternal(internalConfig, targetClassObject);
      }
   }

   @Deprecated
   @InlineMe(replacement = "this.getPrimitive(RegistryConfiguration.get(), targetClassObject)", imports = "com.google.crypto.tink.RegistryConfiguration")
   public <P> P getPrimitive(Class<P> targetClassObject) throws GeneralSecurityException {
      return this.getPrimitive(RegistryConfiguration.get(), targetClassObject);
   }

   @Deprecated
   public KeyHandle primaryKey() throws GeneralSecurityException {
      Keyset keyset = this.getKeyset();
      int primaryKeyId = keyset.getPrimaryKeyId();

      for (Keyset.Key key : keyset.getKeyList()) {
         if (key.getKeyId() == primaryKeyId) {
            return new InternalKeyHandle(new ProtoKey(key.getKeyData(), KeyTemplate.fromProto(key.getOutputPrefixType())), key.getStatus(), key.getKeyId());
         }
      }

      throw new GeneralSecurityException("No primary key found in keyset.");
   }

   public boolean equalsKeyset(KeysetHandle other) {
      if (this.size() != other.size()) {
         return false;
      } else {
         boolean primaryFound = false;

         for (int i = 0; i < this.size(); i++) {
            KeysetHandle.Entry thisEntry = this.entries.get(i);
            KeysetHandle.Entry otherEntry = other.entries.get(i);
            if (thisEntry.keyParsingFailed) {
               return false;
            }

            if (otherEntry.keyParsingFailed) {
               return false;
            }

            if (!isValidKeyStatusType(thisEntry.keyStatusType)) {
               return false;
            }

            if (!isValidKeyStatusType(otherEntry.keyStatusType)) {
               return false;
            }

            if (!thisEntry.equalsEntry(otherEntry)) {
               return false;
            }

            primaryFound |= thisEntry.isPrimary;
         }

         return primaryFound;
      }
   }

   private static ProtoKeySerialization toProtoKeySerialization(Keyset.Key protoKey) throws GeneralSecurityException {
      int id = protoKey.getKeyId();
      Integer idRequirement = protoKey.getOutputPrefixType() == OutputPrefixType.RAW ? null : id;
      return ProtoKeySerialization.create(
         protoKey.getKeyData().getTypeUrl(),
         protoKey.getKeyData().getValue(),
         protoKey.getKeyData().getKeyMaterialType(),
         protoKey.getOutputPrefixType(),
         idRequirement
      );
   }

   private static Key toKey(Keyset.Key protoKey) throws GeneralSecurityException {
      ProtoKeySerialization protoKeySerialization = toProtoKeySerialization(protoKey);
      return MutableSerializationRegistry.globalInstance().parseKeyWithLegacyFallback(protoKeySerialization, InsecureSecretKeyAccess.get());
   }

   private static Keyset.Key toKeysetKey(int id, KeyStatusType status, ProtoKeySerialization protoKeySerialization) {
      return Keyset.Key.newBuilder()
         .setKeyData(
            KeyData.newBuilder()
               .setTypeUrl(protoKeySerialization.getTypeUrl())
               .setValue(protoKeySerialization.getValue())
               .setKeyMaterialType(protoKeySerialization.getKeyMaterialType())
         )
         .setStatus(status)
         .setKeyId(id)
         .setOutputPrefixType(protoKeySerialization.getOutputPrefixType())
         .build();
   }

   private static void validateKeyId(Key key, int id) throws GeneralSecurityException {
      Integer idRequirement = key.getIdRequirementOrNull();
      if (idRequirement != null && idRequirement != id) {
         throw new GeneralSecurityException("Wrong ID set for key with ID requirement");
      }
   }

   private static Keyset.Key createKeysetKey(Key key, KeyStatusType keyStatus, int id) throws GeneralSecurityException {
      ProtoKeySerialization serializedKey = MutableSerializationRegistry.globalInstance()
         .serializeKey(key, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
      validateKeyId(key, id);
      return toKeysetKey(id, keyStatus, serializedKey);
   }

   public static final class Builder {
      private final List<KeysetHandle.Builder.Entry> entries = new ArrayList<>();
      @Nullable
      private GeneralSecurityException errorToThrow = null;
      private MonitoringAnnotations annotations = MonitoringAnnotations.EMPTY;
      private boolean buildCalled = false;

      private void clearPrimary() {
         for (KeysetHandle.Builder.Entry entry : this.entries) {
            entry.isPrimary = false;
         }
      }

      @CanIgnoreReturnValue
      public KeysetHandle.Builder addEntry(KeysetHandle.Builder.Entry entry) {
         if (entry.builder != null) {
            throw new IllegalStateException("Entry has already been added to a KeysetHandle.Builder");
         } else {
            if (entry.isPrimary) {
               this.clearPrimary();
            }

            entry.builder = this;
            this.entries.add(entry);
            return this;
         }
      }

      @CanIgnoreReturnValue
      @Alpha
      public KeysetHandle.Builder setMonitoringAnnotations(MonitoringAnnotations annotations) {
         this.annotations = annotations;
         return this;
      }

      public int size() {
         return this.entries.size();
      }

      public KeysetHandle.Builder.Entry getAt(int i) {
         return this.entries.get(i);
      }

      @Deprecated
      @CanIgnoreReturnValue
      public KeysetHandle.Builder.Entry removeAt(int i) {
         return this.entries.remove(i);
      }

      @CanIgnoreReturnValue
      public KeysetHandle.Builder deleteAt(int i) {
         this.entries.remove(i);
         return this;
      }

      private static void checkIdAssignments(List<KeysetHandle.Builder.Entry> entries) throws GeneralSecurityException {
         for (int i = 0; i < entries.size() - 1; i++) {
            if (entries.get(i).strategy == KeysetHandle.Builder.KeyIdStrategy.RANDOM_ID
               && entries.get(i + 1).strategy != KeysetHandle.Builder.KeyIdStrategy.RANDOM_ID) {
               throw new GeneralSecurityException("Entries with 'withRandomId()' may only be followed by other entries with 'withRandomId()'.");
            }
         }
      }

      private void setErrorToThrow(GeneralSecurityException errorToThrow) {
         this.errorToThrow = errorToThrow;
      }

      private static int randomIdNotInSet(Set<Integer> ids) {
         int id = 0;

         while (id == 0 || ids.contains(id)) {
            id = com.google.crypto.tink.internal.Util.randKeyId();
         }

         return id;
      }

      private static int getNextIdFromBuilderEntry(KeysetHandle.Builder.Entry builderEntry, Set<Integer> idsSoFar) throws GeneralSecurityException {
         int id = 0;
         if (builderEntry.strategy == null) {
            throw new GeneralSecurityException("No ID was set (with withFixedId or withRandomId)");
         } else {
            if (builderEntry.strategy == KeysetHandle.Builder.KeyIdStrategy.RANDOM_ID) {
               id = randomIdNotInSet(idsSoFar);
            } else {
               id = builderEntry.strategy.getFixedId();
            }

            return id;
         }
      }

      public KeysetHandle build() throws GeneralSecurityException {
         if (this.errorToThrow != null) {
            throw new GeneralSecurityException("Cannot build keyset due to error in original", this.errorToThrow);
         } else if (this.buildCalled) {
            throw new GeneralSecurityException("KeysetHandle.Builder#build must only be called once");
         } else {
            this.buildCalled = true;
            List<KeysetHandle.Entry> handleEntries = new ArrayList<>(this.entries.size());
            Integer primaryId = null;
            checkIdAssignments(this.entries);
            Set<Integer> idsSoFar = new HashSet<>();

            for (KeysetHandle.Builder.Entry builderEntry : this.entries) {
               if (builderEntry.keyStatus == null) {
                  throw new GeneralSecurityException("Key Status not set.");
               }

               int id = getNextIdFromBuilderEntry(builderEntry, idsSoFar);
               if (idsSoFar.contains(id)) {
                  throw new GeneralSecurityException("Id " + id + " is used twice in the keyset");
               }

               idsSoFar.add(id);
               KeysetHandle.Entry handleEntry;
               if (builderEntry.key != null) {
                  KeysetHandle.validateKeyId(builderEntry.key, id);
                  handleEntry = new KeysetHandle.Entry(
                     builderEntry.key, KeysetHandle.serializeStatus(builderEntry.keyStatus), id, builderEntry.isPrimary, false, KeysetHandle.Entry.NO_LOGGING
                  );
               } else {
                  Integer idRequirement = builderEntry.parameters.hasIdRequirement() ? id : null;
                  Key key = MutableKeyCreationRegistry.globalInstance().createKey(builderEntry.parameters, idRequirement);
                  handleEntry = new KeysetHandle.Entry(
                     key, KeysetHandle.serializeStatus(builderEntry.keyStatus), id, builderEntry.isPrimary, false, KeysetHandle.Entry.NO_LOGGING
                  );
               }

               if (builderEntry.isPrimary) {
                  if (primaryId != null) {
                     throw new GeneralSecurityException("Two primaries were set");
                  }

                  primaryId = id;
                  if (builderEntry.keyStatus != KeyStatus.ENABLED) {
                     throw new GeneralSecurityException("Primary key is not enabled");
                  }
               }

               handleEntries.add(handleEntry);
            }

            if (primaryId == null) {
               throw new GeneralSecurityException("No primary was set");
            } else {
               KeysetHandle unmonitoredKeyset = new KeysetHandle(handleEntries, this.annotations);
               return KeysetHandle.addMonitoringIfNeeded(unmonitoredKeyset);
            }
         }
      }

      public static final class Entry {
         private boolean isPrimary;
         private KeyStatus keyStatus = KeyStatus.ENABLED;
         @Nullable
         private final Key key;
         @Nullable
         private final Parameters parameters;
         private KeysetHandle.Builder.KeyIdStrategy strategy = null;
         @Nullable
         private KeysetHandle.Builder builder = null;

         private Entry(Key key) {
            this.key = key;
            this.parameters = null;
         }

         private Entry(Parameters parameters) {
            this.key = null;
            this.parameters = parameters;
         }

         @CanIgnoreReturnValue
         public KeysetHandle.Builder.Entry makePrimary() {
            if (this.builder != null) {
               this.builder.clearPrimary();
            }

            this.isPrimary = true;
            return this;
         }

         public boolean isPrimary() {
            return this.isPrimary;
         }

         @CanIgnoreReturnValue
         public KeysetHandle.Builder.Entry setStatus(KeyStatus status) {
            this.keyStatus = status;
            return this;
         }

         public KeyStatus getStatus() {
            return this.keyStatus;
         }

         @CanIgnoreReturnValue
         public KeysetHandle.Builder.Entry withFixedId(int id) {
            this.strategy = KeysetHandle.Builder.KeyIdStrategy.fixedId(id);
            return this;
         }

         @CanIgnoreReturnValue
         public KeysetHandle.Builder.Entry withRandomId() {
            this.strategy = KeysetHandle.Builder.KeyIdStrategy.randomId();
            return this;
         }
      }

      private static class KeyIdStrategy {
         private static final KeysetHandle.Builder.KeyIdStrategy RANDOM_ID = new KeysetHandle.Builder.KeyIdStrategy();
         private final int fixedId;

         private KeyIdStrategy() {
            this.fixedId = 0;
         }

         private KeyIdStrategy(int id) {
            this.fixedId = id;
         }

         private static KeysetHandle.Builder.KeyIdStrategy randomId() {
            return RANDOM_ID;
         }

         private static KeysetHandle.Builder.KeyIdStrategy fixedId(int id) {
            return new KeysetHandle.Builder.KeyIdStrategy(id);
         }

         private int getFixedId() {
            return this.fixedId;
         }
      }
   }

   @Immutable
   public static final class Entry implements KeysetHandleInterface.Entry {
      private static final KeysetHandle.Entry.EntryConsumer NO_LOGGING = e -> {};
      private final Key key;
      private final KeyStatusType keyStatusType;
      private final KeyStatus keyStatus;
      private final int id;
      private final boolean isPrimary;
      private final boolean keyParsingFailed;
      private final KeysetHandle.Entry.EntryConsumer keyExportLogger;

      private Entry(Key key, KeyStatusType keyStatusType, int id, boolean isPrimary, boolean keyParsingFailed, KeysetHandle.Entry.EntryConsumer keyExportLogger) {
         this.key = key;
         this.keyStatusType = keyStatusType;
         this.keyStatus = KeysetHandle.parseStatusWithDisabledFallback(keyStatusType);
         this.id = id;
         this.isPrimary = isPrimary;
         this.keyParsingFailed = keyParsingFailed;
         this.keyExportLogger = keyExportLogger;
      }

      @Override
      public Key getKey() {
         this.keyExportLogger.accept(this);
         return this.key;
      }

      @Override
      public KeyStatus getStatus() {
         return this.keyStatus;
      }

      @Override
      public int getId() {
         return this.id;
      }

      @Override
      public boolean isPrimary() {
         return this.isPrimary;
      }

      private boolean equalsEntry(KeysetHandle.Entry other) {
         if (other.isPrimary != this.isPrimary) {
            return false;
         } else if (!other.keyStatusType.equals(this.keyStatusType)) {
            return false;
         } else {
            return other.id != this.id ? false : other.key.equalsKey(this.key);
         }
      }

      @Immutable
      private interface EntryConsumer {
         void accept(KeysetHandle.Entry entry);
      }
   }
}
