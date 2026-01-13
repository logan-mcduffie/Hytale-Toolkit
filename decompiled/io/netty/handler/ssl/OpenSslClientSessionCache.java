package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class OpenSslClientSessionCache extends OpenSslSessionCache {
   private final Map<OpenSslClientSessionCache.HostPort, Set<OpenSslSessionCache.NativeSslSession>> sessions = new HashMap<>();

   OpenSslClientSessionCache(Map<Long, ReferenceCountedOpenSslEngine> engines) {
      super(engines);
   }

   @Override
   protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession session) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
      if (hostPort == null) {
         return false;
      } else {
         Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
         if (sessionsForHost == null) {
            sessionsForHost = new HashSet<>(4);
            this.sessions.put(hostPort, sessionsForHost);
         }

         sessionsForHost.add(session);
         return true;
      }
   }

   @Override
   protected void sessionRemoved(OpenSslSessionCache.NativeSslSession session) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
      if (hostPort != null) {
         Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
         if (sessionsForHost != null) {
            sessionsForHost.remove(session);
            if (sessionsForHost.isEmpty()) {
               this.sessions.remove(hostPort);
            }
         }
      }
   }

   @Override
   boolean setSession(long ssl, OpenSslInternalSession session, String host, int port) {
      OpenSslClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort == null) {
         return false;
      } else {
         OpenSslSessionCache.NativeSslSession nativeSslSession = null;
         boolean singleUsed = false;
         boolean reused;
         synchronized (this) {
            Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
            if (sessionsForHost == null) {
               return false;
            }

            if (sessionsForHost.isEmpty()) {
               this.sessions.remove(hostPort);
               return false;
            }

            List<OpenSslSessionCache.NativeSslSession> toBeRemoved = null;

            for (OpenSslSessionCache.NativeSslSession sslSession : sessionsForHost) {
               if (sslSession.isValid()) {
                  nativeSslSession = sslSession;
                  break;
               }

               if (toBeRemoved == null) {
                  toBeRemoved = new ArrayList<>(2);
               }

               toBeRemoved.add(sslSession);
            }

            if (toBeRemoved != null) {
               for (OpenSslSessionCache.NativeSslSession sslSession : toBeRemoved) {
                  this.removeSessionWithId(sslSession.sessionId());
               }
            }

            if (nativeSslSession == null) {
               return false;
            }

            reused = SSL.setSession(ssl, nativeSslSession.session());
            if (reused) {
               singleUsed = nativeSslSession.shouldBeSingleUse();
            }
         }

         if (reused) {
            if (singleUsed) {
               nativeSslSession.invalidate();
               session.invalidate();
            }

            nativeSslSession.setLastAccessedTime(System.currentTimeMillis());
            session.setSessionDetails(
               nativeSslSession.getCreationTime(), nativeSslSession.getLastAccessedTime(), nativeSslSession.sessionId(), nativeSslSession.keyValueStorage
            );
         }

         return reused;
      }
   }

   private static OpenSslClientSessionCache.HostPort keyFor(String host, int port) {
      return host == null && port < 1 ? null : new OpenSslClientSessionCache.HostPort(host, port);
   }

   @Override
   synchronized void clear() {
      super.clear();
      this.sessions.clear();
   }

   private static final class HostPort {
      private final int hash;
      private final String host;
      private final int port;

      HostPort(String host, int port) {
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
         if (!(obj instanceof OpenSslClientSessionCache.HostPort)) {
            return false;
         } else {
            OpenSslClientSessionCache.HostPort other = (OpenSslClientSessionCache.HostPort)obj;
            return this.port == other.port && this.host.equalsIgnoreCase(other.host);
         }
      }

      @Override
      public String toString() {
         return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
      }
   }
}
