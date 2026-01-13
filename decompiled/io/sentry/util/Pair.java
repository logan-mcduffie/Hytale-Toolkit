package io.sentry.util;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Pair<A, B> {
   @Nullable
   private final A first;
   @Nullable
   private final B second;

   public Pair(@Nullable A first, @Nullable B second) {
      this.first = first;
      this.second = second;
   }

   @Nullable
   public A getFirst() {
      return this.first;
   }

   @Nullable
   public B getSecond() {
      return this.second;
   }
}
