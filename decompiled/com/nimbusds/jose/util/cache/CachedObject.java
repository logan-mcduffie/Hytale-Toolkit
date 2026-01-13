package com.nimbusds.jose.util.cache;

import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.Objects;

@Immutable
public final class CachedObject<V> {
   private final V object;
   private final long timestamp;
   private final long expirationTime;

   public static long computeExpirationTime(long currentTime, long timeToLive) {
      long expirationTime = currentTime + timeToLive;
      return expirationTime < 0L ? Long.MAX_VALUE : expirationTime;
   }

   public CachedObject(V object, long timestamp, long expirationTime) {
      this.object = Objects.requireNonNull(object);
      this.timestamp = timestamp;
      this.expirationTime = expirationTime;
   }

   public V get() {
      return this.object;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public long getExpirationTime() {
      return this.expirationTime;
   }

   public boolean isValid(long currentTime) {
      return currentTime < this.expirationTime;
   }

   public boolean isExpired(long currentTime) {
      return !this.isValid(currentTime);
   }
}
