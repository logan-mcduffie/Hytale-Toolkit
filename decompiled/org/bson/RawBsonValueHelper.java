package org.bson;

import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BsonInputMark;

final class RawBsonValueHelper {
   private static final CodecRegistry REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());

   static BsonValue decode(byte[] bytes, BsonBinaryReader bsonReader) {
      if (bsonReader.getCurrentBsonType() != BsonType.DOCUMENT && bsonReader.getCurrentBsonType() != BsonType.ARRAY) {
         return REGISTRY.get(BsonValueCodecProvider.getClassForBsonType(bsonReader.getCurrentBsonType())).decode(bsonReader, DecoderContext.builder().build());
      } else {
         int position = bsonReader.getBsonInput().getPosition();
         BsonInputMark mark = bsonReader.getBsonInput().getMark(4);
         int size = bsonReader.getBsonInput().readInt32();
         mark.reset();
         bsonReader.skipValue();
         return (BsonValue)(bsonReader.getCurrentBsonType() == BsonType.DOCUMENT
            ? new RawBsonDocument(bytes, position, size)
            : new RawBsonArray(bytes, position, size));
      }
   }

   private RawBsonValueHelper() {
   }
}
