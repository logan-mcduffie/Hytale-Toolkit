package io.sentry;

import io.sentry.backpressure.BackpressureMonitor;
import io.sentry.backpressure.NoOpBackpressureMonitor;
import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.cache.PersistingScopeObserver;
import io.sentry.config.PropertiesProviderFactory;
import io.sentry.internal.debugmeta.NoOpDebugMetaLoader;
import io.sentry.internal.debugmeta.ResourcesDebugMetaLoader;
import io.sentry.internal.modules.CompositeModulesLoader;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.ManifestModulesLoader;
import io.sentry.internal.modules.NoOpModulesLoader;
import io.sentry.internal.modules.ResourcesModulesLoader;
import io.sentry.logger.ILoggerApi;
import io.sentry.opentelemetry.OpenTelemetryUtil;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.DebugMetaPropertiesApplier;
import io.sentry.util.FileUtils;
import io.sentry.util.InitUtil;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.SentryRandom;
import io.sentry.util.thread.IThreadChecker;
import io.sentry.util.thread.NoOpThreadChecker;
import io.sentry.util.thread.ThreadChecker;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Sentry {
   @NotNull
   private static volatile IScopesStorage scopesStorage = NoOpScopesStorage.getInstance();
   @NotNull
   private static volatile IScopes rootScopes = NoOpScopes.getInstance();
   @NotNull
   private static final IScope globalScope = new Scope(SentryOptions.empty());
   private static final boolean GLOBAL_HUB_DEFAULT_MODE = false;
   private static volatile boolean globalHubMode = false;
   @Internal
   @NotNull
   public static final String APP_START_PROFILING_CONFIG_FILE_NAME = "app_start_profiling_config";
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private static final long classCreationTimestamp = System.currentTimeMillis();
   private static final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

   private Sentry() {
   }

   @Deprecated
   @Internal
   @NotNull
   public static IHub getCurrentHub() {
      return new HubScopesWrapper(getCurrentScopes());
   }

   @Internal
   @NotNull
   public static IScopes getCurrentScopes() {
      return getCurrentScopes(true);
   }

   @Internal
   @NotNull
   public static IScopes getCurrentScopes(boolean ensureForked) {
      if (globalHubMode) {
         return rootScopes;
      } else {
         IScopes scopes = getScopesStorage().get();
         if (scopes == null || scopes.isNoOp()) {
            if (!ensureForked) {
               return NoOpScopes.getInstance();
            }

            scopes = rootScopes.forkedScopes("getCurrentScopes");
            getScopesStorage().set(scopes);
         }

         return scopes;
      }
   }

   @NotNull
   private static IScopesStorage getScopesStorage() {
      return scopesStorage;
   }

   @Internal
   @NotNull
   public static IScopes forkedRootScopes(@NotNull String creator) {
      return globalHubMode ? rootScopes : rootScopes.forkedScopes(creator);
   }

   @NotNull
   public static IScopes forkedScopes(@NotNull String creator) {
      return getCurrentScopes().forkedScopes(creator);
   }

   @NotNull
   public static IScopes forkedCurrentScope(@NotNull String creator) {
      return getCurrentScopes().forkedCurrentScope(creator);
   }

   @Deprecated
   @Internal
   @NotNull
   public static ISentryLifecycleToken setCurrentHub(@NotNull IHub hub) {
      return setCurrentScopes(hub);
   }

   @Internal
   @NotNull
   public static ISentryLifecycleToken setCurrentScopes(@NotNull IScopes scopes) {
      return getScopesStorage().set(scopes);
   }

   @NotNull
   public static IScope getGlobalScope() {
      return globalScope;
   }

   public static boolean isEnabled() {
      return getCurrentScopes().isEnabled();
   }

   public static void init() {
      init(options -> options.setEnableExternalConfiguration(true), false);
   }

   public static void init(@NotNull String dsn) {
      init(options -> options.setDsn(dsn));
   }

   public static <T extends SentryOptions> void init(@NotNull OptionsContainer<T> clazz, @NotNull Sentry.OptionsConfiguration<T> optionsConfiguration) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      init(clazz, optionsConfiguration, false);
   }

   public static <T extends SentryOptions> void init(
      @NotNull OptionsContainer<T> clazz, @NotNull Sentry.OptionsConfiguration<T> optionsConfiguration, boolean globalHubMode
   ) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      T options = (T)clazz.createInstance();
      applyOptionsConfiguration(optionsConfiguration, options);
      init(options, globalHubMode);
   }

   public static void init(@NotNull Sentry.OptionsConfiguration<SentryOptions> optionsConfiguration) {
      init(optionsConfiguration, false);
   }

   public static void init(@NotNull Sentry.OptionsConfiguration<SentryOptions> optionsConfiguration, boolean globalHubMode) {
      SentryOptions options = new SentryOptions();
      applyOptionsConfiguration(optionsConfiguration, options);
      init(options, globalHubMode);
   }

   private static <T extends SentryOptions> void applyOptionsConfiguration(Sentry.OptionsConfiguration<T> optionsConfiguration, T options) {
      try {
         optionsConfiguration.configure(options);
      } catch (Throwable var3) {
         options.getLogger().log(SentryLevel.ERROR, "Error in the 'OptionsConfiguration.configure' callback.", var3);
      }
   }

   @Internal
   public static void init(@NotNull SentryOptions options) {
      init(options, false);
   }

   private static void init(@NotNull SentryOptions options, boolean globalHubMode) {
      ISentryLifecycleToken ignored = lock.acquire();

      label84: {
         try {
            if (!options.getClass().getName().equals("io.sentry.android.core.SentryAndroidOptions") && Platform.isAndroid()) {
               throw new IllegalArgumentException("You are running Android. Please, use SentryAndroid.init. " + options.getClass().getName());
            }

            if (!preInitConfigurations(options)) {
               break label84;
            }

            Boolean globalHubModeFromOptions = options.isGlobalHubMode();
            boolean globalHubModeToUse = globalHubModeFromOptions != null ? globalHubModeFromOptions : globalHubMode;
            options.getLogger().log(SentryLevel.INFO, "GlobalHubMode: '%s'", String.valueOf(globalHubModeToUse));
            Sentry.globalHubMode = globalHubModeToUse;
            initFatalLogger(options);
            boolean shouldInit = InitUtil.shouldInit(globalScope.getOptions(), options, isEnabled());
            if (shouldInit) {
               if (isEnabled()) {
                  options.getLogger().log(SentryLevel.WARNING, "Sentry has been already initialized. Previous configuration will be overwritten.");
               }

               IScopes scopes = getCurrentScopes();
               scopes.close(true);
               globalScope.replaceOptions(options);
               IScope rootScope = new Scope(options);
               IScope rootIsolationScope = new Scope(options);
               rootScopes = new Scopes(rootScope, rootIsolationScope, globalScope, "Sentry.init");
               initLogger(options);
               initForOpenTelemetryMaybe(options);
               getScopesStorage().set(rootScopes);
               initConfigurations(options);
               globalScope.bindClient(new SentryClient(options));
               if (options.getExecutorService().isClosed()) {
                  options.setExecutorService(new SentryExecutorService(options));
                  options.getExecutorService().prewarm();
               }

               try {
                  options.getExecutorService().submit(() -> options.loadLazyFields());
               } catch (RejectedExecutionException var14) {
                  options.getLogger()
                     .log(SentryLevel.DEBUG, "Failed to call the executor. Lazy fields will not be loaded. Did you call Sentry.close()?", var14);
               }

               movePreviousSession(options);

               for (Integration integration : options.getIntegrations()) {
                  try {
                     integration.register(ScopesAdapter.getInstance(), options);
                  } catch (Throwable var13) {
                     options.getLogger().log(SentryLevel.WARNING, "Failed to register the integration " + integration.getClass().getName(), var13);
                  }
               }

               notifyOptionsObservers(options);
               finalizePreviousSession(options, ScopesAdapter.getInstance());
               handleAppStartProfilingConfig(options, options.getExecutorService());
               options.getLogger().log(SentryLevel.DEBUG, "Using openTelemetryMode %s", options.getOpenTelemetryMode());
               options.getLogger().log(SentryLevel.DEBUG, "Using span factory %s", options.getSpanFactory().getClass().getName());
               options.getLogger().log(SentryLevel.DEBUG, "Using scopes storage %s", scopesStorage.getClass().getName());
            } else {
               options.getLogger().log(SentryLevel.WARNING, "This init call has been ignored due to priority being too low.");
            }
         } catch (Throwable var15) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }
            }

            throw var15;
         }

         if (ignored != null) {
            ignored.close();
         }

         return;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   private static void initForOpenTelemetryMaybe(SentryOptions options) {
      OpenTelemetryUtil.updateOpenTelemetryModeIfAuto(options, new LoadClass());
      if (SentryOpenTelemetryMode.OFF == options.getOpenTelemetryMode()) {
         options.setSpanFactory(new DefaultSpanFactory());
      }

      initScopesStorage(options);
      OpenTelemetryUtil.applyIgnoredSpanOrigins(options);
   }

   private static void initLogger(@NotNull SentryOptions options) {
      if (options.isDebug() && options.getLogger() instanceof NoOpLogger) {
         options.setLogger(new SystemOutLogger());
      }
   }

   private static void initFatalLogger(@NotNull SentryOptions options) {
      if (options.getFatalLogger() instanceof NoOpLogger) {
         options.setFatalLogger(new SystemOutLogger());
      }
   }

   private static void initScopesStorage(SentryOptions options) {
      getScopesStorage().close();
      if (SentryOpenTelemetryMode.OFF == options.getOpenTelemetryMode()) {
         scopesStorage = new DefaultScopesStorage();
      } else {
         scopesStorage = ScopesStorageFactory.create(new LoadClass(), NoOpLogger.getInstance());
      }
   }

   private static void handleAppStartProfilingConfig(@NotNull SentryOptions options, @NotNull ISentryExecutorService sentryExecutorService) {
      try {
         sentryExecutorService.submit(
            () -> {
               String cacheDirPath = options.getCacheDirPathWithoutDsn();
               if (cacheDirPath != null) {
                  File appStartProfilingConfigFile = new File(cacheDirPath, "app_start_profiling_config");

                  try {
                     FileUtils.deleteRecursively(appStartProfilingConfigFile);
                     if (!options.isEnableAppStartProfiling() && !options.isStartProfilerOnAppStart()) {
                        return;
                     }

                     if (!options.isStartProfilerOnAppStart() && !options.isTracingEnabled()) {
                        options.getLogger().log(SentryLevel.INFO, "Tracing is disabled and app start profiling will not start.");
                        return;
                     }

                     if (appStartProfilingConfigFile.createNewFile()) {
                        TracesSamplingDecision appStartSamplingDecision = options.isEnableAppStartProfiling()
                           ? sampleAppStartProfiling(options)
                           : new TracesSamplingDecision(false);
                        SentryAppStartProfilingOptions appStartProfilingOptions = new SentryAppStartProfilingOptions(options, appStartSamplingDecision);
                        OutputStream outputStream = new FileOutputStream(appStartProfilingConfigFile);

                        try {
                           Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));

                           try {
                              options.getSerializer().serialize(appStartProfilingOptions, writer);
                           } catch (Throwable var11) {
                              try {
                                 writer.close();
                              } catch (Throwable var10) {
                                 var11.addSuppressed(var10);
                              }

                              throw var11;
                           }

                           writer.close();
                        } catch (Throwable var12) {
                           try {
                              outputStream.close();
                           } catch (Throwable var9) {
                              var12.addSuppressed(var9);
                           }

                           throw var12;
                        }

                        outputStream.close();
                     }
                  } catch (Throwable var13) {
                     options.getLogger().log(SentryLevel.ERROR, "Unable to create app start profiling config file. ", var13);
                  }
               }
            }
         );
      } catch (Throwable var3) {
         options.getLogger()
            .log(SentryLevel.ERROR, "Failed to call the executor. App start profiling config will not be changed. Did you call Sentry.close()?", var3);
      }
   }

   @NotNull
   private static TracesSamplingDecision sampleAppStartProfiling(@NotNull SentryOptions options) {
      TransactionContext appStartTransactionContext = new TransactionContext("app.launch", "profile");
      appStartTransactionContext.setForNextAppStart(true);
      SamplingContext appStartSamplingContext = new SamplingContext(appStartTransactionContext, null, SentryRandom.current().nextDouble(), null);
      return options.getInternalTracesSampler().sample(appStartSamplingContext);
   }

   private static void movePreviousSession(@NotNull SentryOptions options) {
      try {
         options.getExecutorService().submit(new MovePreviousSession(options));
      } catch (Throwable var2) {
         options.getLogger().log(SentryLevel.DEBUG, "Failed to move previous session.", var2);
      }
   }

   private static void finalizePreviousSession(@NotNull SentryOptions options, @NotNull IScopes scopes) {
      try {
         options.getExecutorService().submit(new PreviousSessionFinalizer(options, scopes));
      } catch (Throwable var3) {
         options.getLogger().log(SentryLevel.DEBUG, "Failed to finalize previous session.", var3);
      }
   }

   private static void notifyOptionsObservers(@NotNull SentryOptions options) {
      try {
         options.getExecutorService().submit(() -> {
            for (IOptionsObserver observer : options.getOptionsObservers()) {
               observer.setRelease(options.getRelease());
               observer.setProguardUuid(options.getProguardUuid());
               observer.setSdkVersion(options.getSdkVersion());
               observer.setDist(options.getDist());
               observer.setEnvironment(options.getEnvironment());
               observer.setTags(options.getTags());
               observer.setReplayErrorSampleRate(options.getSessionReplay().getOnErrorSampleRate());
            }

            PersistingScopeObserver scopeCache = options.findPersistingScopeObserver();
            if (scopeCache != null) {
               scopeCache.resetCache();
            }
         });
      } catch (Throwable var2) {
         options.getLogger().log(SentryLevel.DEBUG, "Failed to notify options observers.", var2);
      }
   }

   private static boolean preInitConfigurations(@NotNull SentryOptions options) {
      if (options.isEnableExternalConfiguration()) {
         options.merge(ExternalOptions.from(PropertiesProviderFactory.create(), options.getLogger()));
      }

      String dsn = options.getDsn();
      if (options.isEnabled() && (dsn == null || !dsn.isEmpty())) {
         if (dsn == null) {
            throw new IllegalArgumentException("DSN is required. Use empty string or set enabled to false in SentryOptions to disable SDK.");
         } else {
            options.retrieveParsedDsn();
            return true;
         }
      } else {
         close();
         return false;
      }
   }

   private static void initConfigurations(@NotNull SentryOptions options) {
      ILogger logger = options.getLogger();
      logger.log(SentryLevel.INFO, "Initializing SDK with DSN: '%s'", options.getDsn());
      String outboxPath = options.getOutboxPath();
      if (outboxPath != null) {
         File outboxDir = new File(outboxPath);
         options.getRuntimeManager().runWithRelaxedPolicy(() -> outboxDir.mkdirs());
      } else {
         logger.log(SentryLevel.INFO, "No outbox dir path is defined in options.");
      }

      String cacheDirPath = options.getCacheDirPath();
      if (cacheDirPath != null) {
         File cacheDir = new File(cacheDirPath);
         options.getRuntimeManager().runWithRelaxedPolicy(() -> cacheDir.mkdirs());
         IEnvelopeCache envelopeCache = options.getEnvelopeDiskCache();
         if (envelopeCache instanceof NoOpEnvelopeCache) {
            options.setEnvelopeDiskCache(EnvelopeCache.create(options));
         }
      }

      String profilingTracesDirPath = options.getProfilingTracesDirPath();
      if ((options.isProfilingEnabled() || options.isContinuousProfilingEnabled()) && profilingTracesDirPath != null) {
         File profilingTracesDir = new File(profilingTracesDirPath);
         options.getRuntimeManager().runWithRelaxedPolicy(() -> profilingTracesDir.mkdirs());

         try {
            options.getExecutorService().submit(() -> {
               File[] oldTracesDirContent = profilingTracesDir.listFiles();
               if (oldTracesDirContent != null) {
                  for (File f : oldTracesDirContent) {
                     if (f.lastModified() < classCreationTimestamp - TimeUnit.MINUTES.toMillis(5L)) {
                        FileUtils.deleteRecursively(f);
                     }
                  }
               }
            });
         } catch (RejectedExecutionException var8) {
            options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Old profiles will not be deleted. Did you call Sentry.close()?", var8);
         }
      }

      IModulesLoader modulesLoader = options.getModulesLoader();
      if (!options.isSendModules()) {
         options.setModulesLoader(NoOpModulesLoader.getInstance());
      } else if (modulesLoader instanceof NoOpModulesLoader) {
         options.setModulesLoader(
            new CompositeModulesLoader(
               Arrays.asList(new ManifestModulesLoader(options.getLogger()), new ResourcesModulesLoader(options.getLogger())), options.getLogger()
            )
         );
      }

      if (options.getDebugMetaLoader() instanceof NoOpDebugMetaLoader) {
         options.setDebugMetaLoader(new ResourcesDebugMetaLoader(options.getLogger()));
      }

      List<Properties> propertiesList = options.getDebugMetaLoader().loadDebugMeta();
      DebugMetaPropertiesApplier.apply(options, propertiesList);
      IThreadChecker threadChecker = options.getThreadChecker();
      if (threadChecker instanceof NoOpThreadChecker) {
         options.setThreadChecker(ThreadChecker.getInstance());
      }

      if (options.getPerformanceCollectors().isEmpty()) {
         options.addPerformanceCollector(new JavaMemoryCollector());
      }

      if (options.isEnableBackpressureHandling() && Platform.isJvm()) {
         if (options.getBackpressureMonitor() instanceof NoOpBackpressureMonitor) {
            options.setBackpressureMonitor(new BackpressureMonitor(options, ScopesAdapter.getInstance()));
         }

         options.getBackpressureMonitor().start();
      }

      initJvmContinuousProfiling(options);
      options.getLogger()
         .log(SentryLevel.INFO, "Continuous profiler is enabled %s mode: %s", options.isContinuousProfilingEnabled(), options.getProfileLifecycle());
   }

   private static void initJvmContinuousProfiling(@NotNull SentryOptions options) {
      InitUtil.initializeProfiler(options);
      InitUtil.initializeProfileConverter(options);
   }

   public static void close() {
      ISentryLifecycleToken ignored = lock.acquire();

      try {
         IScopes scopes = getCurrentScopes();
         rootScopes = NoOpScopes.getInstance();
         getScopesStorage().close();
         scopes.close(false);
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

   @NotNull
   public static SentryId captureEvent(@NotNull SentryEvent event) {
      return getCurrentScopes().captureEvent(event);
   }

   @NotNull
   public static SentryId captureEvent(@NotNull SentryEvent event, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureEvent(event, callback);
   }

   @NotNull
   public static SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return getCurrentScopes().captureEvent(event, hint);
   }

   @NotNull
   public static SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureEvent(event, hint, callback);
   }

   @NotNull
   public static SentryId captureMessage(@NotNull String message) {
      return getCurrentScopes().captureMessage(message);
   }

   @NotNull
   public static SentryId captureMessage(@NotNull String message, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureMessage(message, callback);
   }

   @NotNull
   public static SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return getCurrentScopes().captureMessage(message, level);
   }

   @NotNull
   public static SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureMessage(message, level, callback);
   }

   @NotNull
   public static SentryId captureFeedback(@NotNull Feedback feedback) {
      return getCurrentScopes().captureFeedback(feedback);
   }

   @NotNull
   public static SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
      return getCurrentScopes().captureFeedback(feedback, hint);
   }

   @NotNull
   public static SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
      return getCurrentScopes().captureFeedback(feedback, hint, callback);
   }

   @NotNull
   public static SentryId captureException(@NotNull Throwable throwable) {
      return getCurrentScopes().captureException(throwable);
   }

   @NotNull
   public static SentryId captureException(@NotNull Throwable throwable, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureException(throwable, callback);
   }

   @NotNull
   public static SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return getCurrentScopes().captureException(throwable, hint);
   }

   @NotNull
   public static SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return getCurrentScopes().captureException(throwable, hint, callback);
   }

   public static void captureUserFeedback(@NotNull UserFeedback userFeedback) {
      getCurrentScopes().captureUserFeedback(userFeedback);
   }

   public static void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      getCurrentScopes().addBreadcrumb(breadcrumb, hint);
   }

   public static void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      getCurrentScopes().addBreadcrumb(breadcrumb);
   }

   public static void addBreadcrumb(@NotNull String message) {
      getCurrentScopes().addBreadcrumb(message);
   }

   public static void addBreadcrumb(@NotNull String message, @NotNull String category) {
      getCurrentScopes().addBreadcrumb(message, category);
   }

   public static void setLevel(@Nullable SentryLevel level) {
      getCurrentScopes().setLevel(level);
   }

   public static void setTransaction(@Nullable String transaction) {
      getCurrentScopes().setTransaction(transaction);
   }

   public static void setUser(@Nullable User user) {
      getCurrentScopes().setUser(user);
   }

   public static void setFingerprint(@NotNull List<String> fingerprint) {
      getCurrentScopes().setFingerprint(fingerprint);
   }

   public static void clearBreadcrumbs() {
      getCurrentScopes().clearBreadcrumbs();
   }

   public static void setTag(@Nullable String key, @Nullable String value) {
      getCurrentScopes().setTag(key, value);
   }

   public static void removeTag(@Nullable String key) {
      getCurrentScopes().removeTag(key);
   }

   public static void setExtra(@Nullable String key, @Nullable String value) {
      getCurrentScopes().setExtra(key, value);
   }

   public static void removeExtra(@Nullable String key) {
      getCurrentScopes().removeExtra(key);
   }

   @NotNull
   public static SentryId getLastEventId() {
      return getCurrentScopes().getLastEventId();
   }

   @NotNull
   public static ISentryLifecycleToken pushScope() {
      return (ISentryLifecycleToken)(!globalHubMode ? getCurrentScopes().pushScope() : NoOpScopesLifecycleToken.getInstance());
   }

   @NotNull
   public static ISentryLifecycleToken pushIsolationScope() {
      return (ISentryLifecycleToken)(!globalHubMode ? getCurrentScopes().pushIsolationScope() : NoOpScopesLifecycleToken.getInstance());
   }

   @Deprecated
   public static void popScope() {
      if (!globalHubMode) {
         getCurrentScopes().popScope();
      }
   }

   public static void withScope(@NotNull ScopeCallback callback) {
      getCurrentScopes().withScope(callback);
   }

   public static void withIsolationScope(@NotNull ScopeCallback callback) {
      getCurrentScopes().withIsolationScope(callback);
   }

   public static void configureScope(@NotNull ScopeCallback callback) {
      configureScope(null, callback);
   }

   public static void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
      getCurrentScopes().configureScope(scopeType, callback);
   }

   public static void bindClient(@NotNull ISentryClient client) {
      getCurrentScopes().bindClient(client);
   }

   public static boolean isHealthy() {
      return getCurrentScopes().isHealthy();
   }

   public static void flush(long timeoutMillis) {
      getCurrentScopes().flush(timeoutMillis);
   }

   public static void startSession() {
      getCurrentScopes().startSession();
   }

   public static void endSession() {
      getCurrentScopes().endSession();
   }

   @NotNull
   public static ITransaction startTransaction(@NotNull String name, @NotNull String operation) {
      return getCurrentScopes().startTransaction(name, operation);
   }

   @NotNull
   public static ITransaction startTransaction(@NotNull String name, @NotNull String operation, @NotNull TransactionOptions transactionOptions) {
      return getCurrentScopes().startTransaction(name, operation, transactionOptions);
   }

   @NotNull
   public static ITransaction startTransaction(
      @NotNull String name, @NotNull String operation, @Nullable String description, @NotNull TransactionOptions transactionOptions
   ) {
      ITransaction transaction = getCurrentScopes().startTransaction(name, operation, transactionOptions);
      transaction.setDescription(description);
      return transaction;
   }

   @NotNull
   public static ITransaction startTransaction(@NotNull TransactionContext transactionContexts) {
      return getCurrentScopes().startTransaction(transactionContexts);
   }

   @NotNull
   public static ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      return getCurrentScopes().startTransaction(transactionContext, transactionOptions);
   }

   @Experimental
   public static void startProfiler() {
      getCurrentScopes().startProfiler();
   }

   @Experimental
   public static void stopProfiler() {
      getCurrentScopes().stopProfiler();
   }

   @Nullable
   public static ISpan getSpan() {
      return (ISpan)(globalHubMode && Platform.isAndroid() ? getCurrentScopes().getTransaction() : getCurrentScopes().getSpan());
   }

   @Nullable
   public static Boolean isCrashedLastRun() {
      return getCurrentScopes().isCrashedLastRun();
   }

   public static void reportFullyDisplayed() {
      getCurrentScopes().reportFullyDisplayed();
   }

   @Nullable
   public static TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
      return getCurrentScopes().continueTrace(sentryTrace, baggageHeaders);
   }

   @Nullable
   public static SentryTraceHeader getTraceparent() {
      return getCurrentScopes().getTraceparent();
   }

   @Nullable
   public static BaggageHeader getBaggage() {
      return getCurrentScopes().getBaggage();
   }

   @NotNull
   public static SentryId captureCheckIn(@NotNull CheckIn checkIn) {
      return getCurrentScopes().captureCheckIn(checkIn);
   }

   @NotNull
   public static ILoggerApi logger() {
      return getCurrentScopes().logger();
   }

   @NotNull
   public static IReplayApi replay() {
      return getCurrentScopes().getScope().getOptions().getReplayController();
   }

   @NotNull
   public static IDistributionApi distribution() {
      return getCurrentScopes().getScope().getOptions().getDistributionController();
   }

   public static void showUserFeedbackDialog() {
      showUserFeedbackDialog(null);
   }

   public static void showUserFeedbackDialog(@Nullable SentryFeedbackOptions.OptionsConfigurator configurator) {
      showUserFeedbackDialog(null, configurator);
   }

   public static void showUserFeedbackDialog(@Nullable SentryId associatedEventId, @Nullable SentryFeedbackOptions.OptionsConfigurator configurator) {
      SentryOptions options = getCurrentScopes().getOptions();
      options.getFeedbackOptions().getDialogHandler().showDialog(associatedEventId, configurator);
   }

   public static void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      getCurrentScopes().addFeatureFlag(flag, result);
   }

   public interface OptionsConfiguration<T extends SentryOptions> {
      void configure(@NotNull T var1);
   }
}
