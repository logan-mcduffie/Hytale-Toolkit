package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractReference2CharFunction<K> implements Reference2CharFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractReference2CharFunction() {
   }

   @Override
   public void defaultReturnValue(char rv) {
      this.defRetValue = rv;
   }

   @Override
   public char defaultReturnValue() {
      return this.defRetValue;
   }
}
