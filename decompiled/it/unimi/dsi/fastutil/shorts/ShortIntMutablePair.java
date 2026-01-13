package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortIntMutablePair implements ShortIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected int right;

   public ShortIntMutablePair(short left, int right) {
      this.left = left;
      this.right = right;
   }

   public static ShortIntMutablePair of(short left, int right) {
      return new ShortIntMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortIntMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   public ShortIntMutablePair right(int r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortIntPair) {
         return this.left == ((ShortIntPair)other).leftShort() && this.right == ((ShortIntPair)other).rightInt();
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
      return "<" + this.leftShort() + "," + this.rightInt() + ">";
   }
}
