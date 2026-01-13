package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.Objects;
import io.sentry.util.TracingUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TransactionContext extends SpanContext {
   @NotNull
   public static final String DEFAULT_TRANSACTION_NAME = "<unlabeled transaction>";
   @NotNull
   private static final TransactionNameSource DEFAULT_NAME_SOURCE = TransactionNameSource.CUSTOM;
   @NotNull
   private static final String DEFAULT_OPERATION = "default";
   @NotNull
   private String name;
   @NotNull
   private TransactionNameSource transactionNameSource;
   @Nullable
   private TracesSamplingDecision parentSamplingDecision;
   private boolean isForNextAppStart = false;

   @Internal
   public static TransactionContext fromPropagationContext(@NotNull PropagationContext propagationContext) {
      Boolean parentSampled = propagationContext.isSampled();
      Baggage baggage = propagationContext.getBaggage();
      Double sampleRate = baggage.getSampleRate();
      TracesSamplingDecision samplingDecision = parentSampled == null
         ? null
         : new TracesSamplingDecision(parentSampled, sampleRate, propagationContext.getSampleRand());
      return new TransactionContext(
         propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.getParentSpanId(), samplingDecision, baggage
      );
   }

   public TransactionContext(@NotNull String name, @NotNull String operation) {
      this(name, operation, null);
   }

   @Internal
   public TransactionContext(@NotNull String name, @NotNull TransactionNameSource transactionNameSource, @NotNull String operation) {
      this(name, transactionNameSource, operation, null);
   }

   public TransactionContext(@NotNull String name, @NotNull String operation, @Nullable TracesSamplingDecision samplingDecision) {
      this(name, TransactionNameSource.CUSTOM, operation, samplingDecision);
   }

   @Internal
   public TransactionContext(
      @NotNull String name, @NotNull TransactionNameSource transactionNameSource, @NotNull String operation, @Nullable TracesSamplingDecision samplingDecision
   ) {
      super(operation);
      this.name = Objects.requireNonNull(name, "name is required");
      this.transactionNameSource = transactionNameSource;
      this.setSamplingDecision(samplingDecision);
      this.baggage = TracingUtils.ensureBaggage(null, samplingDecision);
   }

   @Internal
   public TransactionContext(
      @NotNull SentryId traceId,
      @NotNull SpanId spanId,
      @Nullable SpanId parentSpanId,
      @Nullable TracesSamplingDecision parentSamplingDecision,
      @Nullable Baggage baggage
   ) {
      super(traceId, spanId, "default", parentSpanId, null);
      this.name = "<unlabeled transaction>";
      this.parentSamplingDecision = parentSamplingDecision;
      this.transactionNameSource = DEFAULT_NAME_SOURCE;
      this.baggage = TracingUtils.ensureBaggage(baggage, parentSamplingDecision);
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @Nullable
   public Boolean getParentSampled() {
      return this.parentSamplingDecision == null ? null : this.parentSamplingDecision.getSampled();
   }

   @Nullable
   public TracesSamplingDecision getParentSamplingDecision() {
      return this.parentSamplingDecision;
   }

   public void setParentSampled(@Nullable Boolean parentSampled) {
      if (parentSampled == null) {
         this.parentSamplingDecision = null;
      } else {
         this.parentSamplingDecision = new TracesSamplingDecision(parentSampled);
      }
   }

   public void setParentSampled(@Nullable Boolean parentSampled, @Nullable Boolean parentProfileSampled) {
      if (parentSampled == null) {
         this.parentSamplingDecision = null;
      } else if (parentProfileSampled == null) {
         this.parentSamplingDecision = new TracesSamplingDecision(parentSampled);
      } else {
         this.parentSamplingDecision = new TracesSamplingDecision(parentSampled, null, parentProfileSampled, null);
      }
   }

   @NotNull
   public TransactionNameSource getTransactionNameSource() {
      return this.transactionNameSource;
   }

   public void setName(@NotNull String name) {
      this.name = Objects.requireNonNull(name, "name is required");
   }

   public void setTransactionNameSource(@NotNull TransactionNameSource transactionNameSource) {
      this.transactionNameSource = transactionNameSource;
   }

   @Internal
   public void setForNextAppStart(boolean forNextAppStart) {
      this.isForNextAppStart = forNextAppStart;
   }

   public boolean isForNextAppStart() {
      return this.isForNextAppStart;
   }
}
