package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectBooleanImmutablePair<K> implements ObjectBooleanPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected final K left;
   protected final boolean right;

   public ObjectBooleanImmutablePair(K left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectBooleanImmutablePair<K> of(K left, boolean right) {
      return new ObjectBooleanImmutablePair<>(left, right);
   }

   @Override
   public K left() {
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
      } else if (other instanceof ObjectBooleanPair) {
         return Objects.equals(this.left, ((ObjectBooleanPair)other).left()) && this.right == ((ObjectBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.left() + "," + this.rightBoolean() + ">";
   }
}
