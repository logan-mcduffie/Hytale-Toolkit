package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceFloatImmutablePair<K> implements ReferenceFloatPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final float right;

   public ReferenceFloatImmutablePair(K left, float right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceFloatImmutablePair<K> of(K left, float right) {
      return new ReferenceFloatImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceFloatPair) {
         return this.left == ((ReferenceFloatPair)other).left() && this.right == ((ReferenceFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightFloat() + ">";
   }
}
