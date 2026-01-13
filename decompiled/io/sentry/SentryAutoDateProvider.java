package io.sentry;

import io.sentry.util.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryAutoDateProvider implements SentryDateProvider {
   @NotNull
   private final SentryDateProvider dateProvider;

   public SentryAutoDateProvider() {
      if (checkInstantAvailabilityAndPrecision()) {
         this.dateProvider = new SentryInstantDateProvider();
      } else {
         this.dateProvider = new SentryNanotimeDateProvider();
      }
   }

   @NotNull
   @Override
   public SentryDate now() {
      return this.dateProvider.now();
   }

   private static boolean checkInstantAvailabilityAndPrecision() {
      return Platform.isJvm() && Platform.isJavaNinePlus();
   }
}
