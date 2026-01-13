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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class App implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "app";
   @Nullable
   private String appIdentifier;
   @Nullable
   private Date appStartTime;
   @Nullable
   private String deviceAppHash;
   @Nullable
   private String buildType;
   @Nullable
   private String appName;
   @Nullable
   private String appVersion;
   @Nullable
   private String appBuild;
   @Nullable
   private Map<String, String> permissions;
   @Nullable
   private List<String> viewNames;
   @Nullable
   private String startType;
   @Nullable
   private Boolean inForeground;
   @Nullable
   private Boolean isSplitApks;
   @Nullable
   private List<String> splitNames;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public App() {
   }

   App(@NotNull App app) {
      this.appBuild = app.appBuild;
      this.appIdentifier = app.appIdentifier;
      this.appName = app.appName;
      this.appStartTime = app.appStartTime;
      this.appVersion = app.appVersion;
      this.buildType = app.buildType;
      this.deviceAppHash = app.deviceAppHash;
      this.permissions = CollectionUtils.newConcurrentHashMap(app.permissions);
      this.inForeground = app.inForeground;
      this.viewNames = CollectionUtils.newArrayList(app.viewNames);
      this.startType = app.startType;
      this.isSplitApks = app.isSplitApks;
      this.splitNames = app.splitNames;
      this.unknown = CollectionUtils.newConcurrentHashMap(app.unknown);
   }

   @Nullable
   public String getAppIdentifier() {
      return this.appIdentifier;
   }

   public void setAppIdentifier(@Nullable String appIdentifier) {
      this.appIdentifier = appIdentifier;
   }

   @Nullable
   public Date getAppStartTime() {
      Date appStartTimeRef = this.appStartTime;
      return appStartTimeRef != null ? (Date)appStartTimeRef.clone() : null;
   }

   public void setAppStartTime(@Nullable Date appStartTime) {
      this.appStartTime = appStartTime;
   }

   @Nullable
   public String getDeviceAppHash() {
      return this.deviceAppHash;
   }

   public void setDeviceAppHash(@Nullable String deviceAppHash) {
      this.deviceAppHash = deviceAppHash;
   }

   @Nullable
   public String getBuildType() {
      return this.buildType;
   }

   public void setBuildType(@Nullable String buildType) {
      this.buildType = buildType;
   }

   @Nullable
   public String getAppName() {
      return this.appName;
   }

   public void setAppName(@Nullable String appName) {
      this.appName = appName;
   }

   @Nullable
   public String getAppVersion() {
      return this.appVersion;
   }

   public void setAppVersion(@Nullable String appVersion) {
      this.appVersion = appVersion;
   }

   @Nullable
   public String getAppBuild() {
      return this.appBuild;
   }

   public void setAppBuild(@Nullable String appBuild) {
      this.appBuild = appBuild;
   }

   @Nullable
   public Map<String, String> getPermissions() {
      return this.permissions;
   }

   public void setPermissions(@Nullable Map<String, String> permissions) {
      this.permissions = permissions;
   }

   @Nullable
   public Boolean getInForeground() {
      return this.inForeground;
   }

   public void setInForeground(@Nullable Boolean inForeground) {
      this.inForeground = inForeground;
   }

   @Nullable
   public List<String> getViewNames() {
      return this.viewNames;
   }

   public void setViewNames(@Nullable List<String> viewNames) {
      this.viewNames = viewNames;
   }

   @Nullable
   public String getStartType() {
      return this.startType;
   }

   public void setStartType(@Nullable String startType) {
      this.startType = startType;
   }

   @Nullable
   public Boolean getSplitApks() {
      return this.isSplitApks;
   }

   public void setSplitApks(@Nullable Boolean splitApks) {
      this.isSplitApks = splitApks;
   }

   @Nullable
   public List<String> getSplitNames() {
      return this.splitNames;
   }

   public void setSplitNames(@Nullable List<String> splitNames) {
      this.splitNames = splitNames;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         App app = (App)o;
         return Objects.equals(this.appIdentifier, app.appIdentifier)
            && Objects.equals(this.appStartTime, app.appStartTime)
            && Objects.equals(this.deviceAppHash, app.deviceAppHash)
            && Objects.equals(this.buildType, app.buildType)
            && Objects.equals(this.appName, app.appName)
            && Objects.equals(this.appVersion, app.appVersion)
            && Objects.equals(this.appBuild, app.appBuild)
            && Objects.equals(this.permissions, app.permissions)
            && Objects.equals(this.inForeground, app.inForeground)
            && Objects.equals(this.viewNames, app.viewNames)
            && Objects.equals(this.startType, app.startType)
            && Objects.equals(this.isSplitApks, app.isSplitApks)
            && Objects.equals(this.splitNames, app.splitNames);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.appIdentifier,
         this.appStartTime,
         this.deviceAppHash,
         this.buildType,
         this.appName,
         this.appVersion,
         this.appBuild,
         this.permissions,
         this.inForeground,
         this.viewNames,
         this.startType,
         this.isSplitApks,
         this.splitNames
      );
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
      if (this.appIdentifier != null) {
         writer.name("app_identifier").value(this.appIdentifier);
      }

      if (this.appStartTime != null) {
         writer.name("app_start_time").value(logger, this.appStartTime);
      }

      if (this.deviceAppHash != null) {
         writer.name("device_app_hash").value(this.deviceAppHash);
      }

      if (this.buildType != null) {
         writer.name("build_type").value(this.buildType);
      }

      if (this.appName != null) {
         writer.name("app_name").value(this.appName);
      }

      if (this.appVersion != null) {
         writer.name("app_version").value(this.appVersion);
      }

      if (this.appBuild != null) {
         writer.name("app_build").value(this.appBuild);
      }

      if (this.permissions != null && !this.permissions.isEmpty()) {
         writer.name("permissions").value(logger, this.permissions);
      }

      if (this.inForeground != null) {
         writer.name("in_foreground").value(this.inForeground);
      }

      if (this.viewNames != null) {
         writer.name("view_names").value(logger, this.viewNames);
      }

      if (this.startType != null) {
         writer.name("start_type").value(this.startType);
      }

      if (this.isSplitApks != null) {
         writer.name("is_split_apks").value(this.isSplitApks);
      }

      if (this.splitNames != null && !this.splitNames.isEmpty()) {
         writer.name("split_names").value(logger, this.splitNames);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<App> {
      @NotNull
      public App deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         App app = new App();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "app_identifier":
                  app.appIdentifier = reader.nextStringOrNull();
                  break;
               case "app_start_time":
                  app.appStartTime = reader.nextDateOrNull(logger);
                  break;
               case "device_app_hash":
                  app.deviceAppHash = reader.nextStringOrNull();
                  break;
               case "build_type":
                  app.buildType = reader.nextStringOrNull();
                  break;
               case "app_name":
                  app.appName = reader.nextStringOrNull();
                  break;
               case "app_version":
                  app.appVersion = reader.nextStringOrNull();
                  break;
               case "app_build":
                  app.appBuild = reader.nextStringOrNull();
                  break;
               case "permissions":
                  app.permissions = CollectionUtils.newConcurrentHashMap((Map<String, String>)reader.nextObjectOrNull());
                  break;
               case "in_foreground":
                  app.inForeground = reader.nextBooleanOrNull();
                  break;
               case "view_names":
                  List<String> viewNames = (List<String>)reader.nextObjectOrNull();
                  if (viewNames != null) {
                     app.setViewNames(viewNames);
                  }
                  break;
               case "start_type":
                  app.startType = reader.nextStringOrNull();
                  break;
               case "is_split_apks":
                  app.isSplitApks = reader.nextBooleanOrNull();
                  break;
               case "split_names":
                  List<String> splitNames = (List<String>)reader.nextObjectOrNull();
                  if (splitNames != null) {
                     app.setSplitNames(splitNames);
                  }
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         app.setUnknown(unknown);
         reader.endObject();
         return app;
      }
   }

   public static final class JsonKeys {
      public static final String APP_IDENTIFIER = "app_identifier";
      public static final String APP_START_TIME = "app_start_time";
      public static final String DEVICE_APP_HASH = "device_app_hash";
      public static final String BUILD_TYPE = "build_type";
      public static final String APP_NAME = "app_name";
      public static final String APP_VERSION = "app_version";
      public static final String APP_BUILD = "app_build";
      public static final String APP_PERMISSIONS = "permissions";
      public static final String IN_FOREGROUND = "in_foreground";
      public static final String VIEW_NAMES = "view_names";
      public static final String START_TYPE = "start_type";
      public static final String IS_SPLIT_APKS = "is_split_apks";
      public static final String SPLIT_NAMES = "split_names";
   }
}
