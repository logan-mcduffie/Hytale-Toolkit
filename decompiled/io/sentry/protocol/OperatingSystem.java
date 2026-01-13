package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OperatingSystem implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "os";
   @Nullable
   private String name;
   @Nullable
   private String version;
   @Nullable
   private String rawDescription;
   @Nullable
   private String build;
   @Nullable
   private String kernelVersion;
   @Nullable
   private Boolean rooted;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public OperatingSystem() {
   }

   OperatingSystem(@NotNull OperatingSystem operatingSystem) {
      this.name = operatingSystem.name;
      this.version = operatingSystem.version;
      this.rawDescription = operatingSystem.rawDescription;
      this.build = operatingSystem.build;
      this.kernelVersion = operatingSystem.kernelVersion;
      this.rooted = operatingSystem.rooted;
      this.unknown = CollectionUtils.newConcurrentHashMap(operatingSystem.unknown);
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public String getVersion() {
      return this.version;
   }

   public void setVersion(@Nullable String version) {
      this.version = version;
   }

   @Nullable
   public String getRawDescription() {
      return this.rawDescription;
   }

   public void setRawDescription(@Nullable String rawDescription) {
      this.rawDescription = rawDescription;
   }

   @Nullable
   public String getBuild() {
      return this.build;
   }

   public void setBuild(@Nullable String build) {
      this.build = build;
   }

   @Nullable
   public String getKernelVersion() {
      return this.kernelVersion;
   }

   public void setKernelVersion(@Nullable String kernelVersion) {
      this.kernelVersion = kernelVersion;
   }

   @Nullable
   public Boolean isRooted() {
      return this.rooted;
   }

   public void setRooted(@Nullable Boolean rooted) {
      this.rooted = rooted;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         OperatingSystem that = (OperatingSystem)o;
         return Objects.equals(this.name, that.name)
            && Objects.equals(this.version, that.version)
            && Objects.equals(this.rawDescription, that.rawDescription)
            && Objects.equals(this.build, that.build)
            && Objects.equals(this.kernelVersion, that.kernelVersion)
            && Objects.equals(this.rooted, that.rooted);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.version, this.rawDescription, this.build, this.kernelVersion, this.rooted);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.version != null) {
         writer.name("version").value(this.version);
      }

      if (this.rawDescription != null) {
         writer.name("raw_description").value(this.rawDescription);
      }

      if (this.build != null) {
         writer.name("build").value(this.build);
      }

      if (this.kernelVersion != null) {
         writer.name("kernel_version").value(this.kernelVersion);
      }

      if (this.rooted != null) {
         writer.name("rooted").value(this.rooted);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<OperatingSystem> {
      @NotNull
      public OperatingSystem deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         OperatingSystem operatingSystem = new OperatingSystem();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "name":
                  operatingSystem.name = reader.nextStringOrNull();
                  break;
               case "version":
                  operatingSystem.version = reader.nextStringOrNull();
                  break;
               case "raw_description":
                  operatingSystem.rawDescription = reader.nextStringOrNull();
                  break;
               case "build":
                  operatingSystem.build = reader.nextStringOrNull();
                  break;
               case "kernel_version":
                  operatingSystem.kernelVersion = reader.nextStringOrNull();
                  break;
               case "rooted":
                  operatingSystem.rooted = reader.nextBooleanOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         operatingSystem.setUnknown(unknown);
         reader.endObject();
         return operatingSystem;
      }
   }

   public static final class JsonKeys {
      public static final String NAME = "name";
      public static final String VERSION = "version";
      public static final String RAW_DESCRIPTION = "raw_description";
      public static final String BUILD = "build";
      public static final String KERNEL_VERSION = "kernel_version";
      public static final String ROOTED = "rooted";
   }
}
