package org.bson.io;

import java.io.Closeable;
import org.bson.types.ObjectId;

public interface BsonInput extends Closeable {
   int getPosition();

   byte readByte();

   void readBytes(byte[] var1);

   void readBytes(byte[] var1, int var2, int var3);

   long readInt64();

   double readDouble();

   int readInt32();

   String readString();

   ObjectId readObjectId();

   String readCString();

   void skipCString();

   void skip(int var1);

   BsonInputMark getMark(int var1);

   boolean hasRemaining();

   @Override
   void close();
}
