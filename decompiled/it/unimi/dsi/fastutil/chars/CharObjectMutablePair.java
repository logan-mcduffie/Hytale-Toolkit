package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class CharObjectMutablePair<V> implements CharObjectPair<V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected char left;
   protected V right;

   public CharObjectMutablePair(char left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <V> CharObjectMutablePair<V> of(char left, V right) {
      return new CharObjectMutablePair<>(left, right);
   }

   @Override
   public char leftChar() {
      return this.left;
   }

   public CharObjectMutablePair<V> left(char l) {
      this.left = l;
      return this;
   }

   @Override
   public V right() {
      return this.right;
   }

   public CharObjectMutablePair<V> right(V r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof CharObjectPair) {
         return this.left == ((CharObjectPair)other).leftChar() && Objects.equals(this.right, ((CharObjectPair)other).right());
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   @Override
   public String toString() {
      return "<" + this.leftChar() + "," + this.right() + ">";
   }
}
