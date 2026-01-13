package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectShortMutablePair<K> implements ObjectShortPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected short right;

   public ObjectShortMutablePair(K left, short right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectShortMutablePair<K> of(K left, short right) {
      return new ObjectShortMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectShortMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   public ObjectShortMutablePair<K> right(short r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectShortPair) {
         return Objects.equals(this.left, ((ObjectShortPair)other).left()) && this.right == ((ObjectShortPair)other).rightShort();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightShort() + ">";
   }
}
