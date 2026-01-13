package io.sentry;

import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum SentryLevel implements JsonSerializable {
   DEBUG,
   INFO,
   WARNING,
   ERROR,
   FATAL;

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.value(this.name().toLowerCase(Locale.ROOT));
   }

   public static final class Deserializer implements JsonDeserializer<SentryLevel> {
      @NotNull
      public SentryLevel deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         return SentryLevel.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
      }
   }
}
