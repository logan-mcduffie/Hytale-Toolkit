package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharByteImmutablePair implements CharBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final byte right;

   public CharByteImmutablePair(char left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static CharByteImmutablePair of(char left, byte right) {
      return new CharByteImmutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharBytePair) {
         return this.left == ((CharBytePair)other).leftChar() && this.right == ((CharBytePair)other).rightByte();
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
      return "<" + this.leftChar() + "," + this.rightByte() + ">";
   }
}
