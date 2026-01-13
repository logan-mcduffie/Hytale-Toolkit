package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.SampleRateUtils;
import io.sentry.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
public final class Baggage {
   @NotNull
   static final String CHARSET = "UTF-8";
   @NotNull
   static final Integer MAX_BAGGAGE_STRING_LENGTH = 8192;
   @NotNull
   static final Integer MAX_BAGGAGE_LIST_MEMBER_COUNT = 64;
   @NotNull
   static final String SENTRY_BAGGAGE_PREFIX = "sentry-";
   private static final Baggage.DecimalFormatterThreadLocal decimalFormatter = new Baggage.DecimalFormatterThreadLocal();
   @NotNull
   private final ConcurrentHashMap<String, String> keyValues;
   @NotNull
   private final AutoClosableReentrantLock keyValuesLock = new AutoClosableReentrantLock();
   @Nullable
   private Double sampleRate;
   @Nullable
   private Double sampleRand;
   @Nullable
   private final String thirdPartyHeader;
   private boolean mutable;
   private final boolean shouldFreeze;
   @NotNull
   final ILogger logger;

   @NotNull
   public static Baggage fromHeader(@Nullable String headerValue) {
      return fromHeader(headerValue, false, ScopesAdapter.getInstance().getOptions().getLogger());
   }

   @NotNull
   public static Baggage fromHeader(@Nullable List<String> headerValues) {
      return fromHeader(headerValues, false, ScopesAdapter.getInstance().getOptions().getLogger());
   }

   @Internal
   @NotNull
   public static Baggage fromHeader(String headerValue, @NotNull ILogger logger) {
      return fromHeader(headerValue, false, logger);
   }

   @Internal
   @NotNull
   public static Baggage fromHeader(@Nullable List<String> headerValues, @NotNull ILogger logger) {
      return fromHeader(headerValues, false, logger);
   }

   @Internal
   @NotNull
   public static Baggage fromHeader(@Nullable List<String> headerValues, boolean includeThirdPartyValues, @NotNull ILogger logger) {
      return headerValues != null
         ? fromHeader(StringUtils.join(",", headerValues), includeThirdPartyValues, logger)
         : fromHeader((String)null, includeThirdPartyValues, logger);
   }

   @Internal
   @NotNull
   public static Baggage fromHeader(@Nullable String headerValue, boolean includeThirdPartyValues, @NotNull ILogger logger) {
      ConcurrentHashMap<String, String> keyValues = new ConcurrentHashMap<>();
      List<String> thirdPartyKeyValueStrings = new ArrayList<>();
      boolean shouldFreeze = false;
      Double sampleRate = null;
      Double sampleRand = null;
      if (headerValue != null) {
         try {
            String[] keyValueStrings = headerValue.split(",", -1);

            for (String keyValueString : keyValueStrings) {
               if (keyValueString.trim().startsWith("sentry-")) {
                  try {
                     int separatorIndex = keyValueString.indexOf("=");
                     String key = keyValueString.substring(0, separatorIndex).trim();
                     String keyDecoded = decode(key);
                     String value = keyValueString.substring(separatorIndex + 1).trim();
                     String valueDecoded = decode(value);
                     if ("sentry-sample_rate".equals(keyDecoded)) {
                        sampleRate = toDouble(valueDecoded);
                     } else if ("sentry-sample_rand".equals(keyDecoded)) {
                        sampleRand = toDouble(valueDecoded);
                     } else {
                        keyValues.put(keyDecoded, valueDecoded);
                     }

                     if (!"sentry-sample_rand".equalsIgnoreCase(key)) {
                        shouldFreeze = true;
                     }
                  } catch (Throwable var18) {
                     logger.log(SentryLevel.ERROR, var18, "Unable to decode baggage key value pair %s", keyValueString);
                  }
               } else if (includeThirdPartyValues) {
                  thirdPartyKeyValueStrings.add(keyValueString.trim());
               }
            }
         } catch (Throwable var19) {
            logger.log(SentryLevel.ERROR, var19, "Unable to decode baggage header %s", headerValue);
         }
      }

      String thirdPartyHeader = thirdPartyKeyValueStrings.isEmpty() ? null : StringUtils.join(",", thirdPartyKeyValueStrings);
      return new Baggage(keyValues, sampleRate, sampleRand, thirdPartyHeader, true, shouldFreeze, logger);
   }

