package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class ByteArrayCodec implements Codec<byte[]> {
   public void encode(BsonWriter writer, byte[] value, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(value));
   }

   public byte[] decode(BsonReader reader, DecoderContext decoderContext) {
      return reader.readBinaryData().getData();
   }

   @Override
   public Class<byte[]> getEncoderClass() {
      return byte[].class;
   }
}
