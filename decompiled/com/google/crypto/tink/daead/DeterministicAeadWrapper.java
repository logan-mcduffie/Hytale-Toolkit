package com.google.crypto.tink.daead;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.daead.internal.LegacyFullDeterministicAead;
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

public class DeterministicAeadWrapper implements PrimitiveWrapper<DeterministicAead, DeterministicAead> {
   private static final DeterministicAeadWrapper WRAPPER = new DeterministicAeadWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, DeterministicAead> LEGACY_FULL_DAEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullDeterministicAead::create, LegacyProtoKey.class, DeterministicAead.class
   );

   private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
      if (key instanceof DeterministicAeadKey) {
         return ((DeterministicAeadKey)key).getOutputPrefix();
      } else if (key instanceof LegacyProtoKey) {
         return ((LegacyProtoKey)key).getOutputPrefix();
      } else {
         throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
      }
   }

   DeterministicAeadWrapper() {
   }

   public DeterministicAead wrap(KeysetHandleInterface handle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<DeterministicAead> factory) throws GeneralSecurityException {
      PrefixMap.Builder<DeterministicAeadWrapper.DeterministicAeadWithId> builder = new PrefixMap.Builder<>();

      for (int i = 0; i < handle.size(); i++) {
         KeysetHandleInterface.Entry entry = handle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            DeterministicAead deterministicAead = factory.create(entry);
            builder.put(getOutputPrefix(entry.getKey()), new DeterministicAeadWrapper.DeterministicAeadWithId(deterministicAead, entry.getId()));
         }
      }

      MonitoringClient.Logger encLogger;
      MonitoringClient.Logger decLogger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         encLogger = client.createLogger(handle, annotations, "daead", "encrypt");
         decLogger = client.createLogger(handle, annotations, "daead", "decrypt");
      } else {
         encLogger = MonitoringUtil.DO_NOTHING_LOGGER;
         decLogger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      return new DeterministicAeadWrapper.WrappedDeterministicAead(
         new DeterministicAeadWrapper.DeterministicAeadWithId(factory.create(handle.getPrimary()), handle.getPrimary().getId()),
         builder.build(),
         encLogger,
         decLogger
      );
   }

   @Override
   public Class<DeterministicAead> getPrimitiveClass() {
      return DeterministicAead.class;
   }

   @Override
   public Class<DeterministicAead> getInputPrimitiveClass() {
      return DeterministicAead.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_DAEAD_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class DeterministicAeadWithId {
      public final DeterministicAead daead;
      public final int id;

      public DeterministicAeadWithId(DeterministicAead daead, int id) {
         this.daead = daead;
         this.id = id;
      }
   }

   private static class WrappedDeterministicAead implements DeterministicAead {
      private final DeterministicAeadWrapper.DeterministicAeadWithId primary;
      private final PrefixMap<DeterministicAeadWrapper.DeterministicAeadWithId> allDaeads;
      private final MonitoringClient.Logger encLogger;
      private final MonitoringClient.Logger decLogger;

      public WrappedDeterministicAead(
         DeterministicAeadWrapper.DeterministicAeadWithId primary,
         PrefixMap<DeterministicAeadWrapper.DeterministicAeadWithId> allDaeads,
         MonitoringClient.Logger encLogger,
         MonitoringClient.Logger decLogger
      ) {
         this.primary = primary;
         this.allDaeads = allDaeads;
         this.encLogger = encLogger;
         this.decLogger = decLogger;
      }

      @Override
      public byte[] encryptDeterministically(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
         try {
            byte[] result = this.primary.daead.encryptDeterministically(plaintext, associatedData);
            this.encLogger.log(this.primary.id, plaintext.length);
            return result;
         } catch (GeneralSecurityException var4) {
            this.encLogger.logFailure();
            throw var4;
         }
      }

      @Override
      public byte[] decryptDeterministically(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
         for (DeterministicAeadWrapper.DeterministicAeadWithId aeadWithId : this.allDaeads.getAllWithMatchingPrefix(ciphertext)) {
            try {
               byte[] result = aeadWithId.daead.decryptDeterministically(ciphertext, associatedData);
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
