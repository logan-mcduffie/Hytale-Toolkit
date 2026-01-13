package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteCharImmutablePair implements ByteCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final byte left;
   protected final char right;

   public ByteCharImmutablePair(byte left, char right) {
      this.left = left;
      this.right = right;
   }

   public static ByteCharImmutablePair of(byte left, char right) {
      return new ByteCharImmutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   @Override
   public char rightChar() {
      return this.right;
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
