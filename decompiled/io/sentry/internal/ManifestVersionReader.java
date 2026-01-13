package io.sentry.internal;

import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ManifestVersionReader {
   @Nullable
   private static volatile ManifestVersionReader INSTANCE;
   @NotNull
   private static final AutoClosableReentrantLock staticLock = new AutoClosableReentrantLock();
   private volatile boolean hasManifestBeenRead = false;
   @NotNull
   private final ManifestVersionReader.VersionInfoHolder versionInfo = new ManifestVersionReader.VersionInfoHolder();
   @NotNull
   private AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

   @NotNull
   public static ManifestVersionReader getInstance() {
      if (INSTANCE == null) {
         ISentryLifecycleToken ignored = staticLock.acquire();

         try {
            if (INSTANCE == null) {
               INSTANCE = new ManifestVersionReader();
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

   private ManifestVersionReader() {
   }

   @Nullable
   public ManifestVersionReader.VersionInfoHolder readOpenTelemetryVersion() {
      this.readManifestFiles();
      return this.versionInfo.sdkVersion == null ? null : this.versionInfo;
   }

   public void readManifestFiles() {
      if (!this.hasManifestBeenRead) {
         try {
            ISentryLifecycleToken ignored;
            label170: {
               ignored = this.lock.acquire();

               try {
                  if (this.hasManifestBeenRead) {
                     break label170;
                  }

                  Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("META-INF/MANIFEST.MF");

                  while (resources.hasMoreElements()) {
                     try {
                        Manifest manifest = new Manifest(resources.nextElement().openStream());
                        Attributes mainAttributes = manifest.getMainAttributes();
                        if (mainAttributes != null) {
                           String name = mainAttributes.getValue("Sentry-Opentelemetry-SDK-Name");
                           String version = mainAttributes.getValue("Implementation-Version");
                           String sdkName = mainAttributes.getValue("Sentry-SDK-Name");
                           String packageName = mainAttributes.getValue("Sentry-SDK-Package-Name");
                           if (name != null && version != null) {
                              this.versionInfo.sdkName = name;
                              this.versionInfo.sdkVersion = version;
                              String otelVersion = mainAttributes.getValue("Sentry-Opentelemetry-Version-Name");
                              if (otelVersion != null) {
                                 SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.opentelemetry:opentelemetry-sdk", otelVersion);
                                 SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry");
                              }

                              String otelJavaagentVersion = mainAttributes.getValue("Sentry-Opentelemetry-Javaagent-Version-Name");
                              if (otelJavaagentVersion != null) {
                                 SentryIntegrationPackageStorage.getInstance()
                                    .addPackage("maven:io.opentelemetry.javaagent:opentelemetry-javaagent", otelJavaagentVersion);
                                 SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agent");
                              }

                              if (name.equals("sentry.java.opentelemetry.agentless")) {
                                 SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agentless");
                              }

                              if (name.equals("sentry.java.opentelemetry.agentless-spring")) {
                                 SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agentless-Spring");
                              }
                           }

                           if (sdkName != null && version != null && packageName != null && sdkName.startsWith("sentry.java")) {
                              SentryIntegrationPackageStorage.getInstance().addPackage(packageName, version);
                           }
                        }
                     } catch (Exception var18) {
                     }
                  }
               } catch (Throwable var19) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var17) {
                        var19.addSuppressed(var17);
                     }
                  }

                  throw var19;
               }

               if (ignored != null) {
                  ignored.close();
               }

               return;
            }

            if (ignored != null) {
               ignored.close();
            }
         } catch (IOException var20) {
            return;
         } finally {
            this.hasManifestBeenRead = true;
         }
      }
   }

   public static final class VersionInfoHolder {
      @Nullable
      private volatile String sdkName;
      @Nullable
      private volatile String sdkVersion;

      @Nullable
      public String getSdkName() {
         return this.sdkName;
      }

      @Nullable
      public String getSdkVersion() {
         return this.sdkVersion;
      }
   }
}
