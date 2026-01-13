package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortFloatMutablePair implements ShortFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected float right;

   public ShortFloatMutablePair(short left, float right) {
      this.left = left;
      this.right = right;
   }

   public static ShortFloatMutablePair of(short left, float right) {
      return new ShortFloatMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortFloatMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public ShortFloatMutablePair right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortFloatPair) {
         return this.left == ((ShortFloatPair)other).leftShort() && this.right == ((ShortFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftShort() + "," + this.rightFloat() + ">";
   }
}
