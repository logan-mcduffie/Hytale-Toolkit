package org.bson.codecs.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.assertions.Assertions;

final class TypeData<T> implements TypeWithTypeParameters<T> {
   private final Class<T> type;
   private final List<TypeData<?>> typeParameters;
   private static final Map<Class<?>, Class<?>> PRIMITIVE_CLASS_MAP;

   public static <T> TypeData.Builder<T> builder(Class<T> type) {
      return new TypeData.Builder<>(Assertions.notNull("type", type));
   }

   public static TypeData<?> newInstance(Method method) {
      return PropertyReflectionUtils.isGetter(method)
         ? newInstance(method.getGenericReturnType(), method.getReturnType())
         : newInstance(method.getGenericParameterTypes()[0], method.getParameterTypes()[0]);
   }

   public static TypeData<?> newInstance(Field field) {
      return newInstance(field.getGenericType(), field.getType());
   }

   public static <T> TypeData<T> newInstance(Type genericType, Class<T> clazz) {
      TypeData.Builder<T> builder = builder(clazz);
      if (genericType instanceof ParameterizedType) {
         ParameterizedType pType = (ParameterizedType)genericType;

         for (Type argType : pType.getActualTypeArguments()) {
            getNestedTypeData(builder, argType);
         }
      }

      return builder.build();
   }

   private static <T> void getNestedTypeData(TypeData.Builder<T> builder, Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType pType = (ParameterizedType)type;
         TypeData.Builder paramBuilder = builder((Class<T>)pType.getRawType());

         for (Type argType : pType.getActualTypeArguments()) {
            getNestedTypeData(paramBuilder, argType);
         }

         builder.addTypeParameter(paramBuilder.build());
      } else if (type instanceof WildcardType) {
         builder.addTypeParameter(builder((Class<T>)((WildcardType)type).getUpperBounds()[0]).build());
      } else if (type instanceof TypeVariable) {
         builder.addTypeParameter(builder(Object.class).build());
      } else if (type instanceof Class) {
         builder.addTypeParameter(builder((Class<T>)type).build());
      }
   }

   @Override
   public Class<T> getType() {
      return this.type;
   }

   @Override
   public List<TypeData<?>> getTypeParameters() {
      return this.typeParameters;
   }

   @Override
   public String toString() {
      String typeParams = this.typeParameters.isEmpty() ? "" : ", typeParameters=[" + nestedTypeParameters(this.typeParameters) + "]";
      return "TypeData{type=" + this.type.getSimpleName() + typeParams + "}";
   }

   private static String nestedTypeParameters(List<TypeData<?>> typeParameters) {
      StringBuilder builder = new StringBuilder();
      int count = 0;
      int last = typeParameters.size();

      for (TypeData<?> typeParameter : typeParameters) {
         count++;
         builder.append(typeParameter.getType().getSimpleName());
         if (!typeParameter.getTypeParameters().isEmpty()) {
            builder.append(String.format("<%s>", nestedTypeParameters(typeParameter.getTypeParameters())));
         }

         if (count < last) {
            builder.append(", ");
         }
      }

      return builder.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof TypeData)) {
         return false;
      } else {
         TypeData<?> that = (TypeData<?>)o;
         return !this.getType().equals(that.getType()) ? false : this.getTypeParameters().equals(that.getTypeParameters());
      }
   }

   @Override
   public int hashCode() {
      int result = this.getType().hashCode();
      return 31 * result + this.getTypeParameters().hashCode();
   }

   private TypeData(Class<T> type, List<TypeData<?>> typeParameters) {
      this.type = this.boxType(type);
      this.typeParameters = typeParameters;
   }

   boolean isAssignableFrom(Class<?> cls) {
      return this.type.isAssignableFrom(this.boxType(cls));
   }

   private <S> Class<S> boxType(Class<S> clazz) {
      return clazz.isPrimitive() ? (Class)PRIMITIVE_CLASS_MAP.get(clazz) : clazz;
   }

   static {
      Map<Class<?>, Class<?>> map = new HashMap<>();
      map.put(boolean.class, Boolean.class);
      map.put(byte.class, Byte.class);
      map.put(char.class, Character.class);
      map.put(double.class, Double.class);
      map.put(float.class, Float.class);
      map.put(int.class, Integer.class);
      map.put(long.class, Long.class);
      map.put(short.class, Short.class);
      PRIMITIVE_CLASS_MAP = map;
   }

   public static final class Builder<T> {
      private final Class<T> type;
      private final List<TypeData<?>> typeParameters = new ArrayList<>();

      private Builder(Class<T> type) {
         this.type = type;
      }

      public <S> TypeData.Builder<T> addTypeParameter(TypeData<S> typeParameter) {
         this.typeParameters.add(Assertions.notNull("typeParameter", typeParameter));
         return this;
      }

      public TypeData.Builder<T> addTypeParameters(List<TypeData<?>> typeParameters) {
         Assertions.notNull("typeParameters", typeParameters);

         for (TypeData<?> typeParameter : typeParameters) {
            this.addTypeParameter(typeParameter);
         }

         return this;
      }

      public TypeData<T> build() {
         return new TypeData<>(this.type, Collections.unmodifiableList(this.typeParameters));
      }
   }
}
