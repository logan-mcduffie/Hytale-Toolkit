package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatIntPair extends Pair<Float, Integer> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatIntPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatIntPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatIntPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatIntPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatIntPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatIntPair key(Float l) {
      return this.key(l.floatValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default FloatIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default FloatIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default FloatIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default FloatIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default FloatIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static FloatIntPair of(float left, int right) {
      return new FloatIntImmutablePair(left, right);
   }

   static Comparator<FloatIntPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
