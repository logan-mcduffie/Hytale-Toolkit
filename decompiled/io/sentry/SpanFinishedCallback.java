package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SpanFinishedCallback {
   void execute(@NotNull Span var1);
}
