package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectCharMutablePair<K> implements ObjectCharPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected char right;

   public ObjectCharMutablePair(K left, char right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectCharMutablePair<K> of(K left, char right) {
      return new ObjectCharMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectCharMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   public ObjectCharMutablePair<K> right(char r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectCharPair) {
         return Objects.equals(this.left, ((ObjectCharPair)other).left()) && this.right == ((ObjectCharPair)other).rightChar();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightChar() + ">";
   }
}
