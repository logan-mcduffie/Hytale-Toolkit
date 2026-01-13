package com.google.crypto.tink.internal;

import java.security.GeneralSecurityException;

public interface PrimitiveWrapper<B, P> {
   P wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<B> primitiveFactory) throws GeneralSecurityException;

   Class<P> getPrimitiveClass();

   Class<B> getInputPrimitiveClass();

   public interface PrimitiveFactory<B> {
      B create(KeysetHandleInterface.Entry k) throws GeneralSecurityException;
   }
}
