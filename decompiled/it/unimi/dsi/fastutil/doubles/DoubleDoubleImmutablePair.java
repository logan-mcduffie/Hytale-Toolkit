package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleDoubleImmutablePair implements DoubleDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final double right;

   public DoubleDoubleImmutablePair(double left, double right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleDoubleImmutablePair of(double left, double right) {
      return new DoubleDoubleImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleDoublePair) {
         return this.left == ((DoubleDoublePair)other).leftDouble() && this.right == ((DoubleDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightDouble() + ">";
   }
}
