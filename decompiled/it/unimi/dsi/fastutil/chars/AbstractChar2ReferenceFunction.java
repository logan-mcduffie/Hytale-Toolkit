package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2ReferenceFunction<V> implements Char2ReferenceFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractChar2ReferenceFunction() {
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
