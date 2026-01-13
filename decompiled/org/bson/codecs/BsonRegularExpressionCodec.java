package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonRegularExpression;
import org.bson.BsonWriter;

public class BsonRegularExpressionCodec implements Codec<BsonRegularExpression> {
   public BsonRegularExpression decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readRegularExpression();
   }

   public void encode(BsonWriter writer, BsonRegularExpression value, EncoderContext encoderContext) {
      writer.writeRegularExpression(value);
   }

   @Override
   public Class<BsonRegularExpression> getEncoderClass() {
      return BsonRegularExpression.class;
   }
}
