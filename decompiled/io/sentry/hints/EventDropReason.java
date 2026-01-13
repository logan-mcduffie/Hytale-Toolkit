package io.sentry.hints;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum EventDropReason {
   MULTITHREADED_DEDUPLICATION;
}
