package org.bson.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class ByteCodec implements Codec<Byte> {
   public void encode(BsonWriter writer, Byte value, EncoderContext encoderContext) {
      writer.writeInt32(value);
   }

   public Byte decode(BsonReader reader, DecoderContext decoderContext) {
      int value = NumberCodecHelper.decodeInt(reader);
      if (value >= -128 && value <= 127) {
         return (byte)value;
      } else {
         throw new BsonInvalidOperationException(String.format("%s can not be converted into a Byte.", value));
      }
   }

   @Override
   public Class<Byte> getEncoderClass() {
      return Byte.class;
   }
}
