package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;

@ThreadSafe
abstract class AbstractCachingJWKSetSource<C extends SecurityContext> extends JWKSetSourceWrapper<C> {
   private volatile CachedObject<JWKSet> cachedJWKSet;
   private final long timeToLive;

   AbstractCachingJWKSetSource(JWKSetSource<C> source, long timeToLive) {
      super(source);
      this.timeToLive = timeToLive;
   }

   CachedObject<JWKSet> getCachedJWKSet() {
      return this.cachedJWKSet;
   }

   void setCachedJWKSet(CachedObject<JWKSet> cachedJWKSet) {
      this.cachedJWKSet = cachedJWKSet;
   }

   CachedObject<JWKSet> getCachedJWKSetIfValid(long currentTime) {
      CachedObject<JWKSet> threadSafeCache = this.getCachedJWKSet();
      return threadSafeCache != null && threadSafeCache.isValid(currentTime) ? threadSafeCache : null;
   }

   public long getTimeToLive() {
      return this.timeToLive;
   }

   CachedObject<JWKSet> cacheJWKSet(JWKSet jwkSet, long fetchTime) {
      long currentTime = this.currentTimeMillis();
      CachedObject<JWKSet> cachedJWKSet = new CachedObject<>(jwkSet, currentTime, CachedObject.computeExpirationTime(fetchTime, this.getTimeToLive()));
      this.setCachedJWKSet(cachedJWKSet);
      return cachedJWKSet;
   }

   long currentTimeMillis() {
      return System.currentTimeMillis();
   }
}
