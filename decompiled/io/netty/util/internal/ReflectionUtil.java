package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public final class ReflectionUtil {
   private ReflectionUtil() {
   }

   public static Throwable trySetAccessible(AccessibleObject object, boolean checkAccessible) {
      if (checkAccessible && !PlatformDependent0.isExplicitTryReflectionSetAccessible()) {
         return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
      } else {
         try {
            object.setAccessible(true);
            return null;
         } catch (SecurityException var3) {
            return var3;
         } catch (RuntimeException var4) {
            return handleInaccessibleObjectException(var4);
         }
      }
   }

   private static RuntimeException handleInaccessibleObjectException(RuntimeException e) {
      if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
         return e;
      } else {
         throw e;
      }
   }

   private static Class<?> fail(Class<?> type, String typeParamName) {
      throw new IllegalStateException("cannot determine the type of the type parameter '" + typeParamName + "': " + type);
   }

   public static Class<?> resolveTypeParameter(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
      Class<?> thisClass = object.getClass();
      Class<?> currentClass = thisClass;

      do {
         while (currentClass.getSuperclass() != parametrizedSuperclass) {
            currentClass = currentClass.getSuperclass();
            if (currentClass == null) {
               return fail(thisClass, typeParamName);
            }
         }

         int typeParamIndex = -1;
         TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();

         for (int i = 0; i < typeParams.length; i++) {
            if (typeParamName.equals(typeParams[i].getName())) {
               typeParamIndex = i;
               break;
            }
         }

         if (typeParamIndex < 0) {
            throw new IllegalStateException("unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass);
         }

         Type genericSuperType = currentClass.getGenericSuperclass();
         if (!(genericSuperType instanceof ParameterizedType)) {
            return Object.class;
         }

         Type[] actualTypeParams = ((ParameterizedType)genericSuperType).getActualTypeArguments();
         Type actualTypeParam = actualTypeParams[typeParamIndex];
         if (actualTypeParam instanceof ParameterizedType) {
            actualTypeParam = ((ParameterizedType)actualTypeParam).getRawType();
         }

         if (actualTypeParam instanceof Class) {
            return (Class<?>)actualTypeParam;
         }

         if (actualTypeParam instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)actualTypeParam).getGenericComponentType();
            if (componentType instanceof ParameterizedType) {
               componentType = ((ParameterizedType)componentType).getRawType();
            }

            if (componentType instanceof Class) {
               return Array.newInstance((Class<?>)componentType, 0).getClass();
            }
         }

         if (!(actualTypeParam instanceof TypeVariable)) {
            return fail(thisClass, typeParamName);
         }

         TypeVariable<?> v = (TypeVariable<?>)actualTypeParam;
         if (!(v.getGenericDeclaration() instanceof Class)) {
            return Object.class;
         }

         currentClass = thisClass;
         parametrizedSuperclass = (Class<?>)v.getGenericDeclaration();
         typeParamName = v.getName();
      } while (parametrizedSuperclass.isAssignableFrom(thisClass));

      return Object.class;
   }
}
