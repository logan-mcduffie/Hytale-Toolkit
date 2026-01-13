package org.bson.json;

interface JsonBuffer {
   int getPosition();

   int read();

   void unread(int var1);

   int mark();

   void reset(int var1);

   void discard(int var1);
}
