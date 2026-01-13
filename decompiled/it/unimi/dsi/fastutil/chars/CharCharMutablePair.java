package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharCharMutablePair implements CharCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected char right;

   public CharCharMutablePair(char left, char right) {
      this.left = left;
      this.right = right;
   }

   public static CharCharMutablePair of(char left, char right) {
      return new CharCharMutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharCharMutablePair left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   public CharCharMutablePair right(char r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharCharPair) {
         return this.left == ((CharCharPair)other).leftChar() && this.right == ((CharCharPair)other).rightChar();
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
      return "<" + this.leftChar() + "," + this.rightChar() + ">";
   }
}
