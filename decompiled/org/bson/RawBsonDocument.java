package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.RawBsonDocumentCodec;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public final class RawBsonDocument extends BsonDocument {
   private static final long serialVersionUID = 1L;
   private static final int MIN_BSON_DOCUMENT_SIZE = 5;
   private final byte[] bytes;
   private final int offset;
   private final int length;

   public static RawBsonDocument parse(String json) {
      Assertions.notNull("json", json);
      return new RawBsonDocumentCodec().decode(new JsonReader(json), DecoderContext.builder().build());
   }

   public RawBsonDocument(byte[] bytes) {
      this(Assertions.notNull("bytes", bytes), 0, bytes.length);
   }

   public RawBsonDocument(byte[] bytes, int offset, int length) {
      Assertions.notNull("bytes", bytes);
      Assertions.isTrueArgument("offset >= 0", offset >= 0);
      Assertions.isTrueArgument("offset < bytes.length", offset < bytes.length);
      Assertions.isTrueArgument("length <= bytes.length - offset", length <= bytes.length - offset);
      Assertions.isTrueArgument("length >= 5", length >= 5);
      this.bytes = bytes;
      this.offset = offset;
      this.length = length;
   }

   public <T> RawBsonDocument(T document, Codec<T> codec) {
      Assertions.notNull("document", document);
      Assertions.notNull("codec", codec);
      BasicOutputBuffer buffer = new BasicOutputBuffer();
      BsonBinaryWriter writer = new BsonBinaryWriter(buffer);

      try {
         codec.encode(writer, document, EncoderContext.builder().build());
         this.bytes = buffer.getInternalBuffer();
         this.offset = 0;
         this.length = buffer.getPosition();
      } finally {
         writer.close();
      }
   }

   public ByteBuf getByteBuffer() {
      ByteBuffer buffer = ByteBuffer.wrap(this.bytes, this.offset, this.length);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      return new ByteBufNIO(buffer);
   }

   public <T> T decode(Codec<T> codec) {
      return this.decode(codec);
   }

   public <T> T decode(Decoder<T> decoder) {
      BsonBinaryReader reader = this.createReader();

      Object var3;
      try {
         var3 = decoder.decode(reader, DecoderContext.builder().build());
      } finally {
         reader.close();
      }

      return (T)var3;
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
   }

   @Override
   public BsonValue put(String key, BsonValue value) {
      throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
   }

   @Override
   public BsonDocument append(String key, BsonValue value) {
      throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
   }

   @Override
   public void putAll(Map<? extends String, ? extends BsonValue> m) {
      throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
   }

   @Override
   public BsonValue remove(Object key) {
      throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
   }

   @Override
   public boolean isEmpty() {
      BsonBinaryReader bsonReader = this.createReader();

      boolean var2;
      try {
         bsonReader.readStartDocument();
         if (bsonReader.readBsonType() == BsonType.END_OF_DOCUMENT) {
            bsonReader.readEndDocument();
            return true;
         }

         var2 = false;
      } finally {
         bsonReader.close();
      }

      return var2;
   }

   @Override
   public int size() {
      int size = 0;
      BsonBinaryReader bsonReader = this.createReader();

      try {
         bsonReader.readStartDocument();

         while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            size++;
            bsonReader.readName();
            bsonReader.skipValue();
         }

         bsonReader.readEndDocument();
      } finally {
         bsonReader.close();
      }

      return size;
   }

   @Override
   public Set<Entry<String, BsonValue>> entrySet() {
      return this.toBaseBsonDocument().entrySet();
   }

   @Override
   public Collection<BsonValue> values() {
      return this.toBaseBsonDocument().values();
   }

   @Override
   public Set<String> keySet() {
      return this.toBaseBsonDocument().keySet();
   }

   @Override
   public String getFirstKey() {
      BsonBinaryReader bsonReader = this.createReader();

      String e;
      try {
         bsonReader.readStartDocument();

         try {
            e = bsonReader.readName();
         } catch (BsonInvalidOperationException var6) {
            throw new NoSuchElementException();
         }
      } finally {
         bsonReader.close();
      }

      return e;
   }

   @Override
   public boolean containsKey(Object key) {
      if (key == null) {
         throw new IllegalArgumentException("key can not be null");
      } else {
         BsonBinaryReader bsonReader = this.createReader();

         try {
            bsonReader.readStartDocument();

            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
               if (bsonReader.readName().equals(key)) {
                  return true;
               }

               bsonReader.skipValue();
            }

            bsonReader.readEndDocument();
            return false;
         } finally {
            bsonReader.close();
         }
      }
   }

   @Override
   public boolean containsValue(Object value) {
      BsonBinaryReader bsonReader = this.createReader();

      boolean var3;
      try {
         bsonReader.readStartDocument();

         do {
            if (bsonReader.readBsonType() == BsonType.END_OF_DOCUMENT) {
               bsonReader.readEndDocument();
               return false;
            }

            bsonReader.skipName();
         } while (!RawBsonValueHelper.decode(this.bytes, bsonReader).equals(value));

         var3 = true;
      } finally {
         bsonReader.close();
      }

      return var3;
   }

   @Override
   public BsonValue get(Object key) {
      Assertions.notNull("key", key);
      BsonBinaryReader bsonReader = this.createReader();

      try {
         bsonReader.readStartDocument();

         while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (bsonReader.readName().equals(key)) {
               return RawBsonValueHelper.decode(this.bytes, bsonReader);
            }

            bsonReader.skipValue();
         }

         bsonReader.readEndDocument();
         return null;
      } finally {
         bsonReader.close();
      }
   }

   @Override
   public String toJson() {
      return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
   }

   @Override
   public String toJson(JsonWriterSettings settings) {
      StringWriter writer = new StringWriter();
      new RawBsonDocumentCodec().encode(new JsonWriter(writer, settings), this, EncoderContext.builder().build());
      return writer.toString();
   }

   @Override
   public boolean equals(Object o) {
      return this.toBaseBsonDocument().equals(o);
   }

   @Override
   public int hashCode() {
      return this.toBaseBsonDocument().hashCode();
   }

   @Override
   public BsonDocument clone() {
      return new RawBsonDocument((byte[])this.bytes.clone(), this.offset, this.length);
   }

   private BsonBinaryReader createReader() {
      return new BsonBinaryReader(new ByteBufferBsonInput(this.getByteBuffer()));
   }

   private BsonDocument toBaseBsonDocument() {
      BsonBinaryReader bsonReader = this.createReader();

      BsonDocument var2;
      try {
         var2 = new BsonDocumentCodec().decode(bsonReader, DecoderContext.builder().build());
      } finally {
         bsonReader.close();
      }

      return var2;
   }

   private Object writeReplace() {
      return new RawBsonDocument.SerializationProxy(this.bytes, this.offset, this.length);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 1L;
      private final byte[] bytes;

      SerializationProxy(byte[] bytes, int offset, int length) {
         if (bytes.length == length) {
            this.bytes = bytes;
         } else {
            this.bytes = new byte[length];
            System.arraycopy(bytes, offset, this.bytes, 0, length);
         }
      }

      private Object readResolve() {
         return new RawBsonDocument(this.bytes);
      }
   }
}
