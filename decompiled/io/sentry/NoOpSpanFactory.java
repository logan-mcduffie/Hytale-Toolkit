package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpSpanFactory implements ISpanFactory {
   private static final NoOpSpanFactory instance = new NoOpSpanFactory();

   private NoOpSpanFactory() {
   }

   public static NoOpSpanFactory getInstance() {
      return instance;
   }

   @NotNull
   @Override
   public ITransaction createTransaction(
      @NotNull TransactionContext context,
      @NotNull IScopes scopes,
      @NotNull TransactionOptions transactionOptions,
      @Nullable CompositePerformanceCollector compositePerformanceCollector
   ) {
      return NoOpTransaction.getInstance();
   }

   @NotNull
   @Override
   public ISpan createSpan(@NotNull IScopes scopes, @NotNull SpanOptions spanOptions, @NotNull SpanContext spanContext, @Nullable ISpan parentSpan) {
      return NoOpSpan.getInstance();
   }
}
