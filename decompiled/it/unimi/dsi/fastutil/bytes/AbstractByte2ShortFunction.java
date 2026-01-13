package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

public abstract class AbstractByte2ShortFunction implements Byte2ShortFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected short defRetValue;

   protected AbstractByte2ShortFunction() {
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
