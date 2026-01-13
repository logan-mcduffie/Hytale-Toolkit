package org.bson.codecs;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonObjectIdCodec implements Codec<BsonObjectId> {
   public void encode(BsonWriter writer, BsonObjectId value, EncoderContext encoderContext) {
      writer.writeObjectId(value.getValue());
   }

   public BsonObjectId decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonObjectId(reader.readObjectId());
   }

   @Override
   public Class<BsonObjectId> getEncoderClass() {
      return BsonObjectId.class;
   }
}
