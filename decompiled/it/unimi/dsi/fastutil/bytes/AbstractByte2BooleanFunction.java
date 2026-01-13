package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

public abstract class AbstractByte2BooleanFunction implements Byte2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractByte2BooleanFunction() {
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
