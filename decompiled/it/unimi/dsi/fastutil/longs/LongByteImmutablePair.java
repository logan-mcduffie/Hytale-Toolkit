package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongByteImmutablePair implements LongBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final byte right;

   public LongByteImmutablePair(long left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static LongByteImmutablePair of(long left, byte right) {
      return new LongByteImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
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
      } else if (other instanceof LongBytePair) {
         return this.left == ((LongBytePair)other).leftLong() && this.right == ((LongBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightByte() + ">";
   }
}
