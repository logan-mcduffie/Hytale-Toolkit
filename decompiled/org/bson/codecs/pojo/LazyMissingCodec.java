package org.bson.codecs.pojo;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

class LazyMissingCodec<S> implements Codec<S> {
   private final Class<S> clazz;
   private final CodecConfigurationException exception;

   LazyMissingCodec(Class<S> clazz, CodecConfigurationException exception) {
      this.clazz = clazz;
      this.exception = exception;
   }

   @Override
   public S decode(BsonReader reader, DecoderContext decoderContext) {
      throw this.exception;
   }

   @Override
   public void encode(BsonWriter writer, S value, EncoderContext encoderContext) {
      throw this.exception;
   }

   @Override
   public Class<S> getEncoderClass() {
      return this.clazz;
   }
}
