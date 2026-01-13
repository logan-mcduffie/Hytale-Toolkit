package io.sentry;

import org.jetbrains.annotations.NotNull;

public interface IEnvelopeSender {
   void processEnvelopeFile(@NotNull String var1, @NotNull Hint var2);
}
