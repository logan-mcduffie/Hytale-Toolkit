package io.sentry.util;

import io.sentry.Baggage;
import io.sentry.BaggageHeader;
import io.sentry.FilterString;
import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.NoOpLogger;
import io.sentry.PropagationContext;
import io.sentry.SentryOptions;
import io.sentry.SentryTraceHeader;
import io.sentry.SpanContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.W3CTraceparentHeader;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TracingUtils {
   public static void startNewTrace(@NotNull IScopes scopes) {
      scopes.configureScope(scope -> scope.withPropagationContext(propagationContext -> scope.setPropagationContext(new PropagationContext())));
   }

   public static void setTrace(@NotNull IScopes scopes, @NotNull PropagationContext propagationContext) {
      scopes.configureScope(scope -> scope.withPropagationContext(oldPropagationContext -> scope.setPropagationContext(propagationContext)));
   }

   @Nullable
   public static TracingUtils.TracingHeaders traceIfAllowed(
      @NotNull IScopes scopes, @NotNull String requestUrl, @Nullable List<String> thirdPartyBaggageHeaders, @Nullable ISpan span
   ) {
      SentryOptions sentryOptions = scopes.getOptions();
      return sentryOptions.isTraceSampling() && shouldAttachTracingHeaders(requestUrl, sentryOptions) ? trace(scopes, thirdPartyBaggageHeaders, span) : null;
   }

   @Nullable
   public static TracingUtils.TracingHeaders trace(@NotNull IScopes scopes, @Nullable List<String> thirdPartyBaggageHeaders, @Nullable ISpan span) {
      SentryOptions sentryOptions = scopes.getOptions();
      if (span != null && !span.isNoOp()) {
         SentryTraceHeader sentryTraceHeader = span.toSentryTrace();
         BaggageHeader baggageHeader = span.toBaggageHeader(thirdPartyBaggageHeaders);
         W3CTraceparentHeader w3cTraceparentHeader = null;
         if (sentryOptions.isPropagateTraceparent()) {
            SpanContext spanContext = span.getSpanContext();
            w3cTraceparentHeader = new W3CTraceparentHeader(spanContext.getTraceId(), spanContext.getSpanId(), sentryTraceHeader.isSampled());
         }

         return new TracingUtils.TracingHeaders(sentryTraceHeader, baggageHeader, w3cTraceparentHeader);
      } else {
         TracingUtils.PropagationContextHolder returnValue = new TracingUtils.PropagationContextHolder();
         scopes.configureScope(scope -> returnValue.propagationContext = maybeUpdateBaggage(scope, sentryOptions));
         if (returnValue.propagationContext != null) {
            PropagationContext propagationContext = returnValue.propagationContext;
            Baggage baggage = propagationContext.getBaggage();
            BaggageHeader baggageHeader = BaggageHeader.fromBaggageAndOutgoingHeader(baggage, thirdPartyBaggageHeaders);
            SentryTraceHeader sentryTraceHeader = new SentryTraceHeader(
               propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.isSampled()
            );
            W3CTraceparentHeader w3cTraceparentHeader = null;
            if (sentryOptions.isPropagateTraceparent()) {
               w3cTraceparentHeader = new W3CTraceparentHeader(propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.isSampled());
            }

            return new TracingUtils.TracingHeaders(sentryTraceHeader, baggageHeader, w3cTraceparentHeader);
         } else {
            return null;
         }
      }
   }

   @NotNull
   public static PropagationContext maybeUpdateBaggage(@NotNull IScope scope, @NotNull SentryOptions sentryOptions) {
      return scope.withPropagationContext(propagationContext -> {
         Baggage baggage = propagationContext.getBaggage();
         if (baggage.isMutable()) {
            baggage.setValuesFromScope(scope, sentryOptions);
            baggage.freeze();
         }
      });
   }

   private static boolean shouldAttachTracingHeaders(@NotNull String requestUrl, @NotNull SentryOptions sentryOptions) {
      return PropagationTargetsUtils.contain(sentryOptions.getTracePropagationTargets(), requestUrl);
   }

   @Internal
   public static boolean isIgnored(@Nullable List<FilterString> ignoredTransactions, @Nullable String transactionName) {
      if (transactionName == null) {
         return false;
      } else if (ignoredTransactions != null && !ignoredTransactions.isEmpty()) {
         for (FilterString ignoredTransaction : ignoredTransactions) {
            if (ignoredTransaction.getFilterString().equalsIgnoreCase(transactionName)) {
               return true;
            }
         }

         for (FilterString ignoredTransactionx : ignoredTransactions) {
            try {
               if (ignoredTransactionx.matches(transactionName)) {
                  return true;
               }
            } catch (Throwable var5) {
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Internal
   @NotNull
   public static Baggage ensureBaggage(@Nullable Baggage incomingBaggage, @Nullable TracesSamplingDecision decision) {
      Boolean decisionSampled = decision == null ? null : decision.getSampled();
      Double decisionSampleRate = decision == null ? null : decision.getSampleRate();
      Double decisionSampleRand = decision == null ? null : decision.getSampleRand();
      return ensureBaggage(incomingBaggage, decisionSampled, decisionSampleRate, decisionSampleRand);
   }

   @Internal
   @NotNull
   public static Baggage ensureBaggage(
      @Nullable Baggage incomingBaggage, @Nullable Boolean decisionSampled, @Nullable Double decisionSampleRate, @Nullable Double decisionSampleRand
   ) {
      Baggage baggage = incomingBaggage == null ? new Baggage(NoOpLogger.getInstance()) : incomingBaggage;
      if (baggage.getSampleRand() == null) {
         Double baggageSampleRate = baggage.getSampleRate();
         Double sampleRateMaybe = baggageSampleRate == null ? decisionSampleRate : baggageSampleRate;
         Double sampleRand = SampleRateUtils.backfilledSampleRand(decisionSampleRand, sampleRateMaybe, decisionSampled);
         baggage.setSampleRand(sampleRand);
      }

      if (baggage.isMutable() && baggage.isShouldFreeze()) {
         baggage.freeze();
      }

      return baggage;
   }

   private static final class PropagationContextHolder {
      @Nullable
      private PropagationContext propagationContext = null;

      private PropagationContextHolder() {
      }
   }

   public static final class TracingHeaders {
      @NotNull
      private final SentryTraceHeader sentryTraceHeader;
      @Nullable
      private final BaggageHeader baggageHeader;
      @Nullable
      private final W3CTraceparentHeader w3cTraceparentHeader;

      public TracingHeaders(@NotNull SentryTraceHeader sentryTraceHeader, @Nullable BaggageHeader baggageHeader) {
         this.sentryTraceHeader = sentryTraceHeader;
         this.baggageHeader = baggageHeader;
         this.w3cTraceparentHeader = null;
      }

      public TracingHeaders(
         @NotNull SentryTraceHeader sentryTraceHeader, @Nullable BaggageHeader baggageHeader, @Nullable W3CTraceparentHeader w3cTraceparentHeader
      ) {
         this.sentryTraceHeader = sentryTraceHeader;
         this.baggageHeader = baggageHeader;
         this.w3cTraceparentHeader = w3cTraceparentHeader;
      }

      @NotNull
      public SentryTraceHeader getSentryTraceHeader() {
         return this.sentryTraceHeader;
      }

      @Nullable
      public BaggageHeader getBaggageHeader() {
         return this.baggageHeader;
      }

      @Nullable
      public W3CTraceparentHeader getW3cTraceparentHeader() {
         return this.w3cTraceparentHeader;
      }
   }
}
