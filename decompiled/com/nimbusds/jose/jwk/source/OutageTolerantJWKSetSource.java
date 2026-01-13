package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;
import com.nimbusds.jose.util.events.EventListener;
import java.util.Objects;

@ThreadSafe
public class OutageTolerantJWKSetSource<C extends SecurityContext> extends AbstractCachingJWKSetSource<C> {
   private final EventListener<OutageTolerantJWKSetSource<C>, C> eventListener;

   public OutageTolerantJWKSetSource(JWKSetSource<C> source, long timeToLive, EventListener<OutageTolerantJWKSetSource<C>, C> eventListener) {
      super(source, timeToLive);
      this.eventListener = eventListener;
   }

   @Override
   public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      try {
         JWKSet jwkSet = this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
         this.cacheJWKSet(jwkSet, currentTime);
         return jwkSet;
      } catch (JWKSetUnavailableException var11) {
         CachedObject<JWKSet> cache = this.getCachedJWKSet();
         if (cache != null && cache.isValid(currentTime)) {
            long remainingTime = cache.getExpirationTime() - currentTime;
            if (this.eventListener != null) {
               this.eventListener.notify(new OutageTolerantJWKSetSource.OutageEvent<>(this, var11, remainingTime, context));
            }

            JWKSet jwkSetx = cache.get();
            JWKSet jwkSetClone = new JWKSet(jwkSetx.getKeys());
            if (!refreshEvaluator.requiresRefresh(jwkSetClone)) {
               return jwkSetClone;
            }
         }

         throw var11;
      }
   }

   public static class OutageEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<OutageTolerantJWKSetSource<C>, C> {
      private final Exception exception;
      private final long remainingTime;

      private OutageEvent(OutageTolerantJWKSetSource<C> source, Exception exception, long remainingTime, C context) {
         super(source, context);
         Objects.requireNonNull(exception);
         this.exception = exception;
         this.remainingTime = remainingTime;
      }

      public Exception getException() {
         return this.exception;
      }

      public long getRemainingTime() {
         return this.remainingTime;
      }
   }
}
