package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Stack;

public interface FloatStack extends Stack<Float> {
   void push(float var1);

   float popFloat();

   float topFloat();

   float peekFloat(int var1);

   @Deprecated
   default void push(Float o) {
      this.push(o.floatValue());
   }

   @Deprecated
   default Float pop() {
      return this.popFloat();
   }

   @Deprecated
   default Float top() {
      return this.topFloat();
   }

   @Deprecated
   default Float peek(int i) {
      return this.peekFloat(i);
   }
}
