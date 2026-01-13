package com.google.crypto.tink.prf;

import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.prf.internal.LegacyFullPrf;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Immutable
public class PrfSetWrapper implements PrimitiveWrapper<Prf, PrfSet> {
   private static final PrfSetWrapper WRAPPER = new PrfSetWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, Prf> LEGACY_FULL_PRF_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullPrf::create, LegacyProtoKey.class, Prf.class
   );

   public PrfSet wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<Prf> factory) throws GeneralSecurityException {
      MonitoringClient.Logger logger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         logger = client.createLogger(keysetHandle, annotations, "prf", "compute");
      } else {
         logger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      Map<Integer, Prf> mutablePrfMap = new HashMap<>();

      for (int i = 0; i < keysetHandle.size(); i++) {
         KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            if (entry.getKey() instanceof LegacyProtoKey) {
               LegacyProtoKey legacyProtoKey = (LegacyProtoKey)entry.getKey();
               if (legacyProtoKey.getOutputPrefix().size() != 0) {
                  throw new GeneralSecurityException("Cannot build PrfSet with keys with non-empty output prefix");
               }
            }

            Prf prf = factory.create(entry);
            mutablePrfMap.put(entry.getId(), new PrfSetWrapper.WrappedPrfSet.PrfWithMonitoring(prf, entry.getId(), logger));
         }
      }

      return new PrfSetWrapper.WrappedPrfSet(mutablePrfMap, keysetHandle.getPrimary().getId());
   }

   @Override
   public Class<PrfSet> getPrimitiveClass() {
      return PrfSet.class;
   }

   @Override
   public Class<Prf> getInputPrimitiveClass() {
      return Prf.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_PRF_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class WrappedPrfSet extends PrfSet {
      private final Map<Integer, Prf> keyIdToPrfMap;
      private final int primaryKeyId;

      private WrappedPrfSet(Map<Integer, Prf> keyIdToPrfMap, int primaryKeyId) {
         this.keyIdToPrfMap = keyIdToPrfMap;
         this.primaryKeyId = primaryKeyId;
      }

      @Override
      public int getPrimaryId() {
         return this.primaryKeyId;
      }

      @Override
      public Map<Integer, Prf> getPrfs() throws GeneralSecurityException {
         return this.keyIdToPrfMap;
      }

      @Immutable
      private static class PrfWithMonitoring implements Prf {
         private final Prf prf;
         private final int keyId;
         private final MonitoringClient.Logger logger;

         @Override
         public byte[] compute(byte[] input, int outputLength) throws GeneralSecurityException {
            try {
               byte[] output = this.prf.compute(input, outputLength);
               this.logger.log(this.keyId, input.length);
               return output;
            } catch (GeneralSecurityException var4) {
               this.logger.logFailure();
               throw var4;
            }
         }

         public PrfWithMonitoring(Prf prf, int keyId, MonitoringClient.Logger logger) {
            this.prf = prf;
            this.keyId = keyId;
            this.logger = logger;
         }
      }
   }
}
