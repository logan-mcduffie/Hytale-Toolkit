package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class BsonDocument extends BsonValue implements Map<String, BsonValue>, Cloneable, Bson, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<String, BsonValue> map = new LinkedHashMap<>();

   public static BsonDocument parse(String json) {
      return new BsonDocumentCodec().decode(new JsonReader(json), DecoderContext.builder().build());
   }

   public BsonDocument(List<BsonElement> bsonElements) {
      for (BsonElement cur : bsonElements) {
         this.put(cur.getName(), cur.getValue());
      }
   }

   public BsonDocument(String key, BsonValue value) {
      this.put(key, value);
   }

   public BsonDocument() {
   }

   @Override
   public <C> BsonDocument toBsonDocument(Class<C> documentClass, CodecRegistry codecRegistry) {
      return this;
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.DOCUMENT;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.map.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.map.containsValue(value);
   }

   public BsonValue get(Object key) {
      return this.map.get(key);
   }

   public BsonDocument getDocument(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asDocument();
   }

   public BsonArray getArray(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asArray();
   }

   public BsonNumber getNumber(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asNumber();
   }

   public BsonInt32 getInt32(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asInt32();
   }

   public BsonInt64 getInt64(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asInt64();
   }

   public BsonDecimal128 getDecimal128(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asDecimal128();
   }

   public BsonDouble getDouble(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asDouble();
   }

   public BsonBoolean getBoolean(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asBoolean();
   }

   public BsonString getString(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asString();
   }

   public BsonDateTime getDateTime(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asDateTime();
   }

   public BsonTimestamp getTimestamp(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asTimestamp();
   }

   public BsonObjectId getObjectId(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asObjectId();
   }

   public BsonRegularExpression getRegularExpression(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asRegularExpression();
   }

   public BsonBinary getBinary(Object key) {
      this.throwIfKeyAbsent(key);
      return this.get(key).asBinary();
   }

   public boolean isNull(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isNull();
   }

   public boolean isDocument(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isDocument();
   }

   public boolean isArray(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isArray();
   }

   public boolean isNumber(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isNumber();
   }

   public boolean isInt32(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isInt32();
   }

   public boolean isInt64(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isInt64();
   }

   public boolean isDecimal128(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isDecimal128();
   }

   public boolean isDouble(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isDouble();
   }

   public boolean isBoolean(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isBoolean();
   }

   public boolean isString(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isString();
   }

   public boolean isDateTime(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isDateTime();
   }

   public boolean isTimestamp(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isTimestamp();
   }

   public boolean isObjectId(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isObjectId();
   }

   public boolean isBinary(Object key) {
      return !this.containsKey(key) ? false : this.get(key).isBinary();
   }

   public BsonValue get(Object key, BsonValue defaultValue) {
      BsonValue value = this.get(key);
      return value != null ? value : defaultValue;
   }

   public BsonDocument getDocument(Object key, BsonDocument defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asDocument();
   }

   public BsonArray getArray(Object key, BsonArray defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asArray();
   }

   public BsonNumber getNumber(Object key, BsonNumber defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asNumber();
   }

   public BsonInt32 getInt32(Object key, BsonInt32 defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asInt32();
   }

   public BsonInt64 getInt64(Object key, BsonInt64 defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asInt64();
   }

   public BsonDecimal128 getDecimal128(Object key, BsonDecimal128 defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asDecimal128();
   }

   public BsonDouble getDouble(Object key, BsonDouble defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asDouble();
   }

   public BsonBoolean getBoolean(Object key, BsonBoolean defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asBoolean();
   }

   public BsonString getString(Object key, BsonString defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asString();
   }

   public BsonDateTime getDateTime(Object key, BsonDateTime defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asDateTime();
   }

   public BsonTimestamp getTimestamp(Object key, BsonTimestamp defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asTimestamp();
   }

   public BsonObjectId getObjectId(Object key, BsonObjectId defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asObjectId();
   }

   public BsonBinary getBinary(Object key, BsonBinary defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asBinary();
   }

   public BsonRegularExpression getRegularExpression(Object key, BsonRegularExpression defaultValue) {
      return !this.containsKey(key) ? defaultValue : this.get(key).asRegularExpression();
   }

   public BsonValue put(String key, BsonValue value) {
      if (value == null) {
         throw new IllegalArgumentException(String.format("The value for key %s can not be null", key));
      } else if (key.contains("\u0000")) {
         throw new BSONException(String.format("BSON cstring '%s' is not valid because it contains a null character at index %d", key, key.indexOf(0)));
      } else {
         return this.map.put(key, value);
      }
   }

   public BsonValue remove(Object key) {
      return this.map.remove(key);
   }

   @Override
   public void putAll(Map<? extends String, ? extends BsonValue> m) {
      for (Entry<? extends String, ? extends BsonValue> cur : m.entrySet()) {
         this.put(cur.getKey(), cur.getValue());
      }
   }

   @Override
   public void clear() {
      this.map.clear();
   }

   @Override
   public Set<String> keySet() {
      return this.map.keySet();
   }

   @Override
   public Collection<BsonValue> values() {
      return this.map.values();
   }

   @Override
   public Set<Entry<String, BsonValue>> entrySet() {
      return this.map.entrySet();
   }

   public BsonDocument append(String key, BsonValue value) {
      this.put(key, value);
      return this;
   }

   public String getFirstKey() {
      return this.keySet().iterator().next();
   }

   public BsonReader asBsonReader() {
      return new BsonDocumentReader(this);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof BsonDocument)) {
         return false;
      } else {
         BsonDocument that = (BsonDocument)o;
         return this.entrySet().equals(that.entrySet());
      }
   }

   @Override
   public int hashCode() {
      return this.entrySet().hashCode();
   }

   public String toJson() {
      return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
   }

   public String toJson(JsonWriterSettings settings) {
      StringWriter writer = new StringWriter();
      new BsonDocumentCodec().encode(new JsonWriter(writer, settings), this, EncoderContext.builder().build());
      return writer.toString();
   }

   @Override
   public String toString() {
      return this.toJson();
   }

   public BsonDocument clone() {
      BsonDocument to = new BsonDocument();

      for (Entry<String, BsonValue> cur : this.entrySet()) {
         switch (cur.getValue().getBsonType()) {
            case DOCUMENT:
               to.put(cur.getKey(), cur.getValue().asDocument().clone());
               break;
            case ARRAY:
               to.put(cur.getKey(), cur.getValue().asArray().clone());
               break;
            case BINARY:
               to.put(cur.getKey(), BsonBinary.clone(cur.getValue().asBinary()));
               break;
            case JAVASCRIPT_WITH_SCOPE:
               to.put(cur.getKey(), BsonJavaScriptWithScope.clone(cur.getValue().asJavaScriptWithScope()));
               break;
            default:
               to.put(cur.getKey(), cur.getValue());
         }
      }

      return to;
   }

   private void throwIfKeyAbsent(Object key) {
      if (!this.containsKey(key)) {
         throw new BsonInvalidOperationException("Document does not contain key " + key);
      }
   }

   private Object writeReplace() {
      return new BsonDocument.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 1L;
      private final byte[] bytes;

      SerializationProxy(BsonDocument document) {
         BasicOutputBuffer buffer = new BasicOutputBuffer();
         new BsonDocumentCodec().encode(new BsonBinaryWriter(buffer), document, EncoderContext.builder().build());
         this.bytes = new byte[buffer.size()];
         int curPos = 0;

         for (ByteBuf cur : buffer.getByteBuffers()) {
            System.arraycopy(cur.array(), cur.position(), this.bytes, curPos, cur.limit());
            curPos += cur.position();
         }
      }

      private Object readResolve() {
         return new BsonDocumentCodec()
            .decode(new BsonBinaryReader(ByteBuffer.wrap(this.bytes).order(ByteOrder.LITTLE_ENDIAN)), DecoderContext.builder().build());
      }
   }
}
