package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharFloatImmutablePair implements CharFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final float right;

   public CharFloatImmutablePair(char left, float right) {
      this.left = left;
      this.right = right;
   }

   public static CharFloatImmutablePair of(char left, float right) {
      return new CharFloatImmutablePair(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharFloatPair) {
         return this.left == ((CharFloatPair)other).leftChar() && this.right == ((CharFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftChar() + "," + this.rightFloat() + ">";
   }
}
