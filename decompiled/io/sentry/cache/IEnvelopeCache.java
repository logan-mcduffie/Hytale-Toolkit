package io.sentry.cache;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import org.jetbrains.annotations.NotNull;

public interface IEnvelopeCache extends Iterable<SentryEnvelope> {
   @Deprecated
   void store(@NotNull SentryEnvelope var1, @NotNull Hint var2);

   default boolean storeEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
      this.store(envelope, hint);
      return true;
   }

   @Deprecated
   default void store(@NotNull SentryEnvelope envelope) {
      this.storeEnvelope(envelope, new Hint());
   }

   void discard(@NotNull SentryEnvelope var1);
}
