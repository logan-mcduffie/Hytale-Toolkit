package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortBooleanMutablePair implements ShortBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected boolean right;

   public ShortBooleanMutablePair(short left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static ShortBooleanMutablePair of(short left, boolean right) {
      return new ShortBooleanMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortBooleanMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public ShortBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortBooleanPair) {
         return this.left == ((ShortBooleanPair)other).leftShort() && this.right == ((ShortBooleanPair)other).rightBoolean();
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
      return "<" + this.leftShort() + "," + this.rightBoolean() + ">";
   }
}
