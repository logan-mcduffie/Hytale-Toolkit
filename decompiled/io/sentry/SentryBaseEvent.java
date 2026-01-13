package io.sentry;

import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.Request;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class SentryBaseEvent {
   public static final String DEFAULT_PLATFORM = "java";
   @Nullable
   private SentryId eventId;
   @NotNull
   private final Contexts contexts = new Contexts();
   @Nullable
   private SdkVersion sdk;
   @Nullable
   private Request request;
   @Nullable
   private Map<String, String> tags;
   @Nullable
   private String release;
   @Nullable
   private String environment;
   @Nullable
   private String platform;
   @Nullable
   private User user;
   @Nullable
   protected transient Throwable throwable;
   @Nullable
   private String serverName;
   @Nullable
   private String dist;
   @Nullable
   private List<Breadcrumb> breadcrumbs;
   @Nullable
   private DebugMeta debugMeta;
   @Nullable
   private Map<String, Object> extra;

   protected SentryBaseEvent(@NotNull SentryId eventId) {
      this.eventId = eventId;
   }

   protected SentryBaseEvent() {
      this(new SentryId());
   }

   @Nullable
   public SentryId getEventId() {
      return this.eventId;
   }

   public void setEventId(@Nullable SentryId eventId) {
      this.eventId = eventId;
   }

   @NotNull
   public Contexts getContexts() {
      return this.contexts;
   }

   @Nullable
   public SdkVersion getSdk() {
      return this.sdk;
   }

   public void setSdk(@Nullable SdkVersion sdk) {
      this.sdk = sdk;
   }

   @Nullable
   public Request getRequest() {
      return this.request;
   }

   public void setRequest(@Nullable Request request) {
      this.request = request;
   }

   @Nullable
   public Throwable getThrowable() {
      Throwable ex = this.throwable;
      return ex instanceof ExceptionMechanismException ? ((ExceptionMechanismException)ex).getThrowable() : ex;
   }

   @Internal
   @Nullable
   public Throwable getThrowableMechanism() {
      return this.throwable;
   }

   public void setThrowable(@Nullable Throwable throwable) {
      this.throwable = throwable;
   }

   @Internal
   @Nullable
   public Map<String, String> getTags() {
      return this.tags;
   }

   public void setTags(@Nullable Map<String, String> tags) {
      this.tags = CollectionUtils.newHashMap(tags);
   }

   public void removeTag(@Nullable String key) {
      if (this.tags != null && key != null) {
         this.tags.remove(key);
      }
   }

   @Nullable
   public String getTag(@Nullable String key) {
      return this.tags != null && key != null ? this.tags.get(key) : null;
   }

   public void setTag(@Nullable String key, @Nullable String value) {
      if (this.tags == null) {
         this.tags = new HashMap<>();
      }

      if (key != null) {
         if (value == null) {
            this.removeTag(key);
         } else {
            this.tags.put(key, value);
         }
      }
   }

   @Nullable
   public String getRelease() {
      return this.release;
   }

   public void setRelease(@Nullable String release) {
      this.release = release;
   }

   @Nullable
   public String getEnvironment() {
      return this.environment;
   }

   public void setEnvironment(@Nullable String environment) {
      this.environment = environment;
   }

   @Nullable
   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(@Nullable String platform) {
      this.platform = platform;
   }

   @Nullable
   public String getServerName() {
      return this.serverName;
   }

   public void setServerName(@Nullable String serverName) {
      this.serverName = serverName;
   }

   @Nullable
   public String getDist() {
      return this.dist;
   }

   public void setDist(@Nullable String dist) {
      this.dist = dist;
   }

   @Nullable
   public User getUser() {
      return this.user;
   }

   public void setUser(@Nullable User user) {
      this.user = user;
   }

   @Nullable
   public List<Breadcrumb> getBreadcrumbs() {
      return this.breadcrumbs;
   }

   public void setBreadcrumbs(@Nullable List<Breadcrumb> breadcrumbs) {
      this.breadcrumbs = CollectionUtils.newArrayList(breadcrumbs);
   }

   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      if (this.breadcrumbs == null) {
         this.breadcrumbs = new ArrayList<>();
      }

      this.breadcrumbs.add(breadcrumb);
   }

   @Nullable
   public DebugMeta getDebugMeta() {
      return this.debugMeta;
   }

   public void setDebugMeta(@Nullable DebugMeta debugMeta) {
      this.debugMeta = debugMeta;
   }

   @Nullable
   public Map<String, Object> getExtras() {
      return this.extra;
   }

   public void setExtras(@Nullable Map<String, Object> extra) {
      this.extra = CollectionUtils.newHashMap(extra);
   }

   public void setExtra(@Nullable String key, @Nullable Object value) {
      if (this.extra == null) {
         this.extra = new HashMap<>();
      }

      if (key != null) {
         if (value == null) {
            this.removeExtra(key);
         } else {
            this.extra.put(key, value);
         }
      }
   }

   public void removeExtra(@Nullable String key) {
      if (this.extra != null && key != null) {
         this.extra.remove(key);
      }
   }

   @Nullable
   public Object getExtra(@Nullable String key) {
      return this.extra != null && key != null ? this.extra.get(key) : null;
   }

   public void addBreadcrumb(@Nullable String message) {
      this.addBreadcrumb(new Breadcrumb(message));
   }

   public static final class Deserializer {
      public boolean deserializeValue(@NotNull SentryBaseEvent baseEvent, @NotNull String nextName, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         switch (nextName) {
            case "event_id":
               baseEvent.eventId = reader.nextOrNull(logger, new SentryId.Deserializer());
               return true;
            case "contexts":
               Contexts deserializedContexts = new Contexts.Deserializer().deserialize(reader, logger);
               baseEvent.contexts.putAll(deserializedContexts);
               return true;
            case "sdk":
               baseEvent.sdk = reader.nextOrNull(logger, new SdkVersion.Deserializer());
               return true;
            case "request":
               baseEvent.request = reader.nextOrNull(logger, new Request.Deserializer());
               return true;
            case "tags":
               Map<String, String> deserializedTags = (Map<String, String>)reader.nextObjectOrNull();
               baseEvent.tags = CollectionUtils.newConcurrentHashMap(deserializedTags);
               return true;
            case "release":
               baseEvent.release = reader.nextStringOrNull();
               return true;
            case "environment":
               baseEvent.environment = reader.nextStringOrNull();
               return true;
            case "platform":
               baseEvent.platform = reader.nextStringOrNull();
               return true;
            case "user":
               baseEvent.user = reader.nextOrNull(logger, new User.Deserializer());
               return true;
            case "server_name":
               baseEvent.serverName = reader.nextStringOrNull();
               return true;
            case "dist":
               baseEvent.dist = reader.nextStringOrNull();
               return true;
            case "breadcrumbs":
               baseEvent.breadcrumbs = reader.nextListOrNull(logger, new Breadcrumb.Deserializer());
               return true;
            case "debug_meta":
               baseEvent.debugMeta = reader.nextOrNull(logger, new DebugMeta.Deserializer());
               return true;
            case "extra":
               Map<String, Object> deserializedExtra = (Map<String, Object>)reader.nextObjectOrNull();
               baseEvent.extra = CollectionUtils.newConcurrentHashMap(deserializedExtra);
               return true;
            default:
               return false;
         }
      }
   }

   public static final class JsonKeys {
      public static final String EVENT_ID = "event_id";
      public static final String CONTEXTS = "contexts";
      public static final String SDK = "sdk";
      public static final String REQUEST = "request";
      public static final String TAGS = "tags";
      public static final String RELEASE = "release";
      public static final String ENVIRONMENT = "environment";
      public static final String PLATFORM = "platform";
      public static final String USER = "user";
      public static final String SERVER_NAME = "server_name";
      public static final String DIST = "dist";
      public static final String BREADCRUMBS = "breadcrumbs";
      public static final String DEBUG_META = "debug_meta";
      public static final String EXTRA = "extra";
   }

   public static final class Serializer {
      public void serialize(@NotNull SentryBaseEvent baseEvent, @NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         if (baseEvent.eventId != null) {
            writer.name("event_id").value(logger, baseEvent.eventId);
         }

         writer.name("contexts").value(logger, baseEvent.contexts);
         if (baseEvent.sdk != null) {
            writer.name("sdk").value(logger, baseEvent.sdk);
         }

         if (baseEvent.request != null) {
            writer.name("request").value(logger, baseEvent.request);
         }

         if (baseEvent.tags != null && !baseEvent.tags.isEmpty()) {
            writer.name("tags").value(logger, baseEvent.tags);
         }

         if (baseEvent.release != null) {
            writer.name("release").value(baseEvent.release);
         }

         if (baseEvent.environment != null) {
            writer.name("environment").value(baseEvent.environment);
         }

         if (baseEvent.platform != null) {
            writer.name("platform").value(baseEvent.platform);
         }

         if (baseEvent.user != null) {
            writer.name("user").value(logger, baseEvent.user);
         }

         if (baseEvent.serverName != null) {
            writer.name("server_name").value(baseEvent.serverName);
         }

         if (baseEvent.dist != null) {
            writer.name("dist").value(baseEvent.dist);
         }

         if (baseEvent.breadcrumbs != null && !baseEvent.breadcrumbs.isEmpty()) {
            writer.name("breadcrumbs").value(logger, baseEvent.breadcrumbs);
         }

         if (baseEvent.debugMeta != null) {
            writer.name("debug_meta").value(logger, baseEvent.debugMeta);
         }

         if (baseEvent.extra != null && !baseEvent.extra.isEmpty()) {
            writer.name("extra").value(logger, baseEvent.extra);
         }
      }
   }
}
