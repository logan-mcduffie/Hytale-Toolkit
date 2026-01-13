package io.sentry;

import io.sentry.protocol.SentryPackage;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryIntegrationPackageStorage {
   @Nullable
   private static volatile SentryIntegrationPackageStorage INSTANCE;
   @NotNull
   private static final AutoClosableReentrantLock staticLock = new AutoClosableReentrantLock();
   @Nullable
   private static volatile Boolean mixedVersionsDetected = null;
   @NotNull
   private static final AutoClosableReentrantLock mixedVersionsLock = new AutoClosableReentrantLock();
   private final Set<String> integrations = new CopyOnWriteArraySet<>();
   private final Set<SentryPackage> packages = new CopyOnWriteArraySet<>();

   @NotNull
   public static SentryIntegrationPackageStorage getInstance() {
      if (INSTANCE == null) {
         ISentryLifecycleToken ignored = staticLock.acquire();

         try {
            if (INSTANCE == null) {
               INSTANCE = new SentryIntegrationPackageStorage();
            }
         } catch (Throwable var4) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var3) {
                  var4.addSuppressed(var3);
               }
            }

            throw var4;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      return INSTANCE;
   }

   private SentryIntegrationPackageStorage() {
   }

   public void addIntegration(@NotNull String integration) {
      Objects.requireNonNull(integration, "integration is required.");
      this.integrations.add(integration);
   }

   @NotNull
   public Set<String> getIntegrations() {
      return this.integrations;
   }

   public void addPackage(@NotNull String name, @NotNull String version) {
      Objects.requireNonNull(name, "name is required.");
      Objects.requireNonNull(version, "version is required.");
      SentryPackage newPackage = new SentryPackage(name, version);
      this.packages.add(newPackage);
      ISentryLifecycleToken ignored = mixedVersionsLock.acquire();

      try {
         mixedVersionsDetected = null;
      } catch (Throwable var8) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @NotNull
   public Set<SentryPackage> getPackages() {
      return this.packages;
   }

   public boolean checkForMixedVersions(@NotNull ILogger logger) {
      Boolean mixedVersionsDetectedBefore = mixedVersionsDetected;
      if (mixedVersionsDetectedBefore != null) {
         return mixedVersionsDetectedBefore;
      } else {
         ISentryLifecycleToken ignored = mixedVersionsLock.acquire();

         boolean var10;
         try {
            String sdkVersion = "8.29.0";
            boolean mixedVersionsDetectedThisCheck = false;

            for (SentryPackage pkg : this.packages) {
               if (pkg.getName().startsWith("maven:io.sentry:") && !"8.29.0".equalsIgnoreCase(pkg.getVersion())) {
                  logger.log(
                     SentryLevel.ERROR,
                     "The Sentry SDK has been configured with mixed versions. Expected %s to match core SDK version %s but was %s",
                     pkg.getName(),
                     "8.29.0",
                     pkg.getVersion()
                  );
                  mixedVersionsDetectedThisCheck = true;
               }
            }

            if (mixedVersionsDetectedThisCheck) {
               logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
               logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
               logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
               logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }

            mixedVersionsDetected = mixedVersionsDetectedThisCheck;
            var10 = mixedVersionsDetectedThisCheck;
         } catch (Throwable var9) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var10;
      }
   }

   @TestOnly
   public void clearStorage() {
      this.integrations.clear();
      this.packages.clear();
   }
}
