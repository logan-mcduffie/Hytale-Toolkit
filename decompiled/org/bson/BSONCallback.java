package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BSONCallback {
   void objectStart();

   void objectStart(String var1);

   Object objectDone();

   void reset();

   Object get();

   BSONCallback createBSONCallback();

   void arrayStart();

   void arrayStart(String var1);

   Object arrayDone();

   void gotNull(String var1);

   void gotUndefined(String var1);

   void gotMinKey(String var1);

   void gotMaxKey(String var1);

   void gotBoolean(String var1, boolean var2);

   void gotDouble(String var1, double var2);

   void gotDecimal128(String var1, Decimal128 var2);

   void gotInt(String var1, int var2);

   void gotLong(String var1, long var2);

   void gotDate(String var1, long var2);

   void gotString(String var1, String var2);

   void gotSymbol(String var1, String var2);

   void gotRegex(String var1, String var2, String var3);

   void gotTimestamp(String var1, int var2, int var3);

   void gotObjectId(String var1, ObjectId var2);

   void gotDBRef(String var1, String var2, ObjectId var3);

   void gotBinary(String var1, byte var2, byte[] var3);

   void gotUUID(String var1, long var2, long var4);

   void gotCode(String var1, String var2);

   void gotCodeWScope(String var1, String var2, Object var3);
}
