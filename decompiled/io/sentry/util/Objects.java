package io.sentry.util;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Objects {
   private Objects() {
   }

   public static <T> T requireNonNull(@Nullable T obj, @NotNull String message) {
      if (obj == null) {
         throw new IllegalArgumentException(message);
      } else {
         return obj;
      }
   }

   public static boolean equals(@Nullable Object a, @Nullable Object b) {
      return a == b || a != null && a.equals(b);
   }

   public static int hash(@Nullable Object... values) {
      return Arrays.hashCode(values);
   }
}
