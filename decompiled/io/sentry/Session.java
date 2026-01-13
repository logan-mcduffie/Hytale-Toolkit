package io.sentry;

import io.sentry.protocol.User;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.StringUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Session implements JsonUnknown, JsonSerializable {
   @NotNull
   private final Date started;
   @Nullable
   private Date timestamp;
   @NotNull
   private final AtomicInteger errorCount;
   @Nullable
   private final String distinctId;
   @Nullable
   private final String sessionId;
   @Nullable
   private Boolean init;
   @NotNull
   private Session.State status;
   @Nullable
   private Long sequence;
   @Nullable
   private Double duration;
   @Nullable
   private final String ipAddress;
   @Nullable
   private String userAgent;
   @Nullable
   private final String environment;
   @NotNull
   private final String release;
   @Nullable
   private String abnormalMechanism;
   @NotNull
   private final AutoClosableReentrantLock sessionLock = new AutoClosableReentrantLock();
   @Nullable
   private Map<String, Object> unknown;

   public Session(
      @NotNull Session.State status,
      @NotNull Date started,
      @Nullable Date timestamp,
      int errorCount,
      @Nullable String distinctId,
      @Nullable String sessionId,
      @Nullable Boolean init,
      @Nullable Long sequence,
      @Nullable Double duration,
      @Nullable String ipAddress,
      @Nullable String userAgent,
      @Nullable String environment,
      @NotNull String release,
      @Nullable String abnormalMechanism
   ) {
      this.status = status;
      this.started = started;
      this.timestamp = timestamp;
      this.errorCount = new AtomicInteger(errorCount);
      this.distinctId = distinctId;
      this.sessionId = sessionId;
      this.init = init;
      this.sequence = sequence;
      this.duration = duration;
      this.ipAddress = ipAddress;
      this.userAgent = userAgent;
      this.environment = environment;
      this.release = release;
      this.abnormalMechanism = abnormalMechanism;
   }

   public Session(@Nullable String distinctId, @Nullable User user, @Nullable String environment, @NotNull String release) {
      this(
         Session.State.Ok,
         DateUtils.getCurrentDateTime(),
         DateUtils.getCurrentDateTime(),
         0,
         distinctId,
         SentryUUID.generateSentryId(),
         true,
         null,
         null,
         user != null ? user.getIpAddress() : null,
         null,
         environment,
         release,
         null
      );
   }

   public boolean isTerminated() {
      return this.status != Session.State.Ok;
   }

   @Nullable
   public Date getStarted() {
      return this.started == null ? null : (Date)this.started.clone();
   }

   @Nullable
   public String getDistinctId() {
      return this.distinctId;
   }

   @Nullable
   public String getSessionId() {
      return this.sessionId;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   @Nullable
   public String getUserAgent() {
      return this.userAgent;
   }

   @Nullable
   public String getEnvironment() {
      return this.environment;
   }

   @NotNull
   public String getRelease() {
      return this.release;
   }

   @Nullable
   public Boolean getInit() {
      return this.init;
   }

   @Internal
   public void setInitAsTrue() {
      this.init = true;
   }

   public int errorCount() {
      return this.errorCount.get();
   }

   @NotNull
   public Session.State getStatus() {
      return this.status;
   }

   @Nullable
   public Long getSequence() {
      return this.sequence;
   }

   @Nullable
   public Double getDuration() {
      return this.duration;
   }

   @Nullable
   public String getAbnormalMechanism() {
      return this.abnormalMechanism;
   }

   @Nullable
   public Date getTimestamp() {
      Date timestampRef = this.timestamp;
      return timestampRef != null ? (Date)timestampRef.clone() : null;
   }

   public void end() {
      this.end(DateUtils.getCurrentDateTime());
   }

   public void end(@Nullable Date timestamp) {
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      try {
         this.init = null;
         if (this.status == Session.State.Ok) {
            this.status = Session.State.Exited;
         }

         if (timestamp != null) {
            this.timestamp = timestamp;
         } else {
            this.timestamp = DateUtils.getCurrentDateTime();
         }

         if (this.timestamp != null) {
            this.duration = this.calculateDurationTime(this.timestamp);
            this.sequence = this.getSequenceTimestamp(this.timestamp);
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   private double calculateDurationTime(@NotNull Date timestamp) {
      long diff = Math.abs(timestamp.getTime() - this.started.getTime());
      return diff / 1000.0;
   }

   public boolean update(@Nullable Session.State status, @Nullable String userAgent, boolean addErrorsCount) {
      return this.update(status, userAgent, addErrorsCount, null);
   }

   public boolean update(@Nullable Session.State status, @Nullable String userAgent, boolean addErrorsCount, @Nullable String abnormalMechanism) {
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      boolean var7;
      try {
         boolean sessionHasBeenUpdated = false;
         if (status != null) {
            this.status = status;
            sessionHasBeenUpdated = true;
         }

         if (userAgent != null) {
            this.userAgent = userAgent;
            sessionHasBeenUpdated = true;
         }

         if (addErrorsCount) {
            this.errorCount.addAndGet(1);
            sessionHasBeenUpdated = true;
         }

         if (abnormalMechanism != null) {
            this.abnormalMechanism = abnormalMechanism;
            sessionHasBeenUpdated = true;
         }

         if (sessionHasBeenUpdated) {
            this.init = null;
            this.timestamp = DateUtils.getCurrentDateTime();
            if (this.timestamp != null) {
               this.sequence = this.getSequenceTimestamp(this.timestamp);
            }
         }

         var7 = sessionHasBeenUpdated;
      } catch (Throwable var9) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var7;
   }

   private long getSequenceTimestamp(@NotNull Date timestamp) {
      long sequence = timestamp.getTime();
      if (sequence < 0L) {
         sequence = Math.abs(sequence);
      }

      return sequence;
   }

   @NotNull
   public Session clone() {
      return new Session(
         this.status,
         this.started,
         this.timestamp,
         this.errorCount.get(),
         this.distinctId,
         this.sessionId,
         this.init,
         this.sequence,
         this.duration,
         this.ipAddress,
         this.userAgent,
         this.environment,
         this.release,
         this.abnormalMechanism
      );
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.sessionId != null) {
         writer.name("sid").value(this.sessionId);
      }

      if (this.distinctId != null) {
         writer.name("did").value(this.distinctId);
      }

      if (this.init != null) {
         writer.name("init").value(this.init);
      }

      writer.name("started").value(logger, this.started);
      writer.name("status").value(logger, this.status.name().toLowerCase(Locale.ROOT));
      if (this.sequence != null) {
         writer.name("seq").value(this.sequence);
      }

      writer.name("errors").value((long)this.errorCount.intValue());
      if (this.duration != null) {
         writer.name("duration").value(this.duration);
      }

      if (this.timestamp != null) {
         writer.name("timestamp").value(logger, this.timestamp);
      }

      if (this.abnormalMechanism != null) {
         writer.name("abnormal_mechanism").value(logger, this.abnormalMechanism);
      }

      writer.name("attrs");
      writer.beginObject();
      writer.name("release").value(logger, this.release);
      if (this.environment != null) {
         writer.name("environment").value(logger, this.environment);
      }

      if (this.ipAddress != null) {
         writer.name("ip_address").value(logger, this.ipAddress);
      }

      if (this.userAgent != null) {
         writer.name("user_agent").value(logger, this.userAgent);
      }

      writer.endObject();
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
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

   public static final class Deserializer implements JsonDeserializer<Session> {
      @NotNull
      public Session deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Date started = null;
         Date timestamp = null;
         Integer errorCount = null;
         String distinctId = null;
         String sessionId = null;
         Boolean init = null;
         Session.State status = null;
         Long sequence = null;
         Double duration = null;
         String ipAddress = null;
         String userAgent = null;
         String environment = null;
         String release = null;
         String abnormalMechanism = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "sid":
                  String sid = reader.nextStringOrNull();
                  if (sid == null || sid.length() != 36 && sid.length() != 32) {
                     logger.log(SentryLevel.ERROR, "%s sid is not valid.", sid);
                  } else {
                     sessionId = sid;
                  }
                  break;
               case "did":
                  distinctId = reader.nextStringOrNull();
                  break;
               case "init":
                  init = reader.nextBooleanOrNull();
                  break;
               case "started":
                  started = reader.nextDateOrNull(logger);
                  break;
               case "status":
                  String statusValue = StringUtils.capitalize(reader.nextStringOrNull());
                  if (statusValue != null) {
                     status = Session.State.valueOf(statusValue);
                  }
                  break;
               case "seq":
                  sequence = reader.nextLongOrNull();
                  break;
               case "errors":
                  errorCount = reader.nextIntegerOrNull();
                  break;
               case "duration":
                  duration = reader.nextDoubleOrNull();
                  break;
               case "timestamp":
                  timestamp = reader.nextDateOrNull(logger);
                  break;
               case "abnormal_mechanism":
                  abnormalMechanism = reader.nextStringOrNull();
                  break;
               case "attrs":
                  reader.beginObject();

                  while (reader.peek() == JsonToken.NAME) {
                     String nextAttrName = reader.nextName();
                     switch (nextAttrName) {
                        case "release":
                           release = reader.nextStringOrNull();
                           break;
                        case "environment":
                           environment = reader.nextStringOrNull();
                           break;
                        case "ip_address":
                           ipAddress = reader.nextStringOrNull();
                           break;
                        case "user_agent":
                           userAgent = reader.nextStringOrNull();
                           break;
                        default:
                           reader.skipValue();
                     }
                  }

                  reader.endObject();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         if (status == null) {
            throw this.missingRequiredFieldException("status", logger);
         } else if (started == null) {
            throw this.missingRequiredFieldException("started", logger);
         } else if (errorCount == null) {
            throw this.missingRequiredFieldException("errors", logger);
         } else if (release == null) {
            throw this.missingRequiredFieldException("release", logger);
         } else {
            Session session = new Session(
               status,
               started,
               timestamp,
               errorCount,
               distinctId,
               sessionId,
               init,
               sequence,
               duration,
               ipAddress,
               userAgent,
               environment,
               release,
               abnormalMechanism
            );
            session.setUnknown(unknown);
            reader.endObject();
            return session;
         }
      }

      private Exception missingRequiredFieldException(String field, ILogger logger) {
         String message = "Missing required field \"" + field + "\"";
         Exception exception = new IllegalStateException(message);
         logger.log(SentryLevel.ERROR, message, exception);
         return exception;
      }
   }

   public static final class JsonKeys {
      public static final String SID = "sid";
      public static final String DID = "did";
      public static final String INIT = "init";
      public static final String STARTED = "started";
      public static final String STATUS = "status";
      public static final String SEQ = "seq";
      public static final String ERRORS = "errors";
      public static final String DURATION = "duration";
      public static final String TIMESTAMP = "timestamp";
      public static final String ATTRS = "attrs";
      public static final String RELEASE = "release";
      public static final String ENVIRONMENT = "environment";
      public static final String IP_ADDRESS = "ip_address";
      public static final String USER_AGENT = "user_agent";
      public static final String ABNORMAL_MECHANISM = "abnormal_mechanism";
   }

   public static enum State {
      Ok,
      Exited,
      Crashed,
      Abnormal;
   }
}
