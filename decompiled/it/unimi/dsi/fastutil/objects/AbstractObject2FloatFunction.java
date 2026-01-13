package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2FloatFunction<K> implements Object2FloatFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractObject2FloatFunction() {
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
