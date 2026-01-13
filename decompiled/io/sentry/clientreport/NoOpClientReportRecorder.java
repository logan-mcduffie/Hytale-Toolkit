package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpClientReportRecorder implements IClientReportRecorder {
   @Override
   public void recordLostEnvelope(@NotNull DiscardReason reason, @Nullable SentryEnvelope envelope) {
   }

   @Override
   public void recordLostEnvelopeItem(@NotNull DiscardReason reason, @Nullable SentryEnvelopeItem envelopeItem) {
   }

   @Override
   public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category) {
   }

   @Override
   public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category, long count) {
   }

   @NotNull
   @Override
   public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope envelope) {
      return envelope;
   }
}
