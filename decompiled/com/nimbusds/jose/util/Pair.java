package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.Immutable;

@Immutable
public class Pair<L, R> {
   private final L left;
   private final R right;

   protected Pair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public static <L, R> Pair<L, R> of(L left, R right) {
      return new Pair<>(left, right);
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }
}
