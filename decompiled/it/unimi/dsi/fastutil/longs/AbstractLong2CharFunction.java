package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2CharFunction implements Long2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractLong2CharFunction() {
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
