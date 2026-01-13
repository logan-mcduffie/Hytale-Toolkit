package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;
import com.nimbusds.jose.util.events.EventListener;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class RefreshAheadCachingJWKSetSource<C extends SecurityContext> extends CachingJWKSetSource<C> {
   private final long refreshAheadTime;
   private final ReentrantLock lazyLock = new ReentrantLock();
   private final ExecutorService executorService;
   private final boolean shutdownExecutorOnClose;
   private final ScheduledExecutorService scheduledExecutorService;
   private final boolean shutdownScheduledExecutorOnClose;
   private volatile long cacheExpiration;
   private ScheduledFuture<?> scheduledRefreshFuture;
   private final EventListener<CachingJWKSetSource<C>, C> eventListener;

   public static ExecutorService createDefaultExecutorService() {
      return Executors.newSingleThreadExecutor();
   }

   public static ScheduledExecutorService createDefaultScheduledExecutorService() {
      return Executors.newSingleThreadScheduledExecutor();
   }

   public RefreshAheadCachingJWKSetSource(
      JWKSetSource<C> source,
      long timeToLive,
      long cacheRefreshTimeout,
      long refreshAheadTime,
      boolean scheduled,
      EventListener<CachingJWKSetSource<C>, C> eventListener
   ) {
      this(
         source,
         timeToLive,
         cacheRefreshTimeout,
         refreshAheadTime,
         createDefaultExecutorService(),
         true,
         eventListener,
         scheduled ? createDefaultScheduledExecutorService() : null,
         scheduled
      );
   }

   public RefreshAheadCachingJWKSetSource(
      JWKSetSource<C> source,
      long timeToLive,
      long cacheRefreshTimeout,
      long refreshAheadTime,
      boolean scheduled,
      ExecutorService executorService,
      boolean shutdownExecutorOnClose,
      EventListener<CachingJWKSetSource<C>, C> eventListener
   ) {
      this(
         source,
         timeToLive,
         cacheRefreshTimeout,
         refreshAheadTime,
         executorService,
         shutdownExecutorOnClose,
         eventListener,
         scheduled ? createDefaultScheduledExecutorService() : null,
         scheduled
      );
   }

   public RefreshAheadCachingJWKSetSource(
      JWKSetSource<C> source,
      long timeToLive,
      long cacheRefreshTimeout,
      long refreshAheadTime,
      ExecutorService executorService,
      boolean shutdownExecutorOnClose,
      EventListener<CachingJWKSetSource<C>, C> eventListener,
      ScheduledExecutorService scheduledExecutorService,
      boolean shutdownScheduledExecutorOnClose
   ) {
      super(source, timeToLive, cacheRefreshTimeout, eventListener);
      if (refreshAheadTime + cacheRefreshTimeout > timeToLive) {
         throw new IllegalArgumentException(
            "The sum of the refresh-ahead time ("
               + refreshAheadTime
               + "ms) and the cache refresh timeout ("
               + cacheRefreshTimeout
               + "ms) must not exceed the time-to-lived time ("
               + timeToLive
               + "ms)"
         );
      } else {
         this.refreshAheadTime = refreshAheadTime;
         Objects.requireNonNull(executorService, "The executor service must not be null");
         this.executorService = executorService;
         this.shutdownExecutorOnClose = shutdownExecutorOnClose;
         this.shutdownScheduledExecutorOnClose = shutdownScheduledExecutorOnClose;
         this.scheduledExecutorService = scheduledExecutorService;
         this.eventListener = eventListener;
      }
   }

   @Override
   public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      CachedObject<JWKSet> cache = this.getCachedJWKSet();
      if (cache == null) {
         return this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.noRefresh(), currentTime, context);
      } else {
         JWKSet jwkSet = cache.get();
         if (refreshEvaluator.requiresRefresh(jwkSet)) {
            return this.loadJWKSetBlocking(refreshEvaluator, currentTime, context);
         } else if (cache.isExpired(currentTime)) {
            return this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.referenceComparison(jwkSet), currentTime, context);
         } else {
            this.refreshAheadOfExpiration(cache, false, currentTime, context);
            return cache.get();
         }
      }
   }

   @Override
   CachedObject<JWKSet> loadJWKSetNotThreadSafe(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      CachedObject<JWKSet> cache = super.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
      if (this.scheduledExecutorService != null) {
         this.scheduleRefreshAheadOfExpiration(cache, currentTime, context);
      }

      return cache;
   }

   void scheduleRefreshAheadOfExpiration(final CachedObject<JWKSet> cache, long currentTime, final C context) {
      if (this.scheduledRefreshFuture != null) {
         this.scheduledRefreshFuture.cancel(false);
      }

      long delay = cache.getExpirationTime() - currentTime - this.refreshAheadTime - this.getCacheRefreshTimeout();
      if (delay > 0L) {
         final RefreshAheadCachingJWKSetSource<C> that = this;
         Runnable command = new Runnable() {
            @Override
            public void run() {
               try {
                  RefreshAheadCachingJWKSetSource.this.refreshAheadOfExpiration(cache, true, System.currentTimeMillis(), context);
               } catch (Exception var2) {
                  if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                     RefreshAheadCachingJWKSetSource.this.eventListener
                        .notify(new RefreshAheadCachingJWKSetSource.ScheduledRefreshFailed<>(that, var2, context));
                  }
               }
            }
         };
         this.scheduledRefreshFuture = this.scheduledExecutorService.schedule(command, delay, TimeUnit.MILLISECONDS);
         if (this.eventListener != null) {
            this.eventListener.notify(new RefreshAheadCachingJWKSetSource.RefreshScheduledEvent<>(this, context));
         }
      } else if (this.eventListener != null) {
         this.eventListener.notify(new RefreshAheadCachingJWKSetSource.RefreshNotScheduledEvent<>(this, context));
      }
   }

   void refreshAheadOfExpiration(CachedObject<JWKSet> cache, boolean forceRefresh, long currentTime, C context) {
      if ((cache.isExpired(currentTime + this.refreshAheadTime) || forceRefresh) && this.cacheExpiration < cache.getExpirationTime() && this.lazyLock.tryLock()
         )
       {
         try {
            this.lockedRefresh(cache, currentTime, context);
         } finally {
            this.lazyLock.unlock();
         }
      }
   }

   void lockedRefresh(CachedObject<JWKSet> cache, final long currentTime, final C context) {
      if (this.cacheExpiration < cache.getExpirationTime()) {
         this.cacheExpiration = cache.getExpirationTime();
         final RefreshAheadCachingJWKSetSource<C> that = this;
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
               try {
                  if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                     RefreshAheadCachingJWKSetSource.this.eventListener
                        .notify(new RefreshAheadCachingJWKSetSource.ScheduledRefreshInitiatedEvent<>(that, context));
                  }

                  JWKSet jwkSet = RefreshAheadCachingJWKSetSource.this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.forceRefresh(), currentTime, context);
                  if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                     RefreshAheadCachingJWKSetSource.this.eventListener
                        .notify(new RefreshAheadCachingJWKSetSource.ScheduledRefreshCompletedEvent<>(that, jwkSet, context));
                  }
               } catch (Throwable var2) {
                  RefreshAheadCachingJWKSetSource.this.cacheExpiration = -1L;
                  if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                     RefreshAheadCachingJWKSetSource.this.eventListener
                        .notify(new RefreshAheadCachingJWKSetSource.UnableToRefreshAheadOfExpirationEvent<>(that, context));
                  }
               }
            }
         };
         this.executorService.execute(runnable);
      }
   }

   public ExecutorService getExecutorService() {
      return this.executorService;
   }

   public ScheduledExecutorService getScheduledExecutorService() {
      return this.scheduledExecutorService;
   }

   ReentrantLock getLazyLock() {
      return this.lazyLock;
   }

   ScheduledFuture<?> getScheduledRefreshFuture() {
      return this.scheduledRefreshFuture;
   }

   @Override
   public void close() throws IOException {
      ScheduledFuture<?> currentScheduledRefreshFuture = this.scheduledRefreshFuture;
      if (currentScheduledRefreshFuture != null) {
         currentScheduledRefreshFuture.cancel(true);
      }

      super.close();
      if (this.shutdownExecutorOnClose) {
         this.executorService.shutdownNow();

         try {
            this.executorService.awaitTermination(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
         }
      }

      if (this.scheduledExecutorService != null && this.shutdownScheduledExecutorOnClose) {
         this.scheduledExecutorService.shutdownNow();

         try {
            this.scheduledExecutorService.awaitTermination(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS);
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         }
      }
   }

   public static class RefreshNotScheduledEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      public RefreshNotScheduledEvent(RefreshAheadCachingJWKSetSource<C> source, C context) {
         super(source, context);
      }
   }

   public static class RefreshScheduledEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      public RefreshScheduledEvent(RefreshAheadCachingJWKSetSource<C> source, C context) {
         super(source, context);
      }
   }

   public static class ScheduledRefreshCompletedEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      private final JWKSet jwkSet;

      private ScheduledRefreshCompletedEvent(CachingJWKSetSource<C> source, JWKSet jwkSet, C context) {
         super(source, context);
         Objects.requireNonNull(jwkSet);
         this.jwkSet = jwkSet;
      }

      public JWKSet getJWKSet() {
         return this.jwkSet;
      }
   }

   public static class ScheduledRefreshFailed<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      private final Exception exception;

      public ScheduledRefreshFailed(CachingJWKSetSource<C> source, Exception exception, C context) {
         super(source, context);
         Objects.requireNonNull(exception);
         this.exception = exception;
      }

      public Exception getException() {
         return this.exception;
      }
   }

   public static class ScheduledRefreshInitiatedEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      private ScheduledRefreshInitiatedEvent(CachingJWKSetSource<C> source, C context) {
         super(source, context);
      }
   }

   public static class UnableToRefreshAheadOfExpirationEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      public UnableToRefreshAheadOfExpirationEvent(CachingJWKSetSource<C> source, C context) {
         super(source, context);
      }
   }
}
