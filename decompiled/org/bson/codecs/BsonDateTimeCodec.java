package org.bson.codecs;

import org.bson.BsonDateTime;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonDateTimeCodec implements Codec<BsonDateTime> {
   public BsonDateTime decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonDateTime(reader.readDateTime());
   }

   public void encode(BsonWriter writer, BsonDateTime value, EncoderContext encoderContext) {
      writer.writeDateTime(value.getValue());
   }

   @Override
   public Class<BsonDateTime> getEncoderClass() {
      return BsonDateTime.class;
   }
}
