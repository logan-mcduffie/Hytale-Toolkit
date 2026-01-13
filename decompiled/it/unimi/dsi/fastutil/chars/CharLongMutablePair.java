package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharLongMutablePair implements CharLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected long right;

   public CharLongMutablePair(char left, long right) {
      this.left = left;
      this.right = right;
   }

   public static CharLongMutablePair of(char left, long right) {
      return new CharLongMutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharLongMutablePair left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public CharLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharLongPair) {
         return this.left == ((CharLongPair)other).leftChar() && this.right == ((CharLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftChar() + "," + this.rightLong() + ">";
   }
}
