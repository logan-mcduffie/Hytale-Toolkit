package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum SentryAttributeType {
   STRING,
   BOOLEAN,
   INTEGER,
   DOUBLE;

   @NotNull
   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
