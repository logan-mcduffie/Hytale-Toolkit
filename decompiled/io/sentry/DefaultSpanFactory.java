package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DefaultSpanFactory implements ISpanFactory {
   @NotNull
   @Override
   public ITransaction createTransaction(
      @NotNull TransactionContext context,
      @NotNull IScopes scopes,
      @NotNull TransactionOptions transactionOptions,
      @Nullable CompositePerformanceCollector compositePerformanceCollector
   ) {
      return new SentryTracer(context, scopes, transactionOptions, compositePerformanceCollector);
   }

   @NotNull
   @Override
   public ISpan createSpan(@NotNull IScopes scopes, @NotNull SpanOptions spanOptions, @NotNull SpanContext spanContext, @Nullable ISpan parentSpan) {
      return NoOpSpan.getInstance();
   }
}
