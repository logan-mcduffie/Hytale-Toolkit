package org.bson.codecs.pojo;

import org.bson.codecs.configuration.CodecConfigurationException;

final class PropertyAccessorImpl<T> implements PropertyAccessor<T> {
   private final PropertyMetadata<T> propertyMetadata;

   PropertyAccessorImpl(PropertyMetadata<T> propertyMetadata) {
      this.propertyMetadata = propertyMetadata;
   }

   @Override
   public <S> T get(S instance) {
      try {
         if (this.propertyMetadata.isSerializable()) {
            return (T)(this.propertyMetadata.getGetter() != null
               ? this.propertyMetadata.getGetter().invoke(instance)
               : this.propertyMetadata.getField().get(instance));
         } else {
            throw this.getError(null);
         }
      } catch (Exception var3) {
         throw this.getError(var3);
      }
   }

   @Override
   public <S> void set(S instance, T value) {
      try {
         if (this.propertyMetadata.isDeserializable()) {
            if (this.propertyMetadata.getSetter() != null) {
               this.propertyMetadata.getSetter().invoke(instance, value);
            } else {
               this.propertyMetadata.getField().set(instance, value);
            }
         }
      } catch (Exception var4) {
         throw this.setError(var4);
      }
   }

   PropertyMetadata<T> getPropertyMetadata() {
      return this.propertyMetadata;
   }

   private CodecConfigurationException getError(Exception cause) {
      return new CodecConfigurationException(
         String.format("Unable to get value for property '%s' in %s", this.propertyMetadata.getName(), this.propertyMetadata.getDeclaringClassName()), cause
      );
   }

   private CodecConfigurationException setError(Exception cause) {
      return new CodecConfigurationException(
         String.format("Unable to set value for property '%s' in %s", this.propertyMetadata.getName(), this.propertyMetadata.getDeclaringClassName()), cause
      );
   }
}
