package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class PojoSpecializationHelper {
   static <V> TypeData<V> specializeTypeData(TypeData<V> typeData, List<TypeData<?>> typeParameters, TypeParameterMap typeParameterMap) {
      if (typeParameterMap.hasTypeParameters() && !typeParameters.isEmpty()) {
         Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap = typeParameterMap.getPropertyToClassParamIndexMap();
         Either<Integer, TypeParameterMap> classTypeParamRepresentsWholeField = propertyToClassParamIndexMap.get(-1);
         if (classTypeParamRepresentsWholeField != null) {
            Integer index = classTypeParamRepresentsWholeField.map(i -> (Integer)i, e -> {
               throw new IllegalStateException("Invalid state, the whole class cannot be represented by a subtype.");
            });
            return (TypeData<V>)typeParameters.get(index);
         } else {
            return getTypeData(typeData, typeParameters, propertyToClassParamIndexMap);
         }
      } else {
         return typeData;
      }
   }

   private static <V> TypeData<V> getTypeData(
      TypeData<V> typeData, List<TypeData<?>> specializedTypeParameters, Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap
   ) {
      List<TypeData<?>> subTypeParameters = new ArrayList<>(typeData.getTypeParameters());

      for (int i = 0; i < typeData.getTypeParameters().size(); i++) {
         subTypeParameters.set(i, getTypeData(subTypeParameters.get(i), specializedTypeParameters, propertyToClassParamIndexMap, i));
      }

      return TypeData.builder(typeData.getType()).addTypeParameters(subTypeParameters).build();
   }

   private static TypeData<?> getTypeData(
      TypeData<?> typeData,
      List<TypeData<?>> specializedTypeParameters,
      Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap,
      int index
   ) {
      return !propertyToClassParamIndexMap.containsKey(index) ? typeData : propertyToClassParamIndexMap.get(index).map(l -> {
         if (typeData.getTypeParameters().isEmpty()) {
            return specializedTypeParameters.get(l);
         } else {
            TypeData.Builder<?> builder = TypeData.builder(typeData.getType());
            List<TypeData<?>> typeParameters = new ArrayList<>(typeData.getTypeParameters());
            typeParameters.set(index, specializedTypeParameters.get(l));
            builder.addTypeParameters(typeParameters);
            return builder.build();
         }
      }, r -> getTypeData(typeData, specializedTypeParameters, r.getPropertyToClassParamIndexMap()));
   }

   private PojoSpecializationHelper() {
   }
}
