package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TransactionOptions extends SpanOptions {
   @Internal
   public static final long DEFAULT_DEADLINE_TIMEOUT_AUTO_TRANSACTION = 30000L;
   @Nullable
   private CustomSamplingContext customSamplingContext = null;
   private boolean isAppStartTransaction = false;
   private boolean waitForChildren = false;
   @Nullable
   private Long idleTimeout = null;
   @Nullable
   private Long deadlineTimeout = null;
   @Nullable
   private TransactionFinishedCallback transactionFinishedCallback = null;
   @Internal
   @Nullable
   private ISpanFactory spanFactory = null;

   @Nullable
   public CustomSamplingContext getCustomSamplingContext() {
      return this.customSamplingContext;
   }

   public void setCustomSamplingContext(@Nullable CustomSamplingContext customSamplingContext) {
      this.customSamplingContext = customSamplingContext;
   }

   public boolean isBindToScope() {
      return ScopeBindingMode.ON == this.getScopeBindingMode();
   }

   public void setBindToScope(boolean bindToScope) {
      this.setScopeBindingMode(bindToScope ? ScopeBindingMode.ON : ScopeBindingMode.OFF);
   }

   public boolean isWaitForChildren() {
      return this.waitForChildren;
   }

   public void setWaitForChildren(boolean waitForChildren) {
      this.waitForChildren = waitForChildren;
   }

   @Nullable
   public Long getIdleTimeout() {
      return this.idleTimeout;
   }

   @Internal
   public void setDeadlineTimeout(@Nullable Long deadlineTimeoutMs) {
      this.deadlineTimeout = deadlineTimeoutMs;
   }

   @Internal
   @Nullable
   public Long getDeadlineTimeout() {
      return this.deadlineTimeout;
   }

   public void setIdleTimeout(@Nullable Long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   @Nullable
   public TransactionFinishedCallback getTransactionFinishedCallback() {
      return this.transactionFinishedCallback;
   }

   public void setTransactionFinishedCallback(@Nullable TransactionFinishedCallback transactionFinishedCallback) {
      this.transactionFinishedCallback = transactionFinishedCallback;
   }

   @Internal
   public void setAppStartTransaction(boolean appStartTransaction) {
      this.isAppStartTransaction = appStartTransaction;
   }

   @Internal
   public boolean isAppStartTransaction() {
      return this.isAppStartTransaction;
   }

   @Internal
   @Nullable
   public ISpanFactory getSpanFactory() {
      return this.spanFactory;
   }

   @Internal
   public void setSpanFactory(@NotNull ISpanFactory spanFactory) {
      this.spanFactory = spanFactory;
   }
}
