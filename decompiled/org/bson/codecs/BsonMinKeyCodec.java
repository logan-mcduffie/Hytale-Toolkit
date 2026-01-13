package org.bson.codecs;

import org.bson.BsonMinKey;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonMinKeyCodec implements Codec<BsonMinKey> {
   public void encode(BsonWriter writer, BsonMinKey value, EncoderContext encoderContext) {
      writer.writeMinKey();
   }

   public BsonMinKey decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readMinKey();
      return new BsonMinKey();
   }

   @Override
   public Class<BsonMinKey> getEncoderClass() {
      return BsonMinKey.class;
   }
}
