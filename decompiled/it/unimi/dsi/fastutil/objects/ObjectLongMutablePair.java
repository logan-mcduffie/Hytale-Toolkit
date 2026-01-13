package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectLongMutablePair<K> implements ObjectLongPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected long right;

   public ObjectLongMutablePair(K left, long right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectLongMutablePair<K> of(K left, long right) {
      return new ObjectLongMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectLongMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public ObjectLongMutablePair<K> right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectLongPair) {
         return Objects.equals(this.left, ((ObjectLongPair)other).left()) && this.right == ((ObjectLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightLong() + ">";
   }
}
