package io.sentry;

import io.sentry.featureflags.FeatureFlagBuffer;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.App;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.protocol.User;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.CollectionUtils;
import io.sentry.util.EventProcessorUtils;
import io.sentry.util.ExceptionUtils;
import io.sentry.util.Objects;
import io.sentry.util.Pair;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Scope implements IScope {
   @NotNull
   private volatile SentryId lastEventId;
   @Nullable
   private SentryLevel level;
   @Nullable
   private ITransaction transaction;
   @NotNull
   private WeakReference<ISpan> activeSpan = new WeakReference<>(null);
   @Nullable
   private String transactionName;
   @Nullable
   private User user;
   @Nullable
   private String screen;
   @Nullable
   private Request request;
   @NotNull
   private List<String> fingerprint = new ArrayList<>();
   @NotNull
   private volatile Queue<Breadcrumb> breadcrumbs;
   @NotNull
   private Map<String, String> tags = new ConcurrentHashMap<>();
   @NotNull
   private Map<String, Object> extra = new ConcurrentHashMap<>();
   @NotNull
   private List<EventProcessorAndOrder> eventProcessors = new CopyOnWriteArrayList<>();
   @NotNull
   private volatile SentryOptions options;
   @Nullable
   private volatile Session session;
   @NotNull
   private final AutoClosableReentrantLock sessionLock = new AutoClosableReentrantLock();
   @NotNull
   private final AutoClosableReentrantLock transactionLock = new AutoClosableReentrantLock();
   @NotNull
   private final AutoClosableReentrantLock propagationContextLock = new AutoClosableReentrantLock();
   @NotNull
   private Contexts contexts = new Contexts();
   @NotNull
   private List<Attachment> attachments = new CopyOnWriteArrayList<>();
   @NotNull
   private PropagationContext propagationContext;
   @NotNull
   private SentryId replayId = SentryId.EMPTY_ID;
   @NotNull
   private ISentryClient client = NoOpSentryClient.getInstance();
   @NotNull
   private final Map<Throwable, Pair<WeakReference<ISpan>, String>> throwableToSpan = Collections.synchronizedMap(new WeakHashMap<>());
   @NotNull
   private final IFeatureFlagBuffer featureFlags;

   public Scope(@NotNull SentryOptions options) {
      this.options = Objects.requireNonNull(options, "SentryOptions is required.");
      this.breadcrumbs = createBreadcrumbsList(this.options.getMaxBreadcrumbs());
      this.featureFlags = FeatureFlagBuffer.create(options);
      this.propagationContext = new PropagationContext();
      this.lastEventId = SentryId.EMPTY_ID;
   }

   private Scope(@NotNull Scope scope) {
      this.transaction = scope.transaction;
      this.transactionName = scope.transactionName;
      this.activeSpan = scope.activeSpan;
      this.session = scope.session;
      this.options = scope.options;
      this.level = scope.level;
      this.client = scope.client;
      this.lastEventId = scope.getLastEventId();
      User userRef = scope.user;
      this.user = userRef != null ? new User(userRef) : null;
      this.screen = scope.screen;
      this.replayId = scope.replayId;
      Request requestRef = scope.request;
      this.request = requestRef != null ? new Request(requestRef) : null;
      this.fingerprint = new ArrayList<>(scope.fingerprint);
      this.eventProcessors = new CopyOnWriteArrayList<>(scope.eventProcessors);
      Breadcrumb[] breadcrumbsRef = scope.breadcrumbs.toArray(new Breadcrumb[0]);
      Queue<Breadcrumb> breadcrumbsClone = createBreadcrumbsList(scope.options.getMaxBreadcrumbs());

      for (Breadcrumb item : breadcrumbsRef) {
         Breadcrumb breadcrumbClone = new Breadcrumb(item);
         breadcrumbsClone.add(breadcrumbClone);
      }

      this.breadcrumbs = breadcrumbsClone;
      Map<String, String> tagsRef = scope.tags;
      Map<String, String> tagsClone = new ConcurrentHashMap<>();

      for (Entry<String, String> item : tagsRef.entrySet()) {
         if (item != null) {
            tagsClone.put(item.getKey(), item.getValue());
         }
      }

      this.tags = tagsClone;
      Map<String, Object> extraRef = scope.extra;
      Map<String, Object> extraClone = new ConcurrentHashMap<>();

      for (Entry<String, Object> itemx : extraRef.entrySet()) {
         if (itemx != null) {
            extraClone.put(itemx.getKey(), itemx.getValue());
         }
      }

      this.extra = extraClone;
      this.contexts = new Contexts(scope.contexts);
      this.attachments = new CopyOnWriteArrayList<>(scope.attachments);
      this.featureFlags = scope.featureFlags.clone();
      this.propagationContext = new PropagationContext(scope.propagationContext);
   }

   @Nullable
   @Override
   public SentryLevel getLevel() {
      return this.level;
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      this.level = level;

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setLevel(level);
      }
   }

   @Nullable
   @Override
   public String getTransactionName() {
      ITransaction tx = this.transaction;
      return tx != null ? tx.getName() : this.transactionName;
   }

   @Override
   public void setTransaction(@NotNull String transaction) {
      if (transaction != null) {
         ITransaction tx = this.transaction;
         if (tx != null) {
            tx.setName(transaction, TransactionNameSource.CUSTOM);
         }

         this.transactionName = transaction;

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setTransaction(transaction);
         }
      } else {
         this.options.getLogger().log(SentryLevel.WARNING, "Transaction cannot be null");
      }
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      ISpan activeSpan = this.activeSpan.get();
      if (activeSpan != null) {
         return activeSpan;
      } else {
         ITransaction tx = this.transaction;
         if (tx != null) {
            ISpan span = tx.getLatestActiveSpan();
            if (span != null) {
               return span;
            }
         }

         return tx;
      }
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
      this.activeSpan = new WeakReference<>(span);
   }

   @Override
   public void setTransaction(@Nullable ITransaction transaction) {
      ISentryLifecycleToken ignored = this.transactionLock.acquire();

      try {
         this.transaction = transaction;

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            if (transaction != null) {
               observer.setTransaction(transaction.getName());
               observer.setTrace(transaction.getSpanContext(), this);
            } else {
               observer.setTransaction(null);
               observer.setTrace(null, this);
            }
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @Nullable
   @Override
   public User getUser() {
      return this.user;
   }

   @Override
   public void setUser(@Nullable User user) {
      this.user = user;

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setUser(user);
      }
   }

   @Internal
   @Nullable
   @Override
   public String getScreen() {
      return this.screen;
   }

   @Internal
   @Override
   public void setScreen(@Nullable String screen) {
      this.screen = screen;
      Contexts contexts = this.getContexts();
      App app = contexts.getApp();
      if (app == null) {
         app = new App();
         contexts.setApp(app);
      }

      if (screen == null) {
         app.setViewNames(null);
      } else {
         List<String> viewNames = new ArrayList<>(1);
         viewNames.add(screen);
         app.setViewNames(viewNames);
      }

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setContexts(contexts);
      }
   }

   @NotNull
   @Override
   public SentryId getReplayId() {
      return this.replayId;
   }

   @Override
   public void setReplayId(@NotNull SentryId replayId) {
      this.replayId = replayId;

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setReplayId(replayId);
      }
   }

   @Nullable
   @Override
   public Request getRequest() {
      return this.request;
   }

   @Override
   public void setRequest(@Nullable Request request) {
      this.request = request;

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setRequest(request);
      }
   }

   @Internal
   @NotNull
   @Override
   public List<String> getFingerprint() {
      return this.fingerprint;
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
      if (fingerprint != null) {
         this.fingerprint = new ArrayList<>(fingerprint);

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setFingerprint(fingerprint);
         }
      }
   }

   @Internal
   @NotNull
   @Override
   public Queue<Breadcrumb> getBreadcrumbs() {
      return this.breadcrumbs;
   }

   @Nullable
   private Breadcrumb executeBeforeBreadcrumb(@NotNull SentryOptions.BeforeBreadcrumbCallback callback, @NotNull Breadcrumb breadcrumb, @NotNull Hint hint) {
      try {
         breadcrumb = callback.execute(breadcrumb, hint);
      } catch (Throwable var5) {
         this.options
            .getLogger()
            .log(SentryLevel.ERROR, "The BeforeBreadcrumbCallback callback threw an exception. Exception details will be added to the breadcrumb.", var5);
         if (var5.getMessage() != null) {
            breadcrumb.setData("sentry:message", var5.getMessage());
         }
      }

      return breadcrumb;
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      if (breadcrumb != null && !(this.breadcrumbs instanceof DisabledQueue)) {
         if (hint == null) {
            hint = new Hint();
         }

         SentryOptions.BeforeBreadcrumbCallback callback = this.options.getBeforeBreadcrumb();
         if (callback != null) {
            breadcrumb = this.executeBeforeBreadcrumb(callback, breadcrumb, hint);
         }

         if (breadcrumb != null) {
            this.breadcrumbs.add(breadcrumb);

            for (IScopeObserver observer : this.options.getScopeObservers()) {
               observer.addBreadcrumb(breadcrumb);
               observer.setBreadcrumbs(this.breadcrumbs);
            }
         } else {
            this.options.getLogger().log(SentryLevel.INFO, "Breadcrumb was dropped by beforeBreadcrumb");
         }
      }
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      this.addBreadcrumb(breadcrumb, null);
   }

   @Override
   public void clearBreadcrumbs() {
      this.breadcrumbs.clear();

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setBreadcrumbs(this.breadcrumbs);
      }
   }

   @Override
   public void clearTransaction() {
      ISentryLifecycleToken ignored = this.transactionLock.acquire();

      try {
         this.transaction = null;
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }

      this.transactionName = null;

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setTransaction(null);
         observer.setTrace(null, this);
      }
   }

   @Nullable
   @Override
   public ITransaction getTransaction() {
      return this.transaction;
   }

   @Override
   public void clear() {
      this.level = null;
      this.user = null;
      this.request = null;
      this.screen = null;
      this.fingerprint.clear();
      this.clearBreadcrumbs();
      this.tags.clear();
      this.extra.clear();
      this.eventProcessors.clear();
      this.clearTransaction();
      this.clearAttachments();
   }

   @Internal
   @NotNull
   @Override
   public Map<String, String> getTags() {
      return CollectionUtils.newConcurrentHashMap(this.tags);
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      if (key != null) {
         if (value == null) {
            this.removeTag(key);
         } else {
            this.tags.put(key, value);

            for (IScopeObserver observer : this.options.getScopeObservers()) {
               observer.setTag(key, value);
               observer.setTags(this.tags);
            }
         }
      }
   }

   @Override
   public void removeTag(@Nullable String key) {
      if (key != null) {
         this.tags.remove(key);

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.removeTag(key);
            observer.setTags(this.tags);
         }
      }
   }

   @Internal
   @NotNull
   @Override
   public Map<String, Object> getExtras() {
      return this.extra;
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
      if (key != null) {
         if (value == null) {
            this.removeExtra(key);
         } else {
            this.extra.put(key, value);

            for (IScopeObserver observer : this.options.getScopeObservers()) {
               observer.setExtra(key, value);
               observer.setExtras(this.extra);
            }
         }
      }
   }

   @Override
   public void removeExtra(@Nullable String key) {
      if (key != null) {
         this.extra.remove(key);

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.removeExtra(key);
            observer.setExtras(this.extra);
         }
      }
   }

   @NotNull
   @Override
   public Contexts getContexts() {
      return this.contexts;
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object value) {
      if (key != null) {
         this.contexts.put(key, value);

         for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setContexts(this.contexts);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Boolean value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, Boolean> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable String value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, String> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Number value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, Number> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, Collection<?>> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object[] value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, Object[]> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Character value) {
      if (key != null) {
         if (value == null) {
            this.setContexts(key, null);
         } else {
            Map<String, Character> map = new HashMap<>();
            map.put("value", value);
            this.setContexts(key, map);
         }
      }
   }

   @Override
   public void removeContexts(@Nullable String key) {
      if (key != null) {
         this.contexts.remove(key);
      }
   }

   @Internal
   @NotNull
   @Override
   public List<Attachment> getAttachments() {
      return new CopyOnWriteArrayList<>(this.attachments);
   }

   @Override
   public void addAttachment(@NotNull Attachment attachment) {
      this.attachments.add(attachment);
   }

   @Override
   public void clearAttachments() {
      this.attachments.clear();
   }

   @NotNull
   static Queue<Breadcrumb> createBreadcrumbsList(int maxBreadcrumb) {
      return (Queue<Breadcrumb>)(maxBreadcrumb > 0 ? SynchronizedQueue.synchronizedQueue(new CircularFifoQueue<>(maxBreadcrumb)) : new DisabledQueue<>());
   }

   @Internal
   @NotNull
   @Override
   public List<EventProcessor> getEventProcessors() {
      return EventProcessorUtils.unwrap(this.eventProcessors);
   }

   @Internal
   @NotNull
   @Override
   public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
      return this.eventProcessors;
   }

   @Override
   public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
      this.eventProcessors.add(new EventProcessorAndOrder(eventProcessor, eventProcessor.getOrder()));
   }

   @Internal
   @Nullable
   @Override
   public Session withSession(@NotNull Scope.IWithSession sessionCallback) {
      Session cloneSession = null;
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      try {
         sessionCallback.accept(this.session);
         if (this.session != null) {
            cloneSession = this.session.clone();
         }
      } catch (Throwable var7) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (ignored != null) {
         ignored.close();
      }

      return cloneSession;
   }

   @Internal
   @Nullable
   @Override
   public Scope.SessionPair startSession() {
      Scope.SessionPair pair = null;
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      try {
         if (this.session != null) {
            this.session.end();
            this.options.getContinuousProfiler().reevaluateSampling();
         }

         Session previousSession = this.session;
         if (this.options.getRelease() != null) {
            this.session = new Session(this.options.getDistinctId(), this.user, this.options.getEnvironment(), this.options.getRelease());
            Session previousClone = previousSession != null ? previousSession.clone() : null;
            pair = new Scope.SessionPair(this.session.clone(), previousClone);
         } else {
            this.options.getLogger().log(SentryLevel.WARNING, "Release is not set on SentryOptions. Session could not be started");
         }
      } catch (Throwable var7) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (ignored != null) {
         ignored.close();
      }

      return pair;
   }

   @Internal
   @Nullable
   @Override
   public Session endSession() {
      Session previousSession = null;
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      try {
         if (this.session != null) {
            this.session.end();
            this.options.getContinuousProfiler().reevaluateSampling();
            previousSession = this.session.clone();
            this.session = null;
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }

      return previousSession;
   }

   @Internal
   @Override
   public void withTransaction(@NotNull Scope.IWithTransaction callback) {
      ISentryLifecycleToken ignored = this.transactionLock.acquire();

      try {
         callback.accept(this.transaction);
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @Internal
   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.options;
   }

   @Internal
   @Nullable
   @Override
   public Session getSession() {
      return this.session;
   }

   @Internal
   @Override
   public void clearSession() {
      this.session = null;
   }

   @Internal
   @Override
   public void setPropagationContext(@NotNull PropagationContext propagationContext) {
      this.propagationContext = propagationContext;
      SpanContext spanContext = propagationContext.toSpanContext();

      for (IScopeObserver observer : this.options.getScopeObservers()) {
         observer.setTrace(spanContext, this);
      }
   }

   @Internal
   @NotNull
   @Override
   public PropagationContext getPropagationContext() {
      return this.propagationContext;
   }

   @Internal
   @NotNull
   @Override
   public PropagationContext withPropagationContext(@NotNull Scope.IWithPropagationContext callback) {
      ISentryLifecycleToken ignored = this.propagationContextLock.acquire();

      PropagationContext var3;
      try {
         callback.accept(this.propagationContext);
         var3 = new PropagationContext(this.propagationContext);
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var3;
   }

   @NotNull
   @Override
   public IScope clone() {
      return new Scope(this);
   }

   @Override
   public void setLastEventId(@NotNull SentryId lastEventId) {
      this.lastEventId = lastEventId;
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return this.lastEventId;
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
      this.client = client;
   }

   @NotNull
   @Override
   public ISentryClient getClient() {
      return this.client;
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.featureFlags.add(flag, result);
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      return this.featureFlags.getFeatureFlags();
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer getFeatureFlagBuffer() {
      return this.featureFlags;
   }

   @Internal
   @Override
   public void assignTraceContext(@NotNull SentryEvent event) {
      if (this.options.isTracingEnabled() && event.getThrowable() != null) {
         Pair<WeakReference<ISpan>, String> pair = this.throwableToSpan.get(ExceptionUtils.findRootCause(event.getThrowable()));
         if (pair != null) {
            WeakReference<ISpan> spanWeakRef = pair.getFirst();
            if (event.getContexts().getTrace() == null && spanWeakRef != null) {
               ISpan span = spanWeakRef.get();
               if (span != null) {
                  event.getContexts().setTrace(span.getSpanContext());
               }
            }

            String transactionName = pair.getSecond();
            if (event.getTransaction() == null && transactionName != null) {
               event.setTransaction(transactionName);
            }
         }
      }
   }

   @Internal
   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
      Objects.requireNonNull(throwable, "throwable is required");
      Objects.requireNonNull(span, "span is required");
      Objects.requireNonNull(transactionName, "transactionName is required");
      Throwable rootCause = ExceptionUtils.findRootCause(throwable);
      if (!this.throwableToSpan.containsKey(rootCause)) {
         this.throwableToSpan.put(rootCause, new Pair<>(new WeakReference<>(span), transactionName));
      }
   }

   @Internal
   @Override
   public void replaceOptions(@NotNull SentryOptions options) {
      this.options = options;
      Queue<Breadcrumb> oldBreadcrumbs = this.breadcrumbs;
      this.breadcrumbs = createBreadcrumbsList(options.getMaxBreadcrumbs());

      for (Breadcrumb breadcrumb : oldBreadcrumbs) {
         this.addBreadcrumb(breadcrumb);
      }
   }

   @Internal
   public interface IWithPropagationContext {
      void accept(@NotNull PropagationContext var1);
   }

   interface IWithSession {
      void accept(@Nullable Session var1);
   }

   @Internal
   public interface IWithTransaction {
      void accept(@Nullable ITransaction var1);
   }

   static final class SessionPair {
      @Nullable
      private final Session previous;
      @NotNull
      private final Session current;

      public SessionPair(@NotNull Session current, @Nullable Session previous) {
         this.current = current;
         this.previous = previous;
      }

      @Nullable
      public Session getPrevious() {
         return this.previous;
      }

      @NotNull
      public Session getCurrent() {
         return this.current;
      }
   }
}
