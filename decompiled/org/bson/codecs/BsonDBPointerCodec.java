package org.bson.codecs;

import org.bson.BsonDbPointer;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonDBPointerCodec implements Codec<BsonDbPointer> {
   public BsonDbPointer decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readDBPointer();
   }

   public void encode(BsonWriter writer, BsonDbPointer value, EncoderContext encoderContext) {
      writer.writeDBPointer(value);
   }

   @Override
   public Class<BsonDbPointer> getEncoderClass() {
      return BsonDbPointer.class;
   }
}
