package io.sentry;

import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SpanStatus implements JsonSerializable {
   OK(0, 399),
   CANCELLED(499),
   INTERNAL_ERROR(500),
   UNKNOWN(500),
   UNKNOWN_ERROR(500),
   INVALID_ARGUMENT(400),
   DEADLINE_EXCEEDED(504),
   NOT_FOUND(404),
   ALREADY_EXISTS(409),
   PERMISSION_DENIED(403),
   RESOURCE_EXHAUSTED(429),
   FAILED_PRECONDITION(400),
   ABORTED(409),
   OUT_OF_RANGE(400),
   UNIMPLEMENTED(501),
   UNAVAILABLE(503),
   DATA_LOSS(500),
   UNAUTHENTICATED(401);

   private final int minHttpStatusCode;
   private final int maxHttpStatusCode;

   private SpanStatus(int httpStatusCode) {
      this.minHttpStatusCode = httpStatusCode;
      this.maxHttpStatusCode = httpStatusCode;
   }

   private SpanStatus(int minHttpStatusCode, int maxHttpStatusCode) {
      this.minHttpStatusCode = minHttpStatusCode;
      this.maxHttpStatusCode = maxHttpStatusCode;
   }

   @Nullable
   public static SpanStatus fromHttpStatusCode(int httpStatusCode) {
      for (SpanStatus status : values()) {
         if (status.matches(httpStatusCode)) {
            return status;
         }
      }

      return null;
   }

   @NotNull
   public static SpanStatus fromHttpStatusCode(@Nullable Integer httpStatusCode, @NotNull SpanStatus defaultStatus) {
      SpanStatus spanStatus = httpStatusCode != null ? fromHttpStatusCode(httpStatusCode) : defaultStatus;
      return spanStatus != null ? spanStatus : defaultStatus;
   }

   private boolean matches(int httpStatusCode) {
      return httpStatusCode >= this.minHttpStatusCode && httpStatusCode <= this.maxHttpStatusCode;
   }

   @NotNull
   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   @Nullable
   public static SpanStatus fromApiNameSafely(@Nullable String apiName) {
      if (apiName == null) {
         return null;
      } else {
         try {
            return valueOf(apiName.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return null;
         }
      }
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.value(this.apiName());
   }

   public static final class Deserializer implements JsonDeserializer<SpanStatus> {
      @NotNull
      public SpanStatus deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         return SpanStatus.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
      }
   }
}
