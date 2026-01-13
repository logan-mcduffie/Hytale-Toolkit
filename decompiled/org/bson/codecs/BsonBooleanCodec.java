package org.bson.codecs;

import org.bson.BsonBoolean;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonBooleanCodec implements Codec<BsonBoolean> {
   public BsonBoolean decode(BsonReader reader, DecoderContext decoderContext) {
      boolean value = reader.readBoolean();
      return BsonBoolean.valueOf(value);
   }

   public void encode(BsonWriter writer, BsonBoolean value, EncoderContext encoderContext) {
      writer.writeBoolean(value.getValue());
   }

   @Override
   public Class<BsonBoolean> getEncoderClass() {
      return BsonBoolean.class;
   }
}
