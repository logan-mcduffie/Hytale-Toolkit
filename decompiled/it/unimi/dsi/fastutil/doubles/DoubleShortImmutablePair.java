package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleShortImmutablePair implements DoubleShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final short right;

   public DoubleShortImmutablePair(double left, short right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleShortImmutablePair of(double left, short right) {
      return new DoubleShortImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleShortPair) {
         return this.left == ((DoubleShortPair)other).leftDouble() && this.right == ((DoubleShortPair)other).rightShort();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightShort() + ">";
   }
}
