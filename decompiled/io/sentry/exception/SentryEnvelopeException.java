package io.sentry.exception;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryEnvelopeException extends Exception {
   private static final long serialVersionUID = -8307801916948173232L;

   public SentryEnvelopeException(@Nullable String message) {
      super(message);
   }

   public SentryEnvelopeException(@Nullable String message, @Nullable Throwable cause) {
      super(message, cause);
   }
}
