package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface JsonDeserializer<T> {
   @NotNull
   T deserialize(@NotNull ObjectReader var1, @NotNull ILogger var2) throws Exception;
}
