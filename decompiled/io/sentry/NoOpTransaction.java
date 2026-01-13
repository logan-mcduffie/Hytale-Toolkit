package io.sentry;

import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class NoOpTransaction implements ITransaction {
   private static final NoOpTransaction instance = new NoOpTransaction();

   private NoOpTransaction() {
   }

   public static NoOpTransaction getInstance() {
      return instance;
   }

   @Override
   public void setName(@NotNull String name) {
   }

   @Internal
   @Override
   public void setName(@NotNull String name, @NotNull TransactionNameSource transactionNameSource) {
   }

   @NotNull
   @Override
   public String getName() {
      return "";
   }

   @NotNull
   @Override
   public TransactionNameSource getTransactionNameSource() {
      return TransactionNameSource.CUSTOM;
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation) {
      return NoOpSpan.getInstance();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
      return NoOpSpan.getInstance();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
      return NoOpSpan.getInstance();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
      return NoOpSpan.getInstance();
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
      return NoOpSpan.getInstance();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description) {
      return NoOpSpan.getInstance();
   }

   @Nullable
   @Override
   public String getDescription() {
      return null;
   }

   @NotNull
   @Override
   public List<Span> getSpans() {
      return Collections.emptyList();
   }

   @NotNull
   @Override
   public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp) {
      return NoOpSpan.getInstance();
   }

   @Nullable
   @Override
   public ISpan getLatestActiveSpan() {
      return null;
   }

   @NotNull
   @Override
   public SentryId getEventId() {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      return NoOpScopesLifecycleToken.getInstance();
   }

   @Override
   public void scheduleFinish() {
   }

   @Override
   public void forceFinish(@NotNull SpanStatus status, boolean dropIfNoChildren, @Nullable Hint hint) {
   }

   @Override
   public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp, boolean dropIfNoChildren, @Nullable Hint hint) {
   }

   @Override
   public boolean isFinished() {
      return true;
   }

   @NotNull
   @Override
   public SentryTraceHeader toSentryTrace() {
      return new SentryTraceHeader(SentryId.EMPTY_ID, SpanId.EMPTY_ID, false);
   }

   @NotNull
   @Override
   public TraceContext traceContext() {
      return new TraceContext(SentryId.EMPTY_ID, "");
   }

   @Nullable
   @Override
   public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
      return null;
   }

   @Override
   public void finish() {
   }

   @Override
   public void finish(@Nullable SpanStatus status) {
   }

   @Override
   public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp) {
   }

   @Override
   public void setOperation(@NotNull String operation) {
   }

   @NotNull
   @Override
   public String getOperation() {
      return "";
   }

   @Override
   public void setDescription(@Nullable String description) {
   }

   @Override
   public void setStatus(@Nullable SpanStatus status) {
   }

   @Nullable
   @Override
   public SpanStatus getStatus() {
      return null;
   }

   @Override
   public void setThrowable(@Nullable Throwable throwable) {
   }

   @Nullable
   @Override
   public Throwable getThrowable() {
      return null;
   }

   @NotNull
   @Override
   public SpanContext getSpanContext() {
      return new SpanContext(SentryId.EMPTY_ID, SpanId.EMPTY_ID, "op", null, null);
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
   }

   @Nullable
   @Override
   public String getTag(@Nullable String key) {
      return null;
   }

   @Nullable
   @Override
   public Boolean isSampled() {
      return null;
   }

   @Nullable
   @Override
   public Boolean isProfileSampled() {
      return null;
   }

   @Nullable
   @Override
   public TracesSamplingDecision getSamplingDecision() {
      return null;
   }

   @Override
   public void setData(@Nullable String key, @Nullable Object value) {
   }

   @Nullable
   @Override
   public Object getData(@Nullable String key) {
      return null;
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value) {
   }

   @Override
   public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
   }

   @Internal
   @Override
   public void setContext(@Nullable String key, @Nullable Object context) {
   }

   @Internal
   @NotNull
   @Override
   public Contexts getContexts() {
      return new Contexts();
   }

   @Override
   public boolean updateEndDate(@NotNull SentryDate date) {
      return false;
   }

   @NotNull
   @Override
   public SentryDate getStartDate() {
      return new SentryNanotimeDate();
   }

   @NotNull
   @Override
   public SentryDate getFinishDate() {
      return new SentryNanotimeDate();
   }

   @Override
   public boolean isNoOp() {
      return true;
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
   }
}
