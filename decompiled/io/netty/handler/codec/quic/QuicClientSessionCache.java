package io.netty.handler.codec.quic;

import io.netty.util.AsciiString;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;

final class QuicClientSessionCache {
   private static final int DEFAULT_CACHE_SIZE;
   private final AtomicInteger maximumCacheSize;
   private final AtomicInteger sessionTimeout;
   private int sessionCounter;
   private final Map<QuicClientSessionCache.HostPort, QuicClientSessionCache.SessionHolder> sessions;

   QuicClientSessionCache() {
      this.maximumCacheSize = new AtomicInteger(DEFAULT_CACHE_SIZE);
      this.sessionTimeout = new AtomicInteger(300);
      this.sessions = new LinkedHashMap<QuicClientSessionCache.HostPort, QuicClientSessionCache.SessionHolder>() {
         private static final long serialVersionUID = -7773696788135734448L;

         @Override
         protected boolean removeEldestEntry(Entry<QuicClientSessionCache.HostPort, QuicClientSessionCache.SessionHolder> eldest) {
            int maxSize = QuicClientSessionCache.this.maximumCacheSize.get();
            return maxSize >= 0 && this.size() > maxSize;
         }
      };
   }

   void saveSession(@Nullable String host, int port, long creationTime, long timeout, byte[] session, boolean isSingleUse) {
      QuicClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort != null) {
         synchronized (this.sessions) {
            if (++this.sessionCounter == 255) {
               this.sessionCounter = 0;
               this.expungeInvalidSessions();
            }

            this.sessions.put(hostPort, new QuicClientSessionCache.SessionHolder(creationTime, timeout, session, isSingleUse));
         }
      }
   }

   boolean hasSession(@Nullable String host, int port) {
      QuicClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort != null) {
         synchronized (this.sessions) {
            return this.sessions.containsKey(hostPort);
         }
      } else {
         return false;
      }
   }

   byte @Nullable [] getSession(@Nullable String host, int port) {
      QuicClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort != null) {
         QuicClientSessionCache.SessionHolder sessionHolder;
         synchronized (this.sessions) {
            sessionHolder = this.sessions.get(hostPort);
            if (sessionHolder == null) {
               return null;
            }

            if (sessionHolder.isSingleUse()) {
               this.sessions.remove(hostPort);
            }
         }

         if (sessionHolder.isValid()) {
            return sessionHolder.sessionBytes();
         }
      }

      return null;
   }

   void removeSession(@Nullable String host, int port) {
      QuicClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort != null) {
         synchronized (this.sessions) {
            this.sessions.remove(hostPort);
         }
      }
   }

   void setSessionTimeout(int seconds) {
      int oldTimeout = this.sessionTimeout.getAndSet(seconds);
      if (oldTimeout > seconds) {
         this.clear();
      }
   }

   int getSessionTimeout() {
      return this.sessionTimeout.get();
   }

   void setSessionCacheSize(int size) {
      long oldSize = this.maximumCacheSize.getAndSet(size);
      if (oldSize > size || size == 0) {
         this.clear();
      }
   }

   int getSessionCacheSize() {
      return this.maximumCacheSize.get();
   }

   void clear() {
      synchronized (this.sessions) {
         this.sessions.clear();
      }
   }

   private void expungeInvalidSessions() {
      assert Thread.holdsLock(this.sessions);

      if (!this.sessions.isEmpty()) {
         long now = System.currentTimeMillis();
         Iterator<Entry<QuicClientSessionCache.HostPort, QuicClientSessionCache.SessionHolder>> iterator = this.sessions.entrySet().iterator();

         while (iterator.hasNext()) {
            QuicClientSessionCache.SessionHolder sessionHolder = iterator.next().getValue();
            if (sessionHolder.isValid(now)) {
               break;
            }

            iterator.remove();
         }
      }
   }

   @Nullable
   private static QuicClientSessionCache.HostPort keyFor(@Nullable String host, int port) {
      return host == null && port < 1 ? null : new QuicClientSessionCache.HostPort(host, port);
   }

   static {
      int cacheSize = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
      if (cacheSize >= 0) {
         DEFAULT_CACHE_SIZE = cacheSize;
      } else {
         DEFAULT_CACHE_SIZE = 20480;
      }
   }

   private static final class HostPort {
      private final int hash;
      private final String host;
      private final int port;

      HostPort(@Nullable String host, int port) {
         this.host = host;
         this.port = port;
         this.hash = 31 * AsciiString.hashCode(host) + port;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof QuicClientSessionCache.HostPort)) {
            return false;
         } else {
            QuicClientSessionCache.HostPort other = (QuicClientSessionCache.HostPort)obj;
            return this.port == other.port && this.host.equalsIgnoreCase(other.host);
         }
      }

      @Override
      public String toString() {
         return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
      }
   }

   private static final class SessionHolder {
      private final long creationTime;
      private final long timeout;
      private final byte[] sessionBytes;
      private final boolean isSingleUse;

      SessionHolder(long creationTime, long timeout, byte[] session, boolean isSingleUse) {
         this.creationTime = creationTime;
         this.timeout = timeout;
         this.sessionBytes = session;
         this.isSingleUse = isSingleUse;
      }

      boolean isValid() {
         return this.isValid(System.currentTimeMillis());
      }

      boolean isValid(long current) {
         return current <= this.creationTime + this.timeout;
      }

      boolean isSingleUse() {
         return this.isSingleUse;
      }

      byte[] sessionBytes() {
         return this.sessionBytes;
      }
   }
}
