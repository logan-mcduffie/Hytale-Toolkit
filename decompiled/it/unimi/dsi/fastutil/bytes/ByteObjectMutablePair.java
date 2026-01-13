package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteObjectMutablePair<V> implements ByteObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected V right;

   public ByteObjectMutablePair(byte left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> ByteObjectMutablePair<V> of(byte left, V right) {
      return new ByteObjectMutablePair<>(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteObjectMutablePair<V> left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ByteObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteObjectPair) {
         return this.left == ((ByteObjectPair)other).leftByte() && Objects.equals(this.right, ((ByteObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.right() + ">";
   }
}
