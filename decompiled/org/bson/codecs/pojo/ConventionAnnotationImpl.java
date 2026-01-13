package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonType;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

final class ConventionAnnotationImpl implements Convention {
   @Override
   public void apply(ClassModelBuilder<?> classModelBuilder) {
      for (Annotation annotation : classModelBuilder.getAnnotations()) {
         this.processClassAnnotation(classModelBuilder, annotation);
      }

      for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
         this.processPropertyAnnotations(classModelBuilder, propertyModelBuilder);
      }

      this.processCreatorAnnotation(classModelBuilder);
      this.cleanPropertyBuilders(classModelBuilder);
   }

   private void processClassAnnotation(ClassModelBuilder<?> classModelBuilder, Annotation annotation) {
      if (annotation instanceof BsonDiscriminator) {
         BsonDiscriminator discriminator = (BsonDiscriminator)annotation;
         String key = discriminator.key();
         if (!key.equals("")) {
            classModelBuilder.discriminatorKey(key);
         }

         String name = discriminator.value();
         if (!name.equals("")) {
            classModelBuilder.discriminator(name);
         }

         classModelBuilder.enableDiscriminator(true);
      }
   }

   private void processPropertyAnnotations(ClassModelBuilder<?> classModelBuilder, PropertyModelBuilder<?> propertyModelBuilder) {
      for (Annotation annotation : propertyModelBuilder.getReadAnnotations()) {
         if (annotation instanceof BsonProperty) {
            BsonProperty bsonProperty = (BsonProperty)annotation;
            if (!"".equals(bsonProperty.value())) {
               propertyModelBuilder.readName(bsonProperty.value());
            }

            propertyModelBuilder.discriminatorEnabled(bsonProperty.useDiscriminator());
            if (propertyModelBuilder.getName().equals(classModelBuilder.getIdPropertyName())) {
               classModelBuilder.idPropertyName(null);
            }
         } else if (annotation instanceof BsonId) {
            classModelBuilder.idPropertyName(propertyModelBuilder.getName());
         } else if (annotation instanceof BsonIgnore) {
            propertyModelBuilder.readName(null);
         } else if (annotation instanceof BsonRepresentation) {
            BsonRepresentation bsonRepresentation = (BsonRepresentation)annotation;
            BsonType bsonRep = bsonRepresentation.value();
            propertyModelBuilder.bsonRepresentation(bsonRep);
         }
      }

      for (Annotation annotationx : propertyModelBuilder.getWriteAnnotations()) {
         if (annotationx instanceof BsonProperty) {
            BsonProperty bsonPropertyx = (BsonProperty)annotationx;
            if (!"".equals(bsonPropertyx.value())) {
               propertyModelBuilder.writeName(bsonPropertyx.value());
            }
         } else if (annotationx instanceof BsonIgnore) {
            propertyModelBuilder.writeName(null);
         }
      }
   }

   private <T> void processCreatorAnnotation(ClassModelBuilder<T> classModelBuilder) {
      Class<T> clazz = classModelBuilder.getType();
      CreatorExecutable<T> creatorExecutable = null;

      for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
         if (Modifier.isPublic(constructor.getModifiers()) && !constructor.isSynthetic()) {
            for (Annotation annotation : constructor.getDeclaredAnnotations()) {
               if (annotation.annotationType().equals(BsonCreator.class)) {
                  if (creatorExecutable != null) {
                     throw new CodecConfigurationException("Found multiple constructors annotated with @BsonCreator");
                  }

                  creatorExecutable = new CreatorExecutable<>(clazz, (Constructor<T>)constructor);
               }
            }
         }
      }

      Class<?> bsonCreatorClass = clazz;

      for (boolean foundStaticBsonCreatorMethod = false;
         bsonCreatorClass != null && !foundStaticBsonCreatorMethod;
         bsonCreatorClass = bsonCreatorClass.getSuperclass()
      ) {
         for (Method method : bsonCreatorClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && !method.isSynthetic() && !method.isBridge()) {
               for (Annotation annotationx : method.getDeclaredAnnotations()) {
                  if (annotationx.annotationType().equals(BsonCreator.class)) {
                     if (creatorExecutable != null) {
                        throw new CodecConfigurationException("Found multiple constructors / methods annotated with @BsonCreator");
                     }

                     if (!bsonCreatorClass.isAssignableFrom(method.getReturnType())) {
                        throw new CodecConfigurationException(
                           String.format("Invalid method annotated with @BsonCreator. Returns '%s', expected %s", method.getReturnType(), bsonCreatorClass)
                        );
                     }

                     creatorExecutable = new CreatorExecutable<>(clazz, method);
                     foundStaticBsonCreatorMethod = true;
                  }
               }
            }
         }
      }

      if (creatorExecutable != null) {
         List<BsonProperty> properties = creatorExecutable.getProperties();
         List<Class<?>> parameterTypes = creatorExecutable.getParameterTypes();
         List<Type> parameterGenericTypes = creatorExecutable.getParameterGenericTypes();
         if (properties.size() != parameterTypes.size()) {
            throw creatorExecutable.getError(clazz, "All parameters in the @BsonCreator method / constructor must be annotated with a @BsonProperty.");
         }

         for (int i = 0; i < properties.size(); i++) {
            boolean isIdProperty = creatorExecutable.getIdPropertyIndex() != null && creatorExecutable.getIdPropertyIndex().equals(i);
            Class<?> parameterType = parameterTypes.get(i);
            Type genericType = parameterGenericTypes.get(i);
            PropertyModelBuilder<?> propertyModelBuilder = null;
            if (isIdProperty) {
               propertyModelBuilder = classModelBuilder.getProperty(classModelBuilder.getIdPropertyName());
            } else {
               BsonProperty bsonProperty = properties.get(i);

               for (PropertyModelBuilder<?> builder : classModelBuilder.getPropertyModelBuilders()) {
                  if (bsonProperty.value().equals(builder.getWriteName())) {
                     propertyModelBuilder = builder;
                     break;
                  }

                  if (bsonProperty.value().equals(builder.getReadName())) {
                     propertyModelBuilder = builder;
                  }
               }

               if (propertyModelBuilder == null) {
                  propertyModelBuilder = classModelBuilder.getProperty(bsonProperty.value());
               }

               if (propertyModelBuilder == null) {
                  propertyModelBuilder = this.addCreatorPropertyToClassModelBuilder(classModelBuilder, bsonProperty.value(), parameterType);
               } else {
                  if (!bsonProperty.value().equals(propertyModelBuilder.getName())) {
                     propertyModelBuilder.writeName(bsonProperty.value());
                  }

                  tryToExpandToGenericType(parameterType, propertyModelBuilder, genericType);
               }
            }

            if (!propertyModelBuilder.getTypeData().isAssignableFrom(parameterType)) {
               throw creatorExecutable.getError(
                  clazz,
                  String.format(
                     "Invalid Property type for '%s'. Expected %s, found %s.",
                     propertyModelBuilder.getWriteName(),
                     propertyModelBuilder.getTypeData().getType(),
                     parameterType
                  )
               );
            }
         }

         classModelBuilder.instanceCreatorFactory(new InstanceCreatorFactoryImpl<>(creatorExecutable));
      }
   }

   private static <T> void tryToExpandToGenericType(Class<?> parameterType, PropertyModelBuilder<T> propertyModelBuilder, Type genericType) {
      if (parameterType.isAssignableFrom(propertyModelBuilder.getTypeData().getType())) {
         propertyModelBuilder.typeData(TypeData.newInstance(genericType, (Class<T>)parameterType));
      }
   }

   private <T, S> PropertyModelBuilder<S> addCreatorPropertyToClassModelBuilder(ClassModelBuilder<T> classModelBuilder, String name, Class<S> clazz) {
      PropertyModelBuilder<S> propertyModelBuilder = PojoBuilderHelper.createPropertyModelBuilder(
            new PropertyMetadata<>(name, classModelBuilder.getType().getSimpleName(), TypeData.builder(clazz).build())
         )
         .readName(null)
         .writeName(name);
      classModelBuilder.addProperty(propertyModelBuilder);
      return propertyModelBuilder;
   }

   private void cleanPropertyBuilders(ClassModelBuilder<?> classModelBuilder) {
      List<String> propertiesToRemove = new ArrayList<>();

      for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
         if (!propertyModelBuilder.isReadable() && !propertyModelBuilder.isWritable()) {
            propertiesToRemove.add(propertyModelBuilder.getName());
         }
      }

      for (String propertyName : propertiesToRemove) {
         classModelBuilder.removeProperty(propertyName);
      }
   }
}
