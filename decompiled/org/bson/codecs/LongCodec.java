package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class LongCodec implements Codec<Long> {
   public void encode(BsonWriter writer, Long value, EncoderContext encoderContext) {
      writer.writeInt64(value);
   }

   public Long decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeLong(reader);
   }

   @Override
   public Class<Long> getEncoderClass() {
      return Long.class;
   }
}
