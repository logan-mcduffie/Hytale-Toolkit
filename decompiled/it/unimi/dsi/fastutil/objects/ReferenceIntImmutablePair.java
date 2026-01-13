package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceIntImmutablePair<K> implements ReferenceIntPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final int right;

   public ReferenceIntImmutablePair(K left, int right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceIntImmutablePair<K> of(K left, int right) {
      return new ReferenceIntImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceIntPair) {
         return this.left == ((ReferenceIntPair)other).left() && this.right == ((ReferenceIntPair)other).rightInt();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightInt() + ">";
   }
}
