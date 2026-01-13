package com.google.crypto.tink.internal;

import com.google.errorprone.annotations.DoNotCall;
import java.security.GeneralSecurityException;

public final class RegistryConfiguration extends InternalConfiguration {
   private static final RegistryConfiguration CONFIG = new RegistryConfiguration();

   public static RegistryConfiguration get() {
      return CONFIG;
   }

   private RegistryConfiguration() {
   }

   @Override
   public <P> P wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<P> clazz) throws GeneralSecurityException {
      return MutablePrimitiveRegistry.globalInstance().wrap(keysetHandle, annotations, clazz);
   }

   @DoNotCall
   public static InternalConfiguration createFromPrimitiveRegistry(PrimitiveRegistry registry) {
      throw new UnsupportedOperationException("Cannot create RegistryConfiguration from a PrimitiveRegistry");
   }
}
