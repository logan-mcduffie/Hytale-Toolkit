package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;

public class ReferenceReferenceMutablePair<K, V> implements ReferenceReferencePair<K, V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected V right;

   public ReferenceReferenceMutablePair(K left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <K, V> ReferenceReferenceMutablePair<K, V> of(K left, V right) {
      return new ReferenceReferenceMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ReferenceReferenceMutablePair<K, V> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ReferenceReferenceMutablePair<K, V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.right() + ">";
   }
}
