package org.bson.codecs.pojo;

import org.bson.BsonObjectId;
import org.bson.BsonType;
import org.bson.types.ObjectId;

final class ConventionObjectIdGeneratorsImpl implements Convention {
   @Override
   public void apply(ClassModelBuilder<?> classModelBuilder) {
      if (classModelBuilder.getIdGenerator() == null && classModelBuilder.getIdPropertyName() != null) {
         PropertyModelBuilder<?> idProperty = classModelBuilder.getProperty(classModelBuilder.getIdPropertyName());
         if (idProperty != null) {
            Class<?> idType = idProperty.getTypeData().getType();
            if (classModelBuilder.getIdGenerator() == null && idType.equals(ObjectId.class)) {
               classModelBuilder.idGenerator(IdGenerators.OBJECT_ID_GENERATOR);
            } else if (classModelBuilder.getIdGenerator() == null && idType.equals(BsonObjectId.class)) {
               classModelBuilder.idGenerator(IdGenerators.BSON_OBJECT_ID_GENERATOR);
            } else if (classModelBuilder.getIdGenerator() == null && idType.equals(String.class) && idProperty.getBsonRepresentation() == BsonType.OBJECT_ID) {
               classModelBuilder.idGenerator(IdGenerators.STRING_ID_GENERATOR);
            }
         }
      }
   }
}
