package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortShortMutablePair implements ShortShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected short right;

   public ShortShortMutablePair(short left, short right) {
      this.left = left;
      this.right = right;
   }

   public static ShortShortMutablePair of(short left, short right) {
      return new ShortShortMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortShortMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   public ShortShortMutablePair right(short r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortShortPair) {
         return this.left == ((ShortShortPair)other).leftShort() && this.right == ((ShortShortPair)other).rightShort();
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
      return "<" + this.leftShort() + "," + this.rightShort() + ">";
   }
}
