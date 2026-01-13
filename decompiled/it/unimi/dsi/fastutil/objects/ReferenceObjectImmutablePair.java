package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceObjectImmutablePair<K, V> implements ReferenceObjectPair<K, V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final V right;

   public ReferenceObjectImmutablePair(K left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <K, V> ReferenceObjectImmutablePair<K, V> of(K left, V right) {
      return new ReferenceObjectImmutablePair<>(left, right);
   }

   @Override
   public K left() {
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
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.right() + ">";
   }
}
