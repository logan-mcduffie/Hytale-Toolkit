package io.sentry;

import io.sentry.transport.ITransport;
import io.sentry.transport.NoOpTransport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpTransportFactory implements ITransportFactory {
   private static final NoOpTransportFactory instance = new NoOpTransportFactory();

   public static NoOpTransportFactory getInstance() {
      return instance;
   }

   private NoOpTransportFactory() {
   }

   @NotNull
   @Override
   public ITransport create(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails) {
      return NoOpTransport.getInstance();
   }
}
