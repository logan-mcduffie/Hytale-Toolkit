package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryUUID;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.StringUtils;
import io.sentry.util.UUIDStringUtils;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryId implements JsonSerializable {
   public static final SentryId EMPTY_ID = new SentryId("00000000-0000-0000-0000-000000000000".replace("-", ""));
   @NotNull
   private final LazyEvaluator<String> lazyStringValue;

   public SentryId() {
      this((UUID)null);
   }

   public SentryId(@Nullable UUID uuid) {
      if (uuid != null) {
         this.lazyStringValue = new LazyEvaluator<>(() -> this.normalize(UUIDStringUtils.toSentryIdString(uuid)));
      } else {
         this.lazyStringValue = new LazyEvaluator<>(SentryUUID::generateSentryId);
      }
   }

   public SentryId(@NotNull String sentryIdString) {
      String normalized = StringUtils.normalizeUUID(sentryIdString);
      if (normalized.length() != 32 && normalized.length() != 36) {
         throw new IllegalArgumentException(
            "String representation of SentryId has either 32 (UUID no dashes) or 36 characters long (completed UUID). Received: " + sentryIdString
         );
      } else {
         if (normalized.length() == 36) {
            this.lazyStringValue = new LazyEvaluator<>(() -> this.normalize(normalized));
         } else {
            this.lazyStringValue = new LazyEvaluator<>(() -> normalized);
         }
      }
   }

   @Override
   public String toString() {
      return this.lazyStringValue.getValue();
   }

   @Override
   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SentryId sentryId = (SentryId)o;
         return this.lazyStringValue.getValue().equals(sentryId.lazyStringValue.getValue());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.lazyStringValue.getValue().hashCode();
   }

   @NotNull
   private String normalize(@NotNull String uuidString) {
      return StringUtils.normalizeUUID(uuidString).replace("-", "");
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.value(this.toString());
   }

   public static final class Deserializer implements JsonDeserializer<SentryId> {
      @NotNull
      public SentryId deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         return new SentryId(reader.nextString());
      }
   }
}
