package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ViewHierarchy implements JsonUnknown, JsonSerializable {
   @Nullable
   private final String renderingSystem;
   @Nullable
   private final List<ViewHierarchyNode> windows;
   @Nullable
   private Map<String, Object> unknown;

   public ViewHierarchy(@Nullable String renderingSystem, @Nullable List<ViewHierarchyNode> windows) {
      this.renderingSystem = renderingSystem;
      this.windows = windows;
   }

   @Nullable
   public String getRenderingSystem() {
      return this.renderingSystem;
   }

   @Nullable
   public List<ViewHierarchyNode> getWindows() {
      return this.windows;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.renderingSystem != null) {
         writer.name("rendering_system").value(this.renderingSystem);
      }

      if (this.windows != null) {
         writer.name("windows").value(logger, this.windows);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<ViewHierarchy> {
      @NotNull
      public ViewHierarchy deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         String renderingSystem = null;
         List<ViewHierarchyNode> windows = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "rendering_system":
                  renderingSystem = reader.nextStringOrNull();
                  break;
               case "windows":
                  windows = reader.nextListOrNull(logger, new ViewHierarchyNode.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         ViewHierarchy viewHierarchy = new ViewHierarchy(renderingSystem, windows);
         viewHierarchy.setUnknown(unknown);
         return viewHierarchy;
      }
   }

   public static final class JsonKeys {
      public static final String RENDERING_SYSTEM = "rendering_system";
      public static final String WINDOWS = "windows";
   }
}
