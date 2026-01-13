package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectDoubleMutablePair<K> implements ObjectDoublePair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected double right;

   public ObjectDoubleMutablePair(K left, double right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectDoubleMutablePair<K> of(K left, double right) {
      return new ObjectDoubleMutablePair<>(left, right);
   }

   @Override
   public K left() {
      return this.left;
   }

   public ObjectDoubleMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public ObjectDoubleMutablePair<K> right(double r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectDoublePair) {
         return Objects.equals(this.left, ((ObjectDoublePair)other).left()) && this.right == ((ObjectDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightDouble() + ">";
   }
}
