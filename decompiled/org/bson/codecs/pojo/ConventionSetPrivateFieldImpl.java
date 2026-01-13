package org.bson.codecs.pojo;

import java.lang.reflect.Modifier;
import org.bson.codecs.configuration.CodecConfigurationException;

final class ConventionSetPrivateFieldImpl implements Convention {
   @Override
   public void apply(ClassModelBuilder<?> classModelBuilder) {
      for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
         if (!(propertyModelBuilder.getPropertyAccessor() instanceof PropertyAccessorImpl)) {
            throw new CodecConfigurationException(
               String.format(
                  "The SET_PRIVATE_FIELDS_CONVENTION is not compatible with propertyModelBuilder instance that have custom implementations of org.bson.codecs.pojo.PropertyAccessor: %s",
                  propertyModelBuilder.getPropertyAccessor().getClass().getName()
               )
            );
         }

         PropertyAccessorImpl<?> defaultAccessor = (PropertyAccessorImpl<?>)propertyModelBuilder.getPropertyAccessor();
         PropertyMetadata<?> propertyMetaData = defaultAccessor.getPropertyMetadata();
         if (!propertyMetaData.isDeserializable() && propertyMetaData.getField() != null && Modifier.isPrivate(propertyMetaData.getField().getModifiers())) {
            this.setPropertyAccessor(propertyModelBuilder);
         }
      }
   }

   private <T> void setPropertyAccessor(PropertyModelBuilder<T> propertyModelBuilder) {
      propertyModelBuilder.propertyAccessor(
         new ConventionSetPrivateFieldImpl.PrivatePropertyAccessor<>((PropertyAccessorImpl)propertyModelBuilder.getPropertyAccessor())
      );
   }

   private static final class PrivatePropertyAccessor<T> implements PropertyAccessor<T> {
      private final PropertyAccessorImpl<T> wrapped;

      private PrivatePropertyAccessor(PropertyAccessorImpl<T> wrapped) {
         this.wrapped = wrapped;

         try {
            wrapped.getPropertyMetadata().getField().setAccessible(true);
         } catch (Exception var3) {
            throw new CodecConfigurationException(
               String.format(
                  "Unable to make private field accessible '%s' in %s",
                  wrapped.getPropertyMetadata().getName(),
                  wrapped.getPropertyMetadata().getDeclaringClassName()
               ),
               var3
            );
         }
      }

      @Override
      public <S> T get(S instance) {
         return this.wrapped.get(instance);
      }

      @Override
      public <S> void set(S instance, T value) {
         try {
            this.wrapped.getPropertyMetadata().getField().set(instance, value);
         } catch (Exception var4) {
            throw new CodecConfigurationException(
               String.format(
                  "Unable to set value for property '%s' in %s",
                  this.wrapped.getPropertyMetadata().getName(),
                  this.wrapped.getPropertyMetadata().getDeclaringClassName()
               ),
               var4
            );
         }
      }
   }
}
