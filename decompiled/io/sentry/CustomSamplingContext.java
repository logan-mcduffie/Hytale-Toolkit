package io.sentry;

import io.sentry.util.Objects;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomSamplingContext {
   @NotNull
   private final Map<String, Object> data = new HashMap<>();

   public void set(@NotNull String key, @Nullable Object value) {
      Objects.requireNonNull(key, "key is required");
      this.data.put(key, value);
   }

   @Nullable
   public Object get(@NotNull String key) {
      Objects.requireNonNull(key, "key is required");
      return this.data.get(key);
   }

   @NotNull
   public Map<String, Object> getData() {
      return this.data;
   }
}
