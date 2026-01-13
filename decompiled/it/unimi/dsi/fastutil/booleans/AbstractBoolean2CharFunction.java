package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2CharFunction implements Boolean2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractBoolean2CharFunction() {
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
