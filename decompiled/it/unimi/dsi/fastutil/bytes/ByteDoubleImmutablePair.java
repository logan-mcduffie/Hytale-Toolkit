package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteDoubleImmutablePair implements ByteDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final byte left;
   protected final double right;

   public ByteDoubleImmutablePair(byte left, double right) {
      this.left = left;
      this.right = right;
   }

   public static ByteDoubleImmutablePair of(byte left, double right) {
      return new ByteDoubleImmutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteDoublePair) {
         return this.left == ((ByteDoublePair)other).leftByte() && this.right == ((ByteDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.rightDouble() + ">";
   }
}
