package org.bson.codecs;

import java.util.UUID;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UuidCodecProvider implements CodecProvider {
   private UuidRepresentation uuidRepresentation;

   public UuidCodecProvider(UuidRepresentation uuidRepresentation) {
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return clazz == UUID.class ? new UuidCodec(this.uuidRepresentation) : null;
   }
}
