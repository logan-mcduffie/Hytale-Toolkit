package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2ByteFunction implements Short2ByteFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected byte defRetValue;

   protected AbstractShort2ByteFunction() {
   }

   @Override
   public void defaultReturnValue(byte rv) {
      this.defRetValue = rv;
   }

   @Override
   public byte defaultReturnValue() {
      return this.defRetValue;
   }
}
