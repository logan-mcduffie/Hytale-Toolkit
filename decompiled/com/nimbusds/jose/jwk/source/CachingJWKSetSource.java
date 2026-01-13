package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;
import com.nimbusds.jose.util.events.EventListener;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class CachingJWKSetSource<C extends SecurityContext> extends AbstractCachingJWKSetSource<C> {
   private final ReentrantLock lock = new ReentrantLock();
   private final long cacheRefreshTimeout;
   private final EventListener<CachingJWKSetSource<C>, C> eventListener;

   public CachingJWKSetSource(JWKSetSource<C> source, long timeToLive, long cacheRefreshTimeout, EventListener<CachingJWKSetSource<C>, C> eventListener) {
      super(source, timeToLive);
      this.cacheRefreshTimeout = cacheRefreshTimeout;
      this.eventListener = eventListener;
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
         } else {
            return cache.isExpired(currentTime)
               ? this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.referenceComparison(jwkSet), currentTime, context)
               : cache.get();
         }
      }
   }

   public long getCacheRefreshTimeout() {
      return this.cacheRefreshTimeout;
   }

   JWKSet loadJWKSetBlocking(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      try {
         CachedObject<JWKSet> cache;
         if (this.lock.tryLock()) {
            try {
               CachedObject<JWKSet> cachedJWKSet = this.getCachedJWKSet();
               if (cachedJWKSet != null && !refreshEvaluator.requiresRefresh(cachedJWKSet.get())) {
                  cache = cachedJWKSet;
               } else {
                  if (this.eventListener != null) {
                     this.eventListener.notify(new CachingJWKSetSource.RefreshInitiatedEvent<>(this, this.lock.getQueueLength(), context));
                  }

                  CachedObject<JWKSet> result = this.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
                  if (this.eventListener != null) {
                     this.eventListener.notify(new CachingJWKSetSource.RefreshCompletedEvent<>(this, result.get(), this.lock.getQueueLength(), context));
                  }

                  cache = result;
               }
            } finally {
               this.lock.unlock();
            }
         } else {
            if (this.eventListener != null) {
               this.eventListener.notify(new CachingJWKSetSource.WaitingForRefreshEvent<>(this, this.lock.getQueueLength(), context));
            }

            if (!this.lock.tryLock(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS)) {
               if (this.eventListener != null) {
                  this.eventListener.notify(new CachingJWKSetSource.RefreshTimedOutEvent<>(this, this.lock.getQueueLength(), context));
               }

               throw new JWKSetUnavailableException("Timeout while waiting for cache refresh (" + this.cacheRefreshTimeout + "ms exceeded)");
            }

            try {
               CachedObject<JWKSet> cachedJWKSet = this.getCachedJWKSet();
               if (cachedJWKSet != null && !refreshEvaluator.requiresRefresh(cachedJWKSet.get())) {
                  cache = cachedJWKSet;
               } else {
                  if (this.eventListener != null) {
                     this.eventListener.notify(new CachingJWKSetSource.RefreshInitiatedEvent<>(this, this.lock.getQueueLength(), context));
                  }

                  cache = this.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
                  if (this.eventListener != null) {
                     this.eventListener.notify(new CachingJWKSetSource.RefreshCompletedEvent<>(this, cache.get(), this.lock.getQueueLength(), context));
                  }
               }
            } finally {
               this.lock.unlock();
            }
         }

         if (cache != null && cache.isValid(currentTime)) {
            return cache.get();
         } else {
            if (this.eventListener != null) {
               this.eventListener.notify(new CachingJWKSetSource.UnableToRefreshEvent<>(this, context));
            }

            throw new JWKSetUnavailableException("Unable to refresh cache");
         }
      } catch (InterruptedException var18) {
         Thread.currentThread().interrupt();
         throw new JWKSetUnavailableException("Interrupted while waiting for cache refresh", var18);
      }
   }

   CachedObject<JWKSet> loadJWKSetNotThreadSafe(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      JWKSet jwkSet = this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
      return this.cacheJWKSet(jwkSet, currentTime);
   }

   ReentrantLock getLock() {
      return this.lock;
   }

   static class AbstractCachingJWKSetSourceEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      private final int threadQueueLength;

      public AbstractCachingJWKSetSourceEvent(CachingJWKSetSource<C> source, int threadQueueLength, C context) {
         super(source, context);
         this.threadQueueLength = threadQueueLength;
      }

      public int getThreadQueueLength() {
         return this.threadQueueLength;
      }
   }

   public static class RefreshCompletedEvent<C extends SecurityContext> extends CachingJWKSetSource.AbstractCachingJWKSetSourceEvent<C> {
      private final JWKSet jwkSet;

      private RefreshCompletedEvent(CachingJWKSetSource<C> source, JWKSet jwkSet, int queueLength, C context) {
         super(source, queueLength, context);
         Objects.requireNonNull(jwkSet);
         this.jwkSet = jwkSet;
      }

      public JWKSet getJWKSet() {
         return this.jwkSet;
      }
   }

   public static class RefreshInitiatedEvent<C extends SecurityContext> extends CachingJWKSetSource.AbstractCachingJWKSetSourceEvent<C> {
      private RefreshInitiatedEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
         super(source, queueLength, context);
      }
   }

   public static class RefreshTimedOutEvent<C extends SecurityContext> extends CachingJWKSetSource.AbstractCachingJWKSetSourceEvent<C> {
      private RefreshTimedOutEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
         super(source, queueLength, context);
      }
   }

   public static class UnableToRefreshEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
      private UnableToRefreshEvent(CachingJWKSetSource<C> source, C context) {
         super(source, context);
      }
   }

   public static class WaitingForRefreshEvent<C extends SecurityContext> extends CachingJWKSetSource.AbstractCachingJWKSetSourceEvent<C> {
      private WaitingForRefreshEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
         super(source, queueLength, context);
      }
   }
}
