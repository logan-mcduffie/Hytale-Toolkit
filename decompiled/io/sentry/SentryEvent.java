package io.sentry;

import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryThread;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class SentryEvent extends SentryBaseEvent implements JsonUnknown, JsonSerializable {
   @NotNull
   private Date timestamp;
   @Nullable
   private Message message;
   @Nullable
   private String logger;
   @Nullable
   private SentryValues<SentryThread> threads;
   @Nullable
   private SentryValues<SentryException> exception;
   @Nullable
   private SentryLevel level;
   @Nullable
   private String transaction;
   @Nullable
   private List<String> fingerprint;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, String> modules;

   SentryEvent(@NotNull SentryId eventId, @NotNull Date timestamp) {
      super(eventId);
      this.timestamp = timestamp;
   }

   public SentryEvent(@Nullable Throwable throwable) {
      this();
      this.throwable = throwable;
   }

   public SentryEvent() {
      this(new SentryId(), DateUtils.getCurrentDateTime());
   }

   @TestOnly
   public SentryEvent(@NotNull Date timestamp) {
      this(new SentryId(), timestamp);
   }

   public Date getTimestamp() {
      return (Date)this.timestamp.clone();
   }

   public void setTimestamp(@NotNull Date timestamp) {
      this.timestamp = timestamp;
   }

   @Nullable
   public Message getMessage() {
      return this.message;
   }

   public void setMessage(@Nullable Message message) {
      this.message = message;
   }

   @Nullable
   public String getLogger() {
      return this.logger;
   }

   public void setLogger(@Nullable String logger) {
      this.logger = logger;
   }

   @Nullable
   public List<SentryThread> getThreads() {
      return this.threads != null ? this.threads.getValues() : null;
   }

   public void setThreads(@Nullable List<SentryThread> threads) {
      this.threads = new SentryValues<>(threads);
   }

   @Nullable
   public List<SentryException> getExceptions() {
      return this.exception == null ? null : this.exception.getValues();
   }

   public void setExceptions(@Nullable List<SentryException> exception) {
      this.exception = new SentryValues<>(exception);
   }

   @Nullable
   public SentryLevel getLevel() {
      return this.level;
   }

   public void setLevel(@Nullable SentryLevel level) {
      this.level = level;
   }

   @Nullable
   public String getTransaction() {
      return this.transaction;
   }

   public void setTransaction(@Nullable String transaction) {
      this.transaction = transaction;
   }

   @Nullable
   public List<String> getFingerprints() {
      return this.fingerprint;
   }

   public void setFingerprints(@Nullable List<String> fingerprint) {
      this.fingerprint = fingerprint != null ? new ArrayList<>(fingerprint) : null;
   }

   @Nullable
   Map<String, String> getModules() {
      return this.modules;
   }

   public void setModules(@Nullable Map<String, String> modules) {
      this.modules = CollectionUtils.newHashMap(modules);
   }

   public void setModule(@NotNull String key, @NotNull String value) {
      if (this.modules == null) {
         this.modules = new HashMap<>();
      }

      this.modules.put(key, value);
   }

   public void removeModule(@NotNull String key) {
      if (this.modules != null) {
         this.modules.remove(key);
      }
   }

   @Nullable
   public String getModule(@NotNull String key) {
      return this.modules != null ? this.modules.get(key) : null;
   }

   public boolean isCrashed() {
      return this.getUnhandledException() != null;
   }

   @Nullable
   public SentryException getUnhandledException() {
      if (this.exception != null) {
         for (SentryException e : this.exception.getValues()) {
            if (e.getMechanism() != null && e.getMechanism().isHandled() != null && !e.getMechanism().isHandled()) {
               return e;
            }
         }
      }

      return null;
   }

   public boolean isErrored() {
      return this.exception != null && !this.exception.getValues().isEmpty();
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("timestamp").value(logger, this.timestamp);
      if (this.message != null) {
         writer.name("message").value(logger, this.message);
      }

      if (this.logger != null) {
         writer.name("logger").value(this.logger);
      }

      if (this.threads != null && !this.threads.getValues().isEmpty()) {
         writer.name("threads");
         writer.beginObject();
         writer.name("values").value(logger, this.threads.getValues());
         writer.endObject();
      }

      if (this.exception != null && !this.exception.getValues().isEmpty()) {
         writer.name("exception");
         writer.beginObject();
         writer.name("values").value(logger, this.exception.getValues());
         writer.endObject();
      }

      if (this.level != null) {
         writer.name("level").value(logger, this.level);
      }

      if (this.transaction != null) {
         writer.name("transaction").value(this.transaction);
      }

      if (this.fingerprint != null) {
         writer.name("fingerprint").value(logger, this.fingerprint);
      }

      if (this.modules != null) {
         writer.name("modules").value(logger, this.modules);
      }

      new SentryBaseEvent.Serializer().serialize(this, writer, logger);
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

   public static final class Deserializer implements JsonDeserializer<SentryEvent> {
      @NotNull
      public SentryEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryEvent event = new SentryEvent();
         Map<String, Object> unknown = null;
         SentryBaseEvent.Deserializer baseEventDeserializer = new SentryBaseEvent.Deserializer();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "timestamp":
                  Date deserializedTimestamp = reader.nextDateOrNull(logger);
                  if (deserializedTimestamp != null) {
                     event.timestamp = deserializedTimestamp;
                  }
                  break;
               case "message":
                  event.message = reader.nextOrNull(logger, new Message.Deserializer());
                  break;
               case "logger":
                  event.logger = reader.nextStringOrNull();
                  break;
               case "threads":
                  reader.beginObject();
                  reader.nextName();
                  event.threads = new SentryValues<>(reader.nextListOrNull(logger, new SentryThread.Deserializer()));
                  reader.endObject();
                  break;
               case "exception":
                  reader.beginObject();
                  reader.nextName();
                  event.exception = new SentryValues<>(reader.nextListOrNull(logger, new SentryException.Deserializer()));
                  reader.endObject();
                  break;
               case "level":
                  event.level = reader.nextOrNull(logger, new SentryLevel.Deserializer());
                  break;
               case "transaction":
                  event.transaction = reader.nextStringOrNull();
                  break;
               case "fingerprint":
                  List<String> deserializedFingerprint = (List<String>)reader.nextObjectOrNull();
                  if (deserializedFingerprint != null) {
                     event.fingerprint = deserializedFingerprint;
                  }
                  break;
               case "modules":
                  Map<String, String> deserializedModules = (Map<String, String>)reader.nextObjectOrNull();
                  event.modules = CollectionUtils.newConcurrentHashMap(deserializedModules);
                  break;
               default:
                  if (!baseEventDeserializer.deserializeValue(event, nextName, reader, logger)) {
                     if (unknown == null) {
                        unknown = new ConcurrentHashMap<>();
                     }

                     reader.nextUnknown(logger, unknown, nextName);
                  }
            }
         }

         event.setUnknown(unknown);
         reader.endObject();
         return event;
      }
   }

   public static final class JsonKeys {
      public static final String TIMESTAMP = "timestamp";
      public static final String MESSAGE = "message";
      public static final String LOGGER = "logger";
      public static final String THREADS = "threads";
      public static final String EXCEPTION = "exception";
      public static final String LEVEL = "level";
      public static final String TRANSACTION = "transaction";
      public static final String FINGERPRINT = "fingerprint";
      public static final String MODULES = "modules";
   }
}
