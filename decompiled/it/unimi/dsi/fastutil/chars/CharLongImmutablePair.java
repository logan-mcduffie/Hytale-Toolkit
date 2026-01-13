package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharLongImmutablePair implements CharLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final long right;

   public CharLongImmutablePair(char left, long right) {
      this.left = left;
      this.right = right;
   }

   public static CharLongImmutablePair of(char left, long right) {
      return new CharLongImmutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   @Override
   public long rightLong() {
      return this.right;
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
