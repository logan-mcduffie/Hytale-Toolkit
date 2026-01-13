package org.bson.codecs;

import java.util.Date;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class DateCodec implements Codec<Date> {
   public void encode(BsonWriter writer, Date value, EncoderContext encoderContext) {
      writer.writeDateTime(value.getTime());
   }

   public Date decode(BsonReader reader, DecoderContext decoderContext) {
      return new Date(reader.readDateTime());
   }

   @Override
   public Class<Date> getEncoderClass() {
      return Date.class;
   }
}
