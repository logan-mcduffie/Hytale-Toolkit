package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2LongFunction implements Short2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractShort2LongFunction() {
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
