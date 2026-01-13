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

public final class ViewHierarchyNode implements JsonUnknown, JsonSerializable {
   @Nullable
   private String renderingSystem;
   @Nullable
   private String type;
   @Nullable
   private String identifier;
   @Nullable
   private String tag;
   @Nullable
   private Double width;
   @Nullable
   private Double height;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private String visibility;
   @Nullable
   private Double alpha;
   @Nullable
   private List<ViewHierarchyNode> children;
   @Nullable
   private Map<String, Object> unknown;

   public void setRenderingSystem(String renderingSystem) {
      this.renderingSystem = renderingSystem;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setIdentifier(@Nullable String identifier) {
      this.identifier = identifier;
   }

   public void setTag(@Nullable String tag) {
      this.tag = tag;
   }

   public void setWidth(@Nullable Double width) {
      this.width = width;
   }

   public void setHeight(@Nullable Double height) {
      this.height = height;
   }

   public void setX(@Nullable Double x) {
      this.x = x;
   }

   public void setY(@Nullable Double y) {
      this.y = y;
   }

   public void setVisibility(@Nullable String visibility) {
      this.visibility = visibility;
   }

   public void setAlpha(@Nullable Double alpha) {
      this.alpha = alpha;
   }

   public void setChildren(@Nullable List<ViewHierarchyNode> children) {
      this.children = children;
   }

   @Nullable
   public String getRenderingSystem() {
      return this.renderingSystem;
   }

   @Nullable
   public String getType() {
      return this.type;
   }

   @Nullable
   public String getIdentifier() {
      return this.identifier;
   }

   @Nullable
   public String getTag() {
      return this.tag;
   }

   @Nullable
   public Double getWidth() {
      return this.width;
   }

   @Nullable
   public Double getHeight() {
      return this.height;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public String getVisibility() {
      return this.visibility;
   }

   @Nullable
   public Double getAlpha() {
      return this.alpha;
   }

   @Nullable
   public List<ViewHierarchyNode> getChildren() {
      return this.children;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.renderingSystem != null) {
         writer.name("rendering_system").value(this.renderingSystem);
      }

      if (this.type != null) {
         writer.name("type").value(this.type);
      }

      if (this.identifier != null) {
         writer.name("identifier").value(this.identifier);
      }

      if (this.tag != null) {
         writer.name("tag").value(this.tag);
      }

      if (this.width != null) {
         writer.name("width").value(this.width);
      }

      if (this.height != null) {
         writer.name("height").value(this.height);
      }

      if (this.x != null) {
         writer.name("x").value(this.x);
      }

      if (this.y != null) {
         writer.name("y").value(this.y);
      }

      if (this.visibility != null) {
         writer.name("visibility").value(this.visibility);
      }

      if (this.alpha != null) {
         writer.name("alpha").value(this.alpha);
      }

      if (this.children != null && !this.children.isEmpty()) {
         writer.name("children").value(logger, this.children);
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

   public static final class Deserializer implements JsonDeserializer<ViewHierarchyNode> {
      @NotNull
      public ViewHierarchyNode deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         ViewHierarchyNode node = new ViewHierarchyNode();
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "rendering_system":
                  node.renderingSystem = reader.nextStringOrNull();
                  break;
               case "type":
                  node.type = reader.nextStringOrNull();
                  break;
               case "identifier":
                  node.identifier = reader.nextStringOrNull();
                  break;
               case "tag":
                  node.tag = reader.nextStringOrNull();
                  break;
               case "width":
                  node.width = reader.nextDoubleOrNull();
                  break;
               case "height":
                  node.height = reader.nextDoubleOrNull();
                  break;
               case "x":
                  node.x = reader.nextDoubleOrNull();
                  break;
               case "y":
                  node.y = reader.nextDoubleOrNull();
                  break;
               case "visibility":
                  node.visibility = reader.nextStringOrNull();
                  break;
               case "alpha":
                  node.alpha = reader.nextDoubleOrNull();
                  break;
               case "children":
                  node.children = reader.nextListOrNull(logger, this);
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         node.setUnknown(unknown);
         return node;
      }
   }

   public static final class JsonKeys {
      public static final String RENDERING_SYSTEM = "rendering_system";
      public static final String TYPE = "type";
      public static final String IDENTIFIER = "identifier";
      public static final String TAG = "tag";
      public static final String WIDTH = "width";
      public static final String HEIGHT = "height";
      public static final String X = "x";
      public static final String Y = "y";
      public static final String VISIBILITY = "visibility";
      public static final String ALPHA = "alpha";
      public static final String CHILDREN = "children";
   }
}
