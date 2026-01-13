package com.google.crypto.tink.internal;

import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;

public abstract class PrimitiveFactory<PrimitiveT, KeyProtoT extends MessageLite> {
   private final Class<PrimitiveT> clazz;

   public PrimitiveFactory(Class<PrimitiveT> clazz) {
      this.clazz = clazz;
   }

   final Class<PrimitiveT> getPrimitiveClass() {
      return this.clazz;
   }

   public abstract PrimitiveT getPrimitive(KeyProtoT key) throws GeneralSecurityException;
}
