package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatReferenceImmutablePair<V> implements FloatReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final float left;
   protected final V right;

   public FloatReferenceImmutablePair(float left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> FloatReferenceImmutablePair<V> of(float left, V right) {
      return new FloatReferenceImmutablePair<>(left, right);
   }

   @Override
   public float leftFloat() {
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
      } else if (other instanceof FloatReferencePair) {
         return this.left == ((FloatReferencePair)other).leftFloat() && this.right == ((FloatReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.float2int(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftFloat() + "," + this.right() + ">";
   }
}
