package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2FloatFunction implements Double2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractDouble2FloatFunction() {
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
