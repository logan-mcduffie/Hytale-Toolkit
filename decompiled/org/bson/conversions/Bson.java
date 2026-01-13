package org.bson.conversions;

import java.util.Arrays;
import org.bson.BsonDocument;
import org.bson.codecs.BsonCodecProvider;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.JsonObjectCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;

public interface Bson {
   CodecRegistry DEFAULT_CODEC_REGISTRY = CodecRegistries.fromProviders(
      Arrays.asList(
         new ValueCodecProvider(),
         new BsonValueCodecProvider(),
         new DocumentCodecProvider(),
         new IterableCodecProvider(),
         new MapCodecProvider(),
         new Jsr310CodecProvider(),
         new JsonObjectCodecProvider(),
         new BsonCodecProvider()
      )
   );

   <TDocument> BsonDocument toBsonDocument(Class<TDocument> var1, CodecRegistry var2);

   default BsonDocument toBsonDocument() {
      return this.toBsonDocument(BsonDocument.class, DEFAULT_CODEC_REGISTRY);
   }
}
