package io.sentry;

import io.sentry.clientreport.ClientReport;
import io.sentry.protocol.SentryTransaction;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum SentryItemType implements JsonSerializable {
   Session("session"),
   Event("event"),
   UserFeedback("user_report"),
   Attachment("attachment"),
   Transaction("transaction"),
   Profile("profile"),
   ProfileChunk("profile_chunk"),
   ClientReport("client_report"),
   ReplayEvent("replay_event"),
   ReplayRecording("replay_recording"),
   ReplayVideo("replay_video"),
   CheckIn("check_in"),
   Feedback("feedback"),
   Log("log"),
   TraceMetric("trace_metric"),
   Span("span"),
   Unknown("__unknown__");

   private final String itemType;

   public static SentryItemType resolve(Object item) {
      if (item instanceof SentryEvent) {
         return ((SentryEvent)item).getContexts().getFeedback() == null ? Event : Feedback;
      } else if (item instanceof SentryTransaction) {
         return Transaction;
      } else if (item instanceof Session) {
         return Session;
      } else {
         return item instanceof ClientReport ? ClientReport : Attachment;
      }
   }

   private SentryItemType(String itemType) {
      this.itemType = itemType;
   }

   public String getItemType() {
      return this.itemType;
   }

   @NotNull
   public static SentryItemType valueOfLabel(String itemType) {
      for (SentryItemType sentryItemType : values()) {
         if (sentryItemType.itemType.equals(itemType)) {
            return sentryItemType;
         }
      }

      return Unknown;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.value(this.itemType);
   }

   public static final class Deserializer implements JsonDeserializer<SentryItemType> {
      @NotNull
      public SentryItemType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         return SentryItemType.valueOfLabel(reader.nextString().toLowerCase(Locale.ROOT));
      }
   }
}
