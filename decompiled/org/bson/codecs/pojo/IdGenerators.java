package org.bson.codecs.pojo;

import org.bson.BsonObjectId;
import org.bson.types.ObjectId;

public final class IdGenerators {
   public static final IdGenerator<ObjectId> OBJECT_ID_GENERATOR = new IdGenerator<ObjectId>() {
      public ObjectId generate() {
         return new ObjectId();
      }

      @Override
      public Class<ObjectId> getType() {
         return ObjectId.class;
      }
   };
   public static final IdGenerator<BsonObjectId> BSON_OBJECT_ID_GENERATOR = new IdGenerator<BsonObjectId>() {
      public BsonObjectId generate() {
         return new BsonObjectId();
      }

      @Override
      public Class<BsonObjectId> getType() {
         return BsonObjectId.class;
      }
   };
   public static final IdGenerator<String> STRING_ID_GENERATOR = new IdGenerator<String>() {
      public String generate() {
         return IdGenerators.OBJECT_ID_GENERATOR.generate().toHexString();
      }

      @Override
      public Class<String> getType() {
         return String.class;
      }
   };

   private IdGenerators() {
   }
}
