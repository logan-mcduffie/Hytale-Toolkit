package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceDoubleMutablePair<K> implements ReferenceDoublePair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected double right;

   public ReferenceDoubleMutablePair(K left, double right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceDoubleMutablePair<K> of(K left, double right) {
      return new ReferenceDoubleMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ReferenceDoubleMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public ReferenceDoubleMutablePair<K> right(double r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceDoublePair) {
         return this.left == ((ReferenceDoublePair)other).left() && this.right == ((ReferenceDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightDouble() + ">";
   }
}
