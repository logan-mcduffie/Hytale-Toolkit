package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanCharPair extends Pair<Boolean, Character> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanCharPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanCharPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanCharPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanCharPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanCharPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanCharPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default BooleanCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default BooleanCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default BooleanCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static BooleanCharPair of(boolean left, char right) {
      return new BooleanCharImmutablePair(left, right);
   }

   static Comparator<BooleanCharPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
