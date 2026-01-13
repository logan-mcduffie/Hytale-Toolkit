package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteReferenceMutablePair<V> implements ByteReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected V right;

   public ByteReferenceMutablePair(byte left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> ByteReferenceMutablePair<V> of(byte left, V right) {
      return new ByteReferenceMutablePair<>(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteReferenceMutablePair<V> left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ByteReferenceMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteReferencePair) {
         return this.left == ((ByteReferencePair)other).leftByte() && this.right == ((ByteReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.right() + ">";
   }
}
