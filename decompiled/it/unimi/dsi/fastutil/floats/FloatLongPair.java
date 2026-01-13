package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatLongPair extends Pair<Float, Long> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatLongPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatLongPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatLongPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatLongPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatLongPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatLongPair key(Float l) {
      return this.key(l.floatValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default FloatLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default FloatLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default FloatLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default FloatLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default FloatLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static FloatLongPair of(float left, long right) {
      return new FloatLongImmutablePair(left, right);
   }

   static Comparator<FloatLongPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
