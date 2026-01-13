package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryNanotimeDateProvider implements SentryDateProvider {
   @Override
   public SentryDate now() {
      return new SentryNanotimeDate();
   }
}
