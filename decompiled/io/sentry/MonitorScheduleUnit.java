package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum MonitorScheduleUnit {
   MINUTE,
   HOUR,
   DAY,
   WEEK,
   MONTH,
   YEAR;

   @NotNull
   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
