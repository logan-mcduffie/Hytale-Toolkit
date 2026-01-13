package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanReferenceImmutablePair<V> implements BooleanReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final V right;

   public BooleanReferenceImmutablePair(boolean left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> BooleanReferenceImmutablePair<V> of(boolean left, V right) {
      return new BooleanReferenceImmutablePair<>(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanReferencePair) {
         return this.left == ((BooleanReferencePair)other).leftBoolean() && this.right == ((BooleanReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.right() + ">";
   }
}
