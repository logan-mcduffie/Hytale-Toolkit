package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IClientReportRecorder {
   void recordLostEnvelope(@NotNull DiscardReason var1, @Nullable SentryEnvelope var2);

   void recordLostEnvelopeItem(@NotNull DiscardReason var1, @Nullable SentryEnvelopeItem var2);

   void recordLostEvent(@NotNull DiscardReason var1, @NotNull DataCategory var2);

   void recordLostEvent(@NotNull DiscardReason var1, @NotNull DataCategory var2, long var3);

   @NotNull
   SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope var1);
}
