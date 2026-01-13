package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2IntFunction implements Char2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractChar2IntFunction() {
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
