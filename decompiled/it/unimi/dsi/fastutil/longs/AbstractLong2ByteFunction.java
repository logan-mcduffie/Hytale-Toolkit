package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ByteFunction implements Long2ByteFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected byte defRetValue;

   protected AbstractLong2ByteFunction() {
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
