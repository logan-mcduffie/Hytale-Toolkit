package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

final class CollectionPropertyCodecProvider implements PropertyCodecProvider {
   @Override
   public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry registry) {
      return (Codec<T>)(Collection.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 1
         ? new CollectionPropertyCodecProvider.CollectionCodec<>(type.getType(), registry.get((TypeWithTypeParameters<?>)type.getTypeParameters().get(0)))
         : null);
   }

   private static class CollectionCodec<T> implements Codec<Collection<T>> {
      private final Class<Collection<T>> encoderClass;
      private final Codec<T> codec;

      CollectionCodec(Class<Collection<T>> encoderClass, Codec<T> codec) {
         this.encoderClass = encoderClass;
         this.codec = codec;
      }

      public void encode(BsonWriter writer, Collection<T> collection, EncoderContext encoderContext) {
         writer.writeStartArray();

         for (T value : collection) {
            if (value == null) {
               writer.writeNull();
            } else {
               this.codec.encode(writer, value, encoderContext);
            }
         }

         writer.writeEndArray();
      }

      public Collection<T> decode(BsonReader reader, DecoderContext context) {
         Collection<T> collection = this.getInstance();
         reader.readStartArray();

         while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (reader.getCurrentBsonType() == BsonType.NULL) {
               collection.add(null);
               reader.readNull();
            } else {
               collection.add(this.codec.decode(reader, context));
            }
         }

         reader.readEndArray();
         return collection;
      }

      @Override
      public Class<Collection<T>> getEncoderClass() {
         return this.encoderClass;
      }

      private Collection<T> getInstance() {
         if (this.encoderClass.isInterface()) {
            if (this.encoderClass.isAssignableFrom(ArrayList.class)) {
               return new ArrayList<>();
            } else if (this.encoderClass.isAssignableFrom(HashSet.class)) {
               return new HashSet<>();
            } else {
               throw new CodecConfigurationException(String.format("Unsupported Collection interface of %s!", this.encoderClass.getName()));
            }
         } else {
            try {
               return this.encoderClass.getDeclaredConstructor().newInstance();
            } catch (Exception var2) {
               throw new CodecConfigurationException(var2.getMessage(), var2);
            }
         }
      }
   }
}
