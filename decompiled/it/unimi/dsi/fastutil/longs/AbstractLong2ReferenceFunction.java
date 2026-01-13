package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ReferenceFunction<V> implements Long2ReferenceFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractLong2ReferenceFunction() {
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
