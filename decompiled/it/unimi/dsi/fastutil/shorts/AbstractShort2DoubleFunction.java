package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2DoubleFunction implements Short2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractShort2DoubleFunction() {
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
