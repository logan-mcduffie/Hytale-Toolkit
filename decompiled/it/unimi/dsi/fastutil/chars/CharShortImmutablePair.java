package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharShortImmutablePair implements CharShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final short right;

   public CharShortImmutablePair(char left, short right) {
      this.left = left;
      this.right = right;
   }

   public static CharShortImmutablePair of(char left, short right) {
      return new CharShortImmutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharShortPair) {
         return this.left == ((CharShortPair)other).leftChar() && this.right == ((CharShortPair)other).rightShort();
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
      return "<" + this.leftChar() + "," + this.rightShort() + ">";
   }
}
