package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntDoubleImmutablePair implements IntDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final double right;

   public IntDoubleImmutablePair(int left, double right) {
      this.left = left;
      this.right = right;
   }

   public static IntDoubleImmutablePair of(int left, double right) {
      return new IntDoubleImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
      return this.left;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntDoublePair) {
         return this.left == ((IntDoublePair)other).leftInt() && this.right == ((IntDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightDouble() + ">";
   }
}
