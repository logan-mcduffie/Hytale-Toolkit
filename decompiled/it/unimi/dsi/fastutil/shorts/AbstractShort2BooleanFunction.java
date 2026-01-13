package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2BooleanFunction implements Short2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractShort2BooleanFunction() {
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
