package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.ObjectId;

public class ObjectIdCodec implements Codec<ObjectId> {
   public void encode(BsonWriter writer, ObjectId value, EncoderContext encoderContext) {
      writer.writeObjectId(value);
   }

   public ObjectId decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readObjectId();
   }

   @Override
   public Class<ObjectId> getEncoderClass() {
      return ObjectId.class;
   }
}
