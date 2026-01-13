package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortDoubleImmutablePair implements ShortDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final short left;
   protected final double right;

   public ShortDoubleImmutablePair(short left, double right) {
      this.left = left;
      this.right = right;
   }

   public static ShortDoubleImmutablePair of(short left, double right) {
      return new ShortDoubleImmutablePair(left, right);
   }

   @Override
   public short leftShort() {
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
      } else if (other instanceof ShortDoublePair) {
         return this.left == ((ShortDoublePair)other).leftShort() && this.right == ((ShortDoublePair)other).rightDouble();
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
      return "<" + this.leftShort() + "," + this.rightDouble() + ">";
   }
}
