package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.MaxKey;

public class MaxKeyCodec implements Codec<MaxKey> {
   public void encode(BsonWriter writer, MaxKey value, EncoderContext encoderContext) {
      writer.writeMaxKey();
   }

   public MaxKey decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readMaxKey();
      return new MaxKey();
   }

   @Override
   public Class<MaxKey> getEncoderClass() {
      return MaxKey.class;
   }
}
