package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanLongImmutablePair implements BooleanLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final long right;

   public BooleanLongImmutablePair(boolean left, long right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanLongImmutablePair of(boolean left, long right) {
      return new BooleanLongImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanLongPair) {
         return this.left == ((BooleanLongPair)other).leftBoolean() && this.right == ((BooleanLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightLong() + ">";
   }
}
