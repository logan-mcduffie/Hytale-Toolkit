package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2LongFunction implements Double2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractDouble2LongFunction() {
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
