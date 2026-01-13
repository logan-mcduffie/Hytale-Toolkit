package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectByteImmutablePair<K> implements ObjectBytePair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final byte right;

   public ObjectByteImmutablePair(K left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectByteImmutablePair<K> of(K left, byte right) {
      return new ObjectByteImmutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectBytePair) {
         return Objects.equals(this.left, ((ObjectBytePair)other).left()) && this.right == ((ObjectBytePair)other).rightByte();
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
      return "<" + this.left() + "," + this.rightByte() + ">";
   }
}
