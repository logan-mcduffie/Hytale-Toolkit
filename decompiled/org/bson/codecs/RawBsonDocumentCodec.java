package org.bson.codecs;

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;

public class RawBsonDocumentCodec implements Codec<RawBsonDocument> {
   public void encode(BsonWriter writer, RawBsonDocument value, EncoderContext encoderContext) {
      BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(value.getByteBuffer()));

      try {
         writer.pipe(reader);
      } finally {
         reader.close();
      }
   }

   public RawBsonDocument decode(BsonReader reader, DecoderContext decoderContext) {
      BasicOutputBuffer buffer = new BasicOutputBuffer(0);
      BsonBinaryWriter writer = new BsonBinaryWriter(buffer);

      RawBsonDocument var5;
      try {
         writer.pipe(reader);
         var5 = new RawBsonDocument(buffer.getInternalBuffer(), 0, buffer.getPosition());
      } finally {
         writer.close();
         buffer.close();
      }

      return var5;
   }

   @Override
   public Class<RawBsonDocument> getEncoderClass() {
      return RawBsonDocument.class;
   }
}
