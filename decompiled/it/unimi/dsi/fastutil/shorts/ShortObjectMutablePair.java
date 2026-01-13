package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortObjectMutablePair<V> implements ShortObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected V right;

   public ShortObjectMutablePair(short left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> ShortObjectMutablePair<V> of(short left, V right) {
      return new ShortObjectMutablePair<>(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortObjectMutablePair<V> left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ShortObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortObjectPair) {
         return this.left == ((ShortObjectPair)other).leftShort() && Objects.equals(this.right, ((ShortObjectPair)other).right());
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
      return "<" + this.leftShort() + "," + this.right() + ">";
   }
}
