package org.bson.io;

import java.io.Closeable;
import org.bson.types.ObjectId;

public interface BsonOutput extends Closeable {
   int getPosition();

   int getSize();

   void truncateToPosition(int var1);

   void writeBytes(byte[] var1);

   void writeBytes(byte[] var1, int var2, int var3);

   void writeByte(int var1);

   void writeCString(String var1);

   void writeString(String var1);

   void writeDouble(double var1);

   void writeInt32(int var1);

   void writeInt32(int var1, int var2);

   void writeInt64(long var1);

   void writeObjectId(ObjectId var1);

   @Override
   void close();
}
