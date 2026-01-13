package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ObjectFunction<V> implements Long2ObjectFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractLong2ObjectFunction() {
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
