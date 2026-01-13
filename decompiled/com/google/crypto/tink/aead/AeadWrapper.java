package com.google.crypto.tink.aead;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.aead.internal.LegacyFullAead;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrefixMap;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public class AeadWrapper implements PrimitiveWrapper<Aead, Aead> {
   private static final AeadWrapper WRAPPER = new AeadWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, Aead> LEGACY_FULL_AEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullAead::create, LegacyProtoKey.class, Aead.class
   );

   private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
      if (key instanceof AeadKey) {
         return ((AeadKey)key).getOutputPrefix();
      } else if (key instanceof LegacyProtoKey) {
         return ((LegacyProtoKey)key).getOutputPrefix();
      } else {
         throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
      }
   }

   AeadWrapper() {
   }

   public Aead wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<Aead> factory) throws GeneralSecurityException {
      PrefixMap.Builder<AeadWrapper.AeadWithId> builder = new PrefixMap.Builder<>();

      for (int i = 0; i < keysetHandle.size(); i++) {
         KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            builder.put(getOutputPrefix(entry.getKey()), new AeadWrapper.AeadWithId(factory.create(entry), entry.getId()));
         }
      }

      MonitoringClient.Logger encLogger;
      MonitoringClient.Logger decLogger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         encLogger = client.createLogger(keysetHandle, annotations, "aead", "encrypt");
         decLogger = client.createLogger(keysetHandle, annotations, "aead", "decrypt");
      } else {
         encLogger = MonitoringUtil.DO_NOTHING_LOGGER;
         decLogger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      return new AeadWrapper.WrappedAead(
         new AeadWrapper.AeadWithId(factory.create(keysetHandle.getPrimary()), keysetHandle.getPrimary().getId()), builder.build(), encLogger, decLogger
      );
   }

   @Override
   public Class<Aead> getPrimitiveClass() {
      return Aead.class;
   }

   @Override
   public Class<Aead> getInputPrimitiveClass() {
      return Aead.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_AEAD_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class AeadWithId {
      public final Aead aead;
      public final int id;

      public AeadWithId(Aead aead, int id) {
         this.aead = aead;
         this.id = id;
      }
   }

   private static class WrappedAead implements Aead {
      private final AeadWrapper.AeadWithId primary;
      private final PrefixMap<AeadWrapper.AeadWithId> allAeads;
      private final MonitoringClient.Logger encLogger;
      private final MonitoringClient.Logger decLogger;

      private WrappedAead(
         AeadWrapper.AeadWithId primary, PrefixMap<AeadWrapper.AeadWithId> allAeads, MonitoringClient.Logger encLogger, MonitoringClient.Logger decLogger
      ) {
         this.primary = primary;
         this.allAeads = allAeads;
         this.encLogger = encLogger;
         this.decLogger = decLogger;
      }

      @Override
      public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
         try {
            byte[] result = this.primary.aead.encrypt(plaintext, associatedData);
            this.encLogger.log(this.primary.id, plaintext.length);
            return result;
         } catch (GeneralSecurityException var4) {
            this.encLogger.logFailure();
            throw var4;
         }
      }

      @Override
      public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
         for (AeadWrapper.AeadWithId aeadWithId : this.allAeads.getAllWithMatchingPrefix(ciphertext)) {
            try {
               byte[] result = aeadWithId.aead.decrypt(ciphertext, associatedData);
               this.decLogger.log(aeadWithId.id, ciphertext.length);
               return result;
            } catch (GeneralSecurityException var6) {
            }
         }

         this.decLogger.logFailure();
         throw new GeneralSecurityException("decryption failed");
      }
   }
}
