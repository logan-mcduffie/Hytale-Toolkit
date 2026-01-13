package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanBooleanImmutablePair implements BooleanBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final boolean right;

   public BooleanBooleanImmutablePair(boolean left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanBooleanImmutablePair of(boolean left, boolean right) {
      return new BooleanBooleanImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanBooleanPair) {
         return this.left == ((BooleanBooleanPair)other).leftBoolean() && this.right == ((BooleanBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightBoolean() + ">";
   }
}
