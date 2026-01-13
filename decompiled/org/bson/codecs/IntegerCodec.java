package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class IntegerCodec implements Codec<Integer> {
   public void encode(BsonWriter writer, Integer value, EncoderContext encoderContext) {
      writer.writeInt32(value);
   }

   public Integer decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeInt(reader);
   }

   @Override
   public Class<Integer> getEncoderClass() {
      return Integer.class;
   }
}
