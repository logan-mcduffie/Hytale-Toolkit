package com.google.crypto.tink.mac;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.Mac;
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
import com.google.crypto.tink.mac.internal.LegacyFullMac;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public class MacWrapper implements PrimitiveWrapper<Mac, Mac> {
   private static final MacWrapper WRAPPER = new MacWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, Mac> LEGACY_FULL_MAC_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullMac::create, LegacyProtoKey.class, Mac.class
   );

   private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
      if (key instanceof MacKey) {
         return ((MacKey)key).getOutputPrefix();
      } else if (key instanceof LegacyProtoKey) {
         return ((LegacyProtoKey)key).getOutputPrefix();
      } else {
         throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
      }
   }

   MacWrapper() {
   }

   public Mac wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<Mac> factory) throws GeneralSecurityException {
      PrefixMap.Builder<MacWrapper.MacWithId> builder = new PrefixMap.Builder<>();

      for (int i = 0; i < keysetHandle.size(); i++) {
         KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            Mac mac = factory.create(entry);
            builder.put(getOutputPrefix(entry.getKey()), new MacWrapper.MacWithId(mac, entry.getId()));
         }
      }

      MonitoringClient.Logger computeLogger;
      MonitoringClient.Logger verifyLogger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         computeLogger = client.createLogger(keysetHandle, annotations, "mac", "compute");
         verifyLogger = client.createLogger(keysetHandle, annotations, "mac", "verify");
      } else {
         computeLogger = MonitoringUtil.DO_NOTHING_LOGGER;
         verifyLogger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      Mac primaryMac = factory.create(keysetHandle.getPrimary());
      return new MacWrapper.WrappedMac(new MacWrapper.MacWithId(primaryMac, keysetHandle.getPrimary().getId()), builder.build(), computeLogger, verifyLogger);
   }

   @Override
   public Class<Mac> getPrimitiveClass() {
      return Mac.class;
   }

   @Override
   public Class<Mac> getInputPrimitiveClass() {
      return Mac.class;
   }

   static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_MAC_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class MacWithId {
      public final Mac mac;
      public final int id;

      public MacWithId(Mac mac, int id) {
         this.mac = mac;
         this.id = id;
      }
   }

   private static class WrappedMac implements Mac {
      private final MacWrapper.MacWithId primary;
      private final PrefixMap<MacWrapper.MacWithId> allMacs;
      private final MonitoringClient.Logger computeLogger;
      private final MonitoringClient.Logger verifyLogger;

      private WrappedMac(
         MacWrapper.MacWithId primary, PrefixMap<MacWrapper.MacWithId> allMacs, MonitoringClient.Logger computeLogger, MonitoringClient.Logger verifyLogger
      ) {
         this.primary = primary;
         this.allMacs = allMacs;
         this.computeLogger = computeLogger;
         this.verifyLogger = verifyLogger;
      }

      @Override
      public byte[] computeMac(final byte[] data) throws GeneralSecurityException {
         try {
            byte[] output = this.primary.mac.computeMac(data);
            this.computeLogger.log(this.primary.id, data.length);
            return output;
         } catch (GeneralSecurityException var3) {
            this.computeLogger.logFailure();
            throw var3;
         }
      }

      @Override
      public void verifyMac(final byte[] mac, final byte[] data) throws GeneralSecurityException {
         for (MacWrapper.MacWithId macWithId : this.allMacs.getAllWithMatchingPrefix(mac)) {
            try {
               macWithId.mac.verifyMac(mac, data);
               this.verifyLogger.log(macWithId.id, data.length);
               return;
            } catch (GeneralSecurityException var6) {
            }
         }

         this.verifyLogger.logFailure();
         throw new GeneralSecurityException("invalid MAC");
      }
   }
}
