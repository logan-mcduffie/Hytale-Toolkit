package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface BytePriorityQueue extends PriorityQueue<Byte> {
   void enqueue(byte var1);

   byte dequeueByte();

   byte firstByte();

   default byte lastByte() {
      throw new UnsupportedOperationException();
   }

   ByteComparator comparator();

   @Deprecated
   default void enqueue(Byte x) {
      this.enqueue(x.byteValue());
   }

   @Deprecated
   default Byte dequeue() {
      return this.dequeueByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   @Deprecated
   default Byte last() {
      return this.lastByte();
   }
}
