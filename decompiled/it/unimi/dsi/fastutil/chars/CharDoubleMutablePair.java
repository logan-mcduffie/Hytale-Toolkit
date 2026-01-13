package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharDoubleMutablePair implements CharDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected double right;

   public CharDoubleMutablePair(char left, double right) {
      this.left = left;
      this.right = right;
   }

   public static CharDoubleMutablePair of(char left, double right) {
      return new CharDoubleMutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharDoubleMutablePair left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public CharDoubleMutablePair right(double r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharDoublePair) {
         return this.left == ((CharDoublePair)other).leftChar() && this.right == ((CharDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftChar() + "," + this.rightDouble() + ">";
   }
}
