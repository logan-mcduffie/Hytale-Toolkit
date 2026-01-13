package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharReferenceMutablePair<V> implements CharReferencePair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected V right;

   public CharReferenceMutablePair(char left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> CharReferenceMutablePair<V> of(char left, V right) {
      return new CharReferenceMutablePair<>(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharReferenceMutablePair<V> left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public CharReferenceMutablePair<V> right(V r) {
      this.right = r;
      return this;
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
