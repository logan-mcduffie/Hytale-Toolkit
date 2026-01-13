package org.bson;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.OutputBuffer;
import org.bson.types.BSONTimestamp;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

public class BasicBSONEncoder implements BSONEncoder {
   private BsonBinaryWriter bsonWriter;
   private OutputBuffer outputBuffer;

   @Override
   public byte[] encode(BSONObject document) {
      OutputBuffer outputBuffer = new BasicOutputBuffer();
      this.set(outputBuffer);
      this.putObject(document);
      this.done();
      return outputBuffer.toByteArray();
   }

   @Override
   public void done() {
      this.bsonWriter.close();
      this.bsonWriter = null;
   }

   @Override
   public void set(OutputBuffer buffer) {
      if (this.bsonWriter != null) {
         throw new IllegalStateException("Performing another operation at this moment");
      } else {
         this.outputBuffer = buffer;
         this.bsonWriter = new BsonBinaryWriter(buffer);
      }
   }

   protected OutputBuffer getOutputBuffer() {
      return this.outputBuffer;
   }

   protected BsonBinaryWriter getBsonWriter() {
      return this.bsonWriter;
   }

   @Override
   public int putObject(BSONObject document) {
      int startPosition = this.getOutputBuffer().getPosition();
      this.bsonWriter.writeStartDocument();
      if (this.isTopLevelDocument() && document.containsField("_id")) {
         this._putObjectField("_id", document.get("_id"));
      }

      for (String key : document.keySet()) {
         if (!this.isTopLevelDocument() || !key.equals("_id")) {
            this._putObjectField(key, document.get(key));
         }
      }

      this.bsonWriter.writeEndDocument();
      return this.getOutputBuffer().getPosition() - startPosition;
   }

   private boolean isTopLevelDocument() {
      return this.bsonWriter.getContext().getParentContext() == null;
   }

   protected void putName(String name) {
      if (this.bsonWriter.getState() == AbstractBsonWriter.State.NAME) {
         this.bsonWriter.writeName(name);
      }
   }

   protected void _putObjectField(String name, Object value) {
      if (!"_transientFields".equals(name)) {
         if (name.contains("\u0000")) {
            throw new IllegalArgumentException("Document field names can't have a NULL character. (Bad Key: '" + name + "')");
         } else {
            if ("$where".equals(name) && value instanceof String) {
               this.putCode(name, new Code((String)value));
            }

            if (value == null) {
               this.putNull(name);
            } else if (value instanceof Date) {
               this.putDate(name, (Date)value);
            } else if (value instanceof Decimal128) {
               this.putDecimal128(name, (Decimal128)value);
            } else if (value instanceof Number) {
               this.putNumber(name, (Number)value);
            } else if (value instanceof Character) {
               this.putString(name, value.toString());
            } else if (value instanceof String) {
               this.putString(name, value.toString());
            } else if (value instanceof ObjectId) {
               this.putObjectId(name, (ObjectId)value);
            } else if (value instanceof Boolean) {
               this.putBoolean(name, (Boolean)value);
            } else if (value instanceof Pattern) {
               this.putPattern(name, (Pattern)value);
            } else if (value instanceof Iterable) {
               this.putIterable(name, (Iterable)value);
            } else if (value instanceof BSONObject) {
               this.putObject(name, (BSONObject)value);
            } else if (value instanceof Map) {
               this.putMap(name, (Map)value);
            } else if (value instanceof byte[]) {
               this.putBinary(name, (byte[])value);
            } else if (value instanceof Binary) {
               this.putBinary(name, (Binary)value);
            } else if (value instanceof UUID) {
               this.putUUID(name, (UUID)value);
            } else if (value.getClass().isArray()) {
               this.putArray(name, value);
            } else if (value instanceof Symbol) {
               this.putSymbol(name, (Symbol)value);
            } else if (value instanceof BSONTimestamp) {
               this.putTimestamp(name, (BSONTimestamp)value);
            } else if (value instanceof CodeWScope) {
               this.putCodeWScope(name, (CodeWScope)value);
            } else if (value instanceof Code) {
               this.putCode(name, (Code)value);
            } else if (value instanceof MinKey) {
               this.putMinKey(name);
            } else if (value instanceof MaxKey) {
               this.putMaxKey(name);
            } else if (!this.putSpecial(name, value)) {
               throw new IllegalArgumentException("Can't serialize " + value.getClass());
            }
         }
      }
   }

   protected void putNull(String name) {
      this.putName(name);
      this.bsonWriter.writeNull();
   }

   protected void putUndefined(String name) {
      this.putName(name);
      this.bsonWriter.writeUndefined();
   }

   protected void putTimestamp(String name, BSONTimestamp timestamp) {
      this.putName(name);
      this.bsonWriter.writeTimestamp(new BsonTimestamp(timestamp.getTime(), timestamp.getInc()));
   }

   protected void putCode(String name, Code code) {
      this.putName(name);
      this.bsonWriter.writeJavaScript(code.getCode());
   }

   protected void putCodeWScope(String name, CodeWScope codeWScope) {
      this.putName(name);
      this.bsonWriter.writeJavaScriptWithScope(codeWScope.getCode());
      this.putObject(codeWScope.getScope());
   }

   protected void putBoolean(String name, Boolean value) {
      this.putName(name);
      this.bsonWriter.writeBoolean(value);
   }

