package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonUndefined;
import org.bson.BsonWriter;

public class BsonUndefinedCodec implements Codec<BsonUndefined> {
   public BsonUndefined decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readUndefined();
      return new BsonUndefined();
   }

   public void encode(BsonWriter writer, BsonUndefined value, EncoderContext encoderContext) {
      writer.writeUndefined();
   }

   @Override
   public Class<BsonUndefined> getEncoderClass() {
      return BsonUndefined.class;
   }
}
