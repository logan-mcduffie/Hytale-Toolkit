package org.bson.codecs;

import org.bson.BsonNull;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonNullCodec implements Codec<BsonNull> {
   public BsonNull decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readNull();
      return BsonNull.VALUE;
   }

   public void encode(BsonWriter writer, BsonNull value, EncoderContext encoderContext) {
      writer.writeNull();
   }

   @Override
   public Class<BsonNull> getEncoderClass() {
      return BsonNull.class;
   }
}
