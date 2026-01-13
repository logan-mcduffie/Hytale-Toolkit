package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortIntImmutablePair implements ShortIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final short left;
   protected final int right;

   public ShortIntImmutablePair(short left, int right) {
      this.left = left;
      this.right = right;
   }

   public static ShortIntImmutablePair of(short left, int right) {
      return new ShortIntImmutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   @Override
   public int rightInt() {
      return this.right;
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
