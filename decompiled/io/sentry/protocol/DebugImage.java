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
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugImage implements JsonUnknown, JsonSerializable {
   public static final String PROGUARD = "proguard";
   public static final String JVM = "jvm";
   @Nullable
   private String uuid;
   @Nullable
   private String type;
   @Nullable
   private String debugId;
   @Nullable
   private String debugFile;
   @Nullable
   private String codeId;
   @Nullable
   private String codeFile;
   @Nullable
   private String imageAddr;
   @Nullable
   private Long imageSize;
   @Nullable
   private String arch;
   @Nullable
   private Map<String, Object> unknown;

   @Nullable
   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(@Nullable String uuid) {
      this.uuid = uuid;
   }

   @Nullable
   public String getType() {
      return this.type;
   }

   public void setType(@Nullable String type) {
      this.type = type;
   }

   @Nullable
   public String getDebugId() {
      return this.debugId;
   }

   public void setDebugId(@Nullable String debugId) {
      this.debugId = debugId;
   }

   @Nullable
   public String getDebugFile() {
      return this.debugFile;
   }

   public void setDebugFile(@Nullable String debugFile) {
      this.debugFile = debugFile;
   }

   @Nullable
   public String getCodeFile() {
      return this.codeFile;
   }

   public void setCodeFile(@Nullable String codeFile) {
      this.codeFile = codeFile;
   }

   @Nullable
   public String getImageAddr() {
      return this.imageAddr;
   }

   public void setImageAddr(@Nullable String imageAddr) {
      this.imageAddr = imageAddr;
   }

   @Nullable
   public Long getImageSize() {
      return this.imageSize;
   }

   public void setImageSize(@Nullable Long imageSize) {
      this.imageSize = imageSize;
   }

   public void setImageSize(long imageSize) {
      this.imageSize = imageSize;
   }

   @Nullable
   public String getArch() {
      return this.arch;
   }

   public void setArch(@Nullable String arch) {
      this.arch = arch;
   }

   @Nullable
   public String getCodeId() {
      return this.codeId;
   }

   public void setCodeId(@Nullable String codeId) {
      this.codeId = codeId;
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
      if (this.uuid != null) {
         writer.name("uuid").value(this.uuid);
      }

      if (this.type != null) {
         writer.name("type").value(this.type);
      }

      if (this.debugId != null) {
         writer.name("debug_id").value(this.debugId);
      }

      if (this.debugFile != null) {
         writer.name("debug_file").value(this.debugFile);
      }

      if (this.codeId != null) {
         writer.name("code_id").value(this.codeId);
      }

      if (this.codeFile != null) {
         writer.name("code_file").value(this.codeFile);
      }

      if (this.imageAddr != null) {
         writer.name("image_addr").value(this.imageAddr);
      }

      if (this.imageSize != null) {
         writer.name("image_size").value(this.imageSize);
      }

      if (this.arch != null) {
         writer.name("arch").value(this.arch);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<DebugImage> {
      @NotNull
      public DebugImage deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         DebugImage debugImage = new DebugImage();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "uuid":
                  debugImage.uuid = reader.nextStringOrNull();
                  break;
               case "type":
                  debugImage.type = reader.nextStringOrNull();
                  break;
               case "debug_id":
                  debugImage.debugId = reader.nextStringOrNull();
                  break;
               case "debug_file":
                  debugImage.debugFile = reader.nextStringOrNull();
                  break;
               case "code_id":
                  debugImage.codeId = reader.nextStringOrNull();
                  break;
               case "code_file":
                  debugImage.codeFile = reader.nextStringOrNull();
                  break;
               case "image_addr":
                  debugImage.imageAddr = reader.nextStringOrNull();
                  break;
               case "image_size":
                  debugImage.imageSize = reader.nextLongOrNull();
                  break;
               case "arch":
                  debugImage.arch = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         debugImage.setUnknown(unknown);
         return debugImage;
      }
   }

   public static final class JsonKeys {
      public static final String UUID = "uuid";
      public static final String TYPE = "type";
      public static final String DEBUG_ID = "debug_id";
      public static final String DEBUG_FILE = "debug_file";
      public static final String CODE_ID = "code_id";
      public static final String CODE_FILE = "code_file";
      public static final String IMAGE_ADDR = "image_addr";
      public static final String IMAGE_SIZE = "image_size";
      public static final String ARCH = "arch";
   }
}
