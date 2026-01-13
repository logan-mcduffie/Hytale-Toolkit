package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface TransactionFinishedCallback {
   void execute(@NotNull ITransaction var1);
}
