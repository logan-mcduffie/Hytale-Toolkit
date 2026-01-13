package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanReferenceMutablePair<V> implements BooleanReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected boolean left;
   protected V right;

   public BooleanReferenceMutablePair(boolean left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> BooleanReferenceMutablePair<V> of(boolean left, V right) {
      return new BooleanReferenceMutablePair<>(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   public BooleanReferenceMutablePair<V> left(boolean l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public BooleanReferenceMutablePair<V> right(V r) {
      this.right = r;
      return this;
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
