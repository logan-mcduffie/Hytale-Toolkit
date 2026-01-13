package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteBooleanPair extends Pair<Byte, Boolean> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteBooleanPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteBooleanPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteBooleanPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteBooleanPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteBooleanPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteBooleanPair key(Byte l) {
      return this.key(l.byteValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default ByteBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default ByteBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ByteBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default ByteBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ByteBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static ByteBooleanPair of(byte left, boolean right) {
      return new ByteBooleanImmutablePair(left, right);
   }

   static Comparator<ByteBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
