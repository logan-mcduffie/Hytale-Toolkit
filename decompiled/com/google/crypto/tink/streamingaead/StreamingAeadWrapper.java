package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.streamingaead.internal.LegacyFullStreamingAead;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class StreamingAeadWrapper implements PrimitiveWrapper<StreamingAead, StreamingAead> {
   private static final StreamingAeadWrapper WRAPPER = new StreamingAeadWrapper();
   private static final PrimitiveConstructor<LegacyProtoKey, StreamingAead> LEGACY_FULL_STREAMING_AEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      LegacyFullStreamingAead::create, LegacyProtoKey.class, StreamingAead.class
   );

   StreamingAeadWrapper() {
   }

   public StreamingAead wrap(KeysetHandleInterface handle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<StreamingAead> factory) throws GeneralSecurityException {
      List<StreamingAead> allStreamingAeads = new ArrayList<>();

      for (int i = 0; i < handle.size(); i++) {
         KeysetHandleInterface.Entry entry = handle.getAt(i);
         if (entry.getStatus().equals(KeyStatus.ENABLED)) {
            StreamingAead streamingAead = factory.create(entry);
            allStreamingAeads.add(streamingAead);
         }
      }

      KeysetHandleInterface.Entry primaryEntry = handle.getPrimary();
      if (primaryEntry == null) {
         throw new GeneralSecurityException("No primary set");
      } else {
         StreamingAead primaryStreamingAead = factory.create(primaryEntry);
         if (primaryStreamingAead == null) {
            throw new GeneralSecurityException("No primary set");
         } else {
            return new StreamingAeadHelper(allStreamingAeads, primaryStreamingAead);
         }
      }
   }

   @Override
   public Class<StreamingAead> getPrimitiveClass() {
      return StreamingAead.class;
   }

   @Override
   public Class<StreamingAead> getInputPrimitiveClass() {
      return StreamingAead.class;
   }

   public static void register() throws GeneralSecurityException {
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
      MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_STREAMING_AEAD_PRIMITIVE_CONSTRUCTOR);
   }

   public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
      primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
   }
}
