package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleLongImmutablePair implements DoubleLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final long right;

   public DoubleLongImmutablePair(double left, long right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleLongImmutablePair of(double left, long right) {
      return new DoubleLongImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleLongPair) {
         return this.left == ((DoubleLongPair)other).leftDouble() && this.right == ((DoubleLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightLong() + ">";
   }
}
