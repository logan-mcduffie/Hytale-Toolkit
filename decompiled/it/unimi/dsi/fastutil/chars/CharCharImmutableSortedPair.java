package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class CharCharImmutableSortedPair extends CharCharImmutablePair implements CharCharSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private CharCharImmutableSortedPair(char left, char right) {
      super(left, right);
   }

   public static CharCharImmutableSortedPair of(char left, char right) {
      return left <= right ? new CharCharImmutableSortedPair(left, right) : new CharCharImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharCharSortedPair) {
         return this.left == ((CharCharSortedPair)other).leftChar() && this.right == ((CharCharSortedPair)other).rightChar();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftChar() + "," + this.rightChar() + "}";
   }
}
