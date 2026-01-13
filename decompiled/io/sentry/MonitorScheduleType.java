package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum MonitorScheduleType {
   CRONTAB,
   INTERVAL;

   @NotNull
   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
