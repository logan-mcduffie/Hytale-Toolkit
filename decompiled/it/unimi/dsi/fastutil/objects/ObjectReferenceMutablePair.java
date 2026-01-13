package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectReferenceMutablePair<K, V> implements ObjectReferencePair<K, V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected V right;

   public ObjectReferenceMutablePair(K left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <K, V> ObjectReferenceMutablePair<K, V> of(K left, V right) {
      return new ObjectReferenceMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectReferenceMutablePair<K, V> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ObjectReferenceMutablePair<K, V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.right() + ">";
   }
}
