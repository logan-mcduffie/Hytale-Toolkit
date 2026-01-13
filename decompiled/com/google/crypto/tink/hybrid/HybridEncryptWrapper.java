package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.hybrid.internal.LegacyFullHybridEncrypt;
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
import java.security.GeneralSecurityException;

public class HybridEncryptWrapper implements PrimitiveWrapper<HybridEncrypt, HybridEncrypt> {
   private static final HybridEncryptWrapper WRAPPER = new HybridEncryptWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, HybridEncrypt> LEGACY_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullHybridEncrypt::create, LegacyProtoKey.class, HybridEncrypt.class
   );

   HybridEncryptWrapper() {
   }

   public HybridEncrypt wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<HybridEncrypt> factory) throws GeneralSecurityException {
      MonitoringClient.Logger encLogger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         encLogger = client.createLogger(keysetHandle, annotations, "hybrid_encrypt", "encrypt");
      } else {
         encLogger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      KeysetHandleInterface.Entry primary = keysetHandle.getPrimary();
      return new HybridEncryptWrapper.WrappedHybridEncrypt(new HybridEncryptWrapper.HybridEncryptWithId(factory.create(primary), primary.getId()), encLogger);
   }

   @Override
   public Class<HybridEncrypt> getPrimitiveClass() {
      return HybridEncrypt.class;
   }

   @Override
   public Class<HybridEncrypt> getInputPrimitiveClass() {
      return HybridEncrypt.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class HybridEncryptWithId {
      public final HybridEncrypt hybridEncrypt;
      public final int id;

      public HybridEncryptWithId(HybridEncrypt hybridEncrypt, int id) {
         this.hybridEncrypt = hybridEncrypt;
         this.id = id;
      }
   }

   private static class WrappedHybridEncrypt implements HybridEncrypt {
      private final HybridEncryptWrapper.HybridEncryptWithId primary;
      private final MonitoringClient.Logger encLogger;

      public WrappedHybridEncrypt(HybridEncryptWrapper.HybridEncryptWithId primary, MonitoringClient.Logger encLogger) {
         this.primary = primary;
         this.encLogger = encLogger;
      }

      @Override
      public byte[] encrypt(final byte[] plaintext, final byte[] contextInfo) throws GeneralSecurityException {
         if (this.primary.hybridEncrypt == null) {
            this.encLogger.logFailure();
            throw new GeneralSecurityException("keyset without primary key");
         } else {
            try {
               byte[] output = this.primary.hybridEncrypt.encrypt(plaintext, contextInfo);
               this.encLogger.log(this.primary.id, plaintext.length);
               return output;
            } catch (GeneralSecurityException var4) {
               this.encLogger.logFailure();
               throw var4;
            }
         }
      }
   }
}
