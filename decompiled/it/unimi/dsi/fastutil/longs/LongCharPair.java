package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongCharPair extends Pair<Long, Character> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongCharPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongCharPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongCharPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongCharPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongCharPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongCharPair key(Long l) {
      return this.key(l.longValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default LongCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default LongCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default LongCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default LongCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default LongCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static LongCharPair of(long left, char right) {
      return new LongCharImmutablePair(left, right);
   }

   static Comparator<LongCharPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
