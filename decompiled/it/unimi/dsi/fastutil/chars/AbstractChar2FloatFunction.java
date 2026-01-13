package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2FloatFunction implements Char2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractChar2FloatFunction() {
   }

   @Override
   public void defaultReturnValue(float rv) {
      this.defRetValue = rv;
   }

   @Override
   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
