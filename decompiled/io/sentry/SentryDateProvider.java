package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SentryDateProvider {
   SentryDate now();
}
