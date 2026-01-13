package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanObjectImmutablePair<V> implements BooleanObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final V right;

   public BooleanObjectImmutablePair(boolean left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> BooleanObjectImmutablePair<V> of(boolean left, V right) {
      return new BooleanObjectImmutablePair<>(left, right);
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
      } else if (other instanceof BooleanObjectPair) {
         return this.left == ((BooleanObjectPair)other).leftBoolean() && Objects.equals(this.right, ((BooleanObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.right() + ">";
   }
}
