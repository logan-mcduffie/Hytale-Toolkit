package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongBooleanImmutablePair implements LongBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final boolean right;

   public LongBooleanImmutablePair(long left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static LongBooleanImmutablePair of(long left, boolean right) {
      return new LongBooleanImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongBooleanPair) {
         return this.left == ((LongBooleanPair)other).leftLong() && this.right == ((LongBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightBoolean() + ">";
   }
}
