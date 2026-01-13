package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteCharPair extends Pair<Byte, Character> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteCharPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteCharPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteCharPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteCharPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteCharPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteCharPair key(Byte l) {
      return this.key(l.byteValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default ByteCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default ByteCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default ByteCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default ByteCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default ByteCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static ByteCharPair of(byte left, char right) {
      return new ByteCharImmutablePair(left, right);
   }

   static Comparator<ByteCharPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
