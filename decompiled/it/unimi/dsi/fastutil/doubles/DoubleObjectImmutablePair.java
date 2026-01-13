package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleObjectImmutablePair<V> implements DoubleObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final V right;

   public DoubleObjectImmutablePair(double left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> DoubleObjectImmutablePair<V> of(double left, V right) {
      return new DoubleObjectImmutablePair<>(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   @Override
   public V right() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleObjectPair) {
         return this.left == ((DoubleObjectPair)other).leftDouble() && Objects.equals(this.right, ((DoubleObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.right() + ">";
   }
}
