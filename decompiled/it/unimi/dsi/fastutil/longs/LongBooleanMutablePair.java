package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongBooleanMutablePair implements LongBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected long left;
   protected boolean right;

   public LongBooleanMutablePair(long left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static LongBooleanMutablePair of(long left, boolean right) {
      return new LongBooleanMutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   public LongBooleanMutablePair left(long l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public LongBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
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
