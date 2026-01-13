package org.bson.codecs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class MapCodec implements Codec<Map<String, Object>>, OverridableUuidRepresentationCodec<Map<String, Object>> {
   private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(
      Arrays.asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecProvider(), new IterableCodecProvider(), new MapCodecProvider())
   );
   private static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();
   private final BsonTypeCodecMap bsonTypeCodecMap;
   private final CodecRegistry registry;
   private final Transformer valueTransformer;
   private final UuidRepresentation uuidRepresentation;

   public MapCodec() {
      this(DEFAULT_REGISTRY);
   }

   public MapCodec(CodecRegistry registry) {
      this(registry, DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public MapCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap) {
      this(registry, bsonTypeClassMap, null);
   }

   public MapCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer, UuidRepresentation.UNSPECIFIED);
   }

   private MapCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer, UuidRepresentation uuidRepresentation) {
      this.registry = Assertions.notNull("registry", registry);
      this.bsonTypeCodecMap = bsonTypeCodecMap;
      this.valueTransformer = valueTransformer != null ? valueTransformer : new Transformer() {
         @Override
         public Object transform(Object value) {
            return value;
         }
      };
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public Codec<Map<String, Object>> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return new MapCodec(this.registry, this.bsonTypeCodecMap, this.valueTransformer, uuidRepresentation);
   }

   public void encode(BsonWriter writer, Map<String, Object> map, EncoderContext encoderContext) {
      writer.writeStartDocument();

      for (Entry<String, Object> entry : map.entrySet()) {
         writer.writeName(entry.getKey());
         this.writeValue(writer, encoderContext, entry.getValue());
      }

      writer.writeEndDocument();
   }

   public Map<String, Object> decode(BsonReader reader, DecoderContext decoderContext) {
      Map<String, Object> map = new HashMap<>();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         String fieldName = reader.readName();
         map.put(fieldName, this.readValue(reader, decoderContext));
      }

      reader.readEndDocument();
      return map;
   }

   @Override
   public Class<Map<String, Object>> getEncoderClass() {
      return Map.class;
   }

   private Object readValue(BsonReader reader, DecoderContext decoderContext) {
      BsonType bsonType = reader.getCurrentBsonType();
      if (bsonType == BsonType.NULL) {
         reader.readNull();
         return null;
      } else if (bsonType == BsonType.ARRAY) {
         return decoderContext.decodeWithChildContext(this.registry.get(List.class), reader);
      } else if (bsonType == BsonType.BINARY && reader.peekBinarySize() == 16) {
         Codec<?> codec = this.bsonTypeCodecMap.get(bsonType);
         switch (reader.peekBinarySubType()) {
            case 3:
               if (this.uuidRepresentation == UuidRepresentation.JAVA_LEGACY
                  || this.uuidRepresentation == UuidRepresentation.C_SHARP_LEGACY
                  || this.uuidRepresentation == UuidRepresentation.PYTHON_LEGACY) {
                  codec = this.registry.get(UUID.class);
               }
               break;
            case 4:
               if (this.uuidRepresentation == UuidRepresentation.STANDARD) {
                  codec = this.registry.get(UUID.class);
               }
         }

         return decoderContext.decodeWithChildContext(codec, reader);
      } else {
         return this.valueTransformer.transform(this.bsonTypeCodecMap.get(bsonType).decode(reader, decoderContext));
      }
   }

   private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
      if (value == null) {
         writer.writeNull();
      } else {
         Codec codec = this.registry.get(value.getClass());
         encoderContext.encodeWithChildContext(codec, writer, value);
      }
   }
}
