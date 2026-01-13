package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Stack;

public interface ShortStack extends Stack<Short> {
   void push(short var1);

   short popShort();

   short topShort();

   short peekShort(int var1);

   @Deprecated
   default void push(Short o) {
      this.push(o.shortValue());
   }

   @Deprecated
   default Short pop() {
      return this.popShort();
   }

   @Deprecated
   default Short top() {
      return this.topShort();
   }

   @Deprecated
   default Short peek(int i) {
      return this.peekShort(i);
   }
}
