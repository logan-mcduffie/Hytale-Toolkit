package io.sentry;

import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryEnvelopeHeader implements JsonSerializable, JsonUnknown {
   @Nullable
   private final SentryId eventId;
   @Nullable
   private final SdkVersion sdkVersion;
   @Nullable
   private final TraceContext traceContext;
   @Nullable
   private Date sentAt;
   @Nullable
   private Map<String, Object> unknown;

   public SentryEnvelopeHeader(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion) {
      this(eventId, sdkVersion, null);
   }

   public SentryEnvelopeHeader(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion, @Nullable TraceContext traceContext) {
      this.eventId = eventId;
      this.sdkVersion = sdkVersion;
      this.traceContext = traceContext;
   }

   public SentryEnvelopeHeader(@Nullable SentryId eventId) {
      this(eventId, null);
   }

   public SentryEnvelopeHeader() {
      this(new SentryId());
   }

   @Nullable
   public SentryId getEventId() {
      return this.eventId;
   }

   @Nullable
   public SdkVersion getSdkVersion() {
      return this.sdkVersion;
   }

   @Nullable
   public TraceContext getTraceContext() {
      return this.traceContext;
   }

   @Nullable
   public Date getSentAt() {
      return this.sentAt;
   }

   public void setSentAt(@Nullable Date sentAt) {
      this.sentAt = sentAt;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.eventId != null) {
         writer.name("event_id").value(logger, this.eventId);
      }

      if (this.sdkVersion != null) {
         writer.name("sdk").value(logger, this.sdkVersion);
      }

      if (this.traceContext != null) {
         writer.name("trace").value(logger, this.traceContext);
      }

      if (this.sentAt != null) {
         writer.name("sent_at").value(logger, DateUtils.getTimestamp(this.sentAt));
      }

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

   public static final class Deserializer implements JsonDeserializer<SentryEnvelopeHeader> {
      @NotNull
      public SentryEnvelopeHeader deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryId eventId = null;
         SdkVersion sdkVersion = null;
         TraceContext traceContext = null;
         Date sentAt = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "event_id":
                  eventId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  break;
               case "sdk":
                  sdkVersion = reader.nextOrNull(logger, new SdkVersion.Deserializer());
                  break;
               case "trace":
                  traceContext = reader.nextOrNull(logger, new TraceContext.Deserializer());
                  break;
               case "sent_at":
                  sentAt = reader.nextDateOrNull(logger);
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(eventId, sdkVersion, traceContext);
         sentryEnvelopeHeader.setSentAt(sentAt);
         sentryEnvelopeHeader.setUnknown(unknown);
         reader.endObject();
         return sentryEnvelopeHeader;
      }
   }

   public static final class JsonKeys {
      public static final String EVENT_ID = "event_id";
      public static final String SDK = "sdk";
      public static final String TRACE = "trace";
      public static final String SENT_AT = "sent_at";
   }
}
