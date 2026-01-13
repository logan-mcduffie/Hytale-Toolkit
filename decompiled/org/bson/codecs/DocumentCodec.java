package org.bson.codecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class DocumentCodec implements CollectibleCodec<Document>, OverridableUuidRepresentationCodec<Document> {
   private static final String ID_FIELD_NAME = "_id";
   private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(
      Arrays.asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecProvider())
   );
   private static final BsonTypeCodecMap DEFAULT_BSON_TYPE_CODEC_MAP = new BsonTypeCodecMap(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, DEFAULT_REGISTRY);
   private static final IdGenerator DEFAULT_ID_GENERATOR = new ObjectIdGenerator();
   private final BsonTypeCodecMap bsonTypeCodecMap;
   private final CodecRegistry registry;
   private final IdGenerator idGenerator;
   private final Transformer valueTransformer;
   private final UuidRepresentation uuidRepresentation;

   public DocumentCodec() {
      this(DEFAULT_REGISTRY, DEFAULT_BSON_TYPE_CODEC_MAP, null);
   }

   public DocumentCodec(CodecRegistry registry) {
      this(registry, BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public DocumentCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap) {
      this(registry, bsonTypeClassMap, null);
   }

   public DocumentCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer);
   }

   private DocumentCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer) {
      this(registry, bsonTypeCodecMap, DEFAULT_ID_GENERATOR, valueTransformer, UuidRepresentation.UNSPECIFIED);
   }

   private DocumentCodec(
      CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, IdGenerator idGenerator, Transformer valueTransformer, UuidRepresentation uuidRepresentation
   ) {
      this.registry = Assertions.notNull("registry", registry);
      this.bsonTypeCodecMap = bsonTypeCodecMap;
      this.idGenerator = idGenerator;
      this.valueTransformer = valueTransformer != null ? valueTransformer : new Transformer() {
         @Override
         public Object transform(Object value) {
            return value;
         }
      };
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public Codec<Document> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return new DocumentCodec(this.registry, this.bsonTypeCodecMap, this.idGenerator, this.valueTransformer, uuidRepresentation);
   }

   public boolean documentHasId(Document document) {
      return document.containsKey("_id");
   }

   public BsonValue getDocumentId(Document document) {
      if (!this.documentHasId(document)) {
         throw new IllegalStateException("The document does not contain an _id");
      } else {
         Object id = document.get("_id");
         if (id instanceof BsonValue) {
            return (BsonValue)id;
         } else {
            BsonDocument idHoldingDocument = new BsonDocument();
            BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
            writer.writeStartDocument();
            writer.writeName("_id");
            this.writeValue(writer, EncoderContext.builder().build(), id);
            writer.writeEndDocument();
            return idHoldingDocument.get("_id");
         }
      }
   }

   public Document generateIdIfAbsentFromDocument(Document document) {
      if (!this.documentHasId(document)) {
         document.put("_id", this.idGenerator.generate());
      }

      return document;
   }

   public void encode(BsonWriter writer, Document document, EncoderContext encoderContext) {
      this.writeMap(writer, document, encoderContext);
   }

   public Document decode(BsonReader reader, DecoderContext decoderContext) {
      Document document = new Document();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         String fieldName = reader.readName();
         document.put(fieldName, this.readValue(reader, decoderContext));
      }

      reader.readEndDocument();
      return document;
   }

   @Override
   public Class<Document> getEncoderClass() {
      return Document.class;
   }

   private void beforeFields(BsonWriter bsonWriter, EncoderContext encoderContext, Map<String, Object> document) {
      if (encoderContext.isEncodingCollectibleDocument() && document.containsKey("_id")) {
         bsonWriter.writeName("_id");
         this.writeValue(bsonWriter, encoderContext, document.get("_id"));
      }
   }

   private boolean skipField(EncoderContext encoderContext, String key) {
      return encoderContext.isEncodingCollectibleDocument() && key.equals("_id");
   }

   private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
      if (value == null) {
         writer.writeNull();
      } else if (value instanceof Iterable) {
         this.writeIterable(writer, (Iterable<Object>)value, encoderContext.getChildContext());
      } else if (value instanceof Map) {
         this.writeMap(writer, (Map<String, Object>)value, encoderContext.getChildContext());
      } else {
         Codec codec = this.registry.get(value.getClass());
         encoderContext.encodeWithChildContext(codec, writer, value);
      }
   }

   private void writeMap(BsonWriter writer, Map<String, Object> map, EncoderContext encoderContext) {
      writer.writeStartDocument();
      this.beforeFields(writer, encoderContext, map);

      for (Entry<String, Object> entry : map.entrySet()) {
         if (!this.skipField(encoderContext, entry.getKey())) {
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
         }
      }

      writer.writeEndDocument();
   }

   private void writeIterable(BsonWriter writer, Iterable<Object> list, EncoderContext encoderContext) {
      writer.writeStartArray();

      for (Object value : list) {
         this.writeValue(writer, encoderContext, value);
      }

      writer.writeEndArray();
   }

   private Object readValue(BsonReader reader, DecoderContext decoderContext) {
      BsonType bsonType = reader.getCurrentBsonType();
      if (bsonType == BsonType.NULL) {
         reader.readNull();
         return null;
      } else if (bsonType == BsonType.ARRAY) {
         return this.readList(reader, decoderContext);
      } else {
         Codec<?> codec = this.bsonTypeCodecMap.get(bsonType);
         if (bsonType == BsonType.BINARY && reader.peekBinarySize() == 16) {
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
         }

         return this.valueTransformer.transform(codec.decode(reader, decoderContext));
      }
   }

   private List<Object> readList(BsonReader reader, DecoderContext decoderContext) {
      reader.readStartArray();
      List<Object> list = new ArrayList<>();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         list.add(this.readValue(reader, decoderContext));
      }

      reader.readEndArray();
      return list;
   }
}
