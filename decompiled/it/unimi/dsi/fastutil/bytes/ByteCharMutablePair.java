package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteCharMutablePair implements ByteCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected char right;

   public ByteCharMutablePair(byte left, char right) {
      this.left = left;
      this.right = right;
   }

   public static ByteCharMutablePair of(byte left, char right) {
      return new ByteCharMutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteCharMutablePair left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   public ByteCharMutablePair right(char r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteCharPair) {
         return this.left == ((ByteCharPair)other).leftByte() && this.right == ((ByteCharPair)other).rightChar();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.rightChar() + ">";
   }
}
