package io.sentry;

import io.sentry.util.LazyEvaluator;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class SpanId implements JsonSerializable {
   public static final SpanId EMPTY_ID = new SpanId("00000000-0000-0000-0000-000000000000".replace("-", "").substring(0, 16));
   @NotNull
   private final LazyEvaluator<String> lazyValue;

   public SpanId(@NotNull String value) {
      Objects.requireNonNull(value, "value is required");
      this.lazyValue = new LazyEvaluator<>(() -> value);
   }

   public SpanId() {
      this.lazyValue = new LazyEvaluator<>(SentryUUID::generateSpanId);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SpanId spanId = (SpanId)o;
         return this.lazyValue.getValue().equals(spanId.lazyValue.getValue());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.lazyValue.getValue().hashCode();
   }

   @Override
   public String toString() {
      return this.lazyValue.getValue();
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.value(this.lazyValue.getValue());
   }

   public static final class Deserializer implements JsonDeserializer<SpanId> {
      @NotNull
      public SpanId deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         return new SpanId(reader.nextString());
      }
   }
}
