package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MonitorConfig implements JsonUnknown, JsonSerializable {
   @NotNull
   private MonitorSchedule schedule;
   @Nullable
   private Long checkinMargin;
   @Nullable
   private Long maxRuntime;
   @Nullable
   private String timezone;
   @Nullable
   private Long failureIssueThreshold;
   @Nullable
   private Long recoveryThreshold;
   @Nullable
   private Map<String, Object> unknown;

   public MonitorConfig(@NotNull MonitorSchedule schedule) {
      this.schedule = schedule;
      SentryOptions.Cron defaultCron = ScopesAdapter.getInstance().getOptions().getCron();
      if (defaultCron != null) {
         this.checkinMargin = defaultCron.getDefaultCheckinMargin();
         this.maxRuntime = defaultCron.getDefaultMaxRuntime();
         this.timezone = defaultCron.getDefaultTimezone();
         this.failureIssueThreshold = defaultCron.getDefaultFailureIssueThreshold();
         this.recoveryThreshold = defaultCron.getDefaultRecoveryThreshold();
      }
   }

   @NotNull
   public MonitorSchedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(@NotNull MonitorSchedule schedule) {
      this.schedule = schedule;
   }

   @Nullable
   public Long getCheckinMargin() {
      return this.checkinMargin;
   }

   public void setCheckinMargin(@Nullable Long checkinMargin) {
      this.checkinMargin = checkinMargin;
   }

   @Nullable
   public Long getMaxRuntime() {
      return this.maxRuntime;
   }

   public void setMaxRuntime(@Nullable Long maxRuntime) {
      this.maxRuntime = maxRuntime;
   }

   @Nullable
   public String getTimezone() {
      return this.timezone;
   }

   public void setTimezone(@Nullable String timezone) {
      this.timezone = timezone;
   }

   @Nullable
   public Long getFailureIssueThreshold() {
      return this.failureIssueThreshold;
   }

   public void setFailureIssueThreshold(@Nullable Long failureIssueThreshold) {
      this.failureIssueThreshold = failureIssueThreshold;
   }

   @Nullable
   public Long getRecoveryThreshold() {
      return this.recoveryThreshold;
   }

   public void setRecoveryThreshold(@Nullable Long recoveryThreshold) {
      this.recoveryThreshold = recoveryThreshold;
   }

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("schedule");
      this.schedule.serialize(writer, logger);
      if (this.checkinMargin != null) {
         writer.name("checkin_margin").value(this.checkinMargin);
      }

      if (this.maxRuntime != null) {
         writer.name("max_runtime").value(this.maxRuntime);
      }

      if (this.timezone != null) {
         writer.name("timezone").value(this.timezone);
      }

      if (this.failureIssueThreshold != null) {
         writer.name("failure_issue_threshold").value(this.failureIssueThreshold);
      }

      if (this.recoveryThreshold != null) {
         writer.name("recovery_threshold").value(this.recoveryThreshold);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<MonitorConfig> {
      @NotNull
      public MonitorConfig deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         MonitorSchedule schedule = null;
         Long checkinMargin = null;
         Long maxRuntime = null;
         String timezone = null;
         Long failureIssureThreshold = null;
         Long recoveryThreshold = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "schedule":
                  schedule = new MonitorSchedule.Deserializer().deserialize(reader, logger);
                  break;
               case "checkin_margin":
                  checkinMargin = reader.nextLongOrNull();
                  break;
               case "max_runtime":
                  maxRuntime = reader.nextLongOrNull();
                  break;
               case "timezone":
                  timezone = reader.nextStringOrNull();
                  break;
               case "failure_issue_threshold":
                  failureIssureThreshold = reader.nextLongOrNull();
                  break;
               case "recovery_threshold":
                  recoveryThreshold = reader.nextLongOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (schedule == null) {
            String message = "Missing required field \"schedule\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            MonitorConfig monitorConfig = new MonitorConfig(schedule);
            monitorConfig.setCheckinMargin(checkinMargin);
            monitorConfig.setMaxRuntime(maxRuntime);
            monitorConfig.setTimezone(timezone);
            monitorConfig.setFailureIssueThreshold(failureIssureThreshold);
            monitorConfig.setRecoveryThreshold(recoveryThreshold);
            monitorConfig.setUnknown(unknown);
            return monitorConfig;
         }
      }
   }

   public static final class JsonKeys {
      public static final String SCHEDULE = "schedule";
      public static final String CHECKIN_MARGIN = "checkin_margin";
      public static final String MAX_RUNTIME = "max_runtime";
      public static final String TIMEZONE = "timezone";
      public static final String FAILURE_ISSUE_THRESHOLD = "failure_issue_threshold";
      public static final String RECOVERY_THRESHOLD = "recovery_threshold";
   }
}
