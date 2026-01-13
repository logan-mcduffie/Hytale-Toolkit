package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.util.Objects;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StdoutTransport implements ITransport {
   @NotNull
   private final ISerializer serializer;

   public StdoutTransport(@NotNull ISerializer serializer) {
      this.serializer = Objects.requireNonNull(serializer, "Serializer is required");
   }

   @Override
   public void send(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
      Objects.requireNonNull(envelope, "SentryEnvelope is required");

      try {
         this.serializer.serialize(envelope, System.out);
      } catch (Throwable var4) {
      }
   }

   @Override
   public void flush(long timeoutMillis) {
      System.out.println("Flushing");
   }

   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return null;
   }

   @Override
   public void close() {
   }

   @Override
   public void close(boolean isRestarting) {
   }
}
