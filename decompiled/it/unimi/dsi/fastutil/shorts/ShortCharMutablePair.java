package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortCharMutablePair implements ShortCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected char right;

   public ShortCharMutablePair(short left, char right) {
      this.left = left;
      this.right = right;
   }

   public static ShortCharMutablePair of(short left, char right) {
      return new ShortCharMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortCharMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   public ShortCharMutablePair right(char r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortCharPair) {
         return this.left == ((ShortCharPair)other).leftShort() && this.right == ((ShortCharPair)other).rightChar();
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
      return "<" + this.leftShort() + "," + this.rightChar() + ">";
   }
}
