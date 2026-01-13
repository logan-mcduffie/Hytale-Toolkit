package io.sentry;

import io.sentry.util.Objects;
import io.sentry.util.SentryRandom;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SamplingContext {
   @NotNull
   private final TransactionContext transactionContext;
   @Nullable
   private final CustomSamplingContext customSamplingContext;
   @NotNull
   private final Double sampleRand;
   @NotNull
   private final Map<String, Object> attributes;

   @Deprecated
   public SamplingContext(@NotNull TransactionContext transactionContext, @Nullable CustomSamplingContext customSamplingContext) {
      this(transactionContext, customSamplingContext, SentryRandom.current().nextDouble(), null);
   }

   @Internal
   public SamplingContext(
      @NotNull TransactionContext transactionContext,
      @Nullable CustomSamplingContext customSamplingContext,
      @NotNull Double sampleRand,
      @Nullable Map<String, Object> attributes
   ) {
      this.transactionContext = Objects.requireNonNull(transactionContext, "transactionContexts is required");
      this.customSamplingContext = customSamplingContext;
      this.sampleRand = sampleRand;
      this.attributes = attributes == null ? Collections.emptyMap() : attributes;
   }

   @Nullable
   public CustomSamplingContext getCustomSamplingContext() {
      return this.customSamplingContext;
   }

   @NotNull
   public TransactionContext getTransactionContext() {
      return this.transactionContext;
   }

   @NotNull
   public Double getSampleRand() {
      return this.sampleRand;
   }

   @Nullable
   public Object getAttribute(@Nullable String key) {
      return key == null ? null : this.attributes.get(key);
   }
}
