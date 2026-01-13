package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonTimestamp;
import org.bson.BsonWriter;

public class BsonTimestampCodec implements Codec<BsonTimestamp> {
   public void encode(BsonWriter writer, BsonTimestamp value, EncoderContext encoderContext) {
      writer.writeTimestamp(value);
   }

   public BsonTimestamp decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readTimestamp();
   }

   @Override
   public Class<BsonTimestamp> getEncoderClass() {
      return BsonTimestamp.class;
   }
}
