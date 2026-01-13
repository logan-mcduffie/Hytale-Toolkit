package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MonitorContexts extends ConcurrentHashMap<String, Object> implements JsonSerializable {
   private static final long serialVersionUID = 3987329379811822556L;

   public MonitorContexts() {
   }

   public MonitorContexts(@NotNull MonitorContexts contexts) {
      for (Entry<String, Object> entry : contexts.entrySet()) {
         if (entry != null) {
            Object value = entry.getValue();
            if ("trace".equals(entry.getKey()) && value instanceof SpanContext) {
               this.setTrace(new SpanContext((SpanContext)value));
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

   public static final class Deserializer implements JsonDeserializer<MonitorContexts> {
      @NotNull
      public MonitorContexts deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         MonitorContexts contexts = new MonitorContexts();
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "trace":
                  contexts.setTrace(new SpanContext.Deserializer().deserialize(reader, logger));
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
