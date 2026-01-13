package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ISpanFactory {
   @NotNull
   ITransaction createTransaction(
      @NotNull TransactionContext var1, @NotNull IScopes var2, @NotNull TransactionOptions var3, @Nullable CompositePerformanceCollector var4
   );

   @NotNull
   ISpan createSpan(@NotNull IScopes var1, @NotNull SpanOptions var2, @NotNull SpanContext var3, @Nullable ISpan var4);
}
