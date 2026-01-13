package com.google.crypto.tink.signature;

import com.google.crypto.tink.PublicKeySign;
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
import com.google.crypto.tink.signature.internal.LegacyFullSign;
import java.security.GeneralSecurityException;

public class PublicKeySignWrapper implements PrimitiveWrapper<PublicKeySign, PublicKeySign> {
   private static final PublicKeySignWrapper WRAPPER = new PublicKeySignWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, PublicKeySign> LEGACY_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullSign::create, LegacyProtoKey.class, PublicKeySign.class
   );

   PublicKeySignWrapper() {
   }

   public PublicKeySign wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<PublicKeySign> factory) throws GeneralSecurityException {
      MonitoringClient.Logger logger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         logger = client.createLogger(keysetHandle, annotations, "public_key_sign", "sign");
      } else {
         logger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      return new PublicKeySignWrapper.WrappedPublicKeySign(
         new PublicKeySignWrapper.PublicKeySignWithId(factory.create(keysetHandle.getPrimary()), keysetHandle.getPrimary().getId()), logger
      );
   }

   @Override
   public Class<PublicKeySign> getPrimitiveClass() {
      return PublicKeySign.class;
   }

   @Override
   public Class<PublicKeySign> getInputPrimitiveClass() {
      return PublicKeySign.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class PublicKeySignWithId {
      public final PublicKeySign publicKeySign;
      public final int id;

      public PublicKeySignWithId(PublicKeySign publicKeySign, int id) {
         this.publicKeySign = publicKeySign;
         this.id = id;
      }
   }

   private static class WrappedPublicKeySign implements PublicKeySign {
      private final PublicKeySignWrapper.PublicKeySignWithId primary;
      private final MonitoringClient.Logger logger;

      public WrappedPublicKeySign(PublicKeySignWrapper.PublicKeySignWithId primary, MonitoringClient.Logger logger) {
         this.primary = primary;
         this.logger = logger;
      }

      @Override
      public byte[] sign(final byte[] data) throws GeneralSecurityException {
         try {
            byte[] output = this.primary.publicKeySign.sign(data);
            this.logger.log(this.primary.id, data.length);
            return output;
         } catch (GeneralSecurityException var3) {
            this.logger.logFailure();
            throw var3;
         }
      }
   }
}
