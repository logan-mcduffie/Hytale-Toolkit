package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntCharPair extends Pair<Integer, Character> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntCharPair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntCharPair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntCharPair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntCharPair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntCharPair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntCharPair key(Integer l) {
      return this.key(l.intValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default IntCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default IntCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default IntCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default IntCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default IntCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static IntCharPair of(int left, char right) {
      return new IntCharImmutablePair(left, right);
   }

   static Comparator<IntCharPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
