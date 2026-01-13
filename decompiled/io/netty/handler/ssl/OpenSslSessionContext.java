package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SessionTicketKey;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

public abstract class OpenSslSessionContext implements SSLSessionContext {
   private final OpenSslSessionStats stats;
   private final OpenSslKeyMaterialProvider provider;
   final ReferenceCountedOpenSslContext context;
   private final OpenSslSessionCache sessionCache;
   private final long mask;

   OpenSslSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider, long mask, OpenSslSessionCache cache) {
      this.context = context;
      this.provider = provider;
      this.mask = mask;
      this.stats = new OpenSslSessionStats(context);
      this.sessionCache = cache;
      SSLContext.setSSLSessionCache(context.ctx, cache);
   }

   final boolean useKeyManager() {
      return this.provider != null;
   }

   @Override
   public void setSessionCacheSize(int size) {
      ObjectUtil.checkPositiveOrZero(size, "size");
      this.sessionCache.setSessionCacheSize(size);
   }

   @Override
   public int getSessionCacheSize() {
      return this.sessionCache.getSessionCacheSize();
   }

   @Override
   public void setSessionTimeout(int seconds) {
      ObjectUtil.checkPositiveOrZero(seconds, "seconds");
      Lock writerLock = this.context.ctxLock.writeLock();
      writerLock.lock();

      try {
         SSLContext.setSessionCacheTimeout(this.context.ctx, seconds);
         this.sessionCache.setSessionTimeout(seconds);
      } finally {
         writerLock.unlock();
      }
   }

   @Override
   public int getSessionTimeout() {
      return this.sessionCache.getSessionTimeout();
   }

   @Override
   public SSLSession getSession(byte[] bytes) {
      return this.sessionCache.getSession(new OpenSslSessionId(bytes));
   }

   @Override
   public Enumeration<byte[]> getIds() {
      return new Enumeration<byte[]>() {
         private final Iterator<OpenSslSessionId> ids = OpenSslSessionContext.this.sessionCache.getIds().iterator();

         @Override
         public boolean hasMoreElements() {
            return this.ids.hasNext();
         }

         public byte[] nextElement() {
            return this.ids.next().cloneBytes();
         }
      };
   }

   @Deprecated
   public void setTicketKeys(byte[] keys) {
      if (keys.length % 48 != 0) {
         throw new IllegalArgumentException("keys.length % 48 != 0");
      } else {
         SessionTicketKey[] tickets = new SessionTicketKey[keys.length / 48];
         int i = 0;

         for (int a = 0; i < tickets.length; i++) {
            byte[] name = Arrays.copyOfRange(keys, a, 16);
            a += 16;
            byte[] hmacKey = Arrays.copyOfRange(keys, a, 16);
            i += 16;
            byte[] aesKey = Arrays.copyOfRange(keys, a, 16);
            a += 16;
            tickets[i] = new SessionTicketKey(name, hmacKey, aesKey);
         }

         Lock writerLock = this.context.ctxLock.writeLock();
         writerLock.lock();

         try {
            SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys(this.context.ctx, tickets);
         } finally {
            writerLock.unlock();
         }
      }
   }

   public void setTicketKeys(OpenSslSessionTicketKey... keys) {
      ObjectUtil.checkNotNull(keys, "keys");
      SessionTicketKey[] ticketKeys = new SessionTicketKey[keys.length];

      for (int i = 0; i < ticketKeys.length; i++) {
         ticketKeys[i] = keys[i].key;
      }

      Lock writerLock = this.context.ctxLock.writeLock();
      writerLock.lock();

      try {
         SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
         if (ticketKeys.length > 0) {
            SSLContext.setSessionTicketKeys(this.context.ctx, ticketKeys);
         }
      } finally {
         writerLock.unlock();
      }
   }

   public void setSessionCacheEnabled(boolean enabled) {
      long mode = enabled ? this.mask | SSL.SSL_SESS_CACHE_NO_INTERNAL_LOOKUP | SSL.SSL_SESS_CACHE_NO_INTERNAL_STORE : SSL.SSL_SESS_CACHE_OFF;
      Lock writerLock = this.context.ctxLock.writeLock();
      writerLock.lock();

      try {
         SSLContext.setSessionCacheMode(this.context.ctx, mode);
         if (!enabled) {
            this.sessionCache.clear();
         }
      } finally {
         writerLock.unlock();
      }
   }

   public boolean isSessionCacheEnabled() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      boolean var2;
      try {
         var2 = (SSLContext.getSessionCacheMode(this.context.ctx) & this.mask) != 0L;
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public OpenSslSessionStats stats() {
      return this.stats;
   }

   final void removeFromCache(OpenSslSessionId id) {
      this.sessionCache.removeSessionWithId(id);
   }

   final boolean isInCache(OpenSslSessionId id) {
      return this.sessionCache.containsSessionWithId(id);
   }

   boolean setSessionFromCache(long ssl, OpenSslInternalSession session, String host, int port) {
      return this.sessionCache.setSession(ssl, session, host, port);
   }

   final void destroy() {
      if (this.provider != null) {
         this.provider.destroy();
      }

      this.sessionCache.clear();
   }
}
