package org.bson.codecs;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonObject;

public final class JsonObjectCodecProvider implements CodecProvider {
   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return clazz.equals(JsonObject.class) ? new JsonObjectCodec() : null;
   }
}
