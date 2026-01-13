package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ThreadSafe
@Deprecated
public class DefaultJWKSetCache implements JWKSetCache {
   public static final long DEFAULT_LIFESPAN_MINUTES = 15L;
   public static final long DEFAULT_REFRESH_TIME_MINUTES = 5L;
   private final long lifespan;
   private final long refreshTime;
   private final TimeUnit timeUnit;
   private volatile JWKSetWithTimestamp jwkSetWithTimestamp;

   public DefaultJWKSetCache() {
      this(15L, 5L, TimeUnit.MINUTES);
   }

   public DefaultJWKSetCache(long lifespan, long refreshTime, TimeUnit timeUnit) {
      this.lifespan = lifespan;
      this.refreshTime = refreshTime;
      if ((lifespan > -1L || refreshTime > -1L) && timeUnit == null) {
         throw new IllegalArgumentException("A time unit must be specified for non-negative lifespans or refresh times");
      } else {
         this.timeUnit = timeUnit;
      }
   }

   @Override
   public void put(JWKSet jwkSet) {
      JWKSetWithTimestamp updatedJWKSetWithTs;
      if (jwkSet != null) {
         updatedJWKSetWithTs = new JWKSetWithTimestamp(jwkSet);
      } else {
         updatedJWKSetWithTs = null;
      }

      this.jwkSetWithTimestamp = updatedJWKSetWithTs;
   }

   @Override
   public JWKSet get() {
      return this.jwkSetWithTimestamp != null && !this.isExpired() ? this.jwkSetWithTimestamp.getJWKSet() : null;
   }

   @Override
   public boolean requiresRefresh() {
      return this.jwkSetWithTimestamp != null
         && this.refreshTime > -1L
         && new Date().getTime() > this.jwkSetWithTimestamp.getDate().getTime() + TimeUnit.MILLISECONDS.convert(this.refreshTime, this.timeUnit);
   }

   public long getPutTimestamp() {
      return this.jwkSetWithTimestamp != null ? this.jwkSetWithTimestamp.getDate().getTime() : -1L;
   }

   public boolean isExpired() {
      return this.jwkSetWithTimestamp != null
         && this.lifespan > -1L
         && new Date().getTime() > this.jwkSetWithTimestamp.getDate().getTime() + TimeUnit.MILLISECONDS.convert(this.lifespan, this.timeUnit);
   }

   public long getLifespan(TimeUnit timeUnit) {
      return this.lifespan < 0L ? this.lifespan : timeUnit.convert(this.lifespan, this.timeUnit);
   }

   public long getRefreshTime(TimeUnit timeUnit) {
      return this.refreshTime < 0L ? this.refreshTime : timeUnit.convert(this.refreshTime, this.timeUnit);
   }
}
