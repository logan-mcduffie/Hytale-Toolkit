package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum CheckInStatus {
   IN_PROGRESS,
   OK,
   ERROR;

   @NotNull
   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
