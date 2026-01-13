package io.sentry;

import io.sentry.protocol.Contexts;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ISpan {
   @NotNull
   ISpan startChild(@NotNull String var1);

   @Internal
   @NotNull
   ISpan startChild(@NotNull String var1, @Nullable String var2, @NotNull SpanOptions var3);

   @Internal
   @NotNull
   ISpan startChild(@NotNull SpanContext var1, @NotNull SpanOptions var2);

   @Internal
   @NotNull
   ISpan startChild(@NotNull String var1, @Nullable String var2, @Nullable SentryDate var3, @NotNull Instrumenter var4);

   @Internal
   @NotNull
   ISpan startChild(@NotNull String var1, @Nullable String var2, @Nullable SentryDate var3, @NotNull Instrumenter var4, @NotNull SpanOptions var5);

   @NotNull
   ISpan startChild(@NotNull String var1, @Nullable String var2);

   @NotNull
   SentryTraceHeader toSentryTrace();

   @Nullable
   @Experimental
   TraceContext traceContext();

   @Nullable
   @Experimental
   BaggageHeader toBaggageHeader(@Nullable List<String> var1);

   void finish();

   void finish(@Nullable SpanStatus var1);

   void finish(@Nullable SpanStatus var1, @Nullable SentryDate var2);

   void setOperation(@NotNull String var1);

   @NotNull
   String getOperation();

   void setDescription(@Nullable String var1);

   @Nullable
   String getDescription();

   void setStatus(@Nullable SpanStatus var1);

   @Nullable
   SpanStatus getStatus();

   void setThrowable(@Nullable Throwable var1);

   @Nullable
   Throwable getThrowable();

   @NotNull
   SpanContext getSpanContext();

   void setTag(@Nullable String var1, @Nullable String var2);

   @Nullable
   String getTag(@Nullable String var1);

   boolean isFinished();

   void setData(@Nullable String var1, @Nullable Object var2);

   @Nullable
   Object getData(@Nullable String var1);

   void setMeasurement(@NotNull String var1, @NotNull Number var2);

   void setMeasurement(@NotNull String var1, @NotNull Number var2, @NotNull MeasurementUnit var3);

   @Internal
   boolean updateEndDate(@NotNull SentryDate var1);

   @Internal
   @NotNull
   SentryDate getStartDate();

   @Internal
   @Nullable
   SentryDate getFinishDate();

   @Internal
   boolean isNoOp();

   void setContext(@Nullable String var1, @Nullable Object var2);

   @NotNull
   Contexts getContexts();

   @Nullable
   Boolean isSampled();

   @Nullable
   TracesSamplingDecision getSamplingDecision();

   @Internal
   @NotNull
   ISentryLifecycleToken makeCurrent();

   void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);
}
