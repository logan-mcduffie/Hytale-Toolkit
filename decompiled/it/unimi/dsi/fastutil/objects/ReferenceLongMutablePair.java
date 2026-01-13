package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceLongMutablePair<K> implements ReferenceLongPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected long right;

   public ReferenceLongMutablePair(K left, long right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceLongMutablePair<K> of(K left, long right) {
      return new ReferenceLongMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ReferenceLongMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public ReferenceLongMutablePair<K> right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceLongPair) {
         return this.left == ((ReferenceLongPair)other).left() && this.right == ((ReferenceLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightLong() + ">";
   }
}
