package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanByteImmutablePair implements BooleanBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final byte right;

   public BooleanByteImmutablePair(boolean left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanByteImmutablePair of(boolean left, byte right) {
      return new BooleanByteImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BooleanBytePair) {
         return this.left == ((BooleanBytePair)other).leftBoolean() && this.right == ((BooleanBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightByte() + ">";
   }
}
