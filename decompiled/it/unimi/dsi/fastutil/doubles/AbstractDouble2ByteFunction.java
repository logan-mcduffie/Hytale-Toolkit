package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2ByteFunction implements Double2ByteFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected byte defRetValue;

   protected AbstractDouble2ByteFunction() {
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
