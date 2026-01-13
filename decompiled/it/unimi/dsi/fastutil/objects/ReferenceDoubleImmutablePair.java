package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceDoubleImmutablePair<K> implements ReferenceDoublePair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final double right;

   public ReferenceDoubleImmutablePair(K left, double right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceDoubleImmutablePair<K> of(K left, double right) {
      return new ReferenceDoubleImmutablePair<>(left, right);
   }

   @Override
   public K left() {
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
