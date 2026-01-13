package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.SocketAddress;

@Deprecated
public abstract class AbstractOioChannel extends AbstractChannel {
   protected static final int SO_TIMEOUT = 1000;
   boolean readPending;
   boolean readWhenInactive;
   final Runnable readTask = new Runnable() {
      @Override
      public void run() {
         AbstractOioChannel.this.doRead();
      }
   };
   private final Runnable clearReadPendingRunnable = new Runnable() {
      @Override
      public void run() {
         AbstractOioChannel.this.readPending = false;
      }
   };

   protected AbstractOioChannel(Channel parent) {
      super(parent);
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new AbstractOioChannel.DefaultOioUnsafe();
   }

   @Override
   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof ThreadPerChannelEventLoop;
   }

   protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   @Override
   protected void doBeginRead() throws Exception {
      if (!this.readPending) {
         if (!this.isActive()) {
            this.readWhenInactive = true;
         } else {
            this.readPending = true;
            this.eventLoop().execute(this.readTask);
         }
      }
   }

   protected abstract void doRead();

   @Deprecated
   protected boolean isReadPending() {
      return this.readPending;
   }

   @Deprecated
   protected void setReadPending(final boolean readPending) {
      if (this.isRegistered()) {
         EventLoop eventLoop = this.eventLoop();
         if (eventLoop.inEventLoop()) {
            this.readPending = readPending;
         } else {
            eventLoop.execute(new Runnable() {
               @Override
               public void run() {
                  AbstractOioChannel.this.readPending = readPending;
               }
            });
         }
      } else {
         this.readPending = readPending;
      }
   }

   protected final void clearReadPending() {
      if (this.isRegistered()) {
         EventLoop eventLoop = this.eventLoop();
         if (eventLoop.inEventLoop()) {
            this.readPending = false;
         } else {
            eventLoop.execute(this.clearReadPendingRunnable);
         }
      } else {
         this.readPending = false;
      }
   }

   private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultOioUnsafe() {
      }

      @Override
      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if (promise.setUncancellable() && this.ensureOpen(promise)) {
            try {
               boolean wasActive = AbstractOioChannel.this.isActive();
               AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
               boolean active = AbstractOioChannel.this.isActive();
               this.safeSetSuccess(promise);
               if (!wasActive && active) {
                  AbstractOioChannel.this.pipeline().fireChannelActive();
               }
            } catch (Throwable var6) {
               this.safeSetFailure(promise, this.annotateConnectException(var6, remoteAddress));
               this.closeIfClosed();
            }
         }
      }
   }
}
