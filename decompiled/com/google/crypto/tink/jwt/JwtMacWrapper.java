package com.google.crypto.tink.jwt;

import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

class JwtMacWrapper implements PrimitiveWrapper<JwtMac, JwtMac> {
   private static final JwtMacWrapper WRAPPER = new JwtMacWrapper();

   private static void validate(KeysetHandleInterface keysetHandle) throws GeneralSecurityException {
      if (keysetHandle.getPrimary() == null) {
         throw new GeneralSecurityException("Primitive set has no primary.");
      }
   }

   public JwtMac wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<JwtMac> factory) throws GeneralSecurityException {
      validate(keysetHandle);
      List<JwtMacWrapper.JwtMacWithId> allMacs = new ArrayList<>(keysetHandle.size());

      for (int i = 0; i < keysetHandle.size(); i++) {
         KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            JwtMac jwtMac = factory.create(entry);
            allMacs.add(new JwtMacWrapper.JwtMacWithId(jwtMac, entry.getId()));
         }
      }

      MonitoringClient.Logger computeLogger;
      MonitoringClient.Logger verifyLogger;
      if (!annotations.isEmpty()) {
         MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
         computeLogger = client.createLogger(keysetHandle, annotations, "jwtmac", "compute");
         verifyLogger = client.createLogger(keysetHandle, annotations, "jwtmac", "verify");
      } else {
         computeLogger = MonitoringUtil.DO_NOTHING_LOGGER;
         verifyLogger = MonitoringUtil.DO_NOTHING_LOGGER;
      }

      JwtMac primaryMac = factory.create(keysetHandle.getPrimary());
      return new JwtMacWrapper.WrappedJwtMac(
         new JwtMacWrapper.JwtMacWithId(primaryMac, keysetHandle.getPrimary().getId()), allMacs, computeLogger, verifyLogger
      );
   }

   @Override
   public Class<JwtMac> getPrimitiveClass() {
      return JwtMac.class;
   }

   @Override
   public Class<JwtMac> getInputPrimitiveClass() {
      return JwtMac.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
   }

   private static class JwtMacWithId {
      final JwtMac jwtMac;
      final int id;

      JwtMacWithId(JwtMac jwtMac, int id) {
         this.jwtMac = jwtMac;
         this.id = id;
      }
   }

   @Immutable
   private static class WrappedJwtMac implements JwtMac {
      private final JwtMacWrapper.JwtMacWithId primary;
      private final List<JwtMacWrapper.JwtMacWithId> allMacs;
      private final MonitoringClient.Logger computeLogger;
      private final MonitoringClient.Logger verifyLogger;

      private WrappedJwtMac(
         JwtMacWrapper.JwtMacWithId primary,
         List<JwtMacWrapper.JwtMacWithId> allMacs,
         MonitoringClient.Logger computeLogger,
         MonitoringClient.Logger verifyLogger
      ) {
         this.primary = primary;
         this.allMacs = allMacs;
         this.computeLogger = computeLogger;
         this.verifyLogger = verifyLogger;
      }

      @Override
      public String computeMacAndEncode(RawJwt token) throws GeneralSecurityException {
         try {
            String result = this.primary.jwtMac.computeMacAndEncode(token);
            this.computeLogger.log(this.primary.id, 1L);
            return result;
         } catch (GeneralSecurityException var3) {
            this.computeLogger.logFailure();
            throw var3;
         }
      }

      @Override
      public VerifiedJwt verifyMacAndDecode(String compact, JwtValidator validator) throws GeneralSecurityException {
         GeneralSecurityException interestingException = null;

         for (JwtMacWrapper.JwtMacWithId macAndId : this.allMacs) {
            try {
               VerifiedJwt result = macAndId.jwtMac.verifyMacAndDecode(compact, validator);
               this.verifyLogger.log(macAndId.id, 1L);
               return result;
            } catch (GeneralSecurityException var7) {
               if (var7 instanceof JwtInvalidException) {
                  interestingException = var7;
               }
            }
         }

         this.verifyLogger.logFailure();
         if (interestingException != null) {
            throw interestingException;
         } else {
            throw new GeneralSecurityException("invalid MAC");
         }
      }
   }
}
