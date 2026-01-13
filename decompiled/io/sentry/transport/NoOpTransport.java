package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpTransport implements ITransport {
   private static final NoOpTransport instance = new NoOpTransport();

   @NotNull
   public static NoOpTransport getInstance() {
      return instance;
   }

   private NoOpTransport() {
   }

   @Override
   public void send(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
   }

   @Override
   public void flush(long timeoutMillis) {
   }

   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return null;
   }

   @Override
   public void close() throws IOException {
   }

   @Override
   public void close(boolean isRestarting) throws IOException {
   }
}
