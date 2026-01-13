package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.ISentryLifecycleToken;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.ProfileContext;
import io.sentry.SpanContext;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Contexts implements JsonSerializable {
   private static final long serialVersionUID = 252445813254943011L;
   public static final String REPLAY_ID = "replay_id";
   @NotNull
   private final ConcurrentHashMap<String, Object> internalStorage = new ConcurrentHashMap<>();
   @NotNull
   protected final AutoClosableReentrantLock responseLock = new AutoClosableReentrantLock();

   public Contexts() {
   }

   public Contexts(@NotNull Contexts contexts) {
      for (Entry<String, Object> entry : contexts.entrySet()) {
         if (entry != null) {
            Object value = entry.getValue();
            if ("app".equals(entry.getKey()) && value instanceof App) {
               this.setApp(new App((App)value));
            } else if ("browser".equals(entry.getKey()) && value instanceof Browser) {
               this.setBrowser(new Browser((Browser)value));
            } else if ("device".equals(entry.getKey()) && value instanceof Device) {
               this.setDevice(new Device((Device)value));
            } else if ("os".equals(entry.getKey()) && value instanceof OperatingSystem) {
               this.setOperatingSystem(new OperatingSystem((OperatingSystem)value));
            } else if ("runtime".equals(entry.getKey()) && value instanceof SentryRuntime) {
               this.setRuntime(new SentryRuntime((SentryRuntime)value));
            } else if ("feedback".equals(entry.getKey()) && value instanceof Feedback) {
               this.setFeedback(new Feedback((Feedback)value));
            } else if ("gpu".equals(entry.getKey()) && value instanceof Gpu) {
               this.setGpu(new Gpu((Gpu)value));
            } else if ("trace".equals(entry.getKey()) && value instanceof SpanContext) {
               this.setTrace(new SpanContext((SpanContext)value));
            } else if ("profile".equals(entry.getKey()) && value instanceof ProfileContext) {
               this.setProfile(new ProfileContext((ProfileContext)value));
            } else if ("response".equals(entry.getKey()) && value instanceof Response) {
               this.setResponse(new Response((Response)value));
            } else if ("spring".equals(entry.getKey()) && value instanceof Spring) {
               this.setSpring(new Spring((Spring)value));
            } else {
               this.put(entry.getKey(), value);
            }
         }
      }
   }

   @Nullable
   private <T> T toContextType(@NotNull String key, @NotNull Class<T> clazz) {
      Object item = this.get(key);
      return clazz.isInstance(item) ? clazz.cast(item) : null;
   }

   @Nullable
   public SpanContext getTrace() {
      return this.toContextType("trace", SpanContext.class);
   }

   public void setTrace(@NotNull SpanContext traceContext) {
      Objects.requireNonNull(traceContext, "traceContext is required");
      this.put("trace", traceContext);
   }

   @Nullable
   public ProfileContext getProfile() {
      return this.toContextType("profile", ProfileContext.class);
   }

   public void setProfile(@Nullable ProfileContext profileContext) {
      Objects.requireNonNull(profileContext, "profileContext is required");
      this.put("profile", profileContext);
   }

   @Nullable
   public App getApp() {
      return this.toContextType("app", App.class);
   }

   public void setApp(@NotNull App app) {
      this.put("app", app);
   }

   @Nullable
   public Browser getBrowser() {
      return this.toContextType("browser", Browser.class);
   }

   public void setBrowser(@NotNull Browser browser) {
      this.put("browser", browser);
   }

   @Nullable
   public Device getDevice() {
      return this.toContextType("device", Device.class);
   }

   public void setDevice(@NotNull Device device) {
      this.put("device", device);
   }

   @Nullable
   public OperatingSystem getOperatingSystem() {
      return this.toContextType("os", OperatingSystem.class);
   }

   public void setOperatingSystem(@NotNull OperatingSystem operatingSystem) {
      this.put("os", operatingSystem);
   }

   @Nullable
   public SentryRuntime getRuntime() {
      return this.toContextType("runtime", SentryRuntime.class);
   }

   public void setRuntime(@NotNull SentryRuntime runtime) {
      this.put("runtime", runtime);
   }

   @Nullable
   public Feedback getFeedback() {
      return this.toContextType("feedback", Feedback.class);
   }

   public void setFeedback(@NotNull Feedback feedback) {
      this.put("feedback", feedback);
   }

   @Nullable
   public Gpu getGpu() {
      return this.toContextType("gpu", Gpu.class);
   }

   public void setGpu(@NotNull Gpu gpu) {
      this.put("gpu", gpu);
   }

   @Nullable
   public Response getResponse() {
      return this.toContextType("response", Response.class);
   }

   public void withResponse(HintUtils.SentryConsumer<Response> callback) {
      ISentryLifecycleToken ignored = this.responseLock.acquire();

      try {
         Response response = this.getResponse();
         if (response != null) {
            callback.accept(response);
         } else {
            Response newResponse = new Response();
            this.setResponse(newResponse);
            callback.accept(newResponse);
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

   public void setResponse(@NotNull Response response) {
      ISentryLifecycleToken ignored = this.responseLock.acquire();

      try {
         this.put("response", response);
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

   @Nullable
   public Spring getSpring() {
      return this.toContextType("spring", Spring.class);
   }

   public void setSpring(@NotNull Spring spring) {
      this.put("spring", spring);
   }

   @Nullable
   public FeatureFlags getFeatureFlags() {
      return this.toContextType("flags", FeatureFlags.class);
   }

   public void setFeatureFlags(@NotNull FeatureFlags featureFlags) {
      this.put("flags", featureFlags);
   }

   public int size() {
      return this.internalStorage.size();
   }

   public int getSize() {
      return this.size();
   }

   public boolean isEmpty() {
      return this.internalStorage.isEmpty();
   }

   public boolean containsKey(@Nullable Object key) {
      return key == null ? false : this.internalStorage.containsKey(key);
   }

   @Nullable
   public Object get(@Nullable Object key) {
      return key == null ? null : this.internalStorage.get(key);
   }

   @Nullable
   public Object put(@Nullable String key, @Nullable Object value) {
      if (key == null) {
         return null;
      } else {
         return value == null ? this.internalStorage.remove(key) : this.internalStorage.put(key, value);
      }
   }

   @Nullable
   public Object set(@Nullable String key, @Nullable Object value) {
      return this.put(key, value);
   }

   @Nullable
   public Object remove(@Nullable Object key) {
      return key == null ? null : this.internalStorage.remove(key);
   }

   @NotNull
   public Enumeration<String> keys() {
      return this.internalStorage.keys();
   }

   @NotNull
   public Set<Entry<String, Object>> entrySet() {
      return this.internalStorage.entrySet();
   }

   public void putAll(@Nullable Map<? extends String, ? extends Object> m) {
      if (m != null) {
         Map<String, Object> tmpMap = new HashMap<>();

         for (Entry<? extends String, ?> entry : m.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
               tmpMap.put(entry.getKey(), entry.getValue());
            }
         }

         this.internalStorage.putAll(tmpMap);
      }
   }

   public void putAll(@Nullable Contexts contexts) {
      if (contexts != null) {
         this.internalStorage.putAll(contexts.internalStorage);
      }
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (obj != null && obj instanceof Contexts) {
         Contexts otherContexts = (Contexts)obj;
         return this.internalStorage.equals(otherContexts.internalStorage);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.internalStorage.hashCode();
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      List<String> sortedKeys = Collections.list(this.keys());
      Collections.sort(sortedKeys);

      for (String key : sortedKeys) {
         Object value = this.get(key);
         if (value != null) {
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<Contexts> {
      @NotNull
      public Contexts deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Contexts contexts = new Contexts();
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "app":
                  contexts.setApp(new App.Deserializer().deserialize(reader, logger));
                  break;
               case "browser":
                  contexts.setBrowser(new Browser.Deserializer().deserialize(reader, logger));
                  break;
               case "device":
                  contexts.setDevice(new Device.Deserializer().deserialize(reader, logger));
                  break;
               case "gpu":
                  contexts.setGpu(new Gpu.Deserializer().deserialize(reader, logger));
                  break;
               case "os":
                  contexts.setOperatingSystem(new OperatingSystem.Deserializer().deserialize(reader, logger));
                  break;
               case "runtime":
                  contexts.setRuntime(new SentryRuntime.Deserializer().deserialize(reader, logger));
                  break;
               case "feedback":
                  contexts.setFeedback(new Feedback.Deserializer().deserialize(reader, logger));
                  break;
               case "trace":
                  contexts.setTrace(new SpanContext.Deserializer().deserialize(reader, logger));
                  break;
               case "profile":
                  contexts.setProfile(new ProfileContext.Deserializer().deserialize(reader, logger));
                  break;
               case "response":
                  contexts.setResponse(new Response.Deserializer().deserialize(reader, logger));
                  break;
               case "spring":
                  contexts.setSpring(new Spring.Deserializer().deserialize(reader, logger));
                  break;
               case "flags":
                  contexts.setFeatureFlags(new FeatureFlags.Deserializer().deserialize(reader, logger));
                  break;
               default:
                  Object object = reader.nextObjectOrNull();
                  if (object != null) {
                     contexts.put(nextName, object);
                  }
            }
         }

         reader.endObject();
         return contexts;
      }
   }
}
