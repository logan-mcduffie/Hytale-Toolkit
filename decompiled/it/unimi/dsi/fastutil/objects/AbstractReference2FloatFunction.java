package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractReference2FloatFunction<K> implements Reference2FloatFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractReference2FloatFunction() {
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
