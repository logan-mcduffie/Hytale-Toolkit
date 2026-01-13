package joptsimple.internal;

import java.lang.reflect.Method;
import joptsimple.ValueConverter;

class MethodInvokingValueConverter<V> implements ValueConverter<V> {
   private final Method method;
   private final Class<V> clazz;

   MethodInvokingValueConverter(Method method, Class<V> clazz) {
      this.method = method;
      this.clazz = clazz;
   }

   @Override
   public V convert(String value) {
      return this.clazz.cast(Reflection.invoke(this.method, value));
   }

   @Override
   public Class<V> valueType() {
      return this.clazz;
   }

   @Override
   public String valuePattern() {
      return null;
   }
}
