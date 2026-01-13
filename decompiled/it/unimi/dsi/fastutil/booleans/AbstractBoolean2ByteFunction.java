package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;

public abstract class AbstractBoolean2ByteFunction implements Boolean2ByteFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected byte defRetValue;

   protected AbstractBoolean2ByteFunction() {
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
