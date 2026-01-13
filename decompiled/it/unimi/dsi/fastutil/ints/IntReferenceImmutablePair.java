package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntReferenceImmutablePair<V> implements IntReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final V right;

   public IntReferenceImmutablePair(int left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> IntReferenceImmutablePair<V> of(int left, V right) {
      return new IntReferenceImmutablePair<>(left, right);
   }

   @Override
   public int leftInt() {
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
      } else if (other instanceof IntReferencePair) {
         return this.left == ((IntReferencePair)other).leftInt() && this.right == ((IntReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.right() + ">";
   }
}
