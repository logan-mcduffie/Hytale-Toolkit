package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractReference2ReferenceFunction<K, V> implements Reference2ReferenceFunction<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractReference2ReferenceFunction() {
   }

   @Override
   public void defaultReturnValue(V rv) {
      this.defRetValue = rv;
   }

   @Override
   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
