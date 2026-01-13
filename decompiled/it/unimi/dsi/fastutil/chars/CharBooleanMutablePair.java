package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharBooleanMutablePair implements CharBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected boolean right;

   public CharBooleanMutablePair(char left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static CharBooleanMutablePair of(char left, boolean right) {
      return new CharBooleanMutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharBooleanMutablePair left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public CharBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharBooleanPair) {
         return this.left == ((CharBooleanPair)other).leftChar() && this.right == ((CharBooleanPair)other).rightBoolean();
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
      return "<" + this.leftChar() + "," + this.rightBoolean() + ">";
   }
}
