package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceIntMutablePair<K> implements ReferenceIntPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected int right;

   public ReferenceIntMutablePair(K left, int right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceIntMutablePair<K> of(K left, int right) {
      return new ReferenceIntMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ReferenceIntMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   public ReferenceIntMutablePair<K> right(int r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceIntPair) {
         return this.left == ((ReferenceIntPair)other).left() && this.right == ((ReferenceIntPair)other).rightInt();
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
      return "<" + this.left() + "," + this.rightInt() + ">";
   }
}
