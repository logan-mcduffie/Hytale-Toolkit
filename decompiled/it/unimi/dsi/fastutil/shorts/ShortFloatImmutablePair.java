package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortFloatImmutablePair implements ShortFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final short left;
   protected final float right;

   public ShortFloatImmutablePair(short left, float right) {
      this.left = left;
      this.right = right;
   }

   public static ShortFloatImmutablePair of(short left, float right) {
      return new ShortFloatImmutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   @Override
   public float rightFloat() {
      return this.right;
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
