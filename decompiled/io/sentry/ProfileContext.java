package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileContext implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "profile";
   @NotNull
   private SentryId profilerId;
   @Nullable
   private Map<String, Object> unknown;

   public ProfileContext() {
      this(SentryId.EMPTY_ID);
   }

   public ProfileContext(@NotNull SentryId profilerId) {
      this.profilerId = profilerId;
   }

   public ProfileContext(@NotNull ProfileContext profileContext) {
      this.profilerId = profileContext.profilerId;
      Map<String, Object> copiedUnknown = CollectionUtils.newConcurrentHashMap(profileContext.unknown);
      if (copiedUnknown != null) {
         this.unknown = copiedUnknown;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ProfileContext)) {
         return false;
      } else {
         ProfileContext that = (ProfileContext)o;
         return this.profilerId.equals(that.profilerId);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.profilerId);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("profiler_id").value(logger, this.profilerId);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   @NotNull
   public SentryId getProfilerId() {
      return this.profilerId;
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

   public static final class Deserializer implements JsonDeserializer<ProfileContext> {
      @NotNull
      public ProfileContext deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfileContext data = new ProfileContext();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "profiler_id":
                  SentryId profilerId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  if (profilerId != null) {
                     data.profilerId = profilerId;
                  }
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         data.setUnknown(unknown);
         reader.endObject();
         return data;
      }
   }

   public static final class JsonKeys {
      public static final String PROFILER_ID = "profiler_id";
   }
}
