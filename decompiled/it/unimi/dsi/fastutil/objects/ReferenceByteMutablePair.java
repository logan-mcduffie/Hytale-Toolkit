package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceByteMutablePair<K> implements ReferenceBytePair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected byte right;

   public ReferenceByteMutablePair(K left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ReferenceByteMutablePair<K> of(K left, byte right) {
      return new ReferenceByteMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ReferenceByteMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   public ReferenceByteMutablePair<K> right(byte r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ReferenceBytePair) {
         return this.left == ((ReferenceBytePair)other).left() && this.right == ((ReferenceBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightByte() + ">";
   }
}
