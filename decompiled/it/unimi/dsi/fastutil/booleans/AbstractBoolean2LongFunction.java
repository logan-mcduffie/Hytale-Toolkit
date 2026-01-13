package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2LongFunction implements Boolean2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractBoolean2LongFunction() {
   }

   @Override
   public void defaultReturnValue(long rv) {
      this.defRetValue = rv;
   }

   @Override
   public long defaultReturnValue() {
      return this.defRetValue;
   }
}
