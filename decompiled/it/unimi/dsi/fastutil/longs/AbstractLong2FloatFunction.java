package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2FloatFunction implements Long2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractLong2FloatFunction() {
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
