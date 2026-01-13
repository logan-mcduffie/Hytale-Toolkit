package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.Binary;

public class BinaryCodec implements Codec<Binary> {
   public void encode(BsonWriter writer, Binary value, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(value.getType(), value.getData()));
   }

   public Binary decode(BsonReader reader, DecoderContext decoderContext) {
      BsonBinary bsonBinary = reader.readBinaryData();
      return new Binary(bsonBinary.getType(), bsonBinary.getData());
   }

   @Override
   public Class<Binary> getEncoderClass() {
      return Binary.class;
   }
}
