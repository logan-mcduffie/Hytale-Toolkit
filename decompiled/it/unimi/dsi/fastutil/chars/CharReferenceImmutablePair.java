package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharReferenceImmutablePair<V> implements CharReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final char left;
   protected final V right;

   public CharReferenceImmutablePair(char left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> CharReferenceImmutablePair<V> of(char left, V right) {
      return new CharReferenceImmutablePair<>(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   @Override
   public V right() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharReferencePair) {
         return this.left == ((CharReferencePair)other).leftChar() && this.right == ((CharReferencePair)other).right();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
   }

   @Override
   public String toString() {
      return "<" + this.leftChar() + "," + this.right() + ">";
   }
}
