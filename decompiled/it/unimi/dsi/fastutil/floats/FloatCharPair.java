package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatCharPair extends Pair<Float, Character> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatCharPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatCharPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatCharPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatCharPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatCharPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatCharPair key(Float l) {
      return this.key(l.floatValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default FloatCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default FloatCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default FloatCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default FloatCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default FloatCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static FloatCharPair of(float left, char right) {
      return new FloatCharImmutablePair(left, right);
   }

   static Comparator<FloatCharPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
