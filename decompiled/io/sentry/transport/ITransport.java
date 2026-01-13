package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITransport extends Closeable {
   void send(@NotNull SentryEnvelope var1, @NotNull Hint var2) throws IOException;

   default void send(@NotNull SentryEnvelope envelope) throws IOException {
      this.send(envelope, new Hint());
   }

   default boolean isHealthy() {
      return true;
   }

   void flush(long var1);

   @Nullable
   RateLimiter getRateLimiter();

   void close(boolean var1) throws IOException;
}
