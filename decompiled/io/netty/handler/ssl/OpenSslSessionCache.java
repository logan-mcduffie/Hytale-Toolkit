package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLSession;
import io.netty.internal.tcnative.SSLSessionCache;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SystemPropertyUtil;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.security.cert.X509Certificate;

class OpenSslSessionCache implements SSLSessionCache {
   private static final OpenSslInternalSession[] EMPTY_SESSIONS = new OpenSslInternalSession[0];
   private static final int DEFAULT_CACHE_SIZE;
   private final Map<Long, ReferenceCountedOpenSslEngine> engines;
   private final Map<OpenSslSessionId, OpenSslSessionCache.NativeSslSession> sessions = new LinkedHashMap<OpenSslSessionId, OpenSslSessionCache.NativeSslSession>(
      
   ) {
      private static final long serialVersionUID = -7773696788135734448L;

      @Override
      protected boolean removeEldestEntry(Entry<OpenSslSessionId, OpenSslSessionCache.NativeSslSession> eldest) {
         int maxSize = OpenSslSessionCache.this.maximumCacheSize.get();
         if (maxSize >= 0 && this.size() > maxSize) {
            OpenSslSessionCache.this.removeSessionWithId(eldest.getKey());
         }

         return false;
      }
   };
   private final AtomicInteger maximumCacheSize;
   private final AtomicInteger sessionTimeout;
   private int sessionCounter;

   OpenSslSessionCache(Map<Long, ReferenceCountedOpenSslEngine> engines) {
      this.maximumCacheSize = new AtomicInteger(DEFAULT_CACHE_SIZE);
      this.sessionTimeout = new AtomicInteger(300);
      this.engines = engines;
   }

   final void setSessionTimeout(int seconds) {
      int oldTimeout = this.sessionTimeout.getAndSet(seconds);
      if (oldTimeout > seconds) {
         this.clear();
      }
   }

   final int getSessionTimeout() {
      return this.sessionTimeout.get();
   }

   protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession session) {
      return true;
   }

   protected void sessionRemoved(OpenSslSessionCache.NativeSslSession session) {
   }

   final void setSessionCacheSize(int size) {
      long oldSize = this.maximumCacheSize.getAndSet(size);
      if (oldSize > size || size == 0) {
         this.clear();
      }
   }

   final int getSessionCacheSize() {
      return this.maximumCacheSize.get();
   }

   private void expungeInvalidSessions() {
      if (!this.sessions.isEmpty()) {
         long now = System.currentTimeMillis();
         Iterator<Entry<OpenSslSessionId, OpenSslSessionCache.NativeSslSession>> iterator = this.sessions.entrySet().iterator();

         while (iterator.hasNext()) {
            OpenSslSessionCache.NativeSslSession session = iterator.next().getValue();
            if (session.isValid(now)) {
               break;
            }

            iterator.remove();
            this.notifyRemovalAndFree(session);
         }
      }
   }

   public boolean sessionCreated(long ssl, long sslSession) {
      ReferenceCountedOpenSslEngine engine = this.engines.get(ssl);
      if (engine == null) {
         return false;
      } else {
         OpenSslInternalSession openSslSession = (OpenSslInternalSession)engine.getSession();
         OpenSslSessionCache.NativeSslSession session = new OpenSslSessionCache.NativeSslSession(
            sslSession, engine.getPeerHost(), engine.getPeerPort(), this.getSessionTimeout() * 1000L, openSslSession.keyValueStorage()
         );
         openSslSession.setSessionDetails(session.creationTime, session.lastAccessedTime, session.sessionId(), session.keyValueStorage);
         synchronized (this) {
            if (++this.sessionCounter == 255) {
               this.sessionCounter = 0;
               this.expungeInvalidSessions();
            }

            if (!this.sessionCreated(session)) {
               session.close();
               return false;
            } else {
               OpenSslSessionCache.NativeSslSession old = this.sessions.put(session.sessionId(), session);
               if (old != null) {
                  this.notifyRemovalAndFree(old);
               }

               return true;
            }
         }
      }
   }

   public final long getSession(long ssl, byte[] sessionId) {
      OpenSslSessionId id = new OpenSslSessionId(sessionId);
      OpenSslSessionCache.NativeSslSession session;
      synchronized (this) {
         session = this.sessions.get(id);
         if (session == null) {
            return -1L;
         }

         if (!session.isValid() || !session.upRef()) {
            this.removeSessionWithId(session.sessionId());
            return -1L;
         }

         if (session.shouldBeSingleUse()) {
            this.removeSessionWithId(session.sessionId());
         }
      }

      session.setLastAccessedTime(System.currentTimeMillis());
      ReferenceCountedOpenSslEngine engine = this.engines.get(ssl);
      if (engine != null) {
         OpenSslInternalSession sslSession = (OpenSslInternalSession)engine.getSession();
         sslSession.setSessionDetails(session.getCreationTime(), session.getLastAccessedTime(), session.sessionId(), session.keyValueStorage);
      }

      return session.session();
   }

   boolean setSession(long ssl, OpenSslInternalSession session, String host, int port) {
      return false;
   }

   final synchronized void removeSessionWithId(OpenSslSessionId id) {
      OpenSslSessionCache.NativeSslSession sslSession = this.sessions.remove(id);
      if (sslSession != null) {
         this.notifyRemovalAndFree(sslSession);
      }
   }

   final synchronized boolean containsSessionWithId(OpenSslSessionId id) {
      return this.sessions.containsKey(id);
   }

   private void notifyRemovalAndFree(OpenSslSessionCache.NativeSslSession session) {
      this.sessionRemoved(session);
      session.free();
   }

   final synchronized OpenSslInternalSession getSession(OpenSslSessionId id) {
      OpenSslSessionCache.NativeSslSession session = this.sessions.get(id);
      if (session != null && !session.isValid()) {
         this.removeSessionWithId(session.sessionId());
         return null;
      } else {
         return session;
      }
   }

   final List<OpenSslSessionId> getIds() {
      OpenSslInternalSession[] sessionsArray;
      synchronized (this) {
         sessionsArray = this.sessions.values().toArray(EMPTY_SESSIONS);
      }

      List<OpenSslSessionId> ids = new ArrayList<>(sessionsArray.length);

      for (OpenSslInternalSession session : sessionsArray) {
         if (session.isValid()) {
            ids.add(session.sessionId());
         }
      }

      return ids;
   }

   synchronized void clear() {
      Iterator<Entry<OpenSslSessionId, OpenSslSessionCache.NativeSslSession>> iterator = this.sessions.entrySet().iterator();

      while (iterator.hasNext()) {
         OpenSslSessionCache.NativeSslSession session = iterator.next().getValue();
         iterator.remove();
         this.notifyRemovalAndFree(session);
      }
   }

   static {
      int cacheSize = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
      if (cacheSize >= 0) {
         DEFAULT_CACHE_SIZE = cacheSize;
      } else {
         DEFAULT_CACHE_SIZE = 20480;
      }
   }

   static final class NativeSslSession implements OpenSslInternalSession {
      static final ResourceLeakDetector<OpenSslSessionCache.NativeSslSession> LEAK_DETECTOR = ResourceLeakDetectorFactory.instance()
         .newResourceLeakDetector(OpenSslSessionCache.NativeSslSession.class);
      private final ResourceLeakTracker<OpenSslSessionCache.NativeSslSession> leakTracker;
      final Map<String, Object> keyValueStorage;
      private final long session;
      private final String peerHost;
      private final int peerPort;
      private final OpenSslSessionId id;
      private final long timeout;
      private final long creationTime = System.currentTimeMillis();
      private volatile long lastAccessedTime = this.creationTime;
      private volatile boolean valid = true;
      private boolean freed;

      NativeSslSession(long session, String peerHost, int peerPort, long timeout, Map<String, Object> keyValueStorage) {
         this.session = session;
         this.peerHost = peerHost;
         this.peerPort = peerPort;
         this.timeout = timeout;
         this.id = new OpenSslSessionId(SSLSession.getSessionId(session));
         this.keyValueStorage = keyValueStorage;
         this.leakTracker = LEAK_DETECTOR.track(this);
      }

      @Override
      public Map<String, Object> keyValueStorage() {
         return this.keyValueStorage;
      }

      @Override
      public void prepareHandshake() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setSessionDetails(long creationTime, long lastAccessedTime, OpenSslSessionId id, Map<String, Object> keyValueStorage) {
         throw new UnsupportedOperationException();
      }

      boolean shouldBeSingleUse() {
         assert !this.freed;

         return SSLSession.shouldBeSingleUse(this.session);
      }

      long session() {
         assert !this.freed;

         return this.session;
      }

      boolean upRef() {
         assert !this.freed;

         return SSLSession.upRef(this.session);
      }

      synchronized void free() {
         this.close();
         SSLSession.free(this.session);
      }

      void close() {
         assert !this.freed;

         this.freed = true;
         this.invalidate();
         if (this.leakTracker != null) {
            this.leakTracker.close(this);
         }
      }

      @Override
      public OpenSslSessionId sessionId() {
         return this.id;
      }

      boolean isValid(long now) {
         return this.creationTime + this.timeout >= now && this.valid;
      }

      @Override
      public void setLocalCertificate(Certificate[] localCertificate) {
         throw new UnsupportedOperationException();
      }

      @Override
      public OpenSslSessionContext getSessionContext() {
         return null;
      }

      @Override
      public void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void handshakeFinished(
         byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout
      ) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte[] getId() {
         return this.id.cloneBytes();
      }

      @Override
      public long getCreationTime() {
         return this.creationTime;
      }

      @Override
      public void setLastAccessedTime(long time) {
         this.lastAccessedTime = time;
      }

      @Override
      public long getLastAccessedTime() {
         return this.lastAccessedTime;
      }

      @Override
      public void invalidate() {
         this.valid = false;
      }

      @Override
      public boolean isValid() {
         return this.isValid(System.currentTimeMillis());
      }

      @Override
      public void putValue(String name, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object getValue(String name) {
         return null;
      }

      @Override
      public void removeValue(String name) {
      }

      @Override
      public String[] getValueNames() {
         return EmptyArrays.EMPTY_STRINGS;
      }

      @Override
      public Certificate[] getPeerCertificates() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasPeerCertificates() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Certificate[] getLocalCertificates() {
         throw new UnsupportedOperationException();
      }

      @Override
      public X509Certificate[] getPeerCertificateChain() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Principal getPeerPrincipal() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Principal getLocalPrincipal() {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getCipherSuite() {
         return null;
      }

      @Override
      public String getProtocol() {
         return null;
      }

      @Override
      public String getPeerHost() {
         return this.peerHost;
      }

      @Override
      public int getPeerPort() {
         return this.peerPort;
      }

      @Override
      public int getPacketBufferSize() {
         return ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
      }

      @Override
      public int getApplicationBufferSize() {
         return ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
      }

      @Override
      public int hashCode() {
         return this.id.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof OpenSslInternalSession)) {
            return false;
         } else {
            OpenSslInternalSession session1 = (OpenSslInternalSession)o;
            return this.id.equals(session1.sessionId());
         }
      }
   }
}
