package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectFloatMutablePair<K> implements ObjectFloatPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected float right;

   public ObjectFloatMutablePair(K left, float right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectFloatMutablePair<K> of(K left, float right) {
      return new ObjectFloatMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectFloatMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public ObjectFloatMutablePair<K> right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectFloatPair) {
         return Objects.equals(this.left, ((ObjectFloatPair)other).left()) && this.right == ((ObjectFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightFloat() + ">";
   }
}
