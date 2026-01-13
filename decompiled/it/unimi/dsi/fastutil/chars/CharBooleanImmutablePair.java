package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharBooleanImmutablePair implements CharBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final boolean right;

   public CharBooleanImmutablePair(char left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static CharBooleanImmutablePair of(char left, boolean right) {
      return new CharBooleanImmutablePair(left, right);
   }

   @Override
   public char leftChar() {
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
