package org.bson.codecs.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class TypeParameterMap {
   private final Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap;

   static TypeParameterMap.Builder builder() {
      return new TypeParameterMap.Builder();
   }

   Map<Integer, Either<Integer, TypeParameterMap>> getPropertyToClassParamIndexMap() {
      return this.propertyToClassParamIndexMap;
   }

   boolean hasTypeParameters() {
      return !this.propertyToClassParamIndexMap.isEmpty();
   }

   @Override
   public String toString() {
      return "TypeParameterMap{fieldToClassParamIndexMap=" + this.propertyToClassParamIndexMap + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TypeParameterMap that = (TypeParameterMap)o;
         return this.getPropertyToClassParamIndexMap().equals(that.getPropertyToClassParamIndexMap());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getPropertyToClassParamIndexMap().hashCode();
   }

   private TypeParameterMap(Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap) {
      this.propertyToClassParamIndexMap = Collections.unmodifiableMap(propertyToClassParamIndexMap);
   }

   static final class Builder {
      private final Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap = new HashMap<>();

      private Builder() {
      }

      TypeParameterMap.Builder addIndex(int classTypeParameterIndex) {
         this.propertyToClassParamIndexMap.put(-1, Either.left(classTypeParameterIndex));
         return this;
      }

      TypeParameterMap.Builder addIndex(int propertyTypeParameterIndex, int classTypeParameterIndex) {
         this.propertyToClassParamIndexMap.put(propertyTypeParameterIndex, Either.left(classTypeParameterIndex));
         return this;
      }

      TypeParameterMap.Builder addIndex(int propertyTypeParameterIndex, TypeParameterMap typeParameterMap) {
         this.propertyToClassParamIndexMap.put(propertyTypeParameterIndex, Either.right(typeParameterMap));
         return this;
      }

      TypeParameterMap build() {
         if (this.propertyToClassParamIndexMap.size() > 1 && this.propertyToClassParamIndexMap.containsKey(-1)) {
            throw new IllegalStateException("You cannot have a generic field that also has type parameters.");
         } else {
            return new TypeParameterMap(this.propertyToClassParamIndexMap);
         }
      }
   }
}
