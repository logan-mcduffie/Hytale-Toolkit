package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceBooleanImmutablePair<K> implements ReferenceBooleanPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final boolean right;

   public ReferenceBooleanImmutablePair(K left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceBooleanImmutablePair<K> of(K left, boolean right) {
      return new ReferenceBooleanImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceBooleanPair) {
         return this.left == ((ReferenceBooleanPair)other).left() && this.right == ((ReferenceBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightBoolean() + ">";
   }
}
