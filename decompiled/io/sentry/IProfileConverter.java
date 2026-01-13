package io.sentry;

import io.sentry.protocol.profiling.SentryProfile;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IProfileConverter {
   @NotNull
   SentryProfile convertFromFile(@NotNull String var1) throws IOException;
}
