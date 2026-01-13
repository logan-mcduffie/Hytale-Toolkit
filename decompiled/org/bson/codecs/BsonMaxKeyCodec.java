package org.bson.codecs;

import org.bson.BsonMaxKey;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonMaxKeyCodec implements Codec<BsonMaxKey> {
   public void encode(BsonWriter writer, BsonMaxKey value, EncoderContext encoderContext) {
      writer.writeMaxKey();
   }

   public BsonMaxKey decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readMaxKey();
      return new BsonMaxKey();
   }

   @Override
   public Class<BsonMaxKey> getEncoderClass() {
      return BsonMaxKey.class;
   }
}
