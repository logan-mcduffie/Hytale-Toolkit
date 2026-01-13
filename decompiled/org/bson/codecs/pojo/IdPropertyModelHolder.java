package org.bson.codecs.pojo;

import org.bson.codecs.configuration.CodecConfigurationException;

final class IdPropertyModelHolder<I> {
   private final PropertyModel<I> propertyModel;
   private final IdGenerator<I> idGenerator;

   static <T, I> IdPropertyModelHolder<I> create(ClassModel<T> classModel, PropertyModel<I> idPropertyModel) {
      return create(classModel.getType(), idPropertyModel, classModel.getIdPropertyModelHolder().getIdGenerator());
   }

   static <T, I, V> IdPropertyModelHolder<I> create(Class<T> type, PropertyModel<I> idProperty, IdGenerator<V> idGenerator) {
      if (idProperty == null && idGenerator != null) {
         throw new CodecConfigurationException(String.format("Invalid IdGenerator. There is no IdProperty set for: %s", type));
      } else if (idGenerator != null && !idProperty.getTypeData().getType().isAssignableFrom(idGenerator.getType())) {
         throw new CodecConfigurationException(
            String.format(
               "Invalid IdGenerator. Mismatching types, the IdProperty type is: %s but the IdGenerator type is: %s",
               idProperty.getTypeData().getType(),
               idGenerator.getType()
            )
         );
      } else {
         return new IdPropertyModelHolder<>(idProperty, (IdGenerator<I>)idGenerator);
      }
   }

   private IdPropertyModelHolder(PropertyModel<I> propertyModel, IdGenerator<I> idGenerator) {
      this.propertyModel = propertyModel;
      this.idGenerator = idGenerator;
   }

   PropertyModel<I> getPropertyModel() {
      return this.propertyModel;
   }

   IdGenerator<I> getIdGenerator() {
      return this.idGenerator;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         IdPropertyModelHolder<?> that = (IdPropertyModelHolder<?>)o;
         if (this.propertyModel != null ? this.propertyModel.equals(that.propertyModel) : that.propertyModel == null) {
            return this.idGenerator != null ? this.idGenerator.equals(that.idGenerator) : that.idGenerator == null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.propertyModel != null ? this.propertyModel.hashCode() : 0;
      return 31 * result + (this.idGenerator != null ? this.idGenerator.hashCode() : 0);
   }
}
