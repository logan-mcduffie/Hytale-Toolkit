package org.bson;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bson.assertions.Assertions;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

public class Document implements Map<String, Object>, Serializable, Bson {
   private static final long serialVersionUID = 6297731997167536582L;
   private final LinkedHashMap<String, Object> documentAsMap;

   public Document() {
      this.documentAsMap = new LinkedHashMap<>();
   }

   public Document(String key, Object value) {
      this.documentAsMap = new LinkedHashMap<>();
      this.documentAsMap.put(key, value);
   }

   public Document(Map<String, Object> map) {
      this.documentAsMap = new LinkedHashMap<>(map);
   }

   public static Document parse(String json) {
      return parse(json, new DocumentCodec());
   }

   public static Document parse(String json, Decoder<Document> decoder) {
      Assertions.notNull("codec", decoder);
      JsonReader bsonReader = new JsonReader(json);
      return decoder.decode(bsonReader, DecoderContext.builder().build());
   }

   @Override
   public <C> BsonDocument toBsonDocument(Class<C> documentClass, CodecRegistry codecRegistry) {
      return new BsonDocumentWrapper<>(this, codecRegistry.get(Document.class));
   }

   public Document append(String key, Object value) {
      this.documentAsMap.put(key, value);
      return this;
   }

   public <T> T get(Object key, Class<T> clazz) {
      Assertions.notNull("clazz", clazz);
      return clazz.cast(this.documentAsMap.get(key));
   }

   public <T> T get(Object key, T defaultValue) {
      Assertions.notNull("defaultValue", defaultValue);
      Object value = this.documentAsMap.get(key);
      return (T)(value == null ? defaultValue : value);
   }

   public <T> T getEmbedded(List<?> keys, Class<T> clazz) {
      Assertions.notNull("keys", keys);
      Assertions.isTrue("keys", !keys.isEmpty());
      Assertions.notNull("clazz", clazz);
      return this.getEmbeddedValue(keys, clazz, null);
   }

   public <T> T getEmbedded(List<?> keys, T defaultValue) {
      Assertions.notNull("keys", keys);
      Assertions.isTrue("keys", !keys.isEmpty());
      Assertions.notNull("defaultValue", defaultValue);
      return this.getEmbeddedValue(keys, null, defaultValue);
   }

   private <T> T getEmbeddedValue(List<?> keys, Class<T> clazz, T defaultValue) {
      Object value = this;
      Iterator<?> keyIterator = keys.iterator();

      while (keyIterator.hasNext()) {
         Object key = keyIterator.next();
         value = ((Document)value).get(key);
         if (!(value instanceof Document)) {
            if (value == null) {
               return defaultValue;
            }

            if (keyIterator.hasNext()) {
               throw new ClassCastException(String.format("At key %s, the value is not a Document (%s)", key, value.getClass().getName()));
            }
         }
      }

      return (T)(clazz != null ? clazz.cast(value) : value);
   }

   public Integer getInteger(Object key) {
      return (Integer)this.get(key);
   }

   public int getInteger(Object key, int defaultValue) {
      return this.get(key, defaultValue);
   }

   public Long getLong(Object key) {
      return (Long)this.get(key);
   }

   public Double getDouble(Object key) {
      return (Double)this.get(key);
   }

   public String getString(Object key) {
      return (String)this.get(key);
   }

   public Boolean getBoolean(Object key) {
      return (Boolean)this.get(key);
   }

   public boolean getBoolean(Object key, boolean defaultValue) {
      return this.get(key, defaultValue);
   }

   public ObjectId getObjectId(Object key) {
      return (ObjectId)this.get(key);
   }

   public Date getDate(Object key) {
      return (Date)this.get(key);
   }

   public <T> List<T> getList(Object key, Class<T> clazz) {
      Assertions.notNull("clazz", clazz);
      return this.constructValuesList(key, clazz, null);
   }

   public <T> List<T> getList(Object key, Class<T> clazz, List<T> defaultValue) {
      Assertions.notNull("defaultValue", defaultValue);
      Assertions.notNull("clazz", clazz);
      return this.constructValuesList(key, clazz, defaultValue);
   }

   private <T> List<T> constructValuesList(Object key, Class<T> clazz, List<T> defaultValue) {
      List<?> value = this.get(key, List.class);
      if (value == null) {
         return defaultValue;
      } else {
         for (Object item : value) {
            if (!clazz.isAssignableFrom(item.getClass())) {
               throw new ClassCastException(String.format("List element cannot be cast to %s", clazz.getName()));
            }
         }

         return (List<T>)value;
      }
   }

   public String toJson() {
      return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
   }

   public String toJson(JsonWriterSettings writerSettings) {
      return this.toJson(writerSettings, new DocumentCodec());
   }

   public String toJson(Encoder<Document> encoder) {
      return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build(), encoder);
   }

   public String toJson(JsonWriterSettings writerSettings, Encoder<Document> encoder) {
      JsonWriter writer = new JsonWriter(new StringWriter(), writerSettings);
      encoder.encode(writer, this, EncoderContext.builder().build());
      return writer.getWriter().toString();
   }

   @Override
   public int size() {
      return this.documentAsMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.documentAsMap.isEmpty();
   }

   @Override
   public boolean containsValue(Object value) {
      return this.documentAsMap.containsValue(value);
   }

   @Override
   public boolean containsKey(Object key) {
      return this.documentAsMap.containsKey(key);
   }

   @Override
   public Object get(Object key) {
      return this.documentAsMap.get(key);
   }

   public Object put(String key, Object value) {
      return this.documentAsMap.put(key, value);
   }

   @Override
   public Object remove(Object key) {
      return this.documentAsMap.remove(key);
   }

   @Override
   public void putAll(Map<? extends String, ?> map) {
      this.documentAsMap.putAll((Map<? extends String, ? extends Object>)map);
   }

   @Override
   public void clear() {
      this.documentAsMap.clear();
   }

   @Override
   public Set<String> keySet() {
      return this.documentAsMap.keySet();
   }

   @Override
   public Collection<Object> values() {
      return this.documentAsMap.values();
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      return this.documentAsMap.entrySet();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Document document = (Document)o;
         return this.documentAsMap.equals(document.documentAsMap);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.documentAsMap.hashCode();
   }

   @Override
   public String toString() {
      return "Document{" + this.documentAsMap + '}';
   }
}
