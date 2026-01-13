package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2IntFunction implements Boolean2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractBoolean2IntFunction() {
   }

   @Override
   public void defaultReturnValue(int rv) {
      this.defRetValue = rv;
   }

   @Override
   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
