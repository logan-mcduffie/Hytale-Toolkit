package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BsonWriter {
   void flush();

   void writeBinaryData(BsonBinary var1);

   void writeBinaryData(String var1, BsonBinary var2);

   void writeBoolean(boolean var1);

   void writeBoolean(String var1, boolean var2);

   void writeDateTime(long var1);

   void writeDateTime(String var1, long var2);

   void writeDBPointer(BsonDbPointer var1);

   void writeDBPointer(String var1, BsonDbPointer var2);

   void writeDouble(double var1);

   void writeDouble(String var1, double var2);

   void writeEndArray();

   void writeEndDocument();

   void writeInt32(int var1);

   void writeInt32(String var1, int var2);

   void writeInt64(long var1);

   void writeInt64(String var1, long var2);

   void writeDecimal128(Decimal128 var1);

   void writeDecimal128(String var1, Decimal128 var2);

   void writeJavaScript(String var1);

   void writeJavaScript(String var1, String var2);

   void writeJavaScriptWithScope(String var1);

   void writeJavaScriptWithScope(String var1, String var2);

   void writeMaxKey();

   void writeMaxKey(String var1);

   void writeMinKey();

   void writeMinKey(String var1);

   void writeName(String var1);

   void writeNull();

   void writeNull(String var1);

   void writeObjectId(ObjectId var1);

   void writeObjectId(String var1, ObjectId var2);

   void writeRegularExpression(BsonRegularExpression var1);

   void writeRegularExpression(String var1, BsonRegularExpression var2);

   void writeStartArray();

   void writeStartArray(String var1);

   void writeStartDocument();

   void writeStartDocument(String var1);

   void writeString(String var1);

   void writeString(String var1, String var2);

   void writeSymbol(String var1);

   void writeSymbol(String var1, String var2);

   void writeTimestamp(BsonTimestamp var1);

   void writeTimestamp(String var1, BsonTimestamp var2);

   void writeUndefined();

   void writeUndefined(String var1);

   void pipe(BsonReader var1);
}
