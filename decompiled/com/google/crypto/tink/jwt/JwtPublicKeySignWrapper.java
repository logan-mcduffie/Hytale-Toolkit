package com.google.crypto.tink.jwt;

import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

class JwtPublicKeySignWrapper implements PrimitiveWrapper<JwtPublicKeySign, JwtPublicKeySign> {
   private static final JwtPublicKeySignWrapper WRAPPER = new JwtPublicKeySignWrapper();

   public JwtPublicKeySign wrap(
      KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<JwtPublicKeySign> factory
   ) throws GeneralSecurityException {
      return new JwtPublicKeySignWrapper.WrappedJwtPublicKeySign(keysetHandle, annotations, factory);
   }

   @Override
   public Class<JwtPublicKeySign> getPrimitiveClass() {
      return JwtPublicKeySign.class;
   }

   @Override
   public Class<JwtPublicKeySign> getInputPrimitiveClass() {
      return JwtPublicKeySign.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   @Immutable
   private static class WrappedJwtPublicKeySign implements JwtPublicKeySign {
      private final JwtPublicKeySign primary;
      private final int primaryKeyId;
      private final MonitoringClient.Logger logger;

      public WrappedJwtPublicKeySign(
         KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<JwtPublicKeySign> factory
      ) throws GeneralSecurityException {
         this.primary = factory.create(keysetHandle.getPrimary());
         this.primaryKeyId = keysetHandle.getPrimary().getId();
         if (!annotations.isEmpty()) {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            this.logger = client.createLogger(keysetHandle, annotations, "jwtsign", "sign");
         } else {
            this.logger = MonitoringUtil.DO_NOTHING_LOGGER;
         }
      }

      @Override
      public String signAndEncode(RawJwt token) throws GeneralSecurityException {
         try {
            String output = this.primary.signAndEncode(token);
            this.logger.log(this.primaryKeyId, 1L);
            return output;
         } catch (GeneralSecurityException var3) {
            this.logger.logFailure();
            throw var3;
         }
      }
   }
}
