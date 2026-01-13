package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2ShortFunction implements Boolean2ShortFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected short defRetValue;

   protected AbstractBoolean2ShortFunction() {
   }

   @Override
   public void defaultReturnValue(short rv) {
      this.defRetValue = rv;
   }

   @Override
   public short defaultReturnValue() {
      return this.defRetValue;
   }
}
