package io.sentry;

import io.sentry.config.PropertiesProvider;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public final class ExternalOptions {
   private static final String PROXY_PORT_DEFAULT = "80";
   @Nullable
   private String dsn;
   @Nullable
   private String environment;
   @Nullable
   private String release;
   @Nullable
   private String dist;
   @Nullable
   private String serverName;
   @Nullable
   private Boolean enableUncaughtExceptionHandler;
   @Nullable
   private Boolean debug;
   @Nullable
   private Boolean enableDeduplication;
   @Nullable
   private Double tracesSampleRate;
   @Nullable
   private Double profilesSampleRate;
   @Nullable
   private SentryOptions.RequestSize maxRequestBodySize;
   @NotNull
   private final Map<String, String> tags = new ConcurrentHashMap<>();
   @Nullable
   private SentryOptions.Proxy proxy;
   @NotNull
   private final List<String> inAppExcludes = new CopyOnWriteArrayList<>();
   @NotNull
   private final List<String> inAppIncludes = new CopyOnWriteArrayList<>();
   @Nullable
   private List<String> tracePropagationTargets = null;
   @NotNull
   private final List<String> contextTags = new CopyOnWriteArrayList<>();
   @Nullable
   private String proguardUuid;
   @Nullable
   private Long idleTimeout;
   @NotNull
   private final Set<Class<? extends Throwable>> ignoredExceptionsForType = new CopyOnWriteArraySet<>();
   @Nullable
   private List<String> ignoredErrors;
   @Nullable
   private Boolean printUncaughtStackTrace;
   @Nullable
   private Boolean sendClientReports;
   @NotNull
   private Set<String> bundleIds = new CopyOnWriteArraySet<>();
   @Nullable
   private Boolean enabled;
   @Nullable
   private Boolean enablePrettySerializationOutput;
   @Nullable
   private Boolean enableSpotlight;
   @Nullable
   private Boolean enableLogs;
   @Nullable
   private String spotlightConnectionUrl;
   @Nullable
   private List<String> ignoredCheckIns;
   @Nullable
   private List<String> ignoredTransactions;
   @Nullable
   private Boolean sendModules;
   @Nullable
   private Boolean sendDefaultPii;
   @Nullable
   private Boolean enableBackpressureHandling;
   @Nullable
   private Boolean globalHubMode;
   @Nullable
   private Boolean forceInit;
   @Nullable
   private Boolean captureOpenTelemetryEvents;
   @Nullable
   private Double profileSessionSampleRate;
   @Nullable
   private String profilingTracesDirPath;
   @Nullable
   private ProfileLifecycle profileLifecycle;
   @Nullable
   private SentryOptions.Cron cron;

   @NotNull
   public static ExternalOptions from(@NotNull PropertiesProvider propertiesProvider, @NotNull ILogger logger) {
      ExternalOptions options = new ExternalOptions();
      options.setDsn(propertiesProvider.getProperty("dsn"));
      options.setEnvironment(propertiesProvider.getProperty("environment"));
      options.setRelease(propertiesProvider.getProperty("release"));
      options.setDist(propertiesProvider.getProperty("dist"));
      options.setServerName(propertiesProvider.getProperty("servername"));
      options.setEnableUncaughtExceptionHandler(propertiesProvider.getBooleanProperty("uncaught.handler.enabled"));
      options.setPrintUncaughtStackTrace(propertiesProvider.getBooleanProperty("uncaught.handler.print-stacktrace"));
      options.setTracesSampleRate(propertiesProvider.getDoubleProperty("traces-sample-rate"));
      options.setProfilesSampleRate(propertiesProvider.getDoubleProperty("profiles-sample-rate"));
      options.setDebug(propertiesProvider.getBooleanProperty("debug"));
      options.setEnableDeduplication(propertiesProvider.getBooleanProperty("enable-deduplication"));
      options.setSendClientReports(propertiesProvider.getBooleanProperty("send-client-reports"));
      options.setForceInit(propertiesProvider.getBooleanProperty("force-init"));
      String maxRequestBodySize = propertiesProvider.getProperty("max-request-body-size");
      if (maxRequestBodySize != null) {
         options.setMaxRequestBodySize(SentryOptions.RequestSize.valueOf(maxRequestBodySize.toUpperCase(Locale.ROOT)));
      }

      Map<String, String> tags = propertiesProvider.getMap("tags");

      for (Entry<String, String> tag : tags.entrySet()) {
         options.setTag(tag.getKey(), tag.getValue());
      }

      String proxyHost = propertiesProvider.getProperty("proxy.host");
      String proxyUser = propertiesProvider.getProperty("proxy.user");
      String proxyPass = propertiesProvider.getProperty("proxy.pass");
      String proxyPort = propertiesProvider.getProperty("proxy.port", "80");
      if (proxyHost != null) {
         options.setProxy(new SentryOptions.Proxy(proxyHost, proxyPort, proxyUser, proxyPass));
      }

      for (String inAppInclude : propertiesProvider.getList("in-app-includes")) {
         options.addInAppInclude(inAppInclude);
      }

      for (String inAppExclude : propertiesProvider.getList("in-app-excludes")) {
         options.addInAppExclude(inAppExclude);
      }

      List<String> tracePropagationTargets = null;
      if (propertiesProvider.getProperty("trace-propagation-targets") != null) {
         tracePropagationTargets = propertiesProvider.getList("trace-propagation-targets");
      }

      if (tracePropagationTargets == null && propertiesProvider.getProperty("tracing-origins") != null) {
         tracePropagationTargets = propertiesProvider.getList("tracing-origins");
      }

      if (tracePropagationTargets != null) {
         for (String tracePropagationTarget : tracePropagationTargets) {
            options.addTracePropagationTarget(tracePropagationTarget);
         }
      }

      for (String contextTag : propertiesProvider.getList("context-tags")) {
         options.addContextTag(contextTag);
      }

      options.setProguardUuid(propertiesProvider.getProperty("proguard-uuid"));

      for (String bundleId : propertiesProvider.getList("bundle-ids")) {
         options.addBundleId(bundleId);
      }

      options.setIdleTimeout(propertiesProvider.getLongProperty("idle-timeout"));
      options.setIgnoredErrors(propertiesProvider.getListOrNull("ignored-errors"));
      options.setEnabled(propertiesProvider.getBooleanProperty("enabled"));
      options.setEnablePrettySerializationOutput(propertiesProvider.getBooleanProperty("enable-pretty-serialization-output"));
      options.setSendModules(propertiesProvider.getBooleanProperty("send-modules"));
      options.setSendDefaultPii(propertiesProvider.getBooleanProperty("send-default-pii"));
      options.setIgnoredCheckIns(propertiesProvider.getListOrNull("ignored-checkins"));
      options.setIgnoredTransactions(propertiesProvider.getListOrNull("ignored-transactions"));
      options.setEnableBackpressureHandling(propertiesProvider.getBooleanProperty("enable-backpressure-handling"));
      options.setGlobalHubMode(propertiesProvider.getBooleanProperty("global-hub-mode"));
      options.setCaptureOpenTelemetryEvents(propertiesProvider.getBooleanProperty("capture-open-telemetry-events"));
      options.setEnableLogs(propertiesProvider.getBooleanProperty("logs.enabled"));

      for (String ignoredExceptionType : propertiesProvider.getList("ignored-exceptions-for-type")) {
         try {
            Class<?> clazz = Class.forName(ignoredExceptionType);
            if (Throwable.class.isAssignableFrom(clazz)) {
               options.addIgnoredExceptionForType((Class<? extends Throwable>)clazz);
            } else {
               logger.log(
                  SentryLevel.WARNING,
                  "Skipping setting %s as ignored-exception-for-type. Reason: %s does not extend Throwable",
                  ignoredExceptionType,
                  ignoredExceptionType
               );
            }
         } catch (ClassNotFoundException var16) {
            logger.log(
               SentryLevel.WARNING,
               "Skipping setting %s as ignored-exception-for-type. Reason: %s class is not found",
               ignoredExceptionType,
               ignoredExceptionType
            );
         }
      }

      Long cronDefaultCheckinMargin = propertiesProvider.getLongProperty("cron.default-checkin-margin");
      Long cronDefaultMaxRuntime = propertiesProvider.getLongProperty("cron.default-max-runtime");
      String cronDefaultTimezone = propertiesProvider.getProperty("cron.default-timezone");
      Long cronDefaultFailureIssueThreshold = propertiesProvider.getLongProperty("cron.default-failure-issue-threshold");
      Long cronDefaultRecoveryThreshold = propertiesProvider.getLongProperty("cron.default-recovery-threshold");
      if (cronDefaultCheckinMargin != null
         || cronDefaultMaxRuntime != null
         || cronDefaultTimezone != null
         || cronDefaultFailureIssueThreshold != null
         || cronDefaultRecoveryThreshold != null) {
         SentryOptions.Cron cron = new SentryOptions.Cron();
         cron.setDefaultCheckinMargin(cronDefaultCheckinMargin);
         cron.setDefaultMaxRuntime(cronDefaultMaxRuntime);
         cron.setDefaultTimezone(cronDefaultTimezone);
         cron.setDefaultFailureIssueThreshold(cronDefaultFailureIssueThreshold);
         cron.setDefaultRecoveryThreshold(cronDefaultRecoveryThreshold);
         options.setCron(cron);
      }

      options.setEnableSpotlight(propertiesProvider.getBooleanProperty("enable-spotlight"));
      options.setSpotlightConnectionUrl(propertiesProvider.getProperty("spotlight-connection-url"));
      options.setProfileSessionSampleRate(propertiesProvider.getDoubleProperty("profile-session-sample-rate"));
      options.setProfilingTracesDirPath(propertiesProvider.getProperty("profiling-traces-dir-path"));
      String profileLifecycleString = propertiesProvider.getProperty("profile-lifecycle");
      if (profileLifecycleString != null && !profileLifecycleString.isEmpty()) {
         options.setProfileLifecycle(ProfileLifecycle.valueOf(profileLifecycleString.toUpperCase()));
      }

      return options;
   }

   @Nullable
   public String getDsn() {
      return this.dsn;
   }

   public void setDsn(@Nullable String dsn) {
      this.dsn = dsn;
   }

   @Nullable
   public String getEnvironment() {
      return this.environment;
   }

   public void setEnvironment(@Nullable String environment) {
      this.environment = environment;
   }

   @Nullable
   public String getRelease() {
      return this.release;
   }

   public void setRelease(@Nullable String release) {
      this.release = release;
   }

   @Nullable
   public String getDist() {
      return this.dist;
   }

   public void setDist(@Nullable String dist) {
      this.dist = dist;
   }

   @Nullable
   public String getServerName() {
      return this.serverName;
   }

   public void setServerName(@Nullable String serverName) {
      this.serverName = serverName;
   }

   @Nullable
   public Boolean getEnableUncaughtExceptionHandler() {
      return this.enableUncaughtExceptionHandler;
   }

   public void setEnableUncaughtExceptionHandler(@Nullable Boolean enableUncaughtExceptionHandler) {
      this.enableUncaughtExceptionHandler = enableUncaughtExceptionHandler;
   }

   @Nullable
   public List<String> getTracePropagationTargets() {
      return this.tracePropagationTargets;
   }

   @Nullable
   public Boolean getDebug() {
      return this.debug;
   }

   public void setDebug(@Nullable Boolean debug) {
      this.debug = debug;
   }

   @Nullable
   public Boolean getEnableDeduplication() {
      return this.enableDeduplication;
   }

   public void setEnableDeduplication(@Nullable Boolean enableDeduplication) {
      this.enableDeduplication = enableDeduplication;
   }

   @Nullable
   public Double getTracesSampleRate() {
      return this.tracesSampleRate;
   }

   public void setTracesSampleRate(@Nullable Double tracesSampleRate) {
      this.tracesSampleRate = tracesSampleRate;
   }

   @Nullable
   public Double getProfilesSampleRate() {
      return this.profilesSampleRate;
   }

   public void setProfilesSampleRate(@Nullable Double profilesSampleRate) {
      this.profilesSampleRate = profilesSampleRate;
   }

   @Nullable
   public SentryOptions.RequestSize getMaxRequestBodySize() {
      return this.maxRequestBodySize;
   }

   public void setMaxRequestBodySize(@Nullable SentryOptions.RequestSize maxRequestBodySize) {
      this.maxRequestBodySize = maxRequestBodySize;
   }

   @NotNull
   public Map<String, String> getTags() {
      return this.tags;
   }

   @Nullable
   public SentryOptions.Proxy getProxy() {
      return this.proxy;
   }

   public void setProxy(@Nullable SentryOptions.Proxy proxy) {
      this.proxy = proxy;
   }

   @NotNull
   public List<String> getInAppExcludes() {
      return this.inAppExcludes;
   }

   @NotNull
   public List<String> getInAppIncludes() {
      return this.inAppIncludes;
   }

   @NotNull
   public List<String> getContextTags() {
      return this.contextTags;
   }

   @Nullable
   public String getProguardUuid() {
      return this.proguardUuid;
   }

   public void setProguardUuid(@Nullable String proguardUuid) {
      this.proguardUuid = proguardUuid;
   }

   @NotNull
   public Set<Class<? extends Throwable>> getIgnoredExceptionsForType() {
      return this.ignoredExceptionsForType;
   }

   public void addInAppInclude(@NotNull String include) {
      this.inAppIncludes.add(include);
   }

   public void addInAppExclude(@NotNull String exclude) {
      this.inAppExcludes.add(exclude);
   }

   public void addTracePropagationTarget(@NotNull String tracePropagationTarget) {
      if (this.tracePropagationTargets == null) {
         this.tracePropagationTargets = new CopyOnWriteArrayList<>();
      }

      if (!tracePropagationTarget.isEmpty()) {
         this.tracePropagationTargets.add(tracePropagationTarget);
      }
   }

   public void addContextTag(@NotNull String contextTag) {
      this.contextTags.add(contextTag);
   }

   public void addIgnoredExceptionForType(@NotNull Class<? extends Throwable> exceptionType) {
      this.ignoredExceptionsForType.add(exceptionType);
   }

   public void setTag(@NotNull String key, @NotNull String value) {
      this.tags.put(key, value);
   }

   @Nullable
   public Boolean getPrintUncaughtStackTrace() {
      return this.printUncaughtStackTrace;
   }

   public void setPrintUncaughtStackTrace(@Nullable Boolean printUncaughtStackTrace) {
      this.printUncaughtStackTrace = printUncaughtStackTrace;
   }

   @Nullable
   public Long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(@Nullable Long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   @Nullable
   public List<String> getIgnoredErrors() {
      return this.ignoredErrors;
   }

   public void setIgnoredErrors(@Nullable List<String> ignoredErrors) {
      this.ignoredErrors = ignoredErrors;
   }

   @Nullable
   public Boolean getSendClientReports() {
      return this.sendClientReports;
   }

   public void setSendClientReports(@Nullable Boolean sendClientReports) {
      this.sendClientReports = sendClientReports;
   }

   @NotNull
   public Set<String> getBundleIds() {
      return this.bundleIds;
   }

   public void addBundleId(@NotNull String bundleId) {
      this.bundleIds.add(bundleId);
   }

   @Nullable
   public Boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(@Nullable Boolean enabled) {
      this.enabled = enabled;
   }

   @Nullable
   public Boolean isEnablePrettySerializationOutput() {
      return this.enablePrettySerializationOutput;
   }

   public void setEnablePrettySerializationOutput(@Nullable Boolean enablePrettySerializationOutput) {
      this.enablePrettySerializationOutput = enablePrettySerializationOutput;
   }

   @Nullable
   public Boolean isSendModules() {
      return this.sendModules;
   }

   public void setSendModules(@Nullable Boolean sendModules) {
      this.sendModules = sendModules;
   }

   @Nullable
   public Boolean isSendDefaultPii() {
      return this.sendDefaultPii;
   }

   public void setSendDefaultPii(@Nullable Boolean sendDefaultPii) {
      this.sendDefaultPii = sendDefaultPii;
   }

   public void setIgnoredCheckIns(@Nullable List<String> ignoredCheckIns) {
      this.ignoredCheckIns = ignoredCheckIns;
   }

   @Nullable
   public List<String> getIgnoredCheckIns() {
      return this.ignoredCheckIns;
   }

   public void setIgnoredTransactions(@Nullable List<String> ignoredTransactions) {
      this.ignoredTransactions = ignoredTransactions;
   }

   @Nullable
   public List<String> getIgnoredTransactions() {
      return this.ignoredTransactions;
   }

   @Experimental
   public void setEnableBackpressureHandling(@Nullable Boolean enableBackpressureHandling) {
      this.enableBackpressureHandling = enableBackpressureHandling;
   }

   @Experimental
   @Nullable
   public Boolean isEnableBackpressureHandling() {
      return this.enableBackpressureHandling;
   }

   public void setGlobalHubMode(@Nullable Boolean globalHubMode) {
      this.globalHubMode = globalHubMode;
   }

   @Experimental
   @Nullable
   public Boolean isGlobalHubMode() {
      return this.globalHubMode;
   }

   public void setForceInit(@Nullable Boolean forceInit) {
      this.forceInit = forceInit;
   }

   @Nullable
   public Boolean isForceInit() {
      return this.forceInit;
   }

   @Nullable
   public SentryOptions.Cron getCron() {
      return this.cron;
   }

   public void setCron(@Nullable SentryOptions.Cron cron) {
      this.cron = cron;
   }

   @Experimental
   public void setEnableSpotlight(@Nullable Boolean enableSpotlight) {
      this.enableSpotlight = enableSpotlight;
   }

   @Experimental
   @Nullable
   public Boolean isEnableSpotlight() {
      return this.enableSpotlight;
   }

   @Experimental
   @Nullable
   public String getSpotlightConnectionUrl() {
      return this.spotlightConnectionUrl;
   }

   @Experimental
   public void setSpotlightConnectionUrl(@Nullable String spotlightConnectionUrl) {
      this.spotlightConnectionUrl = spotlightConnectionUrl;
   }

   @Experimental
   public void setCaptureOpenTelemetryEvents(@Nullable Boolean captureOpenTelemetryEvents) {
      this.captureOpenTelemetryEvents = captureOpenTelemetryEvents;
   }

   @Experimental
   @Nullable
   public Boolean isCaptureOpenTelemetryEvents() {
      return this.captureOpenTelemetryEvents;
   }

   public void setEnableLogs(@Nullable Boolean enableLogs) {
      this.enableLogs = enableLogs;
   }

   @Nullable
   public Boolean isEnableLogs() {
      return this.enableLogs;
   }

   @Nullable
   public Double getProfileSessionSampleRate() {
      return this.profileSessionSampleRate;
   }

   public void setProfileSessionSampleRate(@Nullable Double profileSessionSampleRate) {
      this.profileSessionSampleRate = profileSessionSampleRate;
   }

   @Nullable
   public String getProfilingTracesDirPath() {
      return this.profilingTracesDirPath;
   }

   public void setProfilingTracesDirPath(@Nullable String profilingTracesDirPath) {
      this.profilingTracesDirPath = profilingTracesDirPath;
   }

   @Nullable
   public ProfileLifecycle getProfileLifecycle() {
      return this.profileLifecycle;
   }

   public void setProfileLifecycle(@Nullable ProfileLifecycle profileLifecycle) {
      this.profileLifecycle = profileLifecycle;
   }
}
