package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectReferenceImmutablePair<K, V> implements ObjectReferencePair<K, V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final V right;

   public ObjectReferenceImmutablePair(K left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <K, V> ObjectReferenceImmutablePair<K, V> of(K left, V right) {
      return new ObjectReferenceImmutablePair<>(left, right);
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
