package org.bson.codecs;

import java.math.BigDecimal;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.Decimal128;

public final class BigDecimalCodec implements Codec<BigDecimal> {
   public void encode(BsonWriter writer, BigDecimal value, EncoderContext encoderContext) {
      writer.writeDecimal128(new Decimal128(value));
   }

   public BigDecimal decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readDecimal128().bigDecimalValue();
   }

   @Override
   public Class<BigDecimal> getEncoderClass() {
      return BigDecimal.class;
   }
}
