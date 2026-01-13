package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntBooleanMutablePair implements IntBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected int left;
   protected boolean right;

   public IntBooleanMutablePair(int left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static IntBooleanMutablePair of(int left, boolean right) {
      return new IntBooleanMutablePair(left, right);
   }

   @Override
   public int leftInt() {
      return this.left;
   }

   public IntBooleanMutablePair left(int l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public IntBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntBooleanPair) {
         return this.left == ((IntBooleanPair)other).leftInt() && this.right == ((IntBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightBoolean() + ">";
   }
}
