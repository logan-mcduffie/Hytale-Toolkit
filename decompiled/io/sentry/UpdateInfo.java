package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public final class UpdateInfo {
   @NotNull
   private final String id;
   @NotNull
   private final String buildVersion;
   private final int buildNumber;
   @NotNull
   private final String downloadUrl;
   @NotNull
   private final String appName;
   @Nullable
   private final String createdDate;

   public UpdateInfo(
      @NotNull String id, @NotNull String buildVersion, int buildNumber, @NotNull String downloadUrl, @NotNull String appName, @Nullable String createdDate
   ) {
      this.id = id;
      this.buildVersion = buildVersion;
      this.buildNumber = buildNumber;
      this.downloadUrl = downloadUrl;
      this.appName = appName;
      this.createdDate = createdDate;
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String getBuildVersion() {
      return this.buildVersion;
   }

   public int getBuildNumber() {
      return this.buildNumber;
   }

   @NotNull
   public String getDownloadUrl() {
      return this.downloadUrl;
   }

   @NotNull
   public String getAppName() {
      return this.appName;
   }

   @Nullable
   public String getCreatedDate() {
      return this.createdDate;
   }

   @Override
   public String toString() {
      return "UpdateInfo{id='"
         + this.id
         + '\''
         + ", buildVersion='"
         + this.buildVersion
         + '\''
         + ", buildNumber="
         + this.buildNumber
         + ", downloadUrl='"
         + this.downloadUrl
         + '\''
         + ", appName='"
         + this.appName
         + '\''
         + ", createdDate='"
         + this.createdDate
         + '\''
         + '}';
   }
}
