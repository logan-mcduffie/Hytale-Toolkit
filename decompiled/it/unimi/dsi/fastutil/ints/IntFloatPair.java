package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntFloatPair extends Pair<Integer, Float> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntFloatPair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntFloatPair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntFloatPair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntFloatPair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntFloatPair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntFloatPair key(Integer l) {
      return this.key(l.intValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default IntFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default IntFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default IntFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default IntFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default IntFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static IntFloatPair of(int left, float right) {
      return new IntFloatImmutablePair(left, right);
   }

   static Comparator<IntFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
