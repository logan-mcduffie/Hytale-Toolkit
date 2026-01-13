package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2DoubleFunction implements Long2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractLong2DoubleFunction() {
   }

   @Override
   public void defaultReturnValue(double rv) {
      this.defRetValue = rv;
   }

   @Override
   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
