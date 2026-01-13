package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bson.assertions.Assertions;

final class PojoBuilderHelper {
   static <T> void configureClassModelBuilder(ClassModelBuilder<T> classModelBuilder, Class<T> clazz) {
      classModelBuilder.type(Assertions.notNull("clazz", clazz));
      ArrayList<Annotation> annotations = new ArrayList<>();
      Set<String> propertyNames = new TreeSet<>();
      Map<String, TypeParameterMap> propertyTypeParameterMap = new HashMap<>();
      Class<? super T> currentClass = clazz;
      String declaringClassName = clazz.getSimpleName();
      TypeData<?> parentClassTypeData = null;

      Map<String, PropertyMetadata<?>> propertyNameMap;
      for (propertyNameMap = new HashMap<>(); !currentClass.isEnum() && currentClass.getSuperclass() != null; currentClass = currentClass.getSuperclass()) {
         annotations.addAll(Arrays.asList(currentClass.getDeclaredAnnotations()));
         List<String> genericTypeNames = new ArrayList<>();

         for (TypeVariable<? extends Class<? super T>> classTypeVariable : currentClass.getTypeParameters()) {
            genericTypeNames.add(classTypeVariable.getName());
         }

         PropertyReflectionUtils.PropertyMethods propertyMethods = PropertyReflectionUtils.getPropertyMethods(currentClass);

         for (Method method : propertyMethods.getSetterMethods()) {
            String propertyName = PropertyReflectionUtils.toPropertyName(method);
            propertyNames.add(propertyName);
            PropertyMetadata<?> propertyMetadata = getOrCreateMethodPropertyMetadata(
               propertyName,
               declaringClassName,
               propertyNameMap,
               TypeData.newInstance(method),
               propertyTypeParameterMap,
               parentClassTypeData,
               genericTypeNames,
               getGenericType(method)
            );
            if (propertyMetadata.getSetter() == null) {
               propertyMetadata.setSetter(method);

               for (Annotation annotation : method.getDeclaredAnnotations()) {
                  propertyMetadata.addWriteAnnotation(annotation);
               }
            }
         }

         for (Method methodx : propertyMethods.getGetterMethods()) {
            String propertyName = PropertyReflectionUtils.toPropertyName(methodx);
            propertyNames.add(propertyName);
            PropertyMetadata<?> propertyMetadata = propertyNameMap.get(propertyName);
            if (propertyMetadata == null || propertyMetadata.getGetter() == null) {
               propertyMetadata = getOrCreateMethodPropertyMetadata(
                  propertyName,
                  declaringClassName,
                  propertyNameMap,
                  TypeData.newInstance(methodx),
                  propertyTypeParameterMap,
                  parentClassTypeData,
                  genericTypeNames,
                  getGenericType(methodx)
               );
               if (propertyMetadata.getGetter() == null) {
                  propertyMetadata.setGetter(methodx);

                  for (Annotation annotation : methodx.getDeclaredAnnotations()) {
                     propertyMetadata.addReadAnnotation(annotation);
                  }
               }
            }
         }

         for (Field field : currentClass.getDeclaredFields()) {
            propertyNames.add(field.getName());
            PropertyMetadata<?> propertyMetadata = getOrCreateFieldPropertyMetadata(
               field.getName(),
               declaringClassName,
               propertyNameMap,
               TypeData.newInstance(field),
               propertyTypeParameterMap,
               parentClassTypeData,
               genericTypeNames,
               field.getGenericType()
            );
            if (propertyMetadata != null && propertyMetadata.getField() == null) {
               propertyMetadata.field(field);

               for (Annotation annotation : field.getDeclaredAnnotations()) {
                  propertyMetadata.addReadAnnotation(annotation);
                  propertyMetadata.addWriteAnnotation(annotation);
               }
            }
         }

         parentClassTypeData = TypeData.newInstance(currentClass.getGenericSuperclass(), currentClass);
      }

      if (currentClass.isInterface()) {
         annotations.addAll(Arrays.asList(currentClass.getDeclaredAnnotations()));
      }

      for (String propertyName : propertyNames) {
         PropertyMetadata<?> propertyMetadata = propertyNameMap.get(propertyName);
         if (propertyMetadata.isSerializable() || propertyMetadata.isDeserializable()) {
            classModelBuilder.addProperty(createPropertyModelBuilder(propertyMetadata));
         }
      }

      Collections.reverse(annotations);
      classModelBuilder.annotations(annotations);
      classModelBuilder.propertyNameToTypeParameterMap(propertyTypeParameterMap);
      Constructor<T> noArgsConstructor = null;

      for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
         if (constructor.getParameterTypes().length == 0 && (Modifier.isPublic(constructor.getModifiers()) || Modifier.isProtected(constructor.getModifiers()))
            )
          {
            noArgsConstructor = (Constructor<T>)constructor;
            constructor.setAccessible(true);
         }
      }

