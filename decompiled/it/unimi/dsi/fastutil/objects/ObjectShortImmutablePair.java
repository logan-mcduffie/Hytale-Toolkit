package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectShortImmutablePair<K> implements ObjectShortPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final short right;

   public ObjectShortImmutablePair(K left, short right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectShortImmutablePair<K> of(K left, short right) {
      return new ObjectShortImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectShortPair) {
         return Objects.equals(this.left, ((ObjectShortPair)other).left()) && this.right == ((ObjectShortPair)other).rightShort();
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
      return "<" + this.left() + "," + this.rightShort() + ">";
   }
}
