package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceShortImmutablePair<K> implements ReferenceShortPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final short right;

   public ReferenceShortImmutablePair(K left, short right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceShortImmutablePair<K> of(K left, short right) {
      return new ReferenceShortImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceShortPair) {
         return this.left == ((ReferenceShortPair)other).left() && this.right == ((ReferenceShortPair)other).rightShort();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightShort() + ">";
   }
}
