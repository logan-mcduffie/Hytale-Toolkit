package io.sentry;

import io.sentry.protocol.Contexts;
import io.sentry.protocol.MeasurementValue;
import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Span implements ISpan {
   @NotNull
   private SentryDate startTimestamp;
   @Nullable
   private SentryDate timestamp;
   @NotNull
   private final SpanContext context;
   @NotNull
   private final SentryTracer transaction;
   @Nullable
   private Throwable throwable;
   @NotNull
   private final IScopes scopes;
   private boolean finished = false;
   @NotNull
   private final AtomicBoolean isFinishing = new AtomicBoolean(false);
   @NotNull
   private final SpanOptions options;
   @Nullable
   private SpanFinishedCallback spanFinishedCallback;
   @NotNull
   private final Map<String, Object> data = new ConcurrentHashMap<>();
   @NotNull
   private final Map<String, MeasurementValue> measurements = new ConcurrentHashMap<>();
   @NotNull
   private final Contexts contexts = new Contexts();

   Span(
      @NotNull SentryTracer transaction,
      @NotNull IScopes scopes,
      @NotNull SpanContext spanContext,
      @NotNull SpanOptions options,
      @Nullable SpanFinishedCallback spanFinishedCallback
   ) {
      this.context = spanContext;
      this.context.setOrigin(options.getOrigin());
      this.transaction = Objects.requireNonNull(transaction, "transaction is required");
      this.scopes = Objects.requireNonNull(scopes, "Scopes are required");
      this.options = options;
      this.spanFinishedCallback = spanFinishedCallback;
      SentryDate startTimestamp = options.getStartTimestamp();
      if (startTimestamp != null) {
         this.startTimestamp = startTimestamp;
      } else {
         this.startTimestamp = scopes.getOptions().getDateProvider().now();
      }
   }

   public Span(@NotNull TransactionContext context, @NotNull SentryTracer sentryTracer, @NotNull IScopes scopes, @NotNull SpanOptions options) {
      this.context = Objects.requireNonNull(context, "context is required");
      this.context.setOrigin(options.getOrigin());
      this.transaction = Objects.requireNonNull(sentryTracer, "sentryTracer is required");
      this.scopes = Objects.requireNonNull(scopes, "scopes are required");
      this.spanFinishedCallback = null;
      SentryDate startTimestamp = options.getStartTimestamp();
      if (startTimestamp != null) {
         this.startTimestamp = startTimestamp;
      } else {
         this.startTimestamp = scopes.getOptions().getDateProvider().now();
      }

      this.options = options;
   }

   @NotNull
   @Override
   public SentryDate getStartDate() {
      return this.startTimestamp;
   }

   @Nullable
   @Override
   public SentryDate getFinishDate() {
      return this.timestamp;
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation) {
      return this.startChild(operation, (String)null);
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
      return (ISpan)(this.finished
         ? NoOpSpan.getInstance()
         : this.transaction.startChild(this.context.getSpanId(), operation, description, timestamp, instrumenter, spanOptions));
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description) {
      return (ISpan)(this.finished ? NoOpSpan.getInstance() : this.transaction.startChild(this.context.getSpanId(), operation, description));
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
      return (ISpan)(this.finished ? NoOpSpan.getInstance() : this.transaction.startChild(this.context.getSpanId(), operation, description, spanOptions));
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
      return this.transaction.startChild(spanContext, spanOptions);
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
      return this.startChild(operation, description, timestamp, instrumenter, new SpanOptions());
   }

   @NotNull
   @Override
   public SentryTraceHeader toSentryTrace() {
      return new SentryTraceHeader(this.context.getTraceId(), this.context.getSpanId(), this.context.getSampled());
   }

   @Nullable
   @Override
   public TraceContext traceContext() {
      return this.transaction.traceContext();
   }

   @Nullable
   @Override
   public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
      return this.transaction.toBaggageHeader(thirdPartyBaggageHeaders);
   }

   @Override
   public void finish() {
      this.finish(this.context.getStatus());
   }

   @Override
   public void finish(@Nullable SpanStatus status) {
      this.finish(status, this.scopes.getOptions().getDateProvider().now());
   }

   @Override
   public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp) {
      if (!this.finished && this.isFinishing.compareAndSet(false, true)) {
         this.context.setStatus(status);
         this.timestamp = timestamp == null ? this.scopes.getOptions().getDateProvider().now() : timestamp;
         if (this.options.isTrimStart() || this.options.isTrimEnd()) {
            SentryDate minChildStart = null;
            SentryDate maxChildEnd = null;

            for (Span child : this.transaction.getRoot().getSpanId().equals(this.getSpanId()) ? this.transaction.getChildren() : this.getDirectChildren()) {
               if (minChildStart == null || child.getStartDate().isBefore(minChildStart)) {
                  minChildStart = child.getStartDate();
               }

               if (maxChildEnd == null || child.getFinishDate() != null && child.getFinishDate().isAfter(maxChildEnd)) {
                  maxChildEnd = child.getFinishDate();
               }
            }

            if (this.options.isTrimStart() && minChildStart != null && this.startTimestamp.isBefore(minChildStart)) {
               this.updateStartDate(minChildStart);
            }

            if (this.options.isTrimEnd() && maxChildEnd != null && (this.timestamp == null || this.timestamp.isAfter(maxChildEnd))) {
               this.updateEndDate(maxChildEnd);
            }
         }

         if (this.throwable != null) {
            this.scopes.setSpanContext(this.throwable, this, this.transaction.getName());
         }

         if (this.spanFinishedCallback != null) {
            this.spanFinishedCallback.execute(this);
         }

         this.finished = true;
      }
   }

   @Override
   public void setOperation(@NotNull String operation) {
      this.context.setOperation(operation);
   }

   @NotNull
   @Override
   public String getOperation() {
      return this.context.getOperation();
   }

   @Override
   public void setDescription(@Nullable String description) {
      this.context.setDescription(description);
   }

   @Nullable
   @Override
   public String getDescription() {
      return this.context.getDescription();
   }

   @Override
   public void setStatus(@Nullable SpanStatus status) {
      this.context.setStatus(status);
   }

   @Nullable
   @Override
   public SpanStatus getStatus() {
      return this.context.getStatus();
   }

   @NotNull
   @Override
   public SpanContext getSpanContext() {
      return this.context;
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      this.context.setTag(key, value);
   }

   @Nullable
   @Override
   public String getTag(@Nullable String key) {
      return key == null ? null : this.context.getTags().get(key);
   }

   @Override
   public boolean isFinished() {
      return this.finished;
   }

   @NotNull
   public Map<String, Object> getData() {
      return this.data;
   }

   @Nullable
   @Override
   public Boolean isSampled() {
      return this.context.getSampled();
   }

   @Nullable
   public Boolean isProfileSampled() {
      return this.context.getProfileSampled();
   }

   @Nullable
   @Override
   public TracesSamplingDecision getSamplingDecision() {
      return this.context.getSamplingDecision();
   }

   @Override
   public void setThrowable(@Nullable Throwable throwable) {
      this.throwable = throwable;
   }

   @Nullable
   @Override
   public Throwable getThrowable() {
      return this.throwable;
   }

   @NotNull
   public SentryId getTraceId() {
      return this.context.getTraceId();
   }

   @NotNull
   public SpanId getSpanId() {
      return this.context.getSpanId();
   }

   @Nullable
   public SpanId getParentSpanId() {
      return this.context.getParentSpanId();
   }

   public Map<String, String> getTags() {
      return this.context.getTags();
   }

   @Override
   public void setData(@Nullable String key, @Nullable Object value) {
      if (key != null) {
         if (value == null) {
            this.data.remove(key);
         } else {
            this.data.put(key, value);
         }
      }
   }

   @Nullable
   @Override
   public Object getData(@Nullable String key) {
      return key == null ? null : this.data.get(key);
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value) {
      if (this.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", name);
      } else {
         this.measurements.put(name, new MeasurementValue(value, null));
         if (this.transaction.getRoot() != this) {
            this.transaction.setMeasurementFromChild(name, value);
         }
      }
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
      if (this.isFinished()) {
         this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", name);
      } else {
         this.measurements.put(name, new MeasurementValue(value, unit.apiName()));
         if (this.transaction.getRoot() != this) {
            this.transaction.setMeasurementFromChild(name, value, unit);
         }
      }
   }

   @NotNull
   public Map<String, MeasurementValue> getMeasurements() {
      return this.measurements;
   }

   @Override
   public boolean updateEndDate(@NotNull SentryDate date) {
      if (this.timestamp != null) {
         this.timestamp = date;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isNoOp() {
      return false;
   }

   @Override
   public void setContext(@Nullable String key, @Nullable Object context) {
      this.contexts.put(key, context);
   }

   @NotNull
   @Override
   public Contexts getContexts() {
      return this.contexts;
   }

   void setSpanFinishedCallback(@Nullable SpanFinishedCallback callback) {
      this.spanFinishedCallback = callback;
   }

   @Nullable
   SpanFinishedCallback getSpanFinishedCallback() {
      return this.spanFinishedCallback;
   }

   private void updateStartDate(@NotNull SentryDate date) {
      this.startTimestamp = date;
   }

   @NotNull
   SpanOptions getOptions() {
      return this.options;
   }

   @NotNull
   private List<Span> getDirectChildren() {
      List<Span> children = new ArrayList<>();

      for (Span span : this.transaction.getSpans()) {
         if (span.getParentSpanId() != null && span.getParentSpanId().equals(this.getSpanId())) {
            children.add(span);
         }
      }

      return children;
   }

   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      return NoOpScopesLifecycleToken.getInstance();
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.context.addFeatureFlag(flag, result);
   }
}
