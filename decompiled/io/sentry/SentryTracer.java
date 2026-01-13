package io.sentry;

import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.util.SpanUtils;
import io.sentry.util.thread.IThreadChecker;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryTracer implements ITransaction {
   @NotNull
   private final SentryId eventId = new SentryId();
   @NotNull
   private final Span root;
   @NotNull
   private final List<Span> children = new CopyOnWriteArrayList<>();
   @NotNull
   private final IScopes scopes;
   @NotNull
   private String name;
   @NotNull
   private SentryTracer.FinishStatus finishStatus = SentryTracer.FinishStatus.NOT_FINISHED;
   @Nullable
   private volatile TimerTask idleTimeoutTask;
   @Nullable
   private volatile TimerTask deadlineTimeoutTask;
   @Nullable
   private volatile Timer timer = null;
   @NotNull
   private final AutoClosableReentrantLock timerLock = new AutoClosableReentrantLock();
   @NotNull
   private final AutoClosableReentrantLock tracerLock = new AutoClosableReentrantLock();
   @NotNull
   private final AtomicBoolean isIdleFinishTimerRunning = new AtomicBoolean(false);
   @NotNull
   private final AtomicBoolean isDeadlineTimerRunning = new AtomicBoolean(false);
   @NotNull
   private TransactionNameSource transactionNameSource;
   @NotNull
   private final Instrumenter instrumenter;
   @NotNull
   private final Contexts contexts = new Contexts();
   @Nullable
   private final CompositePerformanceCollector compositePerformanceCollector;
   @NotNull
   private final TransactionOptions transactionOptions;

   public SentryTracer(@NotNull TransactionContext context, @NotNull IScopes scopes) {
      this(context, scopes, new TransactionOptions(), null);
   }

   public SentryTracer(@NotNull TransactionContext context, @NotNull IScopes scopes, @NotNull TransactionOptions transactionOptions) {
      this(context, scopes, transactionOptions, null);
   }

   SentryTracer(
      @NotNull TransactionContext context,
      @NotNull IScopes scopes,
      @NotNull TransactionOptions transactionOptions,
      @Nullable CompositePerformanceCollector compositePerformanceCollector
   ) {
      Objects.requireNonNull(context, "context is required");
      Objects.requireNonNull(scopes, "scopes are required");
      this.root = new Span(context, this, scopes, transactionOptions);
      this.name = context.getName();
      this.instrumenter = context.getInstrumenter();
      this.scopes = scopes;
      this.compositePerformanceCollector = Boolean.TRUE.equals(this.isSampled()) ? compositePerformanceCollector : null;
      this.transactionNameSource = context.getTransactionNameSource();
      this.transactionOptions = transactionOptions;
      this.setDefaultSpanData(this.root);
      SentryId continuousProfilerId = this.getProfilerId();
      if (!continuousProfilerId.equals(SentryId.EMPTY_ID) && Boolean.TRUE.equals(this.isSampled())) {
         this.contexts.setProfile(new ProfileContext(continuousProfilerId));
      }

      if (this.compositePerformanceCollector != null) {
         this.compositePerformanceCollector.start(this);
      }

      if (transactionOptions.getIdleTimeout() != null || transactionOptions.getDeadlineTimeout() != null) {
         this.timer = new Timer(true);
         this.scheduleDeadlineTimeout();
         this.scheduleFinish();
      }
   }

   @Override
   public void scheduleFinish() {
      ISentryLifecycleToken ignored = this.timerLock.acquire();

      try {
         if (this.timer != null) {
            Long idleTimeout = this.transactionOptions.getIdleTimeout();
            if (idleTimeout != null) {
               this.cancelIdleTimer();
               this.isIdleFinishTimerRunning.set(true);
               this.idleTimeoutTask = new TimerTask() {
                  @Override
                  public void run() {
                     SentryTracer.this.onIdleTimeoutReached();
                  }
               };

               try {
                  this.timer.schedule(this.idleTimeoutTask, idleTimeout);
               } catch (Throwable var5) {
                  this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", var5);
                  this.onIdleTimeoutReached();
               }
            }
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var6.addSuppressed(var4);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   private void onIdleTimeoutReached() {
      SpanStatus status = this.getStatus();
      this.finish(status != null ? status : SpanStatus.OK);
      this.isIdleFinishTimerRunning.set(false);
   }

   private void onDeadlineTimeoutReached() {
      SpanStatus status = this.getStatus();
      this.forceFinish(status != null ? status : SpanStatus.DEADLINE_EXCEEDED, this.transactionOptions.getIdleTimeout() != null, null);
      this.isDeadlineTimerRunning.set(false);
   }

   @NotNull
   @Override
   public void forceFinish(@NotNull SpanStatus status, boolean dropIfNoChildren, @Nullable Hint hint) {
      if (!this.isFinished()) {
         SentryDate finishTimestamp = this.scopes.getOptions().getDateProvider().now();
         ListIterator<Span> iterator = CollectionUtils.reverseListIterator((CopyOnWriteArrayList<Span>)this.children);

         while (iterator.hasPrevious()) {
            Span span = iterator.previous();
            span.setSpanFinishedCallback(null);
            span.finish(status, finishTimestamp);
         }

         this.finish(status, finishTimestamp, dropIfNoChildren, hint);
      }
   }

   @Override
   public void finish(@Nullable SpanStatus status, @Nullable SentryDate finishDate, boolean dropIfNoChildren, @Nullable Hint hint) {
      SentryDate finishTimestamp = this.root.getFinishDate();
      if (finishDate != null) {
         finishTimestamp = finishDate;
      }

      if (finishTimestamp == null) {
         finishTimestamp = this.scopes.getOptions().getDateProvider().now();
      }

      for (Span span : this.children) {
         if (span.getOptions().isIdle()) {
            span.finish(status != null ? status : this.getSpanContext().status, finishTimestamp);
         }
      }

      this.finishStatus = SentryTracer.FinishStatus.finishing(status);
      if (!this.root.isFinished() && (!this.transactionOptions.isWaitForChildren() || this.hasAllChildrenFinished())) {
         AtomicReference<List<PerformanceCollectionData>> performanceCollectionData = new AtomicReference<>();
         SpanFinishedCallback oldCallback = this.root.getSpanFinishedCallback();
         this.root.setSpanFinishedCallback(spanx -> {
            if (oldCallback != null) {
               oldCallback.execute(spanx);
            }

            TransactionFinishedCallback finishedCallback = this.transactionOptions.getTransactionFinishedCallback();
            if (finishedCallback != null) {
               finishedCallback.execute(this);
            }

            if (this.compositePerformanceCollector != null) {
               performanceCollectionData.set(this.compositePerformanceCollector.stop(this));
            }
         });
         this.root.finish(this.finishStatus.spanStatus, finishTimestamp);
         ProfilingTraceData profilingTraceData = null;
         if (Boolean.TRUE.equals(this.isSampled()) && Boolean.TRUE.equals(this.isProfileSampled())) {
            profilingTraceData = this.scopes
               .getOptions()
               .getTransactionProfiler()
               .onTransactionFinish(this, performanceCollectionData.get(), this.scopes.getOptions());
         }

         if (this.scopes.getOptions().isContinuousProfilingEnabled()
            && this.scopes.getOptions().getProfileLifecycle() == ProfileLifecycle.TRACE
            && this.root.getSpanContext().getProfilerId().equals(SentryId.EMPTY_ID)) {
            this.scopes.getOptions().getContinuousProfiler().stopProfiler(ProfileLifecycle.TRACE);
         }

         if (performanceCollectionData.get() != null) {
            performanceCollectionData.get().clear();
         }

         this.scopes.configureScope(scope -> scope.withTransaction(transactionx -> {
            if (transactionx == this) {
               scope.clearTransaction();
            }
         }));
         SentryTransaction transaction = new SentryTransaction(this);
         if (this.timer != null) {
            ISentryLifecycleToken ignored = this.timerLock.acquire();

            try {
               if (this.timer != null) {
                  this.cancelIdleTimer();
                  this.cancelDeadlineTimer();
                  this.timer.cancel();
                  this.timer = null;
               }
            } catch (Throwable var14) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         if (dropIfNoChildren && this.children.isEmpty() && this.transactionOptions.getIdleTimeout() != null) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "Dropping idle transaction %s because it has no child spans", this.name);
            return;
         }

         transaction.getMeasurements().putAll(this.root.getMeasurements());
         this.scopes.captureTransaction(transaction, this.traceContext(), hint, profilingTraceData);
      }
   }

   private void cancelIdleTimer() {
      ISentryLifecycleToken ignored = this.timerLock.acquire();

      try {
         if (this.idleTimeoutTask != null) {
            this.idleTimeoutTask.cancel();
            this.isIdleFinishTimerRunning.set(false);
            this.idleTimeoutTask = null;
         }
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
   }

   private void scheduleDeadlineTimeout() {
      Long deadlineTimeOut = this.transactionOptions.getDeadlineTimeout();
      if (deadlineTimeOut != null) {
         ISentryLifecycleToken ignored = this.timerLock.acquire();

         try {
            if (this.timer != null) {
               this.cancelDeadlineTimer();
               this.isDeadlineTimerRunning.set(true);
               this.deadlineTimeoutTask = new TimerTask() {
                  @Override
                  public void run() {
                     SentryTracer.this.onDeadlineTimeoutReached();
                  }
               };

               try {
                  this.timer.schedule(this.deadlineTimeoutTask, deadlineTimeOut);
               } catch (Throwable var6) {
                  this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", var6);
                  this.onDeadlineTimeoutReached();
               }
            }
         } catch (Throwable var7) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var5) {
                  var7.addSuppressed(var5);
               }
            }

            throw var7;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   private void cancelDeadlineTimer() {
      ISentryLifecycleToken ignored = this.timerLock.acquire();

      try {
         if (this.deadlineTimeoutTask != null) {
            this.deadlineTimeoutTask.cancel();
            this.isDeadlineTimerRunning.set(false);
            this.deadlineTimeoutTask = null;
         }
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
   }

   @NotNull
   public List<Span> getChildren() {
      return this.children;
   }

   @NotNull
   @Override
   public SentryDate getStartDate() {
      return this.root.getStartDate();
   }

   @Nullable
   @Override
   public SentryDate getFinishDate() {
      return this.root.getFinishDate();
   }

   @NotNull
   ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description) {
      return this.startChild(parentSpanId, operation, description, new SpanOptions());
   }

   @NotNull
   ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
      return this.createChild(parentSpanId, operation, description, spanOptions);
   }

   @NotNull
   ISpan startChild(
      @NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter
   ) {
      SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
      spanContext.setDescription(description);
      spanContext.setInstrumenter(instrumenter);
      SpanOptions spanOptions = new SpanOptions();
      spanOptions.setStartTimestamp(timestamp);
      return this.createChild(spanContext, spanOptions);
   }

   @NotNull
   ISpan startChild(
      @NotNull SpanId parentSpanId,
      @NotNull String operation,
      @Nullable String description,
      @Nullable SentryDate timestamp,
      @NotNull Instrumenter instrumenter,
      @NotNull SpanOptions spanOptions
   ) {
      SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
      spanContext.setDescription(description);
      spanContext.setInstrumenter(instrumenter);
      spanOptions.setStartTimestamp(timestamp);
      return this.createChild(spanContext, spanOptions);
   }

   @NotNull
   private ISpan createChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @NotNull SpanOptions options) {
      SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
      spanContext.setDescription(description);
      spanContext.setInstrumenter(Instrumenter.SENTRY);
      return this.createChild(spanContext, options);
   }

   @NotNull
   private ISpan createChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
      if (this.root.isFinished()) {
         return NoOpSpan.getInstance();
      } else if (!this.instrumenter.equals(spanContext.getInstrumenter())) {
         return NoOpSpan.getInstance();
      } else if (SpanUtils.isIgnored(this.scopes.getOptions().getIgnoredSpanOrigins(), spanOptions.getOrigin())) {
         return NoOpSpan.getInstance();
      } else {
         SpanId parentSpanId = spanContext.getParentSpanId();
         String operation = spanContext.getOperation();
         String description = spanContext.getDescription();
         if (this.children.size() < this.scopes.getOptions().getMaxSpans()) {
            Objects.requireNonNull(parentSpanId, "parentSpanId is required");
            Objects.requireNonNull(operation, "operation is required");
            this.cancelIdleTimer();
            Span span = new Span(this, this.scopes, spanContext, spanOptions, finishingSpan -> {
               if (this.compositePerformanceCollector != null) {
                  this.compositePerformanceCollector.onSpanFinished(finishingSpan);
               }

               SentryTracer.FinishStatus finishStatus = this.finishStatus;
               if (this.transactionOptions.getIdleTimeout() != null) {
                  if (!this.transactionOptions.isWaitForChildren() || this.hasAllChildrenFinished()) {
                     this.scheduleFinish();
                  }
               } else if (finishStatus.isFinishing) {
                  this.finish(finishStatus.spanStatus);
               }
            });
            this.setDefaultSpanData(span);
            this.children.add(span);
            if (this.compositePerformanceCollector != null) {
               this.compositePerformanceCollector.onSpanStarted(span);
            }

            return span;
         } else {
            this.scopes
               .getOptions()
               .getLogger()
               .log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", operation, description);
            return NoOpSpan.getInstance();
         }
      }
   }

   private void setDefaultSpanData(@NotNull ISpan span) {
      IThreadChecker threadChecker = this.scopes.getOptions().getThreadChecker();
      SentryId profilerId = this.getProfilerId();
      if (!profilerId.equals(SentryId.EMPTY_ID) && Boolean.TRUE.equals(span.isSampled())) {
         span.setData("profiler_id", profilerId.toString());
      }

      span.setData("thread.id", String.valueOf(threadChecker.currentThreadSystemId()));
      span.setData("thread.name", threadChecker.getCurrentThreadName());
   }

   @NotNull
   private SentryId getProfilerId() {
      return !this.root.getSpanContext().getProfilerId().equals(SentryId.EMPTY_ID)
         ? this.root.getSpanContext().getProfilerId()
         : this.scopes.getOptions().getContinuousProfiler().getProfilerId();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation) {
      return this.startChild(operation, (String)null);
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
      return this.startChild(operation, description, timestamp, instrumenter, new SpanOptions());
   }

   @NotNull
   @Override
   public ISpan startChild(
      @NotNull String operation,
      @Nullable String description,
      @Nullable SentryDate timestamp,
      @NotNull Instrumenter instrumenter,
      @NotNull SpanOptions spanOptions
   ) {
      return this.createChild(operation, description, timestamp, instrumenter, spanOptions);
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp) {
      return this.createChild(operation, description, timestamp, Instrumenter.SENTRY, new SpanOptions());
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description) {
      return this.startChild(operation, description, null, Instrumenter.SENTRY, new SpanOptions());
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
      return this.createChild(operation, description, null, Instrumenter.SENTRY, spanOptions);
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
      return this.createChild(spanContext, spanOptions);
   }

   @NotNull
   private ISpan createChild(
      @NotNull String operation,
      @Nullable String description,
      @Nullable SentryDate timestamp,
      @NotNull Instrumenter instrumenter,
      @NotNull SpanOptions spanOptions
   ) {
      if (this.root.isFinished()) {
         return NoOpSpan.getInstance();
      } else if (!this.instrumenter.equals(instrumenter)) {
         return NoOpSpan.getInstance();
      } else if (this.children.size() < this.scopes.getOptions().getMaxSpans()) {
         return this.root.startChild(operation, description, timestamp, instrumenter, spanOptions);
      } else {
         this.scopes
            .getOptions()
            .getLogger()
            .log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", operation, description);
         return NoOpSpan.getInstance();
      }
   }

   @NotNull
   @Override
   public SentryTraceHeader toSentryTrace() {
      return this.root.toSentryTrace();
   }

   @Override
   public void finish() {
      this.finish(this.getStatus());
   }

   @Override
   public void finish(@Nullable SpanStatus status) {
      this.finish(status, null);
   }

   @Internal
   @Override
   public void finish(@Nullable SpanStatus status, @Nullable SentryDate finishDate) {
      this.finish(status, finishDate, true, null);
   }

   @Nullable
   @Override
   public TraceContext traceContext() {
      if (this.scopes.getOptions().isTraceSampling()) {
         Baggage baggage = this.getSpanContext().getBaggage();
         if (baggage != null) {
            this.updateBaggageValues(baggage);
            return baggage.toTraceContext();
         }
      }

      return null;
   }

   private void updateBaggageValues(@NotNull Baggage baggage) {
      ISentryLifecycleToken ignored = this.tracerLock.acquire();

      try {
         if (baggage.isMutable()) {
            AtomicReference<SentryId> replayId = new AtomicReference<>();
            this.scopes.configureScope(scope -> replayId.set(scope.getReplayId()));
            baggage.setValuesFromTransaction(
               this.getSpanContext().getTraceId(),
               replayId.get(),
               this.scopes.getOptions(),
               this.getSamplingDecision(),
               this.getName(),
               this.getTransactionNameSource()
            );
            baggage.freeze();
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
   public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
      if (this.scopes.getOptions().isTraceSampling()) {
         Baggage baggage = this.getSpanContext().getBaggage();
         if (baggage != null) {
            this.updateBaggageValues(baggage);
            return BaggageHeader.fromBaggageAndOutgoingHeader(baggage, thirdPartyBaggageHeaders);
         }
      }

      return null;
   }

   private boolean hasAllChildrenFinished() {
      for (Span span : this.children) {
         if (!span.isFinished() && span.getFinishDate() == null) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void setOperation(@NotNull String operation) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Operation %s cannot be set", operation);
      } else {
         this.root.setOperation(operation);
      }
   }

   @NotNull
   @Override
   public String getOperation() {
      return this.root.getOperation();
   }

   @Override
   public void setDescription(@Nullable String description) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Description %s cannot be set", description);
      } else {
         this.root.setDescription(description);
      }
   }

   @Nullable
   @Override
   public String getDescription() {
      return this.root.getDescription();
   }

   @Override
   public void setStatus(@Nullable SpanStatus status) {
      if (this.root.isFinished()) {
         this.scopes
            .getOptions()
            .getLogger()
            .log(SentryLevel.DEBUG, "The transaction is already finished. Status %s cannot be set", status == null ? "null" : status.name());
      } else {
         this.root.setStatus(status);
      }
   }

   @Nullable
   @Override
   public SpanStatus getStatus() {
      return this.root.getStatus();
   }

   @Override
   public void setThrowable(@Nullable Throwable throwable) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Throwable cannot be set");
      } else {
         this.root.setThrowable(throwable);
      }
   }

   @Nullable
   @Override
   public Throwable getThrowable() {
      return this.root.getThrowable();
   }

   @NotNull
   @Override
   public SpanContext getSpanContext() {
      return this.root.getSpanContext();
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Tag %s cannot be set", key);
      } else {
         this.root.setTag(key, value);
      }
   }

   @Nullable
   @Override
   public String getTag(@Nullable String key) {
      return this.root.getTag(key);
   }

   @Override
   public boolean isFinished() {
      return this.root.isFinished();
   }

   @Override
   public void setData(@Nullable String key, @Nullable Object value) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Data %s cannot be set", key);
      } else {
         this.root.setData(key, value);
      }
   }

   @Nullable
   @Override
   public Object getData(@Nullable String key) {
      return this.root.getData(key);
   }

   @Internal
   public void setMeasurementFromChild(@NotNull String name, @NotNull Number value) {
      if (!this.root.getMeasurements().containsKey(name)) {
         this.setMeasurement(name, value);
      }
   }

   @Internal
   public void setMeasurementFromChild(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
      if (!this.root.getMeasurements().containsKey(name)) {
         this.setMeasurement(name, value, unit);
      }
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value) {
      this.root.setMeasurement(name, value);
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
      this.root.setMeasurement(name, value, unit);
   }

   @Nullable
   public Map<String, Object> getData() {
      return this.root.getData();
   }

   @Nullable
   @Override
   public Boolean isSampled() {
      return this.root.isSampled();
   }

   @Nullable
   @Override
   public Boolean isProfileSampled() {
      return this.root.isProfileSampled();
   }

   @Nullable
   @Override
   public TracesSamplingDecision getSamplingDecision() {
      return this.root.getSamplingDecision();
   }

   @Override
   public void setName(@NotNull String name) {
      this.setName(name, TransactionNameSource.CUSTOM);
   }

   @Internal
   @Override
   public void setName(@NotNull String name, @NotNull TransactionNameSource transactionNameSource) {
      if (this.root.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Name %s cannot be set", name);
      } else {
         this.name = name;
         this.transactionNameSource = transactionNameSource;
      }
   }

   @NotNull
   @Override
   public String getName() {
      return this.name;
   }

   @NotNull
   @Override
   public TransactionNameSource getTransactionNameSource() {
      return this.transactionNameSource;
   }

   @NotNull
   @Override
   public List<Span> getSpans() {
      return this.children;
   }

   @Nullable
   @Override
   public ISpan getLatestActiveSpan() {
      ListIterator<Span> iterator = CollectionUtils.reverseListIterator((CopyOnWriteArrayList<Span>)this.children);

      while (iterator.hasPrevious()) {
         Span span = iterator.previous();
         if (!span.isFinished()) {
            return span;
         }
      }

      return null;
   }

   @NotNull
   @Override
   public SentryId getEventId() {
      return this.eventId;
   }

   @Internal
   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      this.scopes.configureScope(scope -> scope.setTransaction(this));
      return NoOpScopesLifecycleToken.getInstance();
   }

   @NotNull
   Span getRoot() {
      return this.root;
   }

   @TestOnly
   @Nullable
   TimerTask getIdleTimeoutTask() {
      return this.idleTimeoutTask;
   }

   @TestOnly
   @Nullable
   TimerTask getDeadlineTimeoutTask() {
      return this.deadlineTimeoutTask;
   }

   @TestOnly
   @Nullable
   Timer getTimer() {
      return this.timer;
   }

   @TestOnly
   @NotNull
   AtomicBoolean isFinishTimerRunning() {
      return this.isIdleFinishTimerRunning;
   }

   @TestOnly
   @NotNull
   AtomicBoolean isDeadlineTimerRunning() {
      return this.isDeadlineTimerRunning;
   }

   @Internal
   @Override
   public void setContext(@Nullable String key, @Nullable Object context) {
      this.contexts.put(key, context);
   }

   @Internal
   @NotNull
   @Override
   public Contexts getContexts() {
      return this.contexts;
   }

   @Override
   public boolean updateEndDate(@NotNull SentryDate date) {
      return this.root.updateEndDate(date);
   }

   @Override
   public boolean isNoOp() {
      return false;
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.root.addFeatureFlag(flag, result);
   }

   private static final class FinishStatus {
      static final SentryTracer.FinishStatus NOT_FINISHED = notFinished();
      private final boolean isFinishing;
      @Nullable
      private final SpanStatus spanStatus;

      @NotNull
      static SentryTracer.FinishStatus finishing(@Nullable SpanStatus finishStatus) {
         return new SentryTracer.FinishStatus(true, finishStatus);
      }

      @NotNull
      private static SentryTracer.FinishStatus notFinished() {
         return new SentryTracer.FinishStatus(false, null);
      }

      private FinishStatus(boolean isFinishing, @Nullable SpanStatus spanStatus) {
         this.isFinishing = isFinishing;
         this.spanStatus = spanStatus;
      }
   }
}
