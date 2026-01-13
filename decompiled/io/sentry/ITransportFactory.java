package io.sentry;

import io.sentry.transport.ITransport;
import org.jetbrains.annotations.NotNull;

public interface ITransportFactory {
   @NotNull
   ITransport create(@NotNull SentryOptions var1, @NotNull RequestDetails var2);
}
