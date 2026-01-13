package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleByteImmutablePair implements DoubleBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final byte right;

   public DoubleByteImmutablePair(double left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleByteImmutablePair of(double left, byte right) {
      return new DoubleByteImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleBytePair) {
         return this.left == ((DoubleBytePair)other).leftDouble() && this.right == ((DoubleBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightByte() + ">";
   }
}
