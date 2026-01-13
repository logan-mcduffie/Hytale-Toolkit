package io.sentry;

import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfilingTraceData implements JsonUnknown, JsonSerializable {
   private static final String DEFAULT_ENVIRONMENT = "production";
   @Internal
   public static final String TRUNCATION_REASON_NORMAL = "normal";
   @Internal
   public static final String TRUNCATION_REASON_TIMEOUT = "timeout";
   @Internal
   public static final String TRUNCATION_REASON_BACKGROUNDED = "backgrounded";
   @NotNull
   private final File traceFile;
   @NotNull
   private final Callable<List<Integer>> deviceCpuFrequenciesReader;
   private int androidApiLevel;
   @NotNull
   private String deviceLocale;
   @NotNull
   private String deviceManufacturer;
   @NotNull
   private String deviceModel;
   @NotNull
   private String deviceOsBuildNumber;
   @NotNull
   private String deviceOsName;
   @NotNull
   private String deviceOsVersion;
   private boolean deviceIsEmulator;
   @NotNull
   private String cpuArchitecture;
   @NotNull
   private List<Integer> deviceCpuFrequencies = new ArrayList<>();
   @NotNull
   private String devicePhysicalMemoryBytes;
   @NotNull
   private String platform;
   @NotNull
   private String buildId;
   @NotNull
   private List<ProfilingTransactionData> transactions;
   @NotNull
   private String transactionName;
   @NotNull
   private String durationNs;
   @NotNull
   private String versionCode;
   @NotNull
   private String release;
   @NotNull
   private String transactionId;
   @NotNull
   private String traceId;
   @NotNull
   private String profileId;
   @NotNull
   private String environment;
   @NotNull
   private String truncationReason;
   @NotNull
   private Date timestamp;
   @NotNull
   private final Map<String, ProfileMeasurement> measurementsMap;
   @Nullable
   private String sampledProfile = null;
   @Nullable
   private Map<String, Object> unknown;

   private ProfilingTraceData() {
      this(new File("dummy"), NoOpTransaction.getInstance());
   }

   public ProfilingTraceData(@NotNull File traceFile, @NotNull ITransaction transaction) {
      this(
         traceFile,
         DateUtils.getCurrentDateTime(),
         new ArrayList<>(),
         transaction.getName(),
         transaction.getEventId().toString(),
         transaction.getSpanContext().getTraceId().toString(),
         "0",
         0,
         "",
         () -> new ArrayList<>(),
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         "normal",
         new HashMap<>()
      );
   }

   public ProfilingTraceData(
      @NotNull File traceFile,
      @NotNull Date profileStartTimestamp,
      @NotNull List<ProfilingTransactionData> transactions,
      @NotNull String transactionName,
      @NotNull String transactionId,
      @NotNull String traceId,
      @NotNull String durationNanos,
      int sdkInt,
      @NotNull String cpuArchitecture,
      @NotNull Callable<List<Integer>> deviceCpuFrequenciesReader,
      @Nullable String deviceManufacturer,
      @Nullable String deviceModel,
      @Nullable String deviceOsVersion,
      @Nullable Boolean deviceIsEmulator,
      @Nullable String devicePhysicalMemoryBytes,
      @Nullable String buildId,
      @Nullable String release,
      @Nullable String environment,
      @NotNull String truncationReason,
      @NotNull Map<String, ProfileMeasurement> measurementsMap
   ) {
      this.traceFile = traceFile;
      this.timestamp = profileStartTimestamp;
      this.cpuArchitecture = cpuArchitecture;
      this.deviceCpuFrequenciesReader = deviceCpuFrequenciesReader;
      this.androidApiLevel = sdkInt;
      this.deviceLocale = Locale.getDefault().toString();
      this.deviceManufacturer = deviceManufacturer != null ? deviceManufacturer : "";
      this.deviceModel = deviceModel != null ? deviceModel : "";
      this.deviceOsVersion = deviceOsVersion != null ? deviceOsVersion : "";
      this.deviceIsEmulator = deviceIsEmulator != null ? deviceIsEmulator : false;
      this.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes != null ? devicePhysicalMemoryBytes : "0";
      this.deviceOsBuildNumber = "";
      this.deviceOsName = "android";
      this.platform = "android";
      this.buildId = buildId != null ? buildId : "";
      this.transactions = transactions;
      this.transactionName = transactionName.isEmpty() ? "unknown" : transactionName;
      this.durationNs = durationNanos;
      this.versionCode = "";
      this.release = release != null ? release : "";
      this.transactionId = transactionId;
      this.traceId = traceId;
      this.profileId = SentryUUID.generateSentryId();
      this.environment = environment != null ? environment : "production";
      this.truncationReason = truncationReason;
      if (!this.isTruncationReasonValid()) {
         this.truncationReason = "normal";
      }

      this.measurementsMap = measurementsMap;
   }

   private boolean isTruncationReasonValid() {
      return this.truncationReason.equals("normal") || this.truncationReason.equals("timeout") || this.truncationReason.equals("backgrounded");
   }

   @NotNull
   public File getTraceFile() {
      return this.traceFile;
   }

   public int getAndroidApiLevel() {
      return this.androidApiLevel;
   }

   @NotNull
   public String getCpuArchitecture() {
      return this.cpuArchitecture;
   }

   @NotNull
   public String getDeviceLocale() {
      return this.deviceLocale;
   }

   @NotNull
   public String getDeviceManufacturer() {
      return this.deviceManufacturer;
   }

   @NotNull
   public String getDeviceModel() {
      return this.deviceModel;
   }

   @NotNull
   public String getDeviceOsBuildNumber() {
      return this.deviceOsBuildNumber;
   }

   @NotNull
   public String getDeviceOsName() {
      return this.deviceOsName;
   }

   @NotNull
   public String getDeviceOsVersion() {
      return this.deviceOsVersion;
   }

   public boolean isDeviceIsEmulator() {
      return this.deviceIsEmulator;
   }

   @NotNull
   public String getPlatform() {
      return this.platform;
   }

   @NotNull
   public String getBuildId() {
      return this.buildId;
   }

   @NotNull
   public String getTransactionName() {
      return this.transactionName;
   }

   @NotNull
   public String getRelease() {
      return this.release;
   }

   @NotNull
   public String getTransactionId() {
      return this.transactionId;
   }

   @NotNull
   public List<ProfilingTransactionData> getTransactions() {
      return this.transactions;
   }

   @NotNull
   public String getTraceId() {
      return this.traceId;
   }

   @NotNull
   public String getProfileId() {
      return this.profileId;
   }

   @NotNull
   public String getEnvironment() {
      return this.environment;
   }

   @Nullable
   public String getSampledProfile() {
      return this.sampledProfile;
   }

   @NotNull
   public String getDurationNs() {
      return this.durationNs;
   }

   @NotNull
   public List<Integer> getDeviceCpuFrequencies() {
      return this.deviceCpuFrequencies;
   }

   @NotNull
   public String getDevicePhysicalMemoryBytes() {
      return this.devicePhysicalMemoryBytes;
   }

   @NotNull
   public String getTruncationReason() {
      return this.truncationReason;
   }

   @NotNull
   public Date getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public Map<String, ProfileMeasurement> getMeasurementsMap() {
      return this.measurementsMap;
   }

   public void setAndroidApiLevel(int androidApiLevel) {
      this.androidApiLevel = androidApiLevel;
   }

   public void setCpuArchitecture(@NotNull String cpuArchitecture) {
      this.cpuArchitecture = cpuArchitecture;
   }

   public void setDeviceLocale(@NotNull String deviceLocale) {
      this.deviceLocale = deviceLocale;
   }

   public void setDeviceManufacturer(@NotNull String deviceManufacturer) {
      this.deviceManufacturer = deviceManufacturer;
   }

   public void setDeviceModel(@NotNull String deviceModel) {
      this.deviceModel = deviceModel;
   }

   public void setDeviceOsBuildNumber(@NotNull String deviceOsBuildNumber) {
      this.deviceOsBuildNumber = deviceOsBuildNumber;
   }

   public void setDeviceOsVersion(@NotNull String deviceOsVersion) {
      this.deviceOsVersion = deviceOsVersion;
   }

   public void setDeviceIsEmulator(boolean deviceIsEmulator) {
      this.deviceIsEmulator = deviceIsEmulator;
   }

   public void setDeviceCpuFrequencies(@NotNull List<Integer> deviceCpuFrequencies) {
      this.deviceCpuFrequencies = deviceCpuFrequencies;
   }

   public void setDevicePhysicalMemoryBytes(@NotNull String devicePhysicalMemoryBytes) {
      this.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes;
   }

   public void setTimestamp(@NotNull Date timestamp) {
      this.timestamp = timestamp;
   }

   public void setTruncationReason(@NotNull String truncationReason) {
      this.truncationReason = truncationReason;
   }

   public void setTransactions(@NotNull List<ProfilingTransactionData> transactions) {
      this.transactions = transactions;
   }

   public void setBuildId(@NotNull String buildId) {
      this.buildId = buildId;
   }

   public void setTransactionName(@NotNull String transactionName) {
      this.transactionName = transactionName;
   }

   public void setDurationNs(@NotNull String durationNs) {
      this.durationNs = durationNs;
   }

   public void setRelease(@NotNull String release) {
      this.release = release;
   }

   public void setTransactionId(@NotNull String transactionId) {
      this.transactionId = transactionId;
   }

   public void setTraceId(@NotNull String traceId) {
      this.traceId = traceId;
   }

   public void setProfileId(@NotNull String profileId) {
      this.profileId = profileId;
   }

   public void setEnvironment(@NotNull String environment) {
      this.environment = environment;
   }

   public void setSampledProfile(@Nullable String sampledProfile) {
      this.sampledProfile = sampledProfile;
   }

   public void readDeviceCpuFrequencies() {
      try {
         this.deviceCpuFrequencies = this.deviceCpuFrequenciesReader.call();
      } catch (Throwable var2) {
      }
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("android_api_level").value(logger, this.androidApiLevel);
      writer.name("device_locale").value(logger, this.deviceLocale);
      writer.name("device_manufacturer").value(this.deviceManufacturer);
      writer.name("device_model").value(this.deviceModel);
      writer.name("device_os_build_number").value(this.deviceOsBuildNumber);
      writer.name("device_os_name").value(this.deviceOsName);
      writer.name("device_os_version").value(this.deviceOsVersion);
      writer.name("device_is_emulator").value(this.deviceIsEmulator);
      writer.name("architecture").value(logger, this.cpuArchitecture);
      writer.name("device_cpu_frequencies").value(logger, this.deviceCpuFrequencies);
      writer.name("device_physical_memory_bytes").value(this.devicePhysicalMemoryBytes);
      writer.name("platform").value(this.platform);
      writer.name("build_id").value(this.buildId);
      writer.name("transaction_name").value(this.transactionName);
      writer.name("duration_ns").value(this.durationNs);
      writer.name("version_name").value(this.release);
      writer.name("version_code").value(this.versionCode);
      if (!this.transactions.isEmpty()) {
         writer.name("transactions").value(logger, this.transactions);
      }

      writer.name("transaction_id").value(this.transactionId);
      writer.name("trace_id").value(this.traceId);
      writer.name("profile_id").value(this.profileId);
      writer.name("environment").value(this.environment);
      writer.name("truncation_reason").value(this.truncationReason);
      if (this.sampledProfile != null) {
         writer.name("sampled_profile").value(this.sampledProfile);
      }

      String prevIndent = writer.getIndent();
      writer.setIndent("");
      writer.name("measurements").value(logger, this.measurementsMap);
      writer.setIndent(prevIndent);
      writer.name("timestamp").value(logger, this.timestamp);
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

   public static final class Deserializer implements JsonDeserializer<ProfilingTraceData> {
      @NotNull
      public ProfilingTraceData deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfilingTraceData data = new ProfilingTraceData();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "android_api_level":
                  Integer apiLevel = reader.nextIntegerOrNull();
                  if (apiLevel != null) {
                     data.androidApiLevel = apiLevel;
                  }
                  break;
               case "device_locale":
                  String deviceLocale = reader.nextStringOrNull();
                  if (deviceLocale != null) {
                     data.deviceLocale = deviceLocale;
                  }
                  break;
               case "device_manufacturer":
                  String deviceManufacturer = reader.nextStringOrNull();
                  if (deviceManufacturer != null) {
                     data.deviceManufacturer = deviceManufacturer;
                  }
                  break;
               case "device_model":
                  String deviceModel = reader.nextStringOrNull();
                  if (deviceModel != null) {
                     data.deviceModel = deviceModel;
                  }
                  break;
               case "device_os_build_number":
                  String deviceOsBuildNumber = reader.nextStringOrNull();
                  if (deviceOsBuildNumber != null) {
                     data.deviceOsBuildNumber = deviceOsBuildNumber;
                  }
                  break;
               case "device_os_name":
                  String deviceOsName = reader.nextStringOrNull();
                  if (deviceOsName != null) {
                     data.deviceOsName = deviceOsName;
                  }
                  break;
               case "device_os_version":
                  String deviceOsVersion = reader.nextStringOrNull();
                  if (deviceOsVersion != null) {
                     data.deviceOsVersion = deviceOsVersion;
                  }
                  break;
               case "device_is_emulator":
                  Boolean deviceIsEmulator = reader.nextBooleanOrNull();
                  if (deviceIsEmulator != null) {
                     data.deviceIsEmulator = deviceIsEmulator;
                  }
                  break;
               case "architecture":
                  String cpuArchitecture = reader.nextStringOrNull();
                  if (cpuArchitecture != null) {
                     data.cpuArchitecture = cpuArchitecture;
                  }
                  break;
               case "device_cpu_frequencies":
                  List<Integer> deviceCpuFrequencies = (List<Integer>)reader.nextObjectOrNull();
                  if (deviceCpuFrequencies != null) {
                     data.deviceCpuFrequencies = deviceCpuFrequencies;
                  }
                  break;
               case "device_physical_memory_bytes":
                  String devicePhysicalMemoryBytes = reader.nextStringOrNull();
                  if (devicePhysicalMemoryBytes != null) {
                     data.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes;
                  }
                  break;
               case "platform":
                  String platform = reader.nextStringOrNull();
                  if (platform != null) {
                     data.platform = platform;
                  }
                  break;
               case "build_id":
                  String buildId = reader.nextStringOrNull();
                  if (buildId != null) {
                     data.buildId = buildId;
                  }
                  break;
               case "transaction_name":
                  String transactionName = reader.nextStringOrNull();
                  if (transactionName != null) {
                     data.transactionName = transactionName;
                  }
                  break;
               case "duration_ns":
                  String durationNs = reader.nextStringOrNull();
                  if (durationNs != null) {
                     data.durationNs = durationNs;
                  }
                  break;
               case "version_code":
                  String versionCode = reader.nextStringOrNull();
                  if (versionCode != null) {
                     data.versionCode = versionCode;
                  }
                  break;
               case "version_name":
                  String versionName = reader.nextStringOrNull();
                  if (versionName != null) {
                     data.release = versionName;
                  }
                  break;
               case "transactions":
                  List<ProfilingTransactionData> transactions = reader.nextListOrNull(logger, new ProfilingTransactionData.Deserializer());
                  if (transactions != null) {
                     data.transactions.addAll(transactions);
                  }
                  break;
               case "transaction_id":
                  String transactionId = reader.nextStringOrNull();
                  if (transactionId != null) {
                     data.transactionId = transactionId;
                  }
                  break;
               case "trace_id":
                  String traceId = reader.nextStringOrNull();
                  if (traceId != null) {
                     data.traceId = traceId;
                  }
                  break;
               case "profile_id":
                  String profileId = reader.nextStringOrNull();
                  if (profileId != null) {
                     data.profileId = profileId;
                  }
                  break;
               case "environment":
                  String environment = reader.nextStringOrNull();
                  if (environment != null) {
                     data.environment = environment;
                  }
                  break;
               case "truncation_reason":
                  String truncationReason = reader.nextStringOrNull();
                  if (truncationReason != null) {
                     data.truncationReason = truncationReason;
                  }
                  break;
               case "measurements":
                  Map<String, ProfileMeasurement> measurements = reader.nextMapOrNull(logger, new ProfileMeasurement.Deserializer());
                  if (measurements != null) {
                     data.measurementsMap.putAll(measurements);
                  }
                  break;
               case "timestamp":
                  Date timestamp = reader.nextDateOrNull(logger);
                  if (timestamp != null) {
                     data.timestamp = timestamp;
                  }
                  break;
               case "sampled_profile":
                  String sampledProfile = reader.nextStringOrNull();
                  if (sampledProfile != null) {
                     data.sampledProfile = sampledProfile;
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
      public static final String ANDROID_API_LEVEL = "android_api_level";
      public static final String DEVICE_LOCALE = "device_locale";
      public static final String DEVICE_MANUFACTURER = "device_manufacturer";
      public static final String DEVICE_MODEL = "device_model";
      public static final String DEVICE_OS_BUILD_NUMBER = "device_os_build_number";
      public static final String DEVICE_OS_NAME = "device_os_name";
      public static final String DEVICE_OS_VERSION = "device_os_version";
      public static final String DEVICE_IS_EMULATOR = "device_is_emulator";
      public static final String ARCHITECTURE = "architecture";
      public static final String DEVICE_CPU_FREQUENCIES = "device_cpu_frequencies";
      public static final String DEVICE_PHYSICAL_MEMORY_BYTES = "device_physical_memory_bytes";
      public static final String PLATFORM = "platform";
      public static final String BUILD_ID = "build_id";
      public static final String TRANSACTION_NAME = "transaction_name";
      public static final String DURATION_NS = "duration_ns";
      public static final String RELEASE = "version_name";
      public static final String VERSION_CODE = "version_code";
      public static final String TRANSACTION_LIST = "transactions";
      public static final String TRANSACTION_ID = "transaction_id";
      public static final String TRACE_ID = "trace_id";
      public static final String PROFILE_ID = "profile_id";
      public static final String ENVIRONMENT = "environment";
      public static final String SAMPLED_PROFILE = "sampled_profile";
      public static final String TRUNCATION_REASON = "truncation_reason";
      public static final String MEASUREMENTS = "measurements";
      public static final String TIMESTAMP = "timestamp";
   }
}
