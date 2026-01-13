package org.bson;

import java.io.Closeable;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BsonReader extends Closeable {
   BsonType getCurrentBsonType();

   String getCurrentName();

   BsonBinary readBinaryData();

   byte peekBinarySubType();

   int peekBinarySize();

   BsonBinary readBinaryData(String var1);

   boolean readBoolean();

   boolean readBoolean(String var1);

   BsonType readBsonType();

   long readDateTime();

   long readDateTime(String var1);

   double readDouble();

   double readDouble(String var1);

   void readEndArray();

   void readEndDocument();

   int readInt32();

   int readInt32(String var1);

   long readInt64();

   long readInt64(String var1);

   Decimal128 readDecimal128();

   Decimal128 readDecimal128(String var1);

   String readJavaScript();

   String readJavaScript(String var1);

   String readJavaScriptWithScope();

   String readJavaScriptWithScope(String var1);

   void readMaxKey();

   void readMaxKey(String var1);

   void readMinKey();

   void readMinKey(String var1);

   String readName();

   void readName(String var1);

   void readNull();

   void readNull(String var1);

   ObjectId readObjectId();

   ObjectId readObjectId(String var1);

   BsonRegularExpression readRegularExpression();

   BsonRegularExpression readRegularExpression(String var1);

   BsonDbPointer readDBPointer();

   BsonDbPointer readDBPointer(String var1);

   void readStartArray();

   void readStartDocument();

   String readString();

   String readString(String var1);

   String readSymbol();

   String readSymbol(String var1);

   BsonTimestamp readTimestamp();

   BsonTimestamp readTimestamp(String var1);

   void readUndefined();

   void readUndefined(String var1);

   void skipName();

   void skipValue();

   BsonReaderMark getMark();

   @Override
   void close();
}
