package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleObjectMutablePair<V> implements DoubleObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected V right;

   public DoubleObjectMutablePair(double left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> DoubleObjectMutablePair<V> of(double left, V right) {
      return new DoubleObjectMutablePair<>(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleObjectMutablePair<V> left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public DoubleObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
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
