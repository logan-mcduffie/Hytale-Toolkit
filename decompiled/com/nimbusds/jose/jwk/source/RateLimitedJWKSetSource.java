package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.events.EventListener;

@ThreadSafe
public class RateLimitedJWKSetSource<C extends SecurityContext> extends JWKSetSourceWrapper<C> {
   private final long minTimeInterval;
   private long nextOpeningTime = -1L;
   private int counter = 0;
   private final EventListener<RateLimitedJWKSetSource<C>, C> eventListener;

   public RateLimitedJWKSetSource(JWKSetSource<C> source, long minTimeInterval, EventListener<RateLimitedJWKSetSource<C>, C> eventListener) {
      super(source);
      this.minTimeInterval = minTimeInterval;
      this.eventListener = eventListener;
   }

   @Override
   public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      boolean rateLimitHit;
      synchronized (this) {
         if (this.nextOpeningTime <= currentTime) {
            this.nextOpeningTime = currentTime + this.minTimeInterval;
            this.counter = 1;
            rateLimitHit = false;
         } else {
            rateLimitHit = this.counter <= 0;
            if (!rateLimitHit) {
               this.counter--;
            }
         }
      }

      if (rateLimitHit) {
         if (this.eventListener != null) {
            this.eventListener.notify(new RateLimitedJWKSetSource.RateLimitedEvent<>(this, context));
         }

         throw new RateLimitReachedException();
      } else {
         return this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
      }
   }

   public long getMinTimeInterval() {
      return this.minTimeInterval;
   }

   public static class RateLimitedEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<RateLimitedJWKSetSource<C>, C> {
      private RateLimitedEvent(RateLimitedJWKSetSource<C> source, C securityContext) {
         super(source, securityContext);
      }
   }
}
