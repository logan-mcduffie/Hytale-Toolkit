package com.google.protobuf;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;

public enum FieldType {
   DOUBLE(0, FieldType.Collection.SCALAR, JavaType.DOUBLE),
   FLOAT(1, FieldType.Collection.SCALAR, JavaType.FLOAT),
   INT64(2, FieldType.Collection.SCALAR, JavaType.LONG),
   UINT64(3, FieldType.Collection.SCALAR, JavaType.LONG),
   INT32(4, FieldType.Collection.SCALAR, JavaType.INT),
   FIXED64(5, FieldType.Collection.SCALAR, JavaType.LONG),
   FIXED32(6, FieldType.Collection.SCALAR, JavaType.INT),
   BOOL(7, FieldType.Collection.SCALAR, JavaType.BOOLEAN),
   STRING(8, FieldType.Collection.SCALAR, JavaType.STRING),
   MESSAGE(9, FieldType.Collection.SCALAR, JavaType.MESSAGE),
   BYTES(10, FieldType.Collection.SCALAR, JavaType.BYTE_STRING),
   UINT32(11, FieldType.Collection.SCALAR, JavaType.INT),
   ENUM(12, FieldType.Collection.SCALAR, JavaType.ENUM),
   SFIXED32(13, FieldType.Collection.SCALAR, JavaType.INT),
   SFIXED64(14, FieldType.Collection.SCALAR, JavaType.LONG),
   SINT32(15, FieldType.Collection.SCALAR, JavaType.INT),
   SINT64(16, FieldType.Collection.SCALAR, JavaType.LONG),
   GROUP(17, FieldType.Collection.SCALAR, JavaType.MESSAGE),
   DOUBLE_LIST(18, FieldType.Collection.VECTOR, JavaType.DOUBLE),
   FLOAT_LIST(19, FieldType.Collection.VECTOR, JavaType.FLOAT),
   INT64_LIST(20, FieldType.Collection.VECTOR, JavaType.LONG),
   UINT64_LIST(21, FieldType.Collection.VECTOR, JavaType.LONG),
   INT32_LIST(22, FieldType.Collection.VECTOR, JavaType.INT),
   FIXED64_LIST(23, FieldType.Collection.VECTOR, JavaType.LONG),
   FIXED32_LIST(24, FieldType.Collection.VECTOR, JavaType.INT),
   BOOL_LIST(25, FieldType.Collection.VECTOR, JavaType.BOOLEAN),
   STRING_LIST(26, FieldType.Collection.VECTOR, JavaType.STRING),
   MESSAGE_LIST(27, FieldType.Collection.VECTOR, JavaType.MESSAGE),
   BYTES_LIST(28, FieldType.Collection.VECTOR, JavaType.BYTE_STRING),
   UINT32_LIST(29, FieldType.Collection.VECTOR, JavaType.INT),
   ENUM_LIST(30, FieldType.Collection.VECTOR, JavaType.ENUM),
   SFIXED32_LIST(31, FieldType.Collection.VECTOR, JavaType.INT),
   SFIXED64_LIST(32, FieldType.Collection.VECTOR, JavaType.LONG),
   SINT32_LIST(33, FieldType.Collection.VECTOR, JavaType.INT),
   SINT64_LIST(34, FieldType.Collection.VECTOR, JavaType.LONG),
   DOUBLE_LIST_PACKED(35, FieldType.Collection.PACKED_VECTOR, JavaType.DOUBLE),
   FLOAT_LIST_PACKED(36, FieldType.Collection.PACKED_VECTOR, JavaType.FLOAT),
   INT64_LIST_PACKED(37, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   UINT64_LIST_PACKED(38, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   INT32_LIST_PACKED(39, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   FIXED64_LIST_PACKED(40, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   FIXED32_LIST_PACKED(41, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   BOOL_LIST_PACKED(42, FieldType.Collection.PACKED_VECTOR, JavaType.BOOLEAN),
   UINT32_LIST_PACKED(43, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   ENUM_LIST_PACKED(44, FieldType.Collection.PACKED_VECTOR, JavaType.ENUM),
   SFIXED32_LIST_PACKED(45, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   SFIXED64_LIST_PACKED(46, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   SINT32_LIST_PACKED(47, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   SINT64_LIST_PACKED(48, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   GROUP_LIST(49, FieldType.Collection.VECTOR, JavaType.MESSAGE),
   MAP(50, FieldType.Collection.MAP, JavaType.VOID);

   private final JavaType javaType;
   private final int id;
   private final FieldType.Collection collection;
   private final Class<?> elementType;
   private final boolean primitiveScalar;
   private static final FieldType[] VALUES;
   private static final java.lang.reflect.Type[] EMPTY_TYPES = new java.lang.reflect.Type[0];

   private FieldType(int id, FieldType.Collection collection, JavaType javaType) {
      this.id = id;
      this.collection = collection;
      this.javaType = javaType;
      switch (collection) {
         case SCALAR:
         case PACKED_VECTOR:
         default:
            this.elementType = null;
            break;
         case VECTOR:
            this.elementType = javaType.getBoxedType();
            break;
         case MAP:
            this.elementType = javaType.getBoxedType();
      }

      boolean primitiveScalar = false;
      if (collection == FieldType.Collection.SCALAR) {
         switch (javaType) {
            case BYTE_STRING:
            case MESSAGE:
            case STRING:
               break;
            default:
               primitiveScalar = true;
         }
      }

      this.primitiveScalar = primitiveScalar;
   }

   public int id() {
      return this.id;
   }

   public JavaType getJavaType() {
      return this.javaType;
   }

   public boolean isPacked() {
      return FieldType.Collection.PACKED_VECTOR.equals(this.collection);
   }

   public boolean isPrimitiveScalar() {
      return this.primitiveScalar;
   }

   public boolean isScalar() {
      return this.collection == FieldType.Collection.SCALAR;
   }

   public boolean isList() {
      return this.collection.isList();
   }

   public boolean isMap() {
      return this.collection == FieldType.Collection.MAP;
   }

   public boolean isValidForField(java.lang.reflect.Field field) {
      return FieldType.Collection.VECTOR.equals(this.collection) ? this.isValidForList(field) : this.javaType.getType().isAssignableFrom(field.getType());
   }

   private boolean isValidForList(java.lang.reflect.Field field) {
      Class<?> clazz = field.getType();
      if (!this.javaType.getType().isAssignableFrom(clazz)) {
         return false;
      } else {
         java.lang.reflect.Type[] types = EMPTY_TYPES;
         java.lang.reflect.Type genericType = field.getGenericType();
         if (genericType instanceof ParameterizedType) {
            types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
         }

         java.lang.reflect.Type listParameter = getListParameter(clazz, types);
         return !(listParameter instanceof Class) ? true : this.elementType.isAssignableFrom((Class<?>)listParameter);
      }
   }

   public static FieldType forId(int id) {
      return id >= 0 && id < VALUES.length ? VALUES[id] : null;
   }

   private static java.lang.reflect.Type getGenericSuperList(Class<?> clazz) {
      java.lang.reflect.Type[] genericInterfaces = clazz.getGenericInterfaces();

      for (java.lang.reflect.Type genericInterface : genericInterfaces) {
         if (genericInterface instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
            Class<?> rawType = (Class<?>)parameterizedType.getRawType();
            if (List.class.isAssignableFrom(rawType)) {
               return genericInterface;
            }
         }
      }

      java.lang.reflect.Type type = clazz.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         Class<?> rawType = (Class<?>)parameterizedType.getRawType();
         if (List.class.isAssignableFrom(rawType)) {
            return type;
         }
      }

      return null;
   }

   private static java.lang.reflect.Type getListParameter(Class<?> clazz, java.lang.reflect.Type[] realTypes) {
      label60:
      while (clazz != List.class) {
         java.lang.reflect.Type genericType = getGenericSuperList(clazz);
         if (!(genericType instanceof ParameterizedType)) {
            realTypes = EMPTY_TYPES;

            for (Class<?> iface : clazz.getInterfaces()) {
               if (List.class.isAssignableFrom(iface)) {
                  clazz = iface;
                  continue label60;
               }
            }

            clazz = clazz.getSuperclass();
         } else {
            ParameterizedType parameterizedType = (ParameterizedType)genericType;
            java.lang.reflect.Type[] superArgs = parameterizedType.getActualTypeArguments();

            for (int i = 0; i < superArgs.length; i++) {
               java.lang.reflect.Type superArg = superArgs[i];
               if (superArg instanceof TypeVariable) {
                  TypeVariable<?>[] clazzParams = clazz.getTypeParameters();
                  if (realTypes.length != clazzParams.length) {
                     throw new RuntimeException("Type array mismatch");
                  }

                  boolean foundReplacement = false;

                  for (int j = 0; j < clazzParams.length; j++) {
                     if (superArg == clazzParams[j]) {
                        java.lang.reflect.Type realType = realTypes[j];
                        superArgs[i] = realType;
                        foundReplacement = true;
                        break;
                     }
                  }

                  if (!foundReplacement) {
                     throw new RuntimeException("Unable to find replacement for " + superArg);
                  }
               }
            }

            Class<?> parent = (Class<?>)parameterizedType.getRawType();
            realTypes = superArgs;
            clazz = parent;
         }
      }

      if (realTypes.length != 1) {
         throw new RuntimeException("Unable to identify parameter type for List<T>");
      } else {
         return realTypes[0];
      }
   }

   static {
      FieldType[] values = values();
      VALUES = new FieldType[values.length];

      for (FieldType type : values) {
         VALUES[type.id] = type;
      }
   }

   static enum Collection {
      SCALAR(false),
      VECTOR(true),
      PACKED_VECTOR(true),
      MAP(false);

      private final boolean isList;

      private Collection(boolean isList) {
         this.isList = isList;
      }

      public boolean isList() {
         return this.isList;
      }
   }
}
