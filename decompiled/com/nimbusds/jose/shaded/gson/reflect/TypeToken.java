package com.nimbusds.jose.shaded.gson.reflect;

import com.nimbusds.jose.shaded.gson.internal.GsonTypes;
import com.nimbusds.jose.shaded.gson.internal.TroubleshootingGuide;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeToken<T> {
   private final Class<? super T> rawType;
   private final Type type;
   private final int hashCode;

   protected TypeToken() {
      this.type = this.getTypeTokenTypeArgument();
      this.rawType = (Class<? super T>)GsonTypes.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private TypeToken(Type type) {
      this.type = GsonTypes.canonicalize(Objects.requireNonNull(type));
      this.rawType = (Class<? super T>)GsonTypes.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private static boolean isCapturingTypeVariablesForbidden() {
      return !Objects.equals(System.getProperty("gson.allowCapturingTypeVariables"), "true");
   }

   private Type getTypeTokenTypeArgument() {
      Type superclass = this.getClass().getGenericSuperclass();
      if (superclass instanceof ParameterizedType) {
         ParameterizedType parameterized = (ParameterizedType)superclass;
         if (parameterized.getRawType() == TypeToken.class) {
            Type typeArgument = GsonTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
            if (isCapturingTypeVariablesForbidden()) {
               verifyNoTypeVariable(typeArgument);
            }

            return typeArgument;
         }
      } else if (superclass == TypeToken.class) {
         throw new IllegalStateException(
            "TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.\nSee "
               + TroubleshootingGuide.createUrl("type-token-raw")
         );
      }

      throw new IllegalStateException("Must only create direct subclasses of TypeToken");
   }

   private static void verifyNoTypeVariable(Type type) {
      if (type instanceof TypeVariable) {
         TypeVariable<?> typeVariable = (TypeVariable<?>)type;
         throw new IllegalArgumentException(
            "TypeToken type argument must not contain a type variable; captured type variable "
               + typeVariable.getName()
               + " declared by "
               + typeVariable.getGenericDeclaration()
               + "\nSee "
               + TroubleshootingGuide.createUrl("typetoken-type-variable")
         );
      } else {
         if (type instanceof GenericArrayType) {
            verifyNoTypeVariable(((GenericArrayType)type).getGenericComponentType());
         } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
               verifyNoTypeVariable(ownerType);
            }

            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
               verifyNoTypeVariable(typeArgument);
            }
         } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;

            for (Type bound : wildcardType.getLowerBounds()) {
               verifyNoTypeVariable(bound);
            }

            for (Type bound : wildcardType.getUpperBounds()) {
               verifyNoTypeVariable(bound);
            }
         } else if (type == null) {
            throw new IllegalArgumentException("TypeToken captured `null` as type argument; probably a compiler / runtime bug");
         }
      }
   }

   public final Class<? super T> getRawType() {
      return this.rawType;
   }

   public final Type getType() {
      return this.type;
   }

   @Deprecated
   public boolean isAssignableFrom(Class<?> cls) {
      return this.isAssignableFrom(cls);
   }

   @Deprecated
   public boolean isAssignableFrom(Type from) {
      if (from == null) {
         return false;
      } else if (this.type.equals(from)) {
         return true;
      } else if (this.type instanceof Class) {
         return this.rawType.isAssignableFrom(GsonTypes.getRawType(from));
      } else if (this.type instanceof ParameterizedType) {
         return isAssignableFrom(from, (ParameterizedType)this.type, new HashMap<>());
      } else if (!(this.type instanceof GenericArrayType)) {
         throw buildUnsupportedTypeException(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
      } else {
         return this.rawType.isAssignableFrom(GsonTypes.getRawType(from)) && isAssignableFrom(from, (GenericArrayType)this.type);
      }
   }

   @Deprecated
   public boolean isAssignableFrom(TypeToken<?> token) {
      return this.isAssignableFrom(token.getType());
   }

   private static boolean isAssignableFrom(Type from, GenericArrayType to) {
      Type toGenericComponentType = to.getGenericComponentType();
      if (!(toGenericComponentType instanceof ParameterizedType)) {
         return true;
      } else {
         Type t = from;
         if (from instanceof GenericArrayType) {
            t = ((GenericArrayType)from).getGenericComponentType();
         } else if (from instanceof Class) {
            Class<?> classType = (Class<?>)from;

            while (classType.isArray()) {
               classType = classType.getComponentType();
            }

            t = classType;
         }

         return isAssignableFrom(t, (ParameterizedType)toGenericComponentType, new HashMap<>());
      }
   }

   private static boolean isAssignableFrom(Type from, ParameterizedType to, Map<String, Type> typeVarMap) {
      if (from == null) {
         return false;
      } else if (to.equals(from)) {
         return true;
      } else {
         Class<?> clazz = GsonTypes.getRawType(from);
         ParameterizedType ptype = null;
         if (from instanceof ParameterizedType) {
            ptype = (ParameterizedType)from;
         }

         if (ptype != null) {
            Type[] tArgs = ptype.getActualTypeArguments();
            TypeVariable<?>[] tParams = clazz.getTypeParameters();

            for (int i = 0; i < tArgs.length; i++) {
               Type arg = tArgs[i];
               TypeVariable<?> var = tParams[i];

               while (arg instanceof TypeVariable) {
                  TypeVariable<?> v = (TypeVariable<?>)arg;
                  arg = typeVarMap.get(v.getName());
               }

               typeVarMap.put(var.getName(), arg);
            }

            if (typeEquals(ptype, to, typeVarMap)) {
               return true;
            }
         }

         for (Type itype : clazz.getGenericInterfaces()) {
            if (isAssignableFrom(itype, to, new HashMap<>(typeVarMap))) {
               return true;
            }
         }

         Type sType = clazz.getGenericSuperclass();
         return isAssignableFrom(sType, to, new HashMap<>(typeVarMap));
      }
   }

   private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map<String, Type> typeVarMap) {
      if (from.getRawType().equals(to.getRawType())) {
         Type[] fromArgs = from.getActualTypeArguments();
         Type[] toArgs = to.getActualTypeArguments();

         for (int i = 0; i < fromArgs.length; i++) {
            if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static IllegalArgumentException buildUnsupportedTypeException(Type token, Class<?>... expected) {
      StringBuilder exceptionMessage = new StringBuilder("Unsupported type, expected one of: ");

      for (Class<?> clazz : expected) {
         exceptionMessage.append(clazz.getName()).append(", ");
      }

      exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ").append(token.toString());
      return new IllegalArgumentException(exceptionMessage.toString());
   }

   private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
      return to.equals(from) || from instanceof TypeVariable && to.equals(typeMap.get(((TypeVariable)from).getName()));
   }

   @Override
   public final int hashCode() {
      return this.hashCode;
   }

   @Override
   public final boolean equals(Object o) {
      return o instanceof TypeToken && GsonTypes.equals(this.type, ((TypeToken)o).type);
   }

   @Override
   public final String toString() {
      return GsonTypes.typeToString(this.type);
   }

   public static TypeToken<?> get(Type type) {
      return new TypeToken(type);
   }

   public static <T> TypeToken<T> get(Class<T> type) {
      return new TypeToken<>(type);
   }

   public static TypeToken<?> getParameterized(Type rawType, Type... typeArguments) {
      Objects.requireNonNull(rawType);
      Objects.requireNonNull(typeArguments);
      if (!(rawType instanceof Class)) {
         throw new IllegalArgumentException("rawType must be of type Class, but was " + rawType);
      } else {
         Class<?> rawClass = (Class<?>)rawType;
         TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();
         int expectedArgsCount = typeVariables.length;
         int actualArgsCount = typeArguments.length;
         if (actualArgsCount != expectedArgsCount) {
            throw new IllegalArgumentException(rawClass.getName() + " requires " + expectedArgsCount + " type arguments, but got " + actualArgsCount);
         } else if (typeArguments.length == 0) {
            return get(rawClass);
         } else if (GsonTypes.requiresOwnerType(rawType)) {
            throw new IllegalArgumentException("Raw type " + rawClass.getName() + " is not supported because it requires specifying an owner type");
         } else {
            for (int i = 0; i < expectedArgsCount; i++) {
               Type typeArgument = Objects.requireNonNull(typeArguments[i], "Type argument must not be null");
               Class<?> rawTypeArgument = GsonTypes.getRawType(typeArgument);
               TypeVariable<?> typeVariable = typeVariables[i];

               for (Type bound : typeVariable.getBounds()) {
                  Class<?> rawBound = GsonTypes.getRawType(bound);
                  if (!rawBound.isAssignableFrom(rawTypeArgument)) {
                     throw new IllegalArgumentException(
                        "Type argument " + typeArgument + " does not satisfy bounds for type variable " + typeVariable + " declared by " + rawType
                     );
                  }
               }
            }

            return new TypeToken(GsonTypes.newParameterizedTypeWithOwner(null, rawClass, typeArguments));
         }
      }
   }

   public static TypeToken<?> getArray(Type componentType) {
      return new TypeToken(GsonTypes.arrayOf(componentType));
   }
}
