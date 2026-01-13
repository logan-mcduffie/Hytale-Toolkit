package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongCharImmutablePair implements LongCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final char right;

   public LongCharImmutablePair(long left, char right) {
      this.left = left;
      this.right = right;
   }

   public static LongCharImmutablePair of(long left, char right) {
      return new LongCharImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongCharPair) {
         return this.left == ((LongCharPair)other).leftLong() && this.right == ((LongCharPair)other).rightChar();
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
      return "<" + this.leftLong() + "," + this.rightChar() + ">";
   }
}
