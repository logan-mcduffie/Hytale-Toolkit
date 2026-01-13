package com.google.common.flogger.context;

import com.google.common.flogger.LoggingScope;
import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.MustBeClosed;
import java.io.Closeable;
import java.util.concurrent.Callable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class ScopedLoggingContext {
   @CheckReturnValue
   public static ScopedLoggingContext getInstance() {
      return ContextDataProvider.getInstance().getContextApiSingleton();
   }

   protected ScopedLoggingContext() {
   }

   @CheckReturnValue
   public abstract ScopedLoggingContext.Builder newContext();

   @Deprecated
   @CheckReturnValue
   public ScopedLoggingContext.Builder newScope() {
      return this.newContext();
   }

   public boolean addTags(Tags tags) {
      Checks.checkNotNull(tags, "tags");
      return false;
   }

   public <T> boolean addMetadata(MetadataKey<T> key, T value) {
      Checks.checkNotNull(key, "key");
      Checks.checkNotNull(value, "value");
      return false;
   }

   public boolean applyLogLevelMap(LogLevelMap logLevelMap) {
      Checks.checkNotNull(logLevelMap, "log level map");
      return false;
   }

   private static void closeAndMaybePropagateError(ScopedLoggingContext.LoggingContextCloseable context, boolean callerHasError) {
      try {
         context.close();
      } catch (RuntimeException var3) {
         if (!callerHasError) {
            throw var3 instanceof ScopedLoggingContext.InvalidLoggingContextStateException
               ? (ScopedLoggingContext.InvalidLoggingContextStateException)var3
               : new ScopedLoggingContext.InvalidLoggingContextStateException("invalid logging context state", var3);
         }
      }
   }

   public abstract static class Builder {
      private Tags tags = null;
      private ScopeMetadata.Builder metadata = null;
      private LogLevelMap logLevelMap = null;

      protected Builder() {
      }

      @CheckReturnValue
      public final ScopedLoggingContext.Builder withTags(Tags tags) {
         Checks.checkState(this.tags == null, "tags already set");
         Checks.checkNotNull(tags, "tags");
         this.tags = tags;
         return this;
      }

      @CheckReturnValue
      public final <T> ScopedLoggingContext.Builder withMetadata(MetadataKey<T> key, T value) {
         if (this.metadata == null) {
            this.metadata = ScopeMetadata.builder();
         }

         this.metadata.add(key, value);
         return this;
      }

      @CheckReturnValue
      public final ScopedLoggingContext.Builder withLogLevelMap(LogLevelMap logLevelMap) {
         Checks.checkState(this.logLevelMap == null, "log level map already set");
         Checks.checkNotNull(logLevelMap, "log level map");
         this.logLevelMap = logLevelMap;
         return this;
      }

      @CheckReturnValue
      public final Runnable wrap(final Runnable r) {
         return new Runnable() {
            @Override
            public void run() {
               ScopedLoggingContext.LoggingContextCloseable context = Builder.this.install();
               boolean hasError = true;

               try {
                  r.run();
                  hasError = false;
               } finally {
                  ScopedLoggingContext.closeAndMaybePropagateError(context, hasError);
               }
            }
         };
      }

      @CheckReturnValue
      public final <R> Callable<R> wrap(final Callable<R> c) {
         return new Callable<R>() {
            @Override
            public R call() throws Exception {
               ScopedLoggingContext.LoggingContextCloseable context = Builder.this.install();
               boolean hasError = true;

               Object var4;
               try {
                  R result = c.call();
                  hasError = false;
                  var4 = result;
               } finally {
                  ScopedLoggingContext.closeAndMaybePropagateError(context, hasError);
               }

               return (R)var4;
            }
         };
      }

      public final void run(Runnable r) {
         this.wrap(r).run();
      }

      public final <R> R call(Callable<R> c) throws Exception {
         return this.wrap(c).call();
      }

      public final <R> R callUnchecked(Callable<R> c) {
         try {
            return this.call(c);
         } catch (RuntimeException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new RuntimeException("checked exception caught during context call", var4);
         }
      }

      @CheckReturnValue
      @MustBeClosed
      public abstract ScopedLoggingContext.LoggingContextCloseable install();

      @NullableDecl
      protected final Tags getTags() {
         return this.tags;
      }

      @NullableDecl
      protected final ScopeMetadata getMetadata() {
         return this.metadata != null ? this.metadata.build() : null;
      }

      @NullableDecl
      protected final LogLevelMap getLogLevelMap() {
         return this.logLevelMap;
      }
   }

   public static final class InvalidLoggingContextStateException extends IllegalStateException {
      public InvalidLoggingContextStateException(String message, Throwable cause) {
         super(message, cause);
      }

      public InvalidLoggingContextStateException(String message) {
         super(message);
      }
   }

   public interface LoggingContextCloseable extends Closeable {
      @Override
      void close();
   }

   public static final class ScopeList {
      private final ScopeType key;
      private final LoggingScope scope;
      @NullableDecl
      private final ScopedLoggingContext.ScopeList next;

      @NullableDecl
      public static ScopedLoggingContext.ScopeList addScope(@NullableDecl ScopedLoggingContext.ScopeList list, @NullableDecl ScopeType type) {
         return type != null && lookup(list, type) == null ? new ScopedLoggingContext.ScopeList(type, type.newScope(), list) : list;
      }

      @NullableDecl
      public static LoggingScope lookup(@NullableDecl ScopedLoggingContext.ScopeList list, ScopeType type) {
         while (list != null) {
            if (type.equals(list.key)) {
               return list.scope;
            }

            list = list.next;
         }

         return null;
      }

      public ScopeList(ScopeType key, LoggingScope scope, @NullableDecl ScopedLoggingContext.ScopeList next) {
         this.key = Checks.checkNotNull(key, "scope type");
         this.scope = Checks.checkNotNull(scope, "scope");
         this.next = next;
      }
   }
}
