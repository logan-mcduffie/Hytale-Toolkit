package org.bson.codecs.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.codecs.Codec;

final class MapOfCodecsProvider implements CodecProvider {
   private final Map<Class<?>, Codec<?>> codecsMap = new HashMap<>();

   MapOfCodecsProvider(List<? extends Codec<?>> codecsList) {
      for (Codec<?> codec : codecsList) {
         this.codecsMap.put(codec.getEncoderClass(), codec);
      }
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return (Codec<T>)this.codecsMap.get(clazz);
   }
}
