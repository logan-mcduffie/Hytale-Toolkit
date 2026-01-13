package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonBinaryCodec implements Codec<BsonBinary> {
   public void encode(BsonWriter writer, BsonBinary value, EncoderContext encoderContext) {
      writer.writeBinaryData(value);
   }

   public BsonBinary decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readBinaryData();
   }

   @Override
   public Class<BsonBinary> getEncoderClass() {
      return BsonBinary.class;
   }
}
