package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;

public abstract class AbstractFloat2LongFunction implements Float2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractFloat2LongFunction() {
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
