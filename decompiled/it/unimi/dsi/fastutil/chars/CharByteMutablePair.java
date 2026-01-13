package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharByteMutablePair implements CharBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected byte right;

   public CharByteMutablePair(char left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static CharByteMutablePair of(char left, byte right) {
      return new CharByteMutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharByteMutablePair left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   public CharByteMutablePair right(byte r) {
      this.right = r;
      return this;
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
