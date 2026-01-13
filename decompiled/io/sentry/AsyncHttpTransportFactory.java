package io.sentry;

import io.sentry.transport.AsyncHttpTransport;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class AsyncHttpTransportFactory implements ITransportFactory {
   @NotNull
   @Override
   public ITransport create(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails) {
      Objects.requireNonNull(options, "options is required");
      Objects.requireNonNull(requestDetails, "requestDetails is required");
      return new AsyncHttpTransport(options, new RateLimiter(options), options.getTransportGate(), requestDetails);
   }
}
