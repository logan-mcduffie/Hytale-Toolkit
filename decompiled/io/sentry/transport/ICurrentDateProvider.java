package io.sentry.transport;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ICurrentDateProvider {
   long getCurrentTimeMillis();
}
