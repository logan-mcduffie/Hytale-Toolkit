package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

final class CreatorExecutable<T> {
   private final Class<T> clazz;
   private final Constructor<T> constructor;
   private final Method method;
   private final List<BsonProperty> properties = new ArrayList<>();
   private final Integer idPropertyIndex;
   private final List<Class<?>> parameterTypes = new ArrayList<>();
   private final List<Type> parameterGenericTypes = new ArrayList<>();

   CreatorExecutable(Class<T> clazz, Constructor<T> constructor) {
      this(clazz, constructor, null);
   }

   CreatorExecutable(Class<T> clazz, Method method) {
      this(clazz, null, method);
   }

   private CreatorExecutable(Class<T> clazz, Constructor<T> constructor, Method method) {
      this.clazz = clazz;
      this.constructor = constructor;
      this.method = method;
      Integer idPropertyIndex = null;
      if (constructor != null || method != null) {
         Class<?>[] paramTypes = constructor != null ? constructor.getParameterTypes() : method.getParameterTypes();
         Type[] genericParamTypes = constructor != null ? constructor.getGenericParameterTypes() : method.getGenericParameterTypes();
         this.parameterTypes.addAll(Arrays.asList(paramTypes));
         this.parameterGenericTypes.addAll(Arrays.asList(genericParamTypes));
         Annotation[][] parameterAnnotations = constructor != null ? constructor.getParameterAnnotations() : method.getParameterAnnotations();

         for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];

            for (Annotation annotation : parameterAnnotation) {
               if (annotation.annotationType().equals(BsonProperty.class)) {
                  this.properties.add((BsonProperty)annotation);
                  break;
               }

               if (annotation.annotationType().equals(BsonId.class)) {
                  this.properties.add(null);
                  idPropertyIndex = i;
                  break;
               }
            }
         }
      }

      this.idPropertyIndex = idPropertyIndex;
   }

   Class<T> getType() {
      return this.clazz;
   }

   List<BsonProperty> getProperties() {
      return this.properties;
   }

   Integer getIdPropertyIndex() {
      return this.idPropertyIndex;
   }

   List<Class<?>> getParameterTypes() {
      return this.parameterTypes;
   }

   List<Type> getParameterGenericTypes() {
      return this.parameterGenericTypes;
   }

   T getInstance() {
      this.checkHasAnExecutable();

      try {
         return (T)(this.constructor != null ? this.constructor.newInstance() : this.method.invoke(this.clazz));
      } catch (Exception var2) {
         throw new CodecConfigurationException(var2.getMessage(), var2);
      }
   }

   T getInstance(Object[] params) {
      this.checkHasAnExecutable();

      try {
         return (T)(this.constructor != null ? this.constructor.newInstance(params) : this.method.invoke(this.clazz, params));
      } catch (Exception var3) {
         throw new CodecConfigurationException(var3.getMessage(), var3);
      }
   }

   CodecConfigurationException getError(Class<?> clazz, String msg) {
      return getError(clazz, this.constructor != null, msg);
   }

   private void checkHasAnExecutable() {
      if (this.constructor == null && this.method == null) {
         throw new CodecConfigurationException(String.format("Cannot find a public constructor for '%s'.", this.clazz.getSimpleName()));
      }
   }

   private static CodecConfigurationException getError(Class<?> clazz, boolean isConstructor, String msg) {
      return new CodecConfigurationException(
         String.format("Invalid @BsonCreator %s in %s. %s", isConstructor ? "constructor" : "method", clazz.getSimpleName(), msg)
      );
   }
}
