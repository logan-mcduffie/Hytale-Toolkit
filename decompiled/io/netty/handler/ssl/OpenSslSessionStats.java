package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;

public final class OpenSslSessionStats {
   private final ReferenceCountedOpenSslContext context;

   OpenSslSessionStats(ReferenceCountedOpenSslContext context) {
      this.context = context;
   }

   public long number() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionNumber(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long connect() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnect(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long connectGood() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnectGood(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long connectRenegotiate() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnectRenegotiate(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long accept() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAccept(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long acceptGood() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAcceptGood(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long acceptRenegotiate() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAcceptRenegotiate(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long hits() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionHits(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long cbHits() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionCbHits(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long misses() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionMisses(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long timeouts() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTimeouts(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long cacheFull() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionCacheFull(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long ticketKeyFail() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyFail(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long ticketKeyNew() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyNew(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long ticketKeyRenew() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyRenew(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }

   public long ticketKeyResume() {
      Lock readerLock = this.context.ctxLock.readLock();
      readerLock.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyResume(this.context.ctx);
      } finally {
         readerLock.unlock();
      }

      return var2;
   }
}
