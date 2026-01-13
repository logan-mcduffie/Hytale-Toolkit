package org.bson;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.bson.io.ByteBufferBsonInput;
import org.bson.types.BSONTimestamp;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.Symbol;

public class LazyBSONObject implements BSONObject {
   private final byte[] bytes;
   private final int offset;
   private final LazyBSONCallback callback;

   public LazyBSONObject(byte[] bytes, LazyBSONCallback callback) {
      this(bytes, 0, callback);
   }

   public LazyBSONObject(byte[] bytes, int offset, LazyBSONCallback callback) {
      this.bytes = bytes;
      this.callback = callback;
      this.offset = offset;
   }

   protected int getOffset() {
      return this.offset;
   }

   protected byte[] getBytes() {
      return this.bytes;
   }

   @Override
   public Object get(String key) {
      BsonBinaryReader reader = this.getBsonReader();

      Object value;
      try {
         reader.readStartDocument();
         value = null;

         while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (key.equals(reader.readName())) {
               value = this.readValue(reader);
               break;
            }

            reader.skipValue();
         }
      } finally {
         reader.close();
      }

      return value;
   }

   @Override
   public boolean containsField(String s) {
      BsonBinaryReader reader = this.getBsonReader();

      try {
         reader.readStartDocument();

         while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (reader.readName().equals(s)) {
               return true;
            }

            reader.skipValue();
         }

         return false;
      } finally {
         reader.close();
      }
   }

   @Override
   public Set<String> keySet() {
      Set<String> keys = new LinkedHashSet<>();
      BsonBinaryReader reader = this.getBsonReader();

      try {
         reader.readStartDocument();

         while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            keys.add(reader.readName());
            reader.skipValue();
         }

         reader.readEndDocument();
      } finally {
         reader.close();
      }

      return Collections.unmodifiableSet(keys);
   }

   Object readValue(BsonBinaryReader reader) {
      switch (reader.getCurrentBsonType()) {
         case DOCUMENT:
            return this.readDocument(reader);
         case ARRAY:
            return this.readArray(reader);
         case DOUBLE:
            return reader.readDouble();
         case STRING:
            return reader.readString();
         case BINARY:
            byte binarySubType = reader.peekBinarySubType();
            BsonBinary binary = reader.readBinaryData();
            if (binarySubType != BsonBinarySubType.BINARY.getValue() && binarySubType != BsonBinarySubType.OLD_BINARY.getValue()) {
               return new Binary(binary.getType(), binary.getData());
            }

            return binary.getData();
         case NULL:
            reader.readNull();
            return null;
         case UNDEFINED:
            reader.readUndefined();
            return null;
         case OBJECT_ID:
            return reader.readObjectId();
         case BOOLEAN:
            return reader.readBoolean();
         case DATE_TIME:
            return new Date(reader.readDateTime());
         case REGULAR_EXPRESSION:
            BsonRegularExpression regularExpression = reader.readRegularExpression();
            return Pattern.compile(regularExpression.getPattern(), BSON.regexFlags(regularExpression.getOptions()));
         case DB_POINTER:
            BsonDbPointer dbPointer = reader.readDBPointer();
            return this.callback.createDBRef(dbPointer.getNamespace(), dbPointer.getId());
         case JAVASCRIPT:
            return new Code(reader.readJavaScript());
         case SYMBOL:
            return new Symbol(reader.readSymbol());
         case JAVASCRIPT_WITH_SCOPE:
            return new CodeWScope(reader.readJavaScriptWithScope(), (BSONObject)this.readJavaScriptWithScopeDocument(reader));
         case INT32:
            return reader.readInt32();
         case TIMESTAMP:
            BsonTimestamp timestamp = reader.readTimestamp();
            return new BSONTimestamp(timestamp.getTime(), timestamp.getInc());
         case INT64:
            return reader.readInt64();
         case DECIMAL128:
            return reader.readDecimal128();
         case MIN_KEY:
            reader.readMinKey();
            return new MinKey();
         case MAX_KEY:
            reader.readMaxKey();
            return new MaxKey();
         default:
            throw new IllegalArgumentException("unhandled BSON type: " + reader.getCurrentBsonType());
      }
   }

   private Object readArray(BsonBinaryReader reader) {
      int position = reader.getBsonInput().getPosition();
      reader.skipValue();
      return this.callback.createArray(this.bytes, this.offset + position);
   }

   private Object readDocument(BsonBinaryReader reader) {
      int position = reader.getBsonInput().getPosition();
      reader.skipValue();
      return this.callback.createObject(this.bytes, this.offset + position);
   }

   private Object readJavaScriptWithScopeDocument(BsonBinaryReader reader) {
      int position = reader.getBsonInput().getPosition();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         reader.skipName();
         reader.skipValue();
      }

      reader.readEndDocument();
      return this.callback.createObject(this.bytes, this.offset + position);
   }

   BsonBinaryReader getBsonReader() {
      ByteBuffer buffer = this.getBufferForInternalBytes();
      return new BsonBinaryReader(new ByteBufferBsonInput(new ByteBufNIO(buffer)));
   }

   private ByteBuffer getBufferForInternalBytes() {
      ByteBuffer buffer = ByteBuffer.wrap(this.bytes, this.offset, this.bytes.length - this.offset).slice();
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      ((Buffer)buffer).limit(buffer.getInt());
      ((Buffer)buffer).rewind();
      return buffer;
   }

   public boolean isEmpty() {
      return this.keySet().size() == 0;
   }

   public int getBSONSize() {
      return this.getBufferForInternalBytes().getInt();
   }

   public int pipe(OutputStream os) throws IOException {
      WritableByteChannel channel = Channels.newChannel(os);
      return channel.write(this.getBufferForInternalBytes());
   }

   public Set<Entry<String, Object>> entrySet() {
      final List<Entry<String, Object>> entries = new ArrayList<>();
      BsonBinaryReader reader = this.getBsonReader();

      try {
         reader.readStartDocument();

         while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            entries.add(new SimpleImmutableEntry<>(reader.readName(), this.readValue(reader)));
         }

         reader.readEndDocument();
      } finally {
         reader.close();
      }

      return new Set<Entry<String, Object>>() {
         @Override
         public int size() {
            return entries.size();
         }

         @Override
         public boolean isEmpty() {
            return entries.isEmpty();
         }

         @Override
         public Iterator<Entry<String, Object>> iterator() {
            return entries.iterator();
         }

         @Override
         public Object[] toArray() {
            return entries.toArray();
         }

         @Override
         public <T> T[] toArray(T[] a) {
            return (T[])entries.toArray(a);
         }

         @Override
         public boolean contains(Object o) {
            return entries.contains(o);
         }

         @Override
         public boolean containsAll(Collection<?> c) {
            return entries.containsAll(c);
         }

         public boolean add(Entry<String, Object> stringObjectEntry) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object o) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends Entry<String, Object>> c) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public int hashCode() {
      int result = 1;
      int size = this.getBSONSize();

      for (int i = this.offset; i < this.offset + size; i++) {
         result = 31 * result + this.bytes[i];
      }

      return result;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LazyBSONObject other = (LazyBSONObject)o;
         if (this.bytes == other.bytes && this.offset == other.offset) {
            return true;
         } else if (this.bytes != null && other.bytes != null) {
            if (this.bytes.length != 0 && other.bytes.length != 0) {
               int length = this.bytes[this.offset];
               if (other.bytes[other.offset] != length) {
                  return false;
               } else {
                  for (int i = 0; i < length; i++) {
                     if (this.bytes[this.offset + i] != other.bytes[other.offset + i]) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public Object put(String key, Object v) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public void putAll(BSONObject o) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public void putAll(Map m) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public Object removeField(String key) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public Map toMap() {
      Map<String, Object> map = new LinkedHashMap<>();

      for (Entry<String, Object> entry : this.entrySet()) {
         map.put(entry.getKey(), entry.getValue());
      }

      return Collections.unmodifiableMap(map);
   }
}
