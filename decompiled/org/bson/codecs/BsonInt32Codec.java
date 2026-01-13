package org.bson.codecs;

import org.bson.BsonInt32;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonInt32Codec implements Codec<BsonInt32> {
   public BsonInt32 decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonInt32(reader.readInt32());
   }

   public void encode(BsonWriter writer, BsonInt32 value, EncoderContext encoderContext) {
      writer.writeInt32(value.getValue());
   }

   @Override
   public Class<BsonInt32> getEncoderClass() {
      return BsonInt32.class;
   }
}
