package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongObjectMutablePair<V> implements LongObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected long left;
   protected V right;

   public LongObjectMutablePair(long left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> LongObjectMutablePair<V> of(long left, V right) {
      return new LongObjectMutablePair<>(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   public LongObjectMutablePair<V> left(long l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public LongObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongObjectPair) {
         return this.left == ((LongObjectPair)other).leftLong() && Objects.equals(this.right, ((LongObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.right() + ">";
   }
}
