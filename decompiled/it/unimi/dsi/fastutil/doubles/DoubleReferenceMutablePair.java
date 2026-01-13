package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleReferenceMutablePair<V> implements DoubleReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected V right;

   public DoubleReferenceMutablePair(double left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> DoubleReferenceMutablePair<V> of(double left, V right) {
      return new DoubleReferenceMutablePair<>(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleReferenceMutablePair<V> left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public DoubleReferenceMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleReferencePair) {
         return this.left == ((DoubleReferencePair)other).leftDouble() && this.right == ((DoubleReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.right() + ">";
   }
}
