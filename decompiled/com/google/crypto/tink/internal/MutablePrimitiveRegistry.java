package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

public final class MutablePrimitiveRegistry {
   private static MutablePrimitiveRegistry globalInstance = new MutablePrimitiveRegistry();
   private final AtomicReference<PrimitiveRegistry> registry = new AtomicReference<>(PrimitiveRegistry.builder().build());

   public static MutablePrimitiveRegistry globalInstance() {
      return globalInstance;
   }

   public static void resetGlobalInstanceTestOnly() {
      globalInstance = new MutablePrimitiveRegistry();
   }

   MutablePrimitiveRegistry() {
   }

   public synchronized <KeyT extends Key, PrimitiveT> void registerPrimitiveConstructor(PrimitiveConstructor<KeyT, PrimitiveT> constructor) throws GeneralSecurityException {
      PrimitiveRegistry newRegistry = PrimitiveRegistry.builder(this.registry.get()).registerPrimitiveConstructor(constructor).build();
      this.registry.set(newRegistry);
   }

   public synchronized <InputPrimitiveT, WrapperPrimitiveT> void registerPrimitiveWrapper(PrimitiveWrapper<InputPrimitiveT, WrapperPrimitiveT> wrapper) throws GeneralSecurityException {
      PrimitiveRegistry newRegistry = PrimitiveRegistry.builder(this.registry.get()).registerPrimitiveWrapper(wrapper).build();
      this.registry.set(newRegistry);
   }

   public <KeyT extends Key, PrimitiveT> PrimitiveT getPrimitive(KeyT key, Class<PrimitiveT> primitiveClass) throws GeneralSecurityException {
      return this.registry.get().getPrimitive(key, primitiveClass);
   }

   public <WrapperPrimitiveT> WrapperPrimitiveT wrap(
      KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<WrapperPrimitiveT> wrapperClassObject
   ) throws GeneralSecurityException {
      return this.registry.get().wrap(keysetHandle, annotations, wrapperClassObject);
   }
}
