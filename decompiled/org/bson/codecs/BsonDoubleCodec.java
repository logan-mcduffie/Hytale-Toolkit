package org.bson.codecs;

import org.bson.BsonDouble;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonDoubleCodec implements Codec<BsonDouble> {
   public BsonDouble decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonDouble(reader.readDouble());
   }

   public void encode(BsonWriter writer, BsonDouble value, EncoderContext encoderContext) {
      writer.writeDouble(value.getValue());
   }

   @Override
   public Class<BsonDouble> getEncoderClass() {
      return BsonDouble.class;
   }
}
