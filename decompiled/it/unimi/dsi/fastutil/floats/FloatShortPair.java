package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatShortPair extends Pair<Float, Short> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatShortPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatShortPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatShortPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatShortPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatShortPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatShortPair key(Float l) {
      return this.key(l.floatValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default FloatShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default FloatShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default FloatShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default FloatShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default FloatShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static FloatShortPair of(float left, short right) {
      return new FloatShortImmutablePair(left, right);
   }

   static Comparator<FloatShortPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
