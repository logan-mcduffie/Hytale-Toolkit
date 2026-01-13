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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Device implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "device";
   @Nullable
   private String name;
   @Nullable
   private String manufacturer;
   @Nullable
   private String brand;
   @Nullable
   private String family;
   @Nullable
   private String model;
   @Nullable
   private String modelId;
   @Nullable
   private String[] archs;
   @Nullable
   private Float batteryLevel;
   @Nullable
   private Boolean charging;
   @Nullable
   private Boolean online;
   @Nullable
   private Device.DeviceOrientation orientation;
   @Nullable
   private Boolean simulator;
   @Nullable
   private Long memorySize;
   @Nullable
   private Long freeMemory;
   @Nullable
   private Long usableMemory;
   @Nullable
   private Boolean lowMemory;
   @Nullable
   private Long storageSize;
   @Nullable
   private Long freeStorage;
   @Nullable
   private Long externalStorageSize;
   @Nullable
   private Long externalFreeStorage;
   @Nullable
   private Integer screenWidthPixels;
   @Nullable
   private Integer screenHeightPixels;
   @Nullable
   private Float screenDensity;
   @Nullable
   private Integer screenDpi;
   @Nullable
   private Date bootTime;
   @Nullable
   private TimeZone timezone;
   @Nullable
   private String id;
   @Nullable
   private String locale;
   @Nullable
   private String connectionType;
   @Nullable
   private Float batteryTemperature;
   @Nullable
   private Integer processorCount;
   @Nullable
   private Double processorFrequency;
   @Nullable
   private String cpuDescription;
   @Nullable
   private String chipset;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public Device() {
   }

   Device(@NotNull Device device) {
      this.name = device.name;
      this.manufacturer = device.manufacturer;
      this.brand = device.brand;
      this.family = device.family;
      this.model = device.model;
      this.modelId = device.modelId;
      this.charging = device.charging;
      this.online = device.online;
      this.orientation = device.orientation;
      this.simulator = device.simulator;
      this.memorySize = device.memorySize;
      this.freeMemory = device.freeMemory;
      this.usableMemory = device.usableMemory;
      this.lowMemory = device.lowMemory;
      this.storageSize = device.storageSize;
      this.freeStorage = device.freeStorage;
      this.externalStorageSize = device.externalStorageSize;
      this.externalFreeStorage = device.externalFreeStorage;
      this.screenWidthPixels = device.screenWidthPixels;
      this.screenHeightPixels = device.screenHeightPixels;
      this.screenDensity = device.screenDensity;
      this.screenDpi = device.screenDpi;
      this.bootTime = device.bootTime;
      this.id = device.id;
      this.connectionType = device.connectionType;
      this.batteryTemperature = device.batteryTemperature;
      this.batteryLevel = device.batteryLevel;
      String[] archsRef = device.archs;
      this.archs = archsRef != null ? (String[])archsRef.clone() : null;
      this.locale = device.locale;
      TimeZone timezoneRef = device.timezone;
      this.timezone = timezoneRef != null ? (TimeZone)timezoneRef.clone() : null;
      this.processorCount = device.processorCount;
      this.processorFrequency = device.processorFrequency;
      this.cpuDescription = device.cpuDescription;
      this.chipset = device.chipset;
      this.unknown = CollectionUtils.newConcurrentHashMap(device.unknown);
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public String getManufacturer() {
      return this.manufacturer;
   }

   public void setManufacturer(@Nullable String manufacturer) {
      this.manufacturer = manufacturer;
   }

   @Nullable
   public String getBrand() {
      return this.brand;
   }

   public void setBrand(@Nullable String brand) {
      this.brand = brand;
   }

   @Nullable
   public String getFamily() {
      return this.family;
   }

   public void setFamily(@Nullable String family) {
      this.family = family;
   }

   @Nullable
   public String getModel() {
      return this.model;
   }

   public void setModel(@Nullable String model) {
      this.model = model;
   }

   @Nullable
   public String getModelId() {
      return this.modelId;
   }

   public void setModelId(@Nullable String modelId) {
      this.modelId = modelId;
   }

   @Nullable
   public Float getBatteryLevel() {
      return this.batteryLevel;
   }

   public void setBatteryLevel(@Nullable Float batteryLevel) {
      this.batteryLevel = batteryLevel;
   }

   @Nullable
   public Boolean isCharging() {
      return this.charging;
   }

   public void setCharging(@Nullable Boolean charging) {
      this.charging = charging;
   }

   @Nullable
   public Boolean isOnline() {
      return this.online;
   }

   public void setOnline(@Nullable Boolean online) {
      this.online = online;
   }

   @Nullable
   public Device.DeviceOrientation getOrientation() {
      return this.orientation;
   }

   public void setOrientation(@Nullable Device.DeviceOrientation orientation) {
      this.orientation = orientation;
   }

   @Nullable
   public Boolean isSimulator() {
      return this.simulator;
   }

   public void setSimulator(@Nullable Boolean simulator) {
      this.simulator = simulator;
   }

   @Nullable
   public Long getMemorySize() {
      return this.memorySize;
   }

   public void setMemorySize(@Nullable Long memorySize) {
      this.memorySize = memorySize;
   }

   @Nullable
   public Long getFreeMemory() {
      return this.freeMemory;
   }

   public void setFreeMemory(@Nullable Long freeMemory) {
      this.freeMemory = freeMemory;
   }

   @Nullable
   public Long getUsableMemory() {
      return this.usableMemory;
   }

   public void setUsableMemory(@Nullable Long usableMemory) {
      this.usableMemory = usableMemory;
   }

   @Nullable
   public Boolean isLowMemory() {
      return this.lowMemory;
   }

   public void setLowMemory(@Nullable Boolean lowMemory) {
      this.lowMemory = lowMemory;
   }

   @Nullable
   public Long getStorageSize() {
      return this.storageSize;
   }

   public void setStorageSize(@Nullable Long storageSize) {
      this.storageSize = storageSize;
   }

   @Nullable
   public Long getFreeStorage() {
      return this.freeStorage;
   }

   public void setFreeStorage(@Nullable Long freeStorage) {
      this.freeStorage = freeStorage;
   }

   @Nullable
   public Long getExternalStorageSize() {
      return this.externalStorageSize;
   }

   public void setExternalStorageSize(@Nullable Long externalStorageSize) {
      this.externalStorageSize = externalStorageSize;
   }

   @Nullable
   public Long getExternalFreeStorage() {
      return this.externalFreeStorage;
   }

   public void setExternalFreeStorage(@Nullable Long externalFreeStorage) {
      this.externalFreeStorage = externalFreeStorage;
   }

   @Nullable
   public Float getScreenDensity() {
      return this.screenDensity;
   }

   public void setScreenDensity(@Nullable Float screenDensity) {
      this.screenDensity = screenDensity;
   }

   @Nullable
   public Integer getScreenDpi() {
      return this.screenDpi;
   }

   public void setScreenDpi(@Nullable Integer screenDpi) {
      this.screenDpi = screenDpi;
   }

   @Nullable
   public Date getBootTime() {
      Date bootTimeRef = this.bootTime;
      return bootTimeRef != null ? (Date)bootTimeRef.clone() : null;
   }

   public void setBootTime(@Nullable Date bootTime) {
      this.bootTime = bootTime;
   }

   @Nullable
   public TimeZone getTimezone() {
      return this.timezone;
   }

   public void setTimezone(@Nullable TimeZone timezone) {
      this.timezone = timezone;
   }

   @Nullable
   public String[] getArchs() {
      return this.archs;
   }

   public void setArchs(@Nullable String[] archs) {
      this.archs = archs;
   }

   @Nullable
   public Integer getScreenWidthPixels() {
      return this.screenWidthPixels;
   }

   public void setScreenWidthPixels(@Nullable Integer screenWidthPixels) {
      this.screenWidthPixels = screenWidthPixels;
   }

   @Nullable
   public Integer getScreenHeightPixels() {
      return this.screenHeightPixels;
   }

   public void setScreenHeightPixels(@Nullable Integer screenHeightPixels) {
      this.screenHeightPixels = screenHeightPixels;
   }

   @Nullable
   public String getId() {
      return this.id;
   }

   public void setId(@Nullable String id) {
      this.id = id;
   }

   @Nullable
   public String getConnectionType() {
      return this.connectionType;
   }

   public void setConnectionType(@Nullable String connectionType) {
      this.connectionType = connectionType;
   }

   @Nullable
   public Float getBatteryTemperature() {
      return this.batteryTemperature;
   }

   public void setBatteryTemperature(@Nullable Float batteryTemperature) {
      this.batteryTemperature = batteryTemperature;
   }

   @Nullable
   public Integer getProcessorCount() {
      return this.processorCount;
   }

   public void setProcessorCount(@Nullable Integer processorCount) {
      this.processorCount = processorCount;
   }

   @Nullable
   public Double getProcessorFrequency() {
      return this.processorFrequency;
   }

   public void setProcessorFrequency(@Nullable Double processorFrequency) {
      this.processorFrequency = processorFrequency;
   }

   @Nullable
   public String getCpuDescription() {
      return this.cpuDescription;
   }

   public void setCpuDescription(@Nullable String cpuDescription) {
      this.cpuDescription = cpuDescription;
   }

   @Nullable
   public String getChipset() {
      return this.chipset;
   }

   public void setChipset(@Nullable String chipset) {
      this.chipset = chipset;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Device device = (Device)o;
         return Objects.equals(this.name, device.name)
            && Objects.equals(this.manufacturer, device.manufacturer)
            && Objects.equals(this.brand, device.brand)
            && Objects.equals(this.family, device.family)
            && Objects.equals(this.model, device.model)
            && Objects.equals(this.modelId, device.modelId)
            && Arrays.equals((Object[])this.archs, (Object[])device.archs)
            && Objects.equals(this.batteryLevel, device.batteryLevel)
            && Objects.equals(this.charging, device.charging)
            && Objects.equals(this.online, device.online)
            && this.orientation == device.orientation
            && Objects.equals(this.simulator, device.simulator)
            && Objects.equals(this.memorySize, device.memorySize)
            && Objects.equals(this.freeMemory, device.freeMemory)
            && Objects.equals(this.usableMemory, device.usableMemory)
            && Objects.equals(this.lowMemory, device.lowMemory)
            && Objects.equals(this.storageSize, device.storageSize)
            && Objects.equals(this.freeStorage, device.freeStorage)
            && Objects.equals(this.externalStorageSize, device.externalStorageSize)
            && Objects.equals(this.externalFreeStorage, device.externalFreeStorage)
            && Objects.equals(this.screenWidthPixels, device.screenWidthPixels)
            && Objects.equals(this.screenHeightPixels, device.screenHeightPixels)
            && Objects.equals(this.screenDensity, device.screenDensity)
            && Objects.equals(this.screenDpi, device.screenDpi)
            && Objects.equals(this.bootTime, device.bootTime)
            && Objects.equals(this.id, device.id)
            && Objects.equals(this.locale, device.locale)
            && Objects.equals(this.connectionType, device.connectionType)
            && Objects.equals(this.batteryTemperature, device.batteryTemperature)
            && Objects.equals(this.processorCount, device.processorCount)
            && Objects.equals(this.processorFrequency, device.processorFrequency)
            && Objects.equals(this.cpuDescription, device.cpuDescription)
            && Objects.equals(this.chipset, device.chipset);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = Objects.hash(
         this.name,
         this.manufacturer,
         this.brand,
         this.family,
         this.model,
         this.modelId,
         this.batteryLevel,
         this.charging,
         this.online,
         this.orientation,
         this.simulator,
         this.memorySize,
         this.freeMemory,
         this.usableMemory,
         this.lowMemory,
         this.storageSize,
         this.freeStorage,
         this.externalStorageSize,
         this.externalFreeStorage,
         this.screenWidthPixels,
         this.screenHeightPixels,
         this.screenDensity,
         this.screenDpi,
         this.bootTime,
         this.timezone,
         this.id,
         this.locale,
         this.connectionType,
         this.batteryTemperature,
         this.processorCount,
         this.processorFrequency,
         this.cpuDescription,
         this.chipset
      );
      return 31 * result + Arrays.hashCode((Object[])this.archs);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.manufacturer != null) {
         writer.name("manufacturer").value(this.manufacturer);
      }

      if (this.brand != null) {
         writer.name("brand").value(this.brand);
      }

      if (this.family != null) {
         writer.name("family").value(this.family);
      }

      if (this.model != null) {
         writer.name("model").value(this.model);
      }

      if (this.modelId != null) {
         writer.name("model_id").value(this.modelId);
      }

      if (this.archs != null) {
         writer.name("archs").value(logger, this.archs);
      }

      if (this.batteryLevel != null) {
         writer.name("battery_level").value(this.batteryLevel);
      }

      if (this.charging != null) {
         writer.name("charging").value(this.charging);
      }

      if (this.online != null) {
         writer.name("online").value(this.online);
      }

      if (this.orientation != null) {
         writer.name("orientation").value(logger, this.orientation);
      }

      if (this.simulator != null) {
         writer.name("simulator").value(this.simulator);
      }

      if (this.memorySize != null) {
         writer.name("memory_size").value(this.memorySize);
      }

      if (this.freeMemory != null) {
         writer.name("free_memory").value(this.freeMemory);
      }

      if (this.usableMemory != null) {
         writer.name("usable_memory").value(this.usableMemory);
      }

      if (this.lowMemory != null) {
         writer.name("low_memory").value(this.lowMemory);
      }

      if (this.storageSize != null) {
         writer.name("storage_size").value(this.storageSize);
      }

      if (this.freeStorage != null) {
         writer.name("free_storage").value(this.freeStorage);
      }

      if (this.externalStorageSize != null) {
         writer.name("external_storage_size").value(this.externalStorageSize);
      }

      if (this.externalFreeStorage != null) {
         writer.name("external_free_storage").value(this.externalFreeStorage);
      }

      if (this.screenWidthPixels != null) {
         writer.name("screen_width_pixels").value(this.screenWidthPixels);
      }

      if (this.screenHeightPixels != null) {
         writer.name("screen_height_pixels").value(this.screenHeightPixels);
      }

      if (this.screenDensity != null) {
         writer.name("screen_density").value(this.screenDensity);
      }

      if (this.screenDpi != null) {
         writer.name("screen_dpi").value(this.screenDpi);
      }

      if (this.bootTime != null) {
         writer.name("boot_time").value(logger, this.bootTime);
      }

      if (this.timezone != null) {
         writer.name("timezone").value(logger, this.timezone);
      }

      if (this.id != null) {
         writer.name("id").value(this.id);
      }

      if (this.connectionType != null) {
         writer.name("connection_type").value(this.connectionType);
      }

      if (this.batteryTemperature != null) {
         writer.name("battery_temperature").value(this.batteryTemperature);
      }

      if (this.locale != null) {
         writer.name("locale").value(this.locale);
      }

      if (this.processorCount != null) {
         writer.name("processor_count").value(this.processorCount);
      }

      if (this.processorFrequency != null) {
         writer.name("processor_frequency").value(this.processorFrequency);
      }

      if (this.cpuDescription != null) {
         writer.name("cpu_description").value(this.cpuDescription);
      }

      if (this.chipset != null) {
         writer.name("chipset").value(this.chipset);
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
   public String getLocale() {
      return this.locale;
   }

   public void setLocale(@Nullable String locale) {
      this.locale = locale;
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

   public static final class Deserializer implements JsonDeserializer<Device> {
      @NotNull
      public Device deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Device device = new Device();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "name":
                  device.name = reader.nextStringOrNull();
                  break;
               case "manufacturer":
                  device.manufacturer = reader.nextStringOrNull();
                  break;
               case "brand":
                  device.brand = reader.nextStringOrNull();
                  break;
               case "family":
                  device.family = reader.nextStringOrNull();
                  break;
               case "model":
                  device.model = reader.nextStringOrNull();
                  break;
               case "model_id":
                  device.modelId = reader.nextStringOrNull();
                  break;
               case "archs":
                  List<?> archsList = (List<?>)reader.nextObjectOrNull();
                  if (archsList != null) {
                     Object[] archsArray = new String[archsList.size()];
                     archsList.toArray(archsArray);
                     device.archs = (String[])archsArray;
                  }
                  break;
               case "battery_level":
                  device.batteryLevel = reader.nextFloatOrNull();
                  break;
               case "charging":
                  device.charging = reader.nextBooleanOrNull();
                  break;
               case "online":
                  device.online = reader.nextBooleanOrNull();
                  break;
               case "orientation":
                  device.orientation = reader.nextOrNull(logger, new Device.DeviceOrientation.Deserializer());
                  break;
               case "simulator":
                  device.simulator = reader.nextBooleanOrNull();
                  break;
               case "memory_size":
                  device.memorySize = reader.nextLongOrNull();
                  break;
               case "free_memory":
                  device.freeMemory = reader.nextLongOrNull();
                  break;
               case "usable_memory":
                  device.usableMemory = reader.nextLongOrNull();
                  break;
               case "low_memory":
                  device.lowMemory = reader.nextBooleanOrNull();
                  break;
               case "storage_size":
                  device.storageSize = reader.nextLongOrNull();
                  break;
               case "free_storage":
                  device.freeStorage = reader.nextLongOrNull();
                  break;
               case "external_storage_size":
                  device.externalStorageSize = reader.nextLongOrNull();
                  break;
               case "external_free_storage":
                  device.externalFreeStorage = reader.nextLongOrNull();
                  break;
               case "screen_width_pixels":
                  device.screenWidthPixels = reader.nextIntegerOrNull();
                  break;
               case "screen_height_pixels":
                  device.screenHeightPixels = reader.nextIntegerOrNull();
                  break;
               case "screen_density":
                  device.screenDensity = reader.nextFloatOrNull();
                  break;
               case "screen_dpi":
                  device.screenDpi = reader.nextIntegerOrNull();
                  break;
               case "boot_time":
                  if (reader.peek() == JsonToken.STRING) {
                     device.bootTime = reader.nextDateOrNull(logger);
                  }
                  break;
               case "timezone":
                  device.timezone = reader.nextTimeZoneOrNull(logger);
                  break;
               case "id":
                  device.id = reader.nextStringOrNull();
                  break;
               case "connection_type":
                  device.connectionType = reader.nextStringOrNull();
                  break;
               case "battery_temperature":
                  device.batteryTemperature = reader.nextFloatOrNull();
                  break;
               case "locale":
                  device.locale = reader.nextStringOrNull();
                  break;
               case "processor_count":
                  device.processorCount = reader.nextIntegerOrNull();
                  break;
               case "processor_frequency":
                  device.processorFrequency = reader.nextDoubleOrNull();
                  break;
               case "cpu_description":
                  device.cpuDescription = reader.nextStringOrNull();
                  break;
               case "chipset":
                  device.chipset = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         device.setUnknown(unknown);
         reader.endObject();
         return device;
      }
   }

   public static enum DeviceOrientation implements JsonSerializable {
      PORTRAIT,
      LANDSCAPE;

      @Override
      public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         writer.value(this.toString().toLowerCase(Locale.ROOT));
      }

      public static final class Deserializer implements JsonDeserializer<Device.DeviceOrientation> {
         @NotNull
         public Device.DeviceOrientation deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return Device.DeviceOrientation.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
         }
      }
   }

   public static final class JsonKeys {
      public static final String NAME = "name";
      public static final String MANUFACTURER = "manufacturer";
      public static final String BRAND = "brand";
      public static final String FAMILY = "family";
      public static final String MODEL = "model";
      public static final String MODEL_ID = "model_id";
      public static final String ARCHS = "archs";
      public static final String BATTERY_LEVEL = "battery_level";
      public static final String CHARGING = "charging";
      public static final String ONLINE = "online";
      public static final String ORIENTATION = "orientation";
      public static final String SIMULATOR = "simulator";
      public static final String MEMORY_SIZE = "memory_size";
      public static final String FREE_MEMORY = "free_memory";
      public static final String USABLE_MEMORY = "usable_memory";
      public static final String LOW_MEMORY = "low_memory";
      public static final String STORAGE_SIZE = "storage_size";
      public static final String FREE_STORAGE = "free_storage";
      public static final String EXTERNAL_STORAGE_SIZE = "external_storage_size";
      public static final String EXTERNAL_FREE_STORAGE = "external_free_storage";
      public static final String SCREEN_WIDTH_PIXELS = "screen_width_pixels";
      public static final String SCREEN_HEIGHT_PIXELS = "screen_height_pixels";
      public static final String SCREEN_DENSITY = "screen_density";
      public static final String SCREEN_DPI = "screen_dpi";
      public static final String BOOT_TIME = "boot_time";
      public static final String TIMEZONE = "timezone";
      public static final String ID = "id";
      public static final String CONNECTION_TYPE = "connection_type";
      public static final String BATTERY_TEMPERATURE = "battery_temperature";
      public static final String LOCALE = "locale";
      public static final String PROCESSOR_COUNT = "processor_count";
      public static final String CPU_DESCRIPTION = "cpu_description";
      public static final String PROCESSOR_FREQUENCY = "processor_frequency";
      public static final String CHIPSET = "chipset";
   }
}
