package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jose.util.events.EventListener;
import com.nimbusds.jose.util.health.HealthReportListener;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class JWKSourceBuilder<C extends SecurityContext> {
   public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 500;
   public static final int DEFAULT_HTTP_READ_TIMEOUT = 500;
   public static final int DEFAULT_HTTP_SIZE_LIMIT = 51200;
   public static final long DEFAULT_CACHE_TIME_TO_LIVE = 300000L;
   public static final long DEFAULT_CACHE_REFRESH_TIMEOUT = 15000L;
   public static final long DEFAULT_REFRESH_AHEAD_TIME = 30000L;
   public static final long DEFAULT_RATE_LIMIT_MIN_INTERVAL = 30000L;
   private final JWKSetSource<C> jwkSetSource;
   private boolean caching = true;
   private long cacheTimeToLive = 300000L;
   private long cacheRefreshTimeout = 15000L;
   private EventListener<CachingJWKSetSource<C>, C> cachingEventListener;
   private boolean refreshAhead = true;
   private long refreshAheadTime = 30000L;
   private boolean refreshAheadScheduled = false;
   private ExecutorService executorService;
   private boolean shutdownExecutorOnClose = true;
   private ScheduledExecutorService scheduledExecutorService;
   private boolean shutdownScheduledExecutorOnClose = true;
   private boolean rateLimited = true;
   private long minTimeInterval = 30000L;
   private EventListener<RateLimitedJWKSetSource<C>, C> rateLimitedEventListener;
   private boolean retrying = false;
   private EventListener<RetryingJWKSetSource<C>, C> retryingEventListener;
   private boolean outageTolerant = false;
   private long outageCacheTimeToLive = -1L;
   private EventListener<OutageTolerantJWKSetSource<C>, C> outageEventListener;
   private HealthReportListener<JWKSetSourceWithHealthStatusReporting<C>, C> healthReportListener;
   protected JWKSource<C> failover;

   public static <C extends SecurityContext> JWKSourceBuilder<C> create(URL jwkSetURL) {
      DefaultResourceRetriever retriever = new DefaultResourceRetriever(500, 500, 51200);
      JWKSetSource<C> jwkSetSource = new URLBasedJWKSetSource<>(jwkSetURL, retriever);
      return new JWKSourceBuilder<>(jwkSetSource);
   }

   public static <C extends SecurityContext> JWKSourceBuilder<C> create(URL jwkSetURL, ResourceRetriever retriever) {
      return new JWKSourceBuilder<>(new URLBasedJWKSetSource<>(jwkSetURL, retriever));
   }

   public static <C extends SecurityContext> JWKSourceBuilder<C> create(JWKSetSource<C> source) {
      return new JWKSourceBuilder<>(source);
   }

   private JWKSourceBuilder(JWKSetSource<C> jwkSetSource) {
      Objects.requireNonNull(jwkSetSource);
      this.jwkSetSource = jwkSetSource;
   }

   public JWKSourceBuilder<C> cache(boolean enable) {
      this.caching = enable;
      return this;
   }

   public JWKSourceBuilder<C> cache(long timeToLive, long cacheRefreshTimeout) {
      this.caching = true;
      this.cacheTimeToLive = timeToLive;
      this.cacheRefreshTimeout = cacheRefreshTimeout;
      return this;
   }

   public JWKSourceBuilder<C> cache(long timeToLive, long cacheRefreshTimeout, EventListener<CachingJWKSetSource<C>, C> eventListener) {
      this.caching = true;
      this.cacheTimeToLive = timeToLive;
      this.cacheRefreshTimeout = cacheRefreshTimeout;
      this.cachingEventListener = eventListener;
      return this;
   }

   public JWKSourceBuilder<C> cacheForever() {
      this.caching = true;
      this.cacheTimeToLive = Long.MAX_VALUE;
      this.refreshAhead = false;
      return this;
   }

   public JWKSourceBuilder<C> refreshAheadCache(boolean enable) {
      if (enable) {
         this.caching = true;
      }

      this.refreshAhead = enable;
      return this;
   }

   public JWKSourceBuilder<C> refreshAheadCache(long refreshAheadTime, boolean scheduled) {
      this.caching = true;
      this.refreshAhead = true;
      this.refreshAheadTime = refreshAheadTime;
      this.refreshAheadScheduled = scheduled;
      return this;
   }

   public JWKSourceBuilder<C> refreshAheadCache(long refreshAheadTime, boolean scheduled, EventListener<CachingJWKSetSource<C>, C> eventListener) {
      this.caching = true;
      this.refreshAhead = true;
      this.refreshAheadTime = refreshAheadTime;
      this.refreshAheadScheduled = scheduled;
      this.cachingEventListener = eventListener;
      return this;
   }

   public JWKSourceBuilder<C> refreshAheadCache(
      long refreshAheadTime,
      EventListener<CachingJWKSetSource<C>, C> eventListener,
      ExecutorService executorService,
      boolean shutdownExecutorOnClose,
      ScheduledExecutorService scheduledExecutorService,
      boolean shutdownScheduledExecutorOnClose
   ) {
      this.caching = true;
      this.refreshAhead = true;
      this.refreshAheadTime = refreshAheadTime;
      this.refreshAheadScheduled = scheduledExecutorService != null;
      this.cachingEventListener = eventListener;
      this.executorService = executorService;
      this.shutdownExecutorOnClose = shutdownExecutorOnClose;
      this.scheduledExecutorService = scheduledExecutorService;
      this.shutdownScheduledExecutorOnClose = shutdownScheduledExecutorOnClose;
      return this;
   }

   public JWKSourceBuilder<C> rateLimited(boolean enable) {
      this.rateLimited = enable;
      return this;
   }

   public JWKSourceBuilder<C> rateLimited(long minTimeInterval) {
      this.rateLimited = true;
      this.minTimeInterval = minTimeInterval;
      return this;
   }

   public JWKSourceBuilder<C> rateLimited(long minTimeInterval, EventListener<RateLimitedJWKSetSource<C>, C> eventListener) {
      this.rateLimited = true;
      this.minTimeInterval = minTimeInterval;
      this.rateLimitedEventListener = eventListener;
      return this;
   }

   public JWKSourceBuilder<C> failover(JWKSource<C> failover) {
      this.failover = failover;
      return this;
   }

   public JWKSourceBuilder<C> retrying(boolean enable) {
      this.retrying = enable;
      return this;
   }

   public JWKSourceBuilder<C> retrying(EventListener<RetryingJWKSetSource<C>, C> eventListener) {
      this.retrying = true;
      this.retryingEventListener = eventListener;
      return this;
   }

   public JWKSourceBuilder<C> healthReporting(HealthReportListener<JWKSetSourceWithHealthStatusReporting<C>, C> listener) {
      this.healthReportListener = listener;
      return this;
   }

   public JWKSourceBuilder<C> outageTolerant(boolean enable) {
      this.outageTolerant = enable;
      return this;
   }

   public JWKSourceBuilder<C> outageTolerantForever() {
      this.outageTolerant = true;
      this.outageCacheTimeToLive = Long.MAX_VALUE;
      return this;
   }

   public JWKSourceBuilder<C> outageTolerant(long timeToLive) {
      this.outageTolerant = true;
      this.outageCacheTimeToLive = timeToLive;
      return this;
   }

   public JWKSourceBuilder<C> outageTolerant(long timeToLive, EventListener<OutageTolerantJWKSetSource<C>, C> eventListener) {
      this.outageTolerant = true;
      this.outageCacheTimeToLive = timeToLive;
      this.outageEventListener = eventListener;
      return this;
   }

   public JWKSource<C> build() {
      if (!this.caching && this.rateLimited) {
         throw new IllegalStateException("Rate limiting requires caching");
      } else if (!this.caching && this.refreshAhead) {
         throw new IllegalStateException("Refresh-ahead caching requires general caching");
      } else if (this.caching && this.rateLimited && this.cacheTimeToLive <= this.minTimeInterval) {
         throw new IllegalStateException("The rate limiting min time interval between requests must be less than the cache time-to-live");
      } else if (this.caching && this.outageTolerant && this.cacheTimeToLive == Long.MAX_VALUE && this.outageCacheTimeToLive == Long.MAX_VALUE) {
         throw new IllegalStateException("Outage tolerance not necessary with a non-expiring cache");
      } else if (this.caching && this.refreshAhead && this.cacheTimeToLive == Long.MAX_VALUE) {
         throw new IllegalStateException("Refresh-ahead caching not necessary with a non-expiring cache");
      } else {
         JWKSetSource<C> source = this.jwkSetSource;
         if (this.retrying) {
            source = new RetryingJWKSetSource<>(source, this.retryingEventListener);
         }

         if (this.outageTolerant) {
            if (this.outageCacheTimeToLive == -1L) {
               if (this.caching) {
                  this.outageCacheTimeToLive = this.cacheTimeToLive * 10L;
               } else {
                  this.outageCacheTimeToLive = 3000000L;
               }
            }

            source = new OutageTolerantJWKSetSource<>(source, this.outageCacheTimeToLive, this.outageEventListener);
         }

         if (this.healthReportListener != null) {
            source = new JWKSetSourceWithHealthStatusReporting<>(source, this.healthReportListener);
         }

         if (this.rateLimited) {
            source = new RateLimitedJWKSetSource<>(source, this.minTimeInterval, this.rateLimitedEventListener);
         }

         if (this.refreshAhead) {
            if (this.refreshAheadScheduled && this.scheduledExecutorService == null) {
               this.scheduledExecutorService = RefreshAheadCachingJWKSetSource.createDefaultScheduledExecutorService();
            }

            if (this.executorService == null) {
               this.executorService = RefreshAheadCachingJWKSetSource.createDefaultExecutorService();
            }

            source = new RefreshAheadCachingJWKSetSource<>(
               source,
               this.cacheTimeToLive,
               this.cacheRefreshTimeout,
               this.refreshAheadTime,
               this.executorService,
               this.shutdownExecutorOnClose,
               this.cachingEventListener,
               this.scheduledExecutorService,
               this.shutdownScheduledExecutorOnClose
            );
         } else if (this.caching) {
            source = new CachingJWKSetSource<>(source, this.cacheTimeToLive, this.cacheRefreshTimeout, this.cachingEventListener);
         }

         JWKSource<C> jwkSource = new JWKSetBasedJWKSource<>(source);
         return (JWKSource<C>)(this.failover != null ? new JWKSourceWithFailover<>(jwkSource, this.failover) : jwkSource);
      }
   }
}
