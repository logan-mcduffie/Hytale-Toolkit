package io.sentry.util;

import org.jetbrains.annotations.NotNull;

public final class SentryRandom {
   @NotNull
   private static final SentryRandom.SentryRandomThreadLocal instance = new SentryRandom.SentryRandomThreadLocal();

   @NotNull
   public static Random current() {
      return instance.get();
   }

   private static class SentryRandomThreadLocal extends ThreadLocal<Random> {
      private SentryRandomThreadLocal() {
      }

      protected Random initialValue() {
         return new Random();
      }
   }
}
