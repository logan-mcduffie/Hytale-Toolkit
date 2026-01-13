package org.bson.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class FloatCodec implements Codec<Float> {
   public void encode(BsonWriter writer, Float value, EncoderContext encoderContext) {
      writer.writeDouble(value.floatValue());
   }

   public Float decode(BsonReader reader, DecoderContext decoderContext) {
      double value = NumberCodecHelper.decodeDouble(reader);
      if (!(value < -Float.MAX_VALUE) && !(value > Float.MAX_VALUE)) {
         return (float)value;
      } else {
         throw new BsonInvalidOperationException(String.format("%s can not be converted into a Float.", value));
      }
   }

   @Override
   public Class<Float> getEncoderClass() {
      return Float.class;
   }
}
