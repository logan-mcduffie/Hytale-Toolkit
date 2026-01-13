package org.bson.codecs.pojo;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bson.assertions.Assertions;

final class Either<L, R> {
   private final L left;
   private final R right;

   public static <L, R> Either<L, R> left(L value) {
      return new Either<>(Assertions.notNull("value", value), null);
   }

   public static <L, R> Either<L, R> right(R value) {
      return new Either<>(null, Assertions.notNull("value", value));
   }

   private Either(L l, R r) {
      this.left = l;
      this.right = r;
   }

   public <T> T map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc) {
      return (T)(this.left != null ? lFunc.apply(this.left) : rFunc.apply(this.right));
   }

   public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
      if (this.left != null) {
         lFunc.accept(this.left);
      }

      if (this.right != null) {
         rFunc.accept(this.right);
      }
   }

   @Override
   public String toString() {
      return "Either{left=" + this.left + ", right=" + this.right + '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Either<?, ?> either = (Either<?, ?>)o;
         return Objects.equals(this.left, either.left) && Objects.equals(this.right, either.right);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.left, this.right);
   }
}
