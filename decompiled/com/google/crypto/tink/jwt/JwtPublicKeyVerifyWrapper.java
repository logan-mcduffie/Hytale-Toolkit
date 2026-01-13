package com.google.crypto.tink.jwt;

import com.google.crypto.tink.KeyStatus;
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
import java.util.ArrayList;
import java.util.List;

class JwtPublicKeyVerifyWrapper implements PrimitiveWrapper<JwtPublicKeyVerify, JwtPublicKeyVerify> {
   private static final JwtPublicKeyVerifyWrapper WRAPPER = new JwtPublicKeyVerifyWrapper();

   public JwtPublicKeyVerify wrap(
      KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<JwtPublicKeyVerify> factory
   ) throws GeneralSecurityException {
      List<JwtPublicKeyVerifyWrapper.JwtPublicKeyVerifyWithId> allVerifiers = new ArrayList<>(keysetHandle.size());

      for (int i = 0; i < keysetHandle.size(); i++) {
         KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            allVerifiers.add(new JwtPublicKeyVerifyWrapper.JwtPublicKeyVerifyWithId(factory.create(entry), entry.getId()));
         }
      }

      MonitoringClient.Logger logger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         logger = client.createLogger(keysetHandle, annotations, "jwtverify", "verify");
      } else {
         logger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      return new JwtPublicKeyVerifyWrapper.WrappedJwtPublicKeyVerify(logger, allVerifiers);
   }

   @Override
   public Class<JwtPublicKeyVerify> getPrimitiveClass() {
      return JwtPublicKeyVerify.class;
   }

   @Override
   public Class<JwtPublicKeyVerify> getInputPrimitiveClass() {
      return JwtPublicKeyVerify.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }

   private static class JwtPublicKeyVerifyWithId {
      final JwtPublicKeyVerify verify;
      final int id;

      JwtPublicKeyVerifyWithId(JwtPublicKeyVerify verify, int id) {
         this.verify = verify;
         this.id = id;
      }
   }

   @Immutable
   private static class WrappedJwtPublicKeyVerify implements JwtPublicKeyVerify {
      private final MonitoringClient.Logger logger;
      private final List<JwtPublicKeyVerifyWrapper.JwtPublicKeyVerifyWithId> allVerifiers;

      public WrappedJwtPublicKeyVerify(MonitoringClient.Logger logger, List<JwtPublicKeyVerifyWrapper.JwtPublicKeyVerifyWithId> allVerifiers) {
         this.logger = logger;
         this.allVerifiers = allVerifiers;
      }

      @Override
      public VerifiedJwt verifyAndDecode(String compact, JwtValidator validator) throws GeneralSecurityException {
         GeneralSecurityException interestingException = null;

         for (JwtPublicKeyVerifyWrapper.JwtPublicKeyVerifyWithId verifier : this.allVerifiers) {
            try {
               VerifiedJwt result = verifier.verify.verifyAndDecode(compact, validator);
               this.logger.log(verifier.id, 1L);
               return result;
            } catch (GeneralSecurityException var7) {
               if (var7 instanceof JwtInvalidException) {
                  interestingException = var7;
               }
            }
         }

         this.logger.logFailure();
         if (interestingException != null) {
            throw interestingException;
         } else {
            throw new GeneralSecurityException("invalid JWT");
         }
      }
   }
}