      classModelBuilder.instanceCreatorFactory(new InstanceCreatorFactoryImpl<>(new CreatorExecutable<>(clazz, noArgsConstructor)));
   }

   private static <T, S> PropertyMetadata<T> getOrCreateMethodPropertyMetadata(
      String propertyName,
      String declaringClassName,
      Map<String, PropertyMetadata<?>> propertyNameMap,
      TypeData<T> typeData,
      Map<String, TypeParameterMap> propertyTypeParameterMap,
      TypeData<S> parentClassTypeData,
      List<String> genericTypeNames,
      Type genericType
   ) {
      PropertyMetadata<T> propertyMetadata = getOrCreatePropertyMetadata(propertyName, declaringClassName, propertyNameMap, typeData);
      if (!isAssignableClass(propertyMetadata.getTypeData().getType(), typeData.getType())) {
         propertyMetadata.setError(
            String.format(
               "Property '%s' in %s, has differing data types: %s and %s.", propertyName, declaringClassName, propertyMetadata.getTypeData(), typeData
            )
         );
      }

      cachePropertyTypeData(propertyMetadata, propertyTypeParameterMap, parentClassTypeData, genericTypeNames, genericType);
      return propertyMetadata;
   }

   private static boolean isAssignableClass(Class<?> propertyTypeClass, Class<?> typeDataClass) {
      Assertions.notNull("propertyTypeClass", propertyTypeClass);
      Assertions.notNull("typeDataClass", typeDataClass);
      return propertyTypeClass.isAssignableFrom(typeDataClass) || typeDataClass.isAssignableFrom(propertyTypeClass);
   }

   private static <T, S> PropertyMetadata<T> getOrCreateFieldPropertyMetadata(
      String propertyName,
      String declaringClassName,
      Map<String, PropertyMetadata<?>> propertyNameMap,
      TypeData<T> typeData,
      Map<String, TypeParameterMap> propertyTypeParameterMap,
      TypeData<S> parentClassTypeData,
      List<String> genericTypeNames,
      Type genericType
   ) {
      PropertyMetadata<T> propertyMetadata = getOrCreatePropertyMetadata(propertyName, declaringClassName, propertyNameMap, typeData);
      if (!propertyMetadata.getTypeData().getType().isAssignableFrom(typeData.getType())) {
         return null;
      } else {
         cachePropertyTypeData(propertyMetadata, propertyTypeParameterMap, parentClassTypeData, genericTypeNames, genericType);
         return propertyMetadata;
      }
   }

   private static <T> PropertyMetadata<T> getOrCreatePropertyMetadata(
      String propertyName, String declaringClassName, Map<String, PropertyMetadata<?>> propertyNameMap, TypeData<T> typeData
   ) {
      PropertyMetadata<T> propertyMetadata = (PropertyMetadata<T>)propertyNameMap.get(propertyName);
      if (propertyMetadata == null) {
         propertyMetadata = new PropertyMetadata<>(propertyName, declaringClassName, typeData);
         propertyNameMap.put(propertyName, propertyMetadata);
      }

      return propertyMetadata;
   }

   private static <T, S> void cachePropertyTypeData(
      PropertyMetadata<T> propertyMetadata,
      Map<String, TypeParameterMap> propertyTypeParameterMap,
      TypeData<S> parentClassTypeData,
      List<String> genericTypeNames,
      Type genericType
   ) {
      TypeParameterMap typeParameterMap = getTypeParameterMap(genericTypeNames, genericType);
      propertyTypeParameterMap.put(propertyMetadata.getName(), typeParameterMap);
      propertyMetadata.typeParameterInfo(typeParameterMap, parentClassTypeData);
   }

   private static Type getGenericType(Method method) {
      return PropertyReflectionUtils.isGetter(method) ? method.getGenericReturnType() : method.getGenericParameterTypes()[0];
   }

   static <T> PropertyModelBuilder<T> createPropertyModelBuilder(PropertyMetadata<T> propertyMetadata) {
      PropertyModelBuilder<T> propertyModelBuilder = PropertyModel.<T>builder()
         .propertyName(propertyMetadata.getName())
         .readName(propertyMetadata.getName())
         .writeName(propertyMetadata.getName())
         .typeData(propertyMetadata.getTypeData())
         .readAnnotations(propertyMetadata.getReadAnnotations())
         .writeAnnotations(propertyMetadata.getWriteAnnotations())
         .propertySerialization(new PropertyModelSerializationImpl<>())
         .propertyAccessor(new PropertyAccessorImpl<>(propertyMetadata))
         .setError(propertyMetadata.getError());
      if (propertyMetadata.getTypeParameters() != null) {
         propertyModelBuilder.typeData(
            PojoSpecializationHelper.specializeTypeData(
               propertyModelBuilder.getTypeData(), propertyMetadata.getTypeParameters(), propertyMetadata.getTypeParameterMap()
            )
         );
      }

      return propertyModelBuilder;
   }

   private static TypeParameterMap getTypeParameterMap(List<String> genericTypeNames, Type propertyType) {
      int classParamIndex = genericTypeNames.indexOf(propertyType.toString());
      TypeParameterMap.Builder builder = TypeParameterMap.builder();
      if (classParamIndex != -1) {
         builder.addIndex(classParamIndex);
      } else if (propertyType instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)propertyType;

         for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
            classParamIndex = genericTypeNames.indexOf(pt.getActualTypeArguments()[i].toString());
            if (classParamIndex != -1) {
               builder.addIndex(i, classParamIndex);
            } else {
               builder.addIndex(i, getTypeParameterMap(genericTypeNames, pt.getActualTypeArguments()[i]));
            }
         }
      }

      return builder.build();
   }

   static <V> V stateNotNull(String property, V value) {
      if (value == null) {
         throw new IllegalStateException(String.format("%s cannot be null", property));
      } else {
         return value;
      }
   }

   private PojoBuilderHelper() {
   }
}
