package org.bson.codecs;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class BsonCodecProvider implements CodecProvider {
   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return Bson.class.isAssignableFrom(clazz) ? new BsonCodec(registry) : null;
   }
}
