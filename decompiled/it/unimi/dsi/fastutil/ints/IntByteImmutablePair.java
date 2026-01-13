package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntByteImmutablePair implements IntBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final byte right;

   public IntByteImmutablePair(int left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static IntByteImmutablePair of(int left, byte right) {
      return new IntByteImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
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
      } else if (other instanceof IntBytePair) {
         return this.left == ((IntBytePair)other).leftInt() && this.right == ((IntBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightByte() + ">";
   }
}
