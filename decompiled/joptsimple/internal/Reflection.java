package joptsimple.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import joptsimple.ValueConverter;

public final class Reflection {
   private Reflection() {
      throw new UnsupportedOperationException();
   }

   public static <V> ValueConverter<V> findConverter(Class<V> clazz) {
      Class<V> maybeWrapper = Classes.wrapperOf(clazz);
      ValueConverter<V> valueOf = valueOfConverter(maybeWrapper);
      if (valueOf != null) {
         return valueOf;
      } else {
         ValueConverter<V> constructor = constructorConverter(maybeWrapper);
         if (constructor != null) {
            return constructor;
         } else {
            throw new IllegalArgumentException(clazz + " is not a value type");
         }
      }
   }

   private static <V> ValueConverter<V> valueOfConverter(Class<V> clazz) {
      try {
         Method valueOf = clazz.getMethod("valueOf", String.class);
         return meetsConverterRequirements(valueOf, clazz) ? new MethodInvokingValueConverter<>(valueOf, clazz) : null;
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   private static <V> ValueConverter<V> constructorConverter(Class<V> clazz) {
      try {
         return new ConstructorInvokingValueConverter<>(clazz.getConstructor(String.class));
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   public static <T> T instantiate(Constructor<T> constructor, Object... args) {
      try {
         return constructor.newInstance(args);
      } catch (Exception var3) {
         throw reflectionException(var3);
      }
   }

   public static Object invoke(Method method, Object... args) {
      try {
         return method.invoke(null, args);
      } catch (Exception var3) {
         throw reflectionException(var3);
      }
   }

   public static <V> V convertWith(ValueConverter<V> converter, String raw) {
      return (V)(converter == null ? raw : converter.convert(raw));
   }

   private static boolean meetsConverterRequirements(Method method, Class<?> expectedReturnType) {
      int modifiers = method.getModifiers();
      return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && expectedReturnType.equals(method.getReturnType());
   }

   private static RuntimeException reflectionException(Exception ex) {
      if (ex instanceof IllegalArgumentException) {
         return new ReflectionException(ex);
      } else if (ex instanceof InvocationTargetException) {
         return new ReflectionException(ex.getCause());
      } else {
         return (RuntimeException)(ex instanceof RuntimeException ? (RuntimeException)ex : new ReflectionException(ex));
      }
   }
}
