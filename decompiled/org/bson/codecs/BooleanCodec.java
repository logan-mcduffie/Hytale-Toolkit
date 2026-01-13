package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BooleanCodec implements Codec<Boolean> {
   public void encode(BsonWriter writer, Boolean value, EncoderContext encoderContext) {
      writer.writeBoolean(value);
   }

   public Boolean decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readBoolean();
   }

   @Override
   public Class<Boolean> getEncoderClass() {
      return Boolean.class;
   }
}
