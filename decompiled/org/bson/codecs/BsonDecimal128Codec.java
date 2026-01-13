package org.bson.codecs;

import org.bson.BsonDecimal128;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonDecimal128Codec implements Codec<BsonDecimal128> {
   public BsonDecimal128 decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonDecimal128(reader.readDecimal128());
   }

   public void encode(BsonWriter writer, BsonDecimal128 value, EncoderContext encoderContext) {
      writer.writeDecimal128(value.getValue());
   }

   @Override
   public Class<BsonDecimal128> getEncoderClass() {
      return BsonDecimal128.class;
   }
}
