package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Stack;

public interface ByteStack extends Stack<Byte> {
   void push(byte var1);

   byte popByte();

   byte topByte();

   byte peekByte(int var1);

   @Deprecated
   default void push(Byte o) {
      this.push(o.byteValue());
   }

   @Deprecated
   default Byte pop() {
      return this.popByte();
   }

   @Deprecated
   default Byte top() {
      return this.topByte();
   }

   @Deprecated
   default Byte peek(int i) {
      return this.peekByte(i);
   }
}
