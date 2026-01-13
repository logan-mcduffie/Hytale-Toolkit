package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;

public class BsonStringCodec implements Codec<BsonString> {
   public BsonString decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonString(reader.readString());
   }

   public void encode(BsonWriter writer, BsonString value, EncoderContext encoderContext) {
      writer.writeString(value.getValue());
   }

   @Override
   public Class<BsonString> getEncoderClass() {
      return BsonString.class;
   }
}
