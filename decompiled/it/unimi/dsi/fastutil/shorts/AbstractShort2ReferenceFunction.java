package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2ReferenceFunction<V> implements Short2ReferenceFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractShort2ReferenceFunction() {
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
