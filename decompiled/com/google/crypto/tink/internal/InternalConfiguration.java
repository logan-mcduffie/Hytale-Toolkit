package com.google.crypto.tink.internal;

import com.google.crypto.tink.Configuration;
import java.security.GeneralSecurityException;

public abstract class InternalConfiguration extends Configuration {
   public abstract <P> P wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<P> clazz) throws GeneralSecurityException;

   public static InternalConfiguration createFromPrimitiveRegistry(PrimitiveRegistry registry) {
      return new InternalConfiguration.InternalConfigurationImpl(registry);
   }

   private static class InternalConfigurationImpl extends InternalConfiguration {
      private final PrimitiveRegistry registry;

      private InternalConfigurationImpl(PrimitiveRegistry registry) {
         this.registry = registry;
      }

      @Override
      public <P> P wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<P> clazz) throws GeneralSecurityException {
         return this.registry.wrap(keysetHandle, annotations, clazz);
      }
   }
}
