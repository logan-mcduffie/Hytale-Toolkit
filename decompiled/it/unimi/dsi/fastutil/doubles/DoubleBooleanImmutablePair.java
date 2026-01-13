package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleBooleanImmutablePair implements DoubleBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final boolean right;

   public DoubleBooleanImmutablePair(double left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleBooleanImmutablePair of(double left, boolean right) {
      return new DoubleBooleanImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleBooleanPair) {
         return this.left == ((DoubleBooleanPair)other).leftDouble() && this.right == ((DoubleBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightBoolean() + ">";
   }
}
