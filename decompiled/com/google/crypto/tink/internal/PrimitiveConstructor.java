package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import java.security.GeneralSecurityException;

public abstract class PrimitiveConstructor<KeyT extends Key, PrimitiveT> {
   private final Class<KeyT> keyClass;
   private final Class<PrimitiveT> primitiveClass;

   private PrimitiveConstructor(Class<KeyT> keyClass, Class<PrimitiveT> primitiveClass) {
      this.keyClass = keyClass;
      this.primitiveClass = primitiveClass;
   }

   public abstract PrimitiveT constructPrimitive(KeyT key) throws GeneralSecurityException;

   public Class<KeyT> getKeyClass() {
      return this.keyClass;
   }

   public Class<PrimitiveT> getPrimitiveClass() {
      return this.primitiveClass;
   }

   public static <KeyT extends Key, PrimitiveT> PrimitiveConstructor<KeyT, PrimitiveT> create(
      PrimitiveConstructor.PrimitiveConstructionFunction<KeyT, PrimitiveT> function, Class<KeyT> keyClass, Class<PrimitiveT> primitiveClass
   ) {
      return new PrimitiveConstructor<KeyT, PrimitiveT>(keyClass, primitiveClass) {
         @Override
         public PrimitiveT constructPrimitive(KeyT key) throws GeneralSecurityException {
            return function.constructPrimitive(key);
         }
      };
   }

   public interface PrimitiveConstructionFunction<KeyT extends Key, PrimitiveT> {
      PrimitiveT constructPrimitive(KeyT key) throws GeneralSecurityException;
   }
}
