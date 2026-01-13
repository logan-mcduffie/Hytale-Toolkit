package io.sentry;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface JsonSerializable {
   void serialize(@NotNull ObjectWriter var1, @NotNull ILogger var2) throws IOException;
}
