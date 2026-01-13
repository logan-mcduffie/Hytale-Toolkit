package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2BooleanFunction<K> implements Object2BooleanFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractObject2BooleanFunction() {
   }

   @Override
   public void defaultReturnValue(boolean rv) {
      this.defRetValue = rv;
   }

   @Override
   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
