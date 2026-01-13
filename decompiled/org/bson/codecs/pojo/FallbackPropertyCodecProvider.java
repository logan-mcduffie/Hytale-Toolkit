package org.bson.codecs.pojo;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

final class FallbackPropertyCodecProvider implements PropertyCodecProvider {
   private final CodecRegistry codecRegistry;
   private final PojoCodec<?> pojoCodec;

   FallbackPropertyCodecProvider(PojoCodec<?> pojoCodec, CodecRegistry codecRegistry) {
      this.pojoCodec = pojoCodec;
      this.codecRegistry = codecRegistry;
   }

   @Override
   public <S> Codec<S> get(TypeWithTypeParameters<S> type, PropertyCodecRegistry propertyCodecRegistry) {
      Class<S> clazz = type.getType();
      return (Codec<S>)(clazz == this.pojoCodec.getEncoderClass() ? this.pojoCodec : this.codecRegistry.get(type.getType()));
   }
}
