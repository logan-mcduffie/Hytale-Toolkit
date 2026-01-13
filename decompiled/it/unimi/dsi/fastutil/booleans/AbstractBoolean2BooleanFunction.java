package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2BooleanFunction implements Boolean2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractBoolean2BooleanFunction() {
   }

   @Override
   public void defaultReturnValue(boolean rv) {
      this.defRetValue = rv;
   }

   @Override
   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
