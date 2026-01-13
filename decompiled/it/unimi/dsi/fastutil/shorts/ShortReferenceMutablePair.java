package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortReferenceMutablePair<V> implements ShortReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected V right;

   public ShortReferenceMutablePair(short left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> ShortReferenceMutablePair<V> of(short left, V right) {
      return new ShortReferenceMutablePair<>(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortReferenceMutablePair<V> left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public ShortReferenceMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortReferencePair) {
         return this.left == ((ShortReferencePair)other).leftShort() && this.right == ((ShortReferencePair)other).right();
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
      return "<" + this.leftShort() + "," + this.right() + ">";
   }
}
