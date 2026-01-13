package io.netty.util.internal;

import java.util.HashMap;
import java.util.Map;

public abstract class TypeParameterMatcher {
   private static final TypeParameterMatcher NOOP = new TypeParameterMatcher() {
      @Override
      public boolean match(Object msg) {
         return true;
      }
   };

   public static TypeParameterMatcher get(Class<?> parameterType) {
      Map<Class<?>, TypeParameterMatcher> getCache = InternalThreadLocalMap.get().typeParameterMatcherGetCache();
      TypeParameterMatcher matcher = getCache.get(parameterType);
      if (matcher == null) {
         if (parameterType == Object.class) {
            matcher = NOOP;
         } else {
            matcher = new TypeParameterMatcher.ReflectiveMatcher(parameterType);
         }

         getCache.put(parameterType, matcher);
      }

      return matcher;
   }

   public static TypeParameterMatcher find(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
      Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = InternalThreadLocalMap.get().typeParameterMatcherFindCache();
      Class<?> thisClass = object.getClass();
      Map<String, TypeParameterMatcher> map = findCache.get(thisClass);
      if (map == null) {
         map = new HashMap<>();
         findCache.put(thisClass, map);
      }

      TypeParameterMatcher matcher = map.get(typeParamName);
      if (matcher == null) {
         matcher = get(ReflectionUtil.resolveTypeParameter(object, parametrizedSuperclass, typeParamName));
         map.put(typeParamName, matcher);
      }

      return matcher;
   }

   public abstract boolean match(Object var1);

   TypeParameterMatcher() {
   }

   private static final class ReflectiveMatcher extends TypeParameterMatcher {
      private final Class<?> type;

      ReflectiveMatcher(Class<?> type) {
         this.type = type;
      }

      @Override
      public boolean match(Object msg) {
         return this.type.isInstance(msg);
      }
   }
}