   @Internal
   @NotNull
   public static Baggage fromEvent(@NotNull SentryBaseEvent event, @Nullable String transaction, @NotNull SentryOptions options) {
      Baggage baggage = new Baggage(options.getLogger());
      SpanContext trace = event.getContexts().getTrace();
      baggage.setTraceId(trace != null ? trace.getTraceId().toString() : null);
      baggage.setPublicKey(options.retrieveParsedDsn().getPublicKey());
      baggage.setRelease(event.getRelease());
      baggage.setEnvironment(event.getEnvironment());
      baggage.setTransaction(transaction);
      baggage.setSampleRate(null);
      baggage.setSampled(null);
      baggage.setSampleRand(null);
      Object replayId = event.getContexts().get("replay_id");
      if (replayId != null && !replayId.toString().equals(SentryId.EMPTY_ID.toString())) {
         baggage.setReplayId(replayId.toString());
         event.getContexts().remove("replay_id");
      }

      baggage.freeze();
      return baggage;
   }

   @Internal
   public Baggage(@NotNull ILogger logger) {
      this(new ConcurrentHashMap<>(), null, null, null, true, false, logger);
   }

   @Internal
   public Baggage(@NotNull Baggage baggage) {
      this(baggage.keyValues, baggage.sampleRate, baggage.sampleRand, baggage.thirdPartyHeader, baggage.mutable, baggage.shouldFreeze, baggage.logger);
   }

   @Internal
   public Baggage(
      @NotNull ConcurrentHashMap<String, String> keyValues,
      @Nullable Double sampleRate,
      @Nullable Double sampleRand,
      @Nullable String thirdPartyHeader,
      boolean isMutable,
      boolean shouldFreeze,
      @NotNull ILogger logger
   ) {
      this.keyValues = keyValues;
      this.sampleRate = sampleRate;
      this.sampleRand = sampleRand;
      this.logger = logger;
      this.thirdPartyHeader = thirdPartyHeader;
      this.mutable = isMutable;
      this.shouldFreeze = shouldFreeze;
   }

   @Internal
   public void freeze() {
      this.mutable = false;
   }

   @Internal
   public boolean isMutable() {
      return this.mutable;
   }

   @Internal
   public boolean isShouldFreeze() {
      return this.shouldFreeze;
   }

   @Nullable
   public String getThirdPartyHeader() {
      return this.thirdPartyHeader;
   }

