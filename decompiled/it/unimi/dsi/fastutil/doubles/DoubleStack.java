package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Stack;

public interface DoubleStack extends Stack<Double> {
   void push(double var1);

   double popDouble();

   double topDouble();

   double peekDouble(int var1);

   @Deprecated
   default void push(Double o) {
      this.push(o.doubleValue());
   }

   @Deprecated
   default Double pop() {
      return this.popDouble();
   }

   @Deprecated
   default Double top() {
      return this.topDouble();
   }

   @Deprecated
   default Double peek(int i) {
      return this.peekDouble(i);
   }
}
