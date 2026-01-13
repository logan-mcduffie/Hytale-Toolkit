package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonDeserializer;
import com.nimbusds.jose.shaded.gson.JsonSerializer;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.annotations.JsonAdapter;
import com.nimbusds.jose.shaded.gson.internal.ConstructorConstructor;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
   private static final TypeAdapterFactory TREE_TYPE_CLASS_DUMMY_FACTORY = new JsonAdapterAnnotationTypeAdapterFactory.DummyTypeAdapterFactory();
   private static final TypeAdapterFactory TREE_TYPE_FIELD_DUMMY_FACTORY = new JsonAdapterAnnotationTypeAdapterFactory.DummyTypeAdapterFactory();
   private final ConstructorConstructor constructorConstructor;
   private final ConcurrentMap<Class<?>, TypeAdapterFactory> adapterFactoryMap;

   public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
      this.constructorConstructor = constructorConstructor;
      this.adapterFactoryMap = new ConcurrentHashMap<>();
   }

   private static JsonAdapter getAnnotation(Class<?> rawType) {
      return rawType.getAnnotation(JsonAdapter.class);
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
      Class<? super T> rawType = targetType.getRawType();
      JsonAdapter annotation = getAnnotation(rawType);
      return (TypeAdapter<T>)(annotation == null ? null : this.getTypeAdapter(this.constructorConstructor, gson, targetType, annotation, true));
   }

   private static Object createAdapter(ConstructorConstructor constructorConstructor, Class<?> adapterClass) {
      boolean allowUnsafe = true;
      return constructorConstructor.get(TypeToken.get(adapterClass), allowUnsafe).construct();
   }

   private TypeAdapterFactory putFactoryAndGetCurrent(Class<?> rawType, TypeAdapterFactory factory) {
      TypeAdapterFactory existingFactory = this.adapterFactoryMap.putIfAbsent(rawType, factory);
      return existingFactory != null ? existingFactory : factory;
   }

   TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> type, JsonAdapter annotation, boolean isClassAnnotation) {
      Object instance = createAdapter(constructorConstructor, annotation.value());
      boolean nullSafe = annotation.nullSafe();
      TypeAdapter<?> typeAdapter;
      if (instance instanceof TypeAdapter) {
         typeAdapter = (TypeAdapter<?>)instance;
      } else if (instance instanceof TypeAdapterFactory) {
         TypeAdapterFactory factory = (TypeAdapterFactory)instance;
         if (isClassAnnotation) {
            factory = this.putFactoryAndGetCurrent(type.getRawType(), factory);
         }

         typeAdapter = factory.create(gson, type);
      } else {
         if (!(instance instanceof JsonSerializer) && !(instance instanceof JsonDeserializer)) {
            throw new IllegalArgumentException(
               "Invalid attempt to bind an instance of "
                  + instance.getClass().getName()
                  + " as a @JsonAdapter for "
                  + type.toString()
                  + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer."
            );
         }

         JsonSerializer<?> serializer = instance instanceof JsonSerializer ? (JsonSerializer)instance : null;
         JsonDeserializer<?> deserializer = instance instanceof JsonDeserializer ? (JsonDeserializer)instance : null;
         TypeAdapterFactory skipPast;
         if (isClassAnnotation) {
            skipPast = TREE_TYPE_CLASS_DUMMY_FACTORY;
         } else {
            skipPast = TREE_TYPE_FIELD_DUMMY_FACTORY;
         }

         TypeAdapter<?> tempAdapter = new TreeTypeAdapter<>(serializer, deserializer, gson, type, skipPast, nullSafe);
         typeAdapter = tempAdapter;
         nullSafe = false;
      }

      if (typeAdapter != null && nullSafe) {
         typeAdapter = typeAdapter.nullSafe();
      }

      return typeAdapter;
   }

   public boolean isClassJsonAdapterFactory(TypeToken<?> type, TypeAdapterFactory factory) {
      Objects.requireNonNull(type);
      Objects.requireNonNull(factory);
      if (factory == TREE_TYPE_CLASS_DUMMY_FACTORY) {
         return true;
      } else {
         Class<?> rawType = type.getRawType();
         TypeAdapterFactory existingFactory = this.adapterFactoryMap.get(rawType);
         if (existingFactory != null) {
            return existingFactory == factory;
         } else {
            JsonAdapter annotation = getAnnotation(rawType);
            if (annotation == null) {
               return false;
            } else {
               Class<?> adapterClass = annotation.value();
               if (!TypeAdapterFactory.class.isAssignableFrom(adapterClass)) {
                  return false;
               } else {
                  Object adapter = createAdapter(this.constructorConstructor, adapterClass);
                  TypeAdapterFactory newFactory = (TypeAdapterFactory)adapter;
                  return this.putFactoryAndGetCurrent(rawType, newFactory) == factory;
               }
            }
         }
      }
   }

   private static class DummyTypeAdapterFactory implements TypeAdapterFactory {
      private DummyTypeAdapterFactory() {
      }

      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         throw new AssertionError("Factory should not be used");
      }
   }
}
