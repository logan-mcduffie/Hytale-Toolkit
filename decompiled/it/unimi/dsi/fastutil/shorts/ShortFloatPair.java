package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortFloatPair extends Pair<Short, Float> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortFloatPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortFloatPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortFloatPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortFloatPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortFloatPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortFloatPair key(Short l) {
      return this.key(l.shortValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default ShortFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default ShortFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default ShortFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default ShortFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default ShortFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static ShortFloatPair of(short left, float right) {
      return new ShortFloatImmutablePair(left, right);
   }

   static Comparator<ShortFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
