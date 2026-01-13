package org.bson.codecs.jsr310;

import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class Jsr310CodecProvider implements CodecProvider {
   private static final Map<Class<?>, Codec<?>> JSR310_CODEC_MAP = new HashMap<>();

   private static void putCodec(Codec<?> codec) {
      JSR310_CODEC_MAP.put(codec.getEncoderClass(), codec);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return (Codec<T>)JSR310_CODEC_MAP.get(clazz);
   }

   static {
      try {
         Class.forName("java.time.Instant");
         putCodec(new InstantCodec());
         putCodec(new LocalDateCodec());
         putCodec(new LocalDateTimeCodec());
         putCodec(new LocalTimeCodec());
      } catch (ClassNotFoundException var1) {
      }
   }
}
