package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.MinKey;

public class MinKeyCodec implements Codec<MinKey> {
   public void encode(BsonWriter writer, MinKey value, EncoderContext encoderContext) {
      writer.writeMinKey();
   }

   public MinKey decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readMinKey();
      return new MinKey();
   }

   @Override
   public Class<MinKey> getEncoderClass() {
      return MinKey.class;
   }
}
