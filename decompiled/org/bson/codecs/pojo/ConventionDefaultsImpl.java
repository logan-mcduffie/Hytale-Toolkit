package org.bson.codecs.pojo;

final class ConventionDefaultsImpl implements Convention {
   @Override
   public void apply(ClassModelBuilder<?> classModelBuilder) {
      if (classModelBuilder.getDiscriminatorKey() == null) {
         classModelBuilder.discriminatorKey("_t");
      }

      if (classModelBuilder.getDiscriminator() == null && classModelBuilder.getType() != null) {
         classModelBuilder.discriminator(classModelBuilder.getType().getName());
      }

      for (PropertyModelBuilder<?> propertyModel : classModelBuilder.getPropertyModelBuilders()) {
         if (classModelBuilder.getIdPropertyName() == null) {
            String propertyName = propertyModel.getName();
            if (propertyName.equals("_id") || propertyName.equals("id")) {
               classModelBuilder.idPropertyName(propertyName);
            }
         }
      }
   }
}
