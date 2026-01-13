package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2IntFunction implements Long2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractLong2IntFunction() {
   }

   @Override
   public void defaultReturnValue(int rv) {
      this.defRetValue = rv;
   }

   @Override
   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
