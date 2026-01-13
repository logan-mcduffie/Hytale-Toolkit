package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntLongImmutablePair implements IntLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final long right;

   public IntLongImmutablePair(int left, long right) {
      this.left = left;
      this.right = right;
   }

   public static IntLongImmutablePair of(int left, long right) {
      return new IntLongImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
      return this.left;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntLongPair) {
         return this.left == ((IntLongPair)other).leftInt() && this.right == ((IntLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightLong() + ">";
   }
}
