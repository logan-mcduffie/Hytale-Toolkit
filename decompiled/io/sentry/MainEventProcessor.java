package io.sentry;

import io.sentry.hints.AbnormalExit;
import io.sentry.hints.Cached;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MainEventProcessor implements EventProcessor, Closeable {
   @NotNull
   private final SentryOptions options;
   @NotNull
   private final SentryThreadFactory sentryThreadFactory;
   @NotNull
   private final SentryExceptionFactory sentryExceptionFactory;
   @Nullable
   private volatile HostnameCache hostnameCache = null;

   public MainEventProcessor(@NotNull SentryOptions options) {
      this.options = Objects.requireNonNull(options, "The SentryOptions is required.");
      SentryStackTraceFactory sentryStackTraceFactory = new SentryStackTraceFactory(this.options);
      this.sentryExceptionFactory = new SentryExceptionFactory(sentryStackTraceFactory);
      this.sentryThreadFactory = new SentryThreadFactory(sentryStackTraceFactory);
   }

   MainEventProcessor(@NotNull SentryOptions options, @NotNull SentryThreadFactory sentryThreadFactory, @NotNull SentryExceptionFactory sentryExceptionFactory) {
      this.options = Objects.requireNonNull(options, "The SentryOptions is required.");
      this.sentryThreadFactory = Objects.requireNonNull(sentryThreadFactory, "The SentryThreadFactory is required.");
      this.sentryExceptionFactory = Objects.requireNonNull(sentryExceptionFactory, "The SentryExceptionFactory is required.");
   }

   @NotNull
   @Override
   public SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
      this.setCommons(event);
      this.setExceptions(event);
      this.setDebugMeta(event);
      this.setModules(event);
      if (this.shouldApplyScopeData(event, hint)) {
         this.processNonCachedEvent(event);
         this.setThreads(event, hint);
      }

      return event;
   }

   private void setDebugMeta(@NotNull SentryBaseEvent event) {
      DebugMeta debugMeta = DebugMeta.buildDebugMeta(event.getDebugMeta(), this.options);
      if (debugMeta != null) {
         event.setDebugMeta(debugMeta);
      }
   }

   private void setModules(@NotNull SentryEvent event) {
      Map<String, String> modules = this.options.getModulesLoader().getOrLoadModules();
      if (modules != null) {
         Map<String, String> eventModules = event.getModules();
         if (eventModules == null) {
            event.setModules(modules);
         } else {
            eventModules.putAll(modules);
         }
      }
   }

   private boolean shouldApplyScopeData(@NotNull SentryBaseEvent event, @NotNull Hint hint) {
      if (HintUtils.shouldApplyScopeData(hint)) {
         return true;
      } else {
         this.options
            .getLogger()
            .log(SentryLevel.DEBUG, "Event was cached so not applying data relevant to the current app execution/version: %s", event.getEventId());
         return false;
      }
   }

   private void processNonCachedEvent(@NotNull SentryBaseEvent event) {
      this.setRelease(event);
      this.setEnvironment(event);
      this.setServerName(event);
      this.setDist(event);
      this.setSdk(event);
      this.setTags(event);
      this.mergeUser(event);
   }

   @NotNull
   @Override
   public SentryTransaction process(@NotNull SentryTransaction transaction, @NotNull Hint hint) {
      this.setCommons(transaction);
      this.setDebugMeta(transaction);
      if (this.shouldApplyScopeData(transaction, hint)) {
         this.processNonCachedEvent(transaction);
      }

      return transaction;
   }

   @NotNull
   @Override
   public SentryReplayEvent process(@NotNull SentryReplayEvent event, @NotNull Hint hint) {
      this.setCommons(event);
      if (this.shouldApplyScopeData(event, hint)) {
         this.processNonCachedEvent(event);
         SdkVersion replaySdkVersion = this.options.getSessionReplay().getSdkVersion();
         if (replaySdkVersion != null) {
            event.setSdk(replaySdkVersion);
         }
      }

      return event;
   }

   @Nullable
   @Override
   public SentryLogEvent process(@NotNull SentryLogEvent event) {
      return event;
   }

   private void setCommons(@NotNull SentryBaseEvent event) {
      this.setPlatform(event);
   }

   private void setPlatform(@NotNull SentryBaseEvent event) {
      if (event.getPlatform() == null) {
         event.setPlatform("java");
      }
   }

   private void setRelease(@NotNull SentryBaseEvent event) {
      if (event.getRelease() == null) {
         event.setRelease(this.options.getRelease());
      }
   }

   private void setEnvironment(@NotNull SentryBaseEvent event) {
      if (event.getEnvironment() == null) {
         event.setEnvironment(this.options.getEnvironment());
      }
   }

   private void setServerName(@NotNull SentryBaseEvent event) {
      if (event.getServerName() == null) {
         event.setServerName(this.options.getServerName());
      }

      if (this.options.isAttachServerName() && event.getServerName() == null) {
         this.ensureHostnameCache();
         if (this.hostnameCache != null) {
            event.setServerName(this.hostnameCache.getHostname());
         }
      }
   }

   private void ensureHostnameCache() {
      if (this.hostnameCache == null) {
         this.hostnameCache = HostnameCache.getInstance();
      }
   }

   private void setDist(@NotNull SentryBaseEvent event) {
      if (event.getDist() == null) {
         event.setDist(this.options.getDist());
      }
   }

   private void setSdk(@NotNull SentryBaseEvent event) {
      if (event.getSdk() == null) {
         event.setSdk(this.options.getSdkVersion());
      }
   }

   private void setTags(@NotNull SentryBaseEvent event) {
      if (event.getTags() == null) {
         event.setTags(new HashMap<>(this.options.getTags()));
      } else {
         for (Entry<String, String> item : this.options.getTags().entrySet()) {
            if (!event.getTags().containsKey(item.getKey())) {
               event.setTag(item.getKey(), item.getValue());
            }
         }
      }
   }

   private void mergeUser(@NotNull SentryBaseEvent event) {
      User user = event.getUser();
      if (user == null) {
         user = new User();
         event.setUser(user);
      }

      if (user.getIpAddress() == null && this.options.isSendDefaultPii()) {
         user.setIpAddress("{{auto}}");
      }
   }

   private void setExceptions(@NotNull SentryEvent event) {
      Throwable throwable = event.getThrowableMechanism();
      if (throwable != null) {
         event.setExceptions(this.sentryExceptionFactory.getSentryExceptions(throwable));
      }
   }

   private void setThreads(@NotNull SentryEvent event, @NotNull Hint hint) {
      if (event.getThreads() == null) {
         List<Long> mechanismThreadIds = null;
         List<SentryException> eventExceptions = event.getExceptions();
         if (eventExceptions != null && !eventExceptions.isEmpty()) {
            for (SentryException item : eventExceptions) {
               if (item.getMechanism() != null && item.getThreadId() != null) {
                  if (mechanismThreadIds == null) {
                     mechanismThreadIds = new ArrayList<>();
                  }

                  mechanismThreadIds.add(item.getThreadId());
               }
            }
         }

         if (this.options.isAttachThreads() || HintUtils.hasType(hint, AbnormalExit.class)) {
            Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
            boolean ignoreCurrentThread = false;
            boolean attachStacktrace = this.options.isAttachStacktrace();
            if (sentrySdkHint instanceof AbnormalExit) {
               ignoreCurrentThread = ((AbnormalExit)sentrySdkHint).ignoreCurrentThread();
               attachStacktrace = true;
            }

            event.setThreads(this.sentryThreadFactory.getCurrentThreads(mechanismThreadIds, ignoreCurrentThread, attachStacktrace));
         } else if (this.options.isAttachStacktrace() && (eventExceptions == null || eventExceptions.isEmpty()) && !this.isCachedHint(hint)) {
            event.setThreads(this.sentryThreadFactory.getCurrentThread(this.options.isAttachStacktrace()));
         }
      }
   }

   private boolean isCachedHint(@NotNull Hint hint) {
      return HintUtils.hasType(hint, Cached.class);
   }

   @Override
   public void close() throws IOException {
      if (this.hostnameCache != null) {
         this.hostnameCache.close();
      }
   }

   boolean isClosed() {
      return this.hostnameCache != null ? this.hostnameCache.isClosed() : true;
   }

   @VisibleForTesting
   @Nullable
   HostnameCache getHostnameCache() {
      return this.hostnameCache;
   }

   @Nullable
   @Override
   public Long getOrder() {
      return 0L;
   }
}
