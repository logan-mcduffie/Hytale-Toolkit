package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2CharFunction implements Double2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractDouble2CharFunction() {
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