   @NotNull
   public String toHeaderString(@Nullable String thirdPartyBaggageHeaderString) {
      StringBuilder sb = new StringBuilder();
      String separator = "";
      int listMemberCount = 0;
      if (thirdPartyBaggageHeaderString != null && !thirdPartyBaggageHeaderString.isEmpty()) {
         sb.append(thirdPartyBaggageHeaderString);
         listMemberCount = StringUtils.countOf(thirdPartyBaggageHeaderString, ',') + 1;
         separator = ",";
      }

      ISentryLifecycleToken ignored = this.keyValuesLock.acquire();

      Set<String> keys;
      try {
         keys = new TreeSet<>(Collections.list(this.keyValues.keys()));
      } catch (Throwable var16) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var14) {
               var16.addSuppressed(var14);
            }
         }

         throw var16;
      }

      if (ignored != null) {
         ignored.close();
      }

      keys.add("sentry-sample_rate");
      keys.add("sentry-sample_rand");

      for (String key : keys) {
         String value;
         if ("sentry-sample_rate".equals(key)) {
            value = sampleRateToString(this.sampleRate);
         } else if ("sentry-sample_rand".equals(key)) {
            value = sampleRateToString(this.sampleRand);
         } else {
            value = this.keyValues.get(key);
         }

         if (value != null) {
            if (listMemberCount >= MAX_BAGGAGE_LIST_MEMBER_COUNT) {
               this.logger
                  .log(
                     SentryLevel.ERROR,
                     "Not adding baggage value %s as the total number of list members would exceed the maximum of %s.",
                     key,
                     MAX_BAGGAGE_LIST_MEMBER_COUNT
                  );
            } else {
               try {
                  String encodedKey = this.encode(key);
                  String encodedValue = this.encode(value);
                  String encodedKeyValue = separator + encodedKey + "=" + encodedValue;
                  int valueLength = encodedKeyValue.length();
                  int totalLengthIfValueAdded = sb.length() + valueLength;
                  if (totalLengthIfValueAdded > MAX_BAGGAGE_STRING_LENGTH) {
                     this.logger
                        .log(
                           SentryLevel.ERROR,
                           "Not adding baggage value %s as the total header value length would exceed the maximum of %s.",
                           key,
                           MAX_BAGGAGE_STRING_LENGTH
                        );
                  } else {
                     listMemberCount++;
                     sb.append(encodedKeyValue);
                     separator = ",";
                  }
               } catch (Throwable var15) {
                  this.logger.log(SentryLevel.ERROR, var15, "Unable to encode baggage key value pair (key=%s,value=%s).", key, value);
               }
            }
         }
      }

      return sb.toString();
   }

   private String encode(@NotNull String value) throws UnsupportedEncodingException {
      return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
   }

   private static String decode(@NotNull String value) throws UnsupportedEncodingException {
      return URLDecoder.decode(value, "UTF-8");
   }

   @Internal
   @Nullable
   public String get(@Nullable String key) {
      return key == null ? null : this.keyValues.get(key);
   }

   @Internal
   @Nullable
   public String getTraceId() {
      return this.get("sentry-trace_id");
   }

   @Internal
   public void setTraceId(@Nullable String traceId) {
      this.set("sentry-trace_id", traceId);
   }

   @Internal
   @Nullable
   public String getPublicKey() {
      return this.get("sentry-public_key");
   }

   @Internal
   public void setPublicKey(@Nullable String publicKey) {
      this.set("sentry-public_key", publicKey);
   }

   @Internal
   @Nullable
   public String getEnvironment() {
      return this.get("sentry-environment");
   }

   @Internal
   public void setEnvironment(@Nullable String environment) {
      this.set("sentry-environment", environment);
   }

   @Internal
   @Nullable
   public String getRelease() {
      return this.get("sentry-release");
   }

   @Internal
   public void setRelease(@Nullable String release) {
      this.set("sentry-release", release);
   }

   @Internal
   @Nullable
   public String getUserId() {
      return this.get("sentry-user_id");
   }

   @Internal
   public void setUserId(@Nullable String userId) {
      this.set("sentry-user_id", userId);
   }

   @Internal
   @Nullable
   public String getTransaction() {
      return this.get("sentry-transaction");
   }

   @Internal
   public void setTransaction(@Nullable String transaction) {
      this.set("sentry-transaction", transaction);
   }

   @Internal
   @Nullable
   public Double getSampleRate() {
      return this.sampleRate;
   }

   @Internal
   @Nullable
   public String getSampled() {
      return this.get("sentry-sampled");
   }

   @Internal
   public void setSampleRate(@Nullable Double sampleRate) {
      if (this.isMutable()) {
         this.sampleRate = sampleRate;
      }
   }

   @Internal
   public void forceSetSampleRate(@Nullable Double sampleRate) {
      this.sampleRate = sampleRate;
   }

   @Internal
   @Nullable
   public Double getSampleRand() {
      return this.sampleRand;
   }

   @Internal
   public void setSampleRand(@Nullable Double sampleRand) {
      if (this.isMutable()) {
         this.sampleRand = sampleRand;
      }
   }

   @Internal
   public void setSampled(@Nullable String sampled) {
      this.set("sentry-sampled", sampled);
   }

   @Internal
   @Nullable
   public String getReplayId() {
      return this.get("sentry-replay_id");
   }

   @Internal
   public void setReplayId(@Nullable String replayId) {
      this.set("sentry-replay_id", replayId);
   }

   @Internal
   public void set(@NotNull String key, @Nullable String value) {
      if (this.mutable) {
         if (value == null) {
            this.keyValues.remove(key);
         } else {
            this.keyValues.put(key, value);
         }
      }
   }

   @Internal
   @NotNull
   public Map<String, Object> getUnknown() {
      Map<String, Object> unknown = new ConcurrentHashMap<>();
      ISentryLifecycleToken ignored = this.keyValuesLock.acquire();

      try {
         for (Entry<String, String> keyValue : this.keyValues.entrySet()) {
            String key = keyValue.getKey();
            String value = keyValue.getValue();
            if (!Baggage.DSCKeys.ALL.contains(key) && value != null) {
               String unknownKey = key.replaceFirst("sentry-", "");
               unknown.put(unknownKey, value);
            }
         }
      } catch (Throwable var9) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (ignored != null) {
         ignored.close();
      }

      return unknown;
   }

   @Internal
   public void setValuesFromTransaction(
      @NotNull SentryId traceId,
      @Nullable SentryId replayId,
      @NotNull SentryOptions sentryOptions,
      @Nullable TracesSamplingDecision samplingDecision,
      @Nullable String transactionName,
      @Nullable TransactionNameSource transactionNameSource
   ) {
      this.setTraceId(traceId.toString());
      this.setPublicKey(sentryOptions.retrieveParsedDsn().getPublicKey());
      this.setRelease(sentryOptions.getRelease());
      this.setEnvironment(sentryOptions.getEnvironment());
      this.setTransaction(isHighQualityTransactionName(transactionNameSource) ? transactionName : null);
      if (replayId != null && !SentryId.EMPTY_ID.equals(replayId)) {
         this.setReplayId(replayId.toString());
      }

      this.setSampleRate(sampleRate(samplingDecision));
      this.setSampled(StringUtils.toString(sampled(samplingDecision)));
      this.setSampleRand(sampleRand(samplingDecision));
   }

   @Internal
   public void setValuesFromSamplingDecision(@Nullable TracesSamplingDecision samplingDecision) {
      if (samplingDecision != null) {
         this.setSampled(StringUtils.toString(sampled(samplingDecision)));
         if (samplingDecision.getSampleRand() != null) {
            this.setSampleRand(sampleRand(samplingDecision));
         }

         if (samplingDecision.getSampleRate() != null) {
            this.forceSetSampleRate(sampleRate(samplingDecision));
         }
      }
   }

   @Internal
   public void setValuesFromScope(@NotNull IScope scope, @NotNull SentryOptions options) {
      PropagationContext propagationContext = scope.getPropagationContext();
      SentryId replayId = scope.getReplayId();
      this.setTraceId(propagationContext.getTraceId().toString());
      this.setPublicKey(options.retrieveParsedDsn().getPublicKey());
      this.setRelease(options.getRelease());
      this.setEnvironment(options.getEnvironment());
      if (!SentryId.EMPTY_ID.equals(replayId)) {
         this.setReplayId(replayId.toString());
      }

      this.setTransaction(null);
      this.setSampleRate(null);
      this.setSampled(null);
   }

   @Nullable
   private static Double sampleRate(@Nullable TracesSamplingDecision samplingDecision) {
      return samplingDecision == null ? null : samplingDecision.getSampleRate();
   }

   @Nullable
   private static Double sampleRand(@Nullable TracesSamplingDecision samplingDecision) {
      return samplingDecision == null ? null : samplingDecision.getSampleRand();
   }

   @Nullable
   private static String sampleRateToString(@Nullable Double sampleRateAsDouble) {
      return !SampleRateUtils.isValidTracesSampleRate(sampleRateAsDouble, false) ? null : decimalFormatter.get().format(sampleRateAsDouble);
   }

   @Nullable
   private static Boolean sampled(@Nullable TracesSamplingDecision samplingDecision) {
      return samplingDecision == null ? null : samplingDecision.getSampled();
   }

   private static boolean isHighQualityTransactionName(@Nullable TransactionNameSource transactionNameSource) {
      return transactionNameSource != null && !TransactionNameSource.URL.equals(transactionNameSource);
   }

   @Nullable
   private static Double toDouble(@Nullable String stringValue) {
      if (stringValue != null) {
         try {
            double doubleValue = Double.parseDouble(stringValue);
            if (SampleRateUtils.isValidTracesSampleRate(doubleValue, false)) {
               return doubleValue;
            }
         } catch (NumberFormatException var3) {
            return null;
         }
      }

      return null;
   }

   @Internal
   @Nullable
   public TraceContext toTraceContext() {
      String traceIdString = this.getTraceId();
      String replayIdString = this.getReplayId();
      String publicKey = this.getPublicKey();
      if (traceIdString != null && publicKey != null) {
         TraceContext traceContext = new TraceContext(
            new SentryId(traceIdString),
            publicKey,
            this.getRelease(),
            this.getEnvironment(),
            this.getUserId(),
            this.getTransaction(),
            sampleRateToString(this.getSampleRate()),
            this.getSampled(),
            replayIdString == null ? null : new SentryId(replayIdString),
            sampleRateToString(this.getSampleRand())
         );
         traceContext.setUnknown(this.getUnknown());
         return traceContext;
      } else {
         return null;
      }
   }

   @Internal
   public static final class DSCKeys {
      public static final String TRACE_ID = "sentry-trace_id";
      public static final String PUBLIC_KEY = "sentry-public_key";
      public static final String RELEASE = "sentry-release";
      public static final String USER_ID = "sentry-user_id";
      public static final String ENVIRONMENT = "sentry-environment";
      public static final String TRANSACTION = "sentry-transaction";
      public static final String SAMPLE_RATE = "sentry-sample_rate";
      public static final String SAMPLE_RAND = "sentry-sample_rand";
      public static final String SAMPLED = "sentry-sampled";
      public static final String REPLAY_ID = "sentry-replay_id";
      public static final List<String> ALL = Arrays.asList(
         "sentry-trace_id",
         "sentry-public_key",
         "sentry-release",
         "sentry-user_id",
         "sentry-environment",
         "sentry-transaction",
         "sentry-sample_rate",
         "sentry-sample_rand",
         "sentry-sampled",
         "sentry-replay_id"
      );
   }

   private static class DecimalFormatterThreadLocal extends ThreadLocal<DecimalFormat> {
      private DecimalFormatterThreadLocal() {
      }

      protected DecimalFormat initialValue() {
         return new DecimalFormat("#.################", DecimalFormatSymbols.getInstance(Locale.ROOT));
      }
   }
}
