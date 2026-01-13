package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongDoubleMutablePair implements LongDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected long left;
   protected double right;

   public LongDoubleMutablePair(long left, double right) {
      this.left = left;
      this.right = right;
   }

   public static LongDoubleMutablePair of(long left, double right) {
      return new LongDoubleMutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   public LongDoubleMutablePair left(long l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public LongDoubleMutablePair right(double r) {
      this.right = r;
      return this;
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
