package io.sentry.util;

import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugMetaPropertiesApplier {
   @NotNull
   public static String DEBUG_META_PROPERTIES_FILENAME = "sentry-debug-meta.properties";

   public static void apply(@NotNull SentryOptions options, @Nullable List<Properties> debugMetaProperties) {
      if (debugMetaProperties != null) {
         applyToOptions(options, debugMetaProperties);
         applyBuildTool(options, debugMetaProperties);
         applyDistributionOptions(options, debugMetaProperties);
      }
   }

   public static void applyToOptions(@NotNull SentryOptions options, @Nullable List<Properties> debugMetaProperties) {
      if (debugMetaProperties != null) {
         applyBundleIds(options, debugMetaProperties);
         applyProguardUuid(options, debugMetaProperties);
      }
   }

   private static void applyBundleIds(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
      if (options.getBundleIds().isEmpty()) {
         for (Properties properties : debugMetaProperties) {
            String bundleIdStrings = properties.getProperty("io.sentry.bundle-ids");
            options.getLogger().log(SentryLevel.DEBUG, "Bundle IDs found: %s", bundleIdStrings);
            if (bundleIdStrings != null) {
               String[] bundleIds = bundleIdStrings.split(",", -1);

               for (String bundleId : bundleIds) {
                  options.addBundleId(bundleId);
               }
            }
         }
      }
   }

   private static void applyProguardUuid(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
      if (options.getProguardUuid() == null) {
         for (Properties properties : debugMetaProperties) {
            String proguardUuid = getProguardUuid(properties);
            if (proguardUuid != null) {
               options.getLogger().log(SentryLevel.DEBUG, "Proguard UUID found: %s", proguardUuid);
               options.setProguardUuid(proguardUuid);
               break;
            }
         }
      }
   }

   private static void applyBuildTool(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
      for (Properties properties : debugMetaProperties) {
         String buildTool = getBuildTool(properties);
         if (buildTool != null) {
            String buildToolVersion = getBuildToolVersion(properties);
            if (buildToolVersion == null) {
               buildToolVersion = "unknown";
            }

            options.getLogger().log(SentryLevel.DEBUG, "Build tool found: %s, version %s", buildTool, buildToolVersion);
            SentryIntegrationPackageStorage.getInstance().addPackage(buildTool, buildToolVersion);
            break;
         }
      }
   }

   @Nullable
   public static String getProguardUuid(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.ProguardUuids");
   }

   @Nullable
   public static String getBuildTool(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.build-tool");
   }

   @Nullable
   public static String getBuildToolVersion(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.build-tool-version");
   }

   private static void applyDistributionOptions(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
      for (Properties properties : debugMetaProperties) {
         String orgSlug = getDistributionOrgSlug(properties);
         String projectSlug = getDistributionProjectSlug(properties);
         String orgAuthToken = getDistributionAuthToken(properties);
         String buildConfiguration = getDistributionBuildConfiguration(properties);
         if (orgSlug != null || projectSlug != null || orgAuthToken != null || buildConfiguration != null) {
            SentryOptions.DistributionOptions distributionOptions = options.getDistribution();
            if (orgSlug != null && !orgSlug.isEmpty() && distributionOptions.orgSlug.isEmpty()) {
               options.getLogger().log(SentryLevel.DEBUG, "Distribution org slug found: %s", orgSlug);
               distributionOptions.orgSlug = orgSlug;
            }

            if (projectSlug != null && !projectSlug.isEmpty() && distributionOptions.projectSlug.isEmpty()) {
               options.getLogger().log(SentryLevel.DEBUG, "Distribution project slug found: %s", projectSlug);
               distributionOptions.projectSlug = projectSlug;
            }

            if (orgAuthToken != null && !orgAuthToken.isEmpty() && distributionOptions.orgAuthToken.isEmpty()) {
               options.getLogger().log(SentryLevel.DEBUG, "Distribution org auth token found");
               distributionOptions.orgAuthToken = orgAuthToken;
            }

            if (buildConfiguration != null && !buildConfiguration.isEmpty() && distributionOptions.buildConfiguration == null) {
               options.getLogger().log(SentryLevel.DEBUG, "Distribution build configuration found: %s", buildConfiguration);
               distributionOptions.buildConfiguration = buildConfiguration;
            }
            break;
         }
      }
   }

   @Nullable
   private static String getDistributionOrgSlug(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.distribution.org-slug");
   }

   @Nullable
   private static String getDistributionProjectSlug(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.distribution.project-slug");
   }

   @Nullable
   private static String getDistributionAuthToken(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.distribution.auth-token");
   }

   @Nullable
   private static String getDistributionBuildConfiguration(@NotNull Properties debugMetaProperties) {
      return debugMetaProperties.getProperty("io.sentry.distribution.build-configuration");
   }
}
