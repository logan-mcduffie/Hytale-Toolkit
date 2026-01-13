package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryInstantDateProvider implements SentryDateProvider {
   @Override
   public SentryDate now() {
      return new SentryInstantDate();
   }
}
