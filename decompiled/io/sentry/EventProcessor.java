package io.sentry;

import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventProcessor {
   @Nullable
   default SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
      return event;
   }

   @Nullable
   default SentryTransaction process(@NotNull SentryTransaction transaction, @NotNull Hint hint) {
      return transaction;
   }

   @Nullable
   default SentryReplayEvent process(@NotNull SentryReplayEvent event, @NotNull Hint hint) {
      return event;
   }

   @Nullable
   default SentryLogEvent process(@NotNull SentryLogEvent event) {
      return event;
   }

   @Nullable
   default Long getOrder() {
      return null;
   }
}
