package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanDoubleImmutablePair implements BooleanDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final double right;

   public BooleanDoubleImmutablePair(boolean left, double right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanDoubleImmutablePair of(boolean left, double right) {
      return new BooleanDoubleImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanDoublePair) {
         return this.left == ((BooleanDoublePair)other).leftBoolean() && this.right == ((BooleanDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightDouble() + ">";
   }
}
