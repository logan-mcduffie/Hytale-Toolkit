package com.nimbusds.jose.shaded.gson.internal;

import com.nimbusds.jose.shaded.gson.ExclusionStrategy;
import com.nimbusds.jose.shaded.gson.FieldAttributes;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.annotations.Expose;
import com.nimbusds.jose.shaded.gson.annotations.Since;
import com.nimbusds.jose.shaded.gson.annotations.Until;
import com.nimbusds.jose.shaded.gson.internal.reflect.ReflectionHelper;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
   private static final double IGNORE_VERSIONS = -1.0;
   public static final Excluder DEFAULT = new Excluder();
   private double version = -1.0;
   private int modifiers = 136;
   private boolean serializeInnerClasses = true;
   private boolean requireExpose;
   private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
   private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

   protected Excluder clone() {
      try {
         return (Excluder)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError(var2);
      }
   }

   public Excluder withVersion(double ignoreVersionsAfter) {
      Excluder result = this.clone();
      result.version = ignoreVersionsAfter;
      return result;
   }

   public Excluder withModifiers(int... modifiers) {
      Excluder result = this.clone();
      result.modifiers = 0;

      for (int modifier : modifiers) {
         result.modifiers |= modifier;
      }

      return result;
   }

   public Excluder disableInnerClassSerialization() {
      Excluder result = this.clone();
      result.serializeInnerClasses = false;
      return result;
   }

   public Excluder excludeFieldsWithoutExposeAnnotation() {
      Excluder result = this.clone();
      result.requireExpose = true;
      return result;
   }

   public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
      Excluder result = this.clone();
      if (serialization) {
         result.serializationStrategies = new ArrayList<>(this.serializationStrategies);
         result.serializationStrategies.add(exclusionStrategy);
      }

      if (deserialization) {
         result.deserializationStrategies = new ArrayList<>(this.deserializationStrategies);
         result.deserializationStrategies.add(exclusionStrategy);
      }

      return result;
   }

   @Override
   public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
      Class<?> rawType = type.getRawType();
      final boolean skipSerialize = this.excludeClass(rawType, true);
      final boolean skipDeserialize = this.excludeClass(rawType, false);
      return !skipSerialize && !skipDeserialize ? null : new TypeAdapter<T>() {
         private volatile TypeAdapter<T> delegate;

         @Override
         public T read(JsonReader in) throws IOException {
            if (skipDeserialize) {
               in.skipValue();
               return null;
            } else {
               return (T)this.delegate().read(in);
            }
         }

         @Override
         public void write(JsonWriter out, T value) throws IOException {
            if (skipSerialize) {
               out.nullValue();
            } else {
               this.delegate().write(out, value);
            }
         }

         private TypeAdapter<T> delegate() {
            TypeAdapter<T> d = this.delegate;
            if (d == null) {
               d = this.delegate = gson.getDelegateAdapter(Excluder.this, type);
            }

            return d;
         }
      };
   }

   public boolean excludeField(Field field, boolean serialize) {
      if ((this.modifiers & field.getModifiers()) != 0) {
         return true;
      } else if (this.version != -1.0 && !this.isValidVersion(field.getAnnotation(Since.class), field.getAnnotation(Until.class))) {
         return true;
      } else if (field.isSynthetic()) {
         return true;
      } else {
         if (this.requireExpose) {
            Expose annotation = field.getAnnotation(Expose.class);
            if (annotation == null || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
               return true;
            }
         }

         if (this.excludeClass(field.getType(), serialize)) {
            return true;
         } else {
            List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
            if (!list.isEmpty()) {
               FieldAttributes fieldAttributes = new FieldAttributes(field);

               for (ExclusionStrategy exclusionStrategy : list) {
                  if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean excludeClass(Class<?> clazz, boolean serialize) {
      if (this.version != -1.0 && !this.isValidVersion(clazz.getAnnotation(Since.class), clazz.getAnnotation(Until.class))) {
         return true;
      } else if (!this.serializeInnerClasses && isInnerClass(clazz)) {
         return true;
      } else if (!serialize && !Enum.class.isAssignableFrom(clazz) && ReflectionHelper.isAnonymousOrNonStaticLocal(clazz)) {
         return true;
      } else {
         for (ExclusionStrategy exclusionStrategy : serialize ? this.serializationStrategies : this.deserializationStrategies) {
            if (exclusionStrategy.shouldSkipClass(clazz)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isInnerClass(Class<?> clazz) {
      return clazz.isMemberClass() && !ReflectionHelper.isStatic(clazz);
   }

   private boolean isValidVersion(Since since, Until until) {
      return this.isValidSince(since) && this.isValidUntil(until);
   }

   private boolean isValidSince(Since annotation) {
      if (annotation != null) {
         double annotationVersion = annotation.value();
         return this.version >= annotationVersion;
      } else {
         return true;
      }
   }

   private boolean isValidUntil(Until annotation) {
      if (annotation != null) {
         double annotationVersion = annotation.value();
         return this.version < annotationVersion;
      } else {
         return true;
      }
   }
}
