package io.sentry;

import io.sentry.exception.ExceptionMechanismException;
import io.sentry.hints.BlockingFlushHint;
import io.sentry.hints.EventDropReason;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.TransactionEnd;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.SentryId;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class UncaughtExceptionHandlerIntegration implements Integration, java.lang.Thread.UncaughtExceptionHandler, Closeable {
   @Nullable
   private UncaughtExceptionHandler defaultExceptionHandler;
   @NotNull
   private static final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
   @Nullable
   private IScopes scopes;
   @Nullable
   private SentryOptions options;
   private boolean registered = false;
   @NotNull
   private final UncaughtExceptionHandler threadAdapter;

   public UncaughtExceptionHandlerIntegration() {
      this(UncaughtExceptionHandler.Adapter.getInstance());
   }

   UncaughtExceptionHandlerIntegration(@NotNull UncaughtExceptionHandler threadAdapter) {
      this.threadAdapter = Objects.requireNonNull(threadAdapter, "threadAdapter is required.");
   }

   @Override
   public final void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
      if (this.registered) {
         options.getLogger().log(SentryLevel.ERROR, "Attempt to register a UncaughtExceptionHandlerIntegration twice.");
      } else {
         this.registered = true;
         this.scopes = Objects.requireNonNull(scopes, "Scopes are required");
         this.options = Objects.requireNonNull(options, "SentryOptions is required");
         this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration enabled: %s", this.options.isEnableUncaughtExceptionHandler());
         if (this.options.isEnableUncaughtExceptionHandler()) {
            ISentryLifecycleToken ignored = lock.acquire();

            try {
               java.lang.Thread.UncaughtExceptionHandler currentHandler = this.threadAdapter.getDefaultUncaughtExceptionHandler();
               if (currentHandler != null) {
                  this.options.getLogger().log(SentryLevel.DEBUG, "default UncaughtExceptionHandler class='" + currentHandler.getClass().getName() + "'");
                  if (currentHandler instanceof UncaughtExceptionHandlerIntegration) {
                     UncaughtExceptionHandlerIntegration currentHandlerIntegration = (UncaughtExceptionHandlerIntegration)currentHandler;
                     if (currentHandlerIntegration.scopes != null && scopes.getGlobalScope() == currentHandlerIntegration.scopes.getGlobalScope()) {
                        this.defaultExceptionHandler = currentHandlerIntegration.defaultExceptionHandler;
                     } else {
                        this.defaultExceptionHandler = currentHandler;
                     }
                  } else {
                     this.defaultExceptionHandler = currentHandler;
                  }
               }

               this.threadAdapter.setDefaultUncaughtExceptionHandler(this);
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

            this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration installed.");
            IntegrationUtils.addIntegrationToSdkVersion("UncaughtExceptionHandler");
         }
      }
   }

   @Override
   public void uncaughtException(Thread thread, Throwable thrown) {
      if (this.options != null && this.scopes != null) {
         this.options.getLogger().log(SentryLevel.INFO, "Uncaught exception received.");

         try {
            UncaughtExceptionHandlerIntegration.UncaughtExceptionHint exceptionHint = new UncaughtExceptionHandlerIntegration.UncaughtExceptionHint(
               this.options.getFlushTimeoutMillis(), this.options.getLogger()
            );
            Throwable throwable = getUnhandledThrowable(thread, thrown);
            SentryEvent event = new SentryEvent(throwable);
            event.setLevel(SentryLevel.FATAL);
            ITransaction transaction = this.scopes.getTransaction();
            if (transaction == null && event.getEventId() != null) {
               exceptionHint.setFlushable(event.getEventId());
            }

            Hint hint = HintUtils.createWithTypeCheckHint(exceptionHint);
            SentryId sentryId = this.scopes.captureEvent(event, hint);
            boolean isEventDropped = sentryId.equals(SentryId.EMPTY_ID);
            EventDropReason eventDropReason = HintUtils.getEventDropReason(hint);
            if ((!isEventDropped || EventDropReason.MULTITHREADED_DEDUPLICATION.equals(eventDropReason)) && !exceptionHint.waitFlush()) {
               this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush event to disk before crashing. Event: %s", event.getEventId());
            }
         } catch (Throwable var11) {
            this.options.getLogger().log(SentryLevel.ERROR, "Error sending uncaught exception to Sentry.", var11);
         }

         if (this.defaultExceptionHandler != null) {
            this.options.getLogger().log(SentryLevel.INFO, "Invoking inner uncaught exception handler.");
            this.defaultExceptionHandler.uncaughtException(thread, thrown);
         } else if (this.options.isPrintUncaughtStackTrace()) {
            thrown.printStackTrace();
         }
      }
   }

   @TestOnly
   @NotNull
   static Throwable getUnhandledThrowable(@NotNull Thread thread, @NotNull Throwable thrown) {
      Mechanism mechanism = new Mechanism();
      mechanism.setHandled(false);
      mechanism.setType("UncaughtExceptionHandler");
      return new ExceptionMechanismException(mechanism, thrown, thread);
   }

   @Override
   public void close() {
      ISentryLifecycleToken ignored = lock.acquire();

      try {
         if (this == this.threadAdapter.getDefaultUncaughtExceptionHandler()) {
            this.threadAdapter.setDefaultUncaughtExceptionHandler(this.defaultExceptionHandler);
            if (this.options != null) {
               this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration removed.");
            }
         } else {
            this.removeFromHandlerTree(this.threadAdapter.getDefaultUncaughtExceptionHandler());
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

   private void removeFromHandlerTree(@Nullable UncaughtExceptionHandler currentHandler) {
      this.removeFromHandlerTree(currentHandler, new HashSet<>());
   }

   private void removeFromHandlerTree(@Nullable UncaughtExceptionHandler currentHandler, @NotNull Set<UncaughtExceptionHandler> visited) {
      if (currentHandler == null) {
         if (this.options != null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Found no UncaughtExceptionHandler to remove.");
         }
      } else if (!visited.add(currentHandler)) {
         if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Cycle detected in UncaughtExceptionHandler chain while removing handler.");
         }
      } else if (currentHandler instanceof UncaughtExceptionHandlerIntegration) {
         UncaughtExceptionHandlerIntegration currentHandlerIntegration = (UncaughtExceptionHandlerIntegration)currentHandler;
         if (this == currentHandlerIntegration.defaultExceptionHandler) {
            currentHandlerIntegration.defaultExceptionHandler = this.defaultExceptionHandler;
            if (this.options != null) {
               this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration removed.");
            }
         } else {
            this.removeFromHandlerTree(currentHandlerIntegration.defaultExceptionHandler, visited);
         }
      }
   }

   @Internal
   public static class UncaughtExceptionHint extends BlockingFlushHint implements SessionEnd, TransactionEnd {
      private final AtomicReference<SentryId> flushableEventId = new AtomicReference<>();

      public UncaughtExceptionHint(long flushTimeoutMillis, @NotNull ILogger logger) {
         super(flushTimeoutMillis, logger);
      }

      @Override
      public boolean isFlushable(@Nullable SentryId eventId) {
         SentryId unwrapped = this.flushableEventId.get();
         return unwrapped != null && unwrapped.equals(eventId);
      }

      @Override
      public void setFlushable(@NotNull SentryId eventId) {
         this.flushableEventId.set(eventId);
      }
   }
}
