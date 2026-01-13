package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatObjectMutablePair<V> implements FloatObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected float left;
   protected V right;

   public FloatObjectMutablePair(float left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> FloatObjectMutablePair<V> of(float left, V right) {
      return new FloatObjectMutablePair<>(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   public FloatObjectMutablePair<V> left(float l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public FloatObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatObjectPair) {
         return this.left == ((FloatObjectPair)other).leftFloat() && Objects.equals(this.right, ((FloatObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.float2int(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftFloat() + "," + this.right() + ">";
   }
}
