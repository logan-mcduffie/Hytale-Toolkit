package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongDoubleImmutablePair implements LongDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final double right;

   public LongDoubleImmutablePair(long left, double right) {
      this.left = left;
      this.right = right;
   }

   public static LongDoubleImmutablePair of(long left, double right) {
      return new LongDoubleImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
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
      } else if (other instanceof LongDoublePair) {
         return this.left == ((LongDoublePair)other).leftLong() && this.right == ((LongDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightDouble() + ">";
   }
}
