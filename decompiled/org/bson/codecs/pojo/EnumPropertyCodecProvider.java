package org.bson.codecs.pojo;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;

final class EnumPropertyCodecProvider implements PropertyCodecProvider {
   private final CodecRegistry codecRegistry;

   EnumPropertyCodecProvider(CodecRegistry codecRegistry) {
      this.codecRegistry = codecRegistry;
   }

   @Override
   public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry propertyCodecRegistry) {
      Class<T> clazz = type.getType();
      if (Enum.class.isAssignableFrom(clazz)) {
         try {
            return this.codecRegistry.get(clazz);
         } catch (CodecConfigurationException var5) {
            return new EnumPropertyCodecProvider.EnumCodec(clazz);
         }
      } else {
         return null;
      }
   }

   private static class EnumCodec<T extends Enum<T>> implements Codec<T> {
      private final Class<T> clazz;

      EnumCodec(Class<T> clazz) {
         this.clazz = clazz;
      }

      public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
         writer.writeString(value.name());
      }

      @Override
      public Class<T> getEncoderClass() {
         return this.clazz;
      }

      public T decode(BsonReader reader, DecoderContext decoderContext) {
         return Enum.valueOf(this.clazz, reader.readString());
      }
   }
}