   protected void putDate(String name, Date date) {
      this.putName(name);
      this.bsonWriter.writeDateTime(date.getTime());
   }

   protected void putNumber(String name, Number number) {
      this.putName(name);
      if (number instanceof Integer || number instanceof Short || number instanceof Byte || number instanceof AtomicInteger) {
         this.bsonWriter.writeInt32(number.intValue());
      } else if (!(number instanceof Long) && !(number instanceof AtomicLong)) {
         if (!(number instanceof Float) && !(number instanceof Double)) {
            throw new IllegalArgumentException("Can't serialize " + number.getClass());
         }

         this.bsonWriter.writeDouble(number.doubleValue());
      } else {
         this.bsonWriter.writeInt64(number.longValue());
      }
   }

   protected void putDecimal128(String name, Decimal128 value) {
      this.putName(name);
      this.bsonWriter.writeDecimal128(value);
   }

   protected void putBinary(String name, byte[] bytes) {
      this.putName(name);
      this.bsonWriter.writeBinaryData(new BsonBinary(bytes));
   }

   protected void putBinary(String name, Binary binary) {
      this.putName(name);
      this.bsonWriter.writeBinaryData(new BsonBinary(binary.getType(), binary.getData()));
   }

   protected void putUUID(String name, UUID uuid) {
      this.putName(name);
      byte[] bytes = new byte[16];
      writeLongToArrayLittleEndian(bytes, 0, uuid.getMostSignificantBits());
      writeLongToArrayLittleEndian(bytes, 8, uuid.getLeastSignificantBits());
      this.bsonWriter.writeBinaryData(new BsonBinary(BsonBinarySubType.UUID_LEGACY, bytes));
   }

   protected void putSymbol(String name, Symbol symbol) {
      this.putName(name);
      this.bsonWriter.writeSymbol(symbol.getSymbol());
   }

   protected void putString(String name, String value) {
      this.putName(name);
      this.bsonWriter.writeString(value);
   }

   protected void putPattern(String name, Pattern value) {
      this.putName(name);
      this.bsonWriter.writeRegularExpression(new BsonRegularExpression(value.pattern(), BSON.regexFlags(value.flags())));
   }

   protected void putObjectId(String name, ObjectId objectId) {
      this.putName(name);
      this.bsonWriter.writeObjectId(objectId);
   }

   protected void putArray(String name, Object object) {
      this.putName(name);
      this.bsonWriter.writeStartArray();
      if (object instanceof int[]) {
         for (int i : (int[])object) {
            this.bsonWriter.writeInt32(i);
         }
      } else if (object instanceof long[]) {
         for (long i : (long[])object) {
            this.bsonWriter.writeInt64(i);
         }
      } else if (object instanceof float[]) {
         for (float i : (float[])object) {
            this.bsonWriter.writeDouble(i);
         }
      } else if (object instanceof short[]) {
         for (short i : (short[])object) {
            this.bsonWriter.writeInt32(i);
         }
      } else if (object instanceof byte[]) {
         for (byte i : (byte[])object) {
            this.bsonWriter.writeInt32(i);
         }
      } else if (object instanceof double[]) {
         for (double i : (double[])object) {
            this.bsonWriter.writeDouble(i);
         }
      } else if (object instanceof boolean[]) {
         for (boolean i : (boolean[])object) {
            this.bsonWriter.writeBoolean(i);
         }
      } else if (object instanceof String[]) {
         for (String i : (String[])object) {
            this.bsonWriter.writeString(i);
         }
      } else {
         int length = Array.getLength(object);

         for (int i = 0; i < length; i++) {
            this._putObjectField(String.valueOf(i), Array.get(object, i));
         }
      }

      this.bsonWriter.writeEndArray();
   }

   protected void putIterable(String name, Iterable iterable) {
      this.putName(name);
      this.bsonWriter.writeStartArray();
      int i = 0;

      for (Object o : iterable) {
         this._putObjectField(String.valueOf(i), o);
      }

      this.bsonWriter.writeEndArray();
   }

   protected void putMap(String name, Map map) {
      this.putName(name);
      this.bsonWriter.writeStartDocument();

      for (Entry entry : map.entrySet()) {
         this._putObjectField((String)entry.getKey(), entry.getValue());
      }

      this.bsonWriter.writeEndDocument();
   }

   protected int putObject(String name, BSONObject document) {
      this.putName(name);
      return this.putObject(document);
   }

   protected boolean putSpecial(String name, Object special) {
      return false;
   }

   protected void putMinKey(String name) {
      this.putName(name);
      this.bsonWriter.writeMinKey();
   }

   protected void putMaxKey(String name) {
      this.putName(name);
      this.bsonWriter.writeMaxKey();
   }

   private static void writeLongToArrayLittleEndian(byte[] bytes, int offset, long x) {
      bytes[offset] = (byte)(255L & x);
      bytes[offset + 1] = (byte)(255L & x >> 8);
      bytes[offset + 2] = (byte)(255L & x >> 16);
      bytes[offset + 3] = (byte)(255L & x >> 24);
      bytes[offset + 4] = (byte)(255L & x >> 32);
      bytes[offset + 5] = (byte)(255L & x >> 40);
      bytes[offset + 6] = (byte)(255L & x >> 48);
      bytes[offset + 7] = (byte)(255L & x >> 56);
   }
}
