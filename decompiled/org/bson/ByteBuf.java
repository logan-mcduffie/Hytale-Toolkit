package org.bson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface ByteBuf {
   int capacity();

   ByteBuf put(int var1, byte var2);

   int remaining();

   ByteBuf put(byte[] var1, int var2, int var3);

   boolean hasRemaining();

   ByteBuf put(byte var1);

   ByteBuf flip();

   byte[] array();

   int limit();

   ByteBuf position(int var1);

   ByteBuf clear();

   ByteBuf order(ByteOrder var1);

   byte get();

   byte get(int var1);

   ByteBuf get(byte[] var1);

   ByteBuf get(int var1, byte[] var2);

   ByteBuf get(byte[] var1, int var2, int var3);

   ByteBuf get(int var1, byte[] var2, int var3, int var4);

   long getLong();

   long getLong(int var1);

   double getDouble();

   double getDouble(int var1);

   int getInt();

   int getInt(int var1);

   int position();

   ByteBuf limit(int var1);

   ByteBuf asReadOnly();

   ByteBuf duplicate();

   ByteBuffer asNIO();

   int getReferenceCount();

   ByteBuf retain();

   void release();
}
