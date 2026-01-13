package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UserFeedback implements JsonUnknown, JsonSerializable {
   private final SentryId eventId;
   @Nullable
   private String name;
   @Nullable
   private String email;
   @Nullable
   private String comments;
   @Nullable
   private Map<String, Object> unknown;

   public UserFeedback(SentryId eventId) {
      this(eventId, null, null, null);
   }

   public UserFeedback(SentryId eventId, @Nullable String name, @Nullable String email, @Nullable String comments) {
      this.eventId = eventId;
      this.name = name;
      this.email = email;
      this.comments = comments;
   }

   public SentryId getEventId() {
      return this.eventId;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Nullable
   public String getEmail() {
      return this.email;
   }

   public void setEmail(@Nullable String email) {
      this.email = email;
   }

   @Nullable
   public String getComments() {
      return this.comments;
   }

   public void setComments(@Nullable String comments) {
      this.comments = comments;
   }

   @Override
   public String toString() {
      return "UserFeedback{eventId="
         + this.eventId
         + ", name='"
         + this.name
         + '\''
         + ", email='"
         + this.email
         + '\''
         + ", comments='"
         + this.comments
         + '\''
         + '}';
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
      writer.name("event_id");
      this.eventId.serialize(writer, logger);
      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.email != null) {
         writer.name("email").value(this.email);
      }

      if (this.comments != null) {
         writer.name("comments").value(this.comments);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<UserFeedback> {
      @NotNull
      public UserFeedback deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         SentryId sentryId = null;
         String name = null;
         String email = null;
         String comments = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "event_id":
                  sentryId = new SentryId.Deserializer().deserialize(reader, logger);
                  break;
               case "name":
                  name = reader.nextStringOrNull();
                  break;
               case "email":
                  email = reader.nextStringOrNull();
                  break;
               case "comments":
                  comments = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (sentryId == null) {
            String message = "Missing required field \"event_id\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            UserFeedback userFeedback = new UserFeedback(sentryId, name, email, comments);
            userFeedback.setUnknown(unknown);
            return userFeedback;
         }
      }
   }

   public static final class JsonKeys {
      public static final String EVENT_ID = "event_id";
      public static final String NAME = "name";
      public static final String EMAIL = "email";
      public static final String COMMENTS = "comments";
   }
}
