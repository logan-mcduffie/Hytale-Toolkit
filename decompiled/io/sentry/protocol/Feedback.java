package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Feedback implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "feedback";
   @NotNull
   private String message;
   @Nullable
   private String contactEmail;
   @Nullable
   private String name;
   @Nullable
   private SentryId associatedEventId;
   @Nullable
   private SentryId replayId;
   @Nullable
   private String url;
   @Nullable
   private Map<String, Object> unknown;

   public Feedback(@NotNull String message) {
      this.setMessage(message);
   }

   public Feedback(@NotNull Feedback feedback) {
      this.message = feedback.message;
      this.contactEmail = feedback.contactEmail;
      this.name = feedback.name;
      this.associatedEventId = feedback.associatedEventId;
      this.replayId = feedback.replayId;
      this.url = feedback.url;
      this.unknown = CollectionUtils.newConcurrentHashMap(feedback.unknown);
   }

   @Nullable
   public String getContactEmail() {
      return this.contactEmail;
   }

   public void setContactEmail(@Nullable String contactEmail) {
      this.contactEmail = contactEmail;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public SentryId getAssociatedEventId() {
      return this.associatedEventId;
   }

   public void setAssociatedEventId(@NotNull SentryId associatedEventId) {
      this.associatedEventId = associatedEventId;
   }

   @Nullable
   public SentryId getReplayId() {
      return this.replayId;
   }

   public void setReplayId(@NotNull SentryId replayId) {
      this.replayId = replayId;
   }

   @Nullable
   public String getUrl() {
      return this.url;
   }

   public void setUrl(@Nullable String url) {
      this.url = url;
   }

   @NotNull
   public String getMessage() {
      return this.message;
   }

   public void setMessage(@NotNull String message) {
      if (message.length() > 4096) {
         this.message = message.substring(0, 4096);
      } else {
         this.message = message;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Feedback)) {
         return false;
      } else {
         Feedback feedback = (Feedback)o;
         return Objects.equals(this.message, feedback.message)
            && Objects.equals(this.contactEmail, feedback.contactEmail)
            && Objects.equals(this.name, feedback.name)
            && Objects.equals(this.associatedEventId, feedback.associatedEventId)
            && Objects.equals(this.replayId, feedback.replayId)
            && Objects.equals(this.url, feedback.url)
            && Objects.equals(this.unknown, feedback.unknown);
      }
   }

   @Override
   public String toString() {
      return "Feedback{message='"
         + this.message
         + '\''
         + ", contactEmail='"
         + this.contactEmail
         + '\''
         + ", name='"
         + this.name
         + '\''
         + ", associatedEventId="
         + this.associatedEventId
         + ", replayId="
         + this.replayId
         + ", url='"
         + this.url
         + '\''
         + ", unknown="
         + this.unknown
         + '}';
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.message, this.contactEmail, this.name, this.associatedEventId, this.replayId, this.url, this.unknown);
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
      writer.name("message").value(this.message);
      if (this.contactEmail != null) {
         writer.name("contact_email").value(this.contactEmail);
      }

      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.associatedEventId != null) {
         writer.name("associated_event_id");
         this.associatedEventId.serialize(writer, logger);
      }

      if (this.replayId != null) {
         writer.name("replay_id");
         this.replayId.serialize(writer, logger);
      }

      if (this.url != null) {
         writer.name("url").value(this.url);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<Feedback> {
      @NotNull
      public Feedback deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         String message = null;
         String contactEmail = null;
         String name = null;
         SentryId associatedEventId = null;
         SentryId replayId = null;
         String url = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "message":
                  message = reader.nextStringOrNull();
                  break;
               case "contact_email":
                  contactEmail = reader.nextStringOrNull();
                  break;
               case "name":
                  name = reader.nextStringOrNull();
                  break;
               case "associated_event_id":
                  associatedEventId = new SentryId.Deserializer().deserialize(reader, logger);
                  break;
               case "replay_id":
                  replayId = new SentryId.Deserializer().deserialize(reader, logger);
                  break;
               case "url":
                  url = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (message == null) {
            String errorMessage = "Missing required field \"message\"";
            Exception exception = new IllegalStateException(errorMessage);
            logger.log(SentryLevel.ERROR, errorMessage, exception);
            throw exception;
         } else {
            Feedback feedback = new Feedback(message);
            feedback.contactEmail = contactEmail;
            feedback.name = name;
            feedback.associatedEventId = associatedEventId;
            feedback.replayId = replayId;
            feedback.url = url;
            feedback.unknown = unknown;
            return feedback;
         }
      }
   }

   public static final class JsonKeys {
      public static final String MESSAGE = "message";
      public static final String CONTACT_EMAIL = "contact_email";
      public static final String NAME = "name";
      public static final String ASSOCIATED_EVENT_ID = "associated_event_id";
      public static final String REPLAY_ID = "replay_id";
      public static final String URL = "url";
   }
}
