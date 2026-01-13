package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteBooleanMutablePair implements ByteBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected boolean right;

   public ByteBooleanMutablePair(byte left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static ByteBooleanMutablePair of(byte left, boolean right) {
      return new ByteBooleanMutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteBooleanMutablePair left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public ByteBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteBooleanPair) {
         return this.left == ((ByteBooleanPair)other).leftByte() && this.right == ((ByteBooleanPair)other).rightBoolean();
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
      return "<" + this.leftByte() + "," + this.rightBoolean() + ">";
   }
}
