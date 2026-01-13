package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractServerChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.IoEvent;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoRegistration;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel extends AbstractServerChannel {
   private final ChannelConfig config = new DefaultChannelConfig(this, new ServerChannelRecvByteBufAllocator()) {};
   private final Queue<Object> inboundBuffer = new ArrayDeque<>();
   private final Runnable shutdownHook = new Runnable() {
      @Override
      public void run() {
         LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
      }
   };
   private IoRegistration registration;
   private volatile int state;
   private volatile LocalAddress localAddress;
   private volatile boolean acceptInProgress;

   public LocalServerChannel() {
      this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
   }

   @Override
   public ChannelConfig config() {
      return this.config;
   }

   public LocalAddress localAddress() {
      return (LocalAddress)super.localAddress();
   }

   public LocalAddress remoteAddress() {
      return (LocalAddress)super.remoteAddress();
   }

   @Override
   public boolean isOpen() {
      return this.state < 2;
   }

   @Override
   public boolean isActive() {
      return this.state == 1;
   }

   @Override
   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof SingleThreadEventLoop
         || loop instanceof IoEventLoopGroup && ((IoEventLoopGroup)loop).isCompatible(LocalServerChannel.LocalServerUnsafe.class);
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.localAddress;
   }

   @Override
   protected void doRegister(ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if (loop instanceof IoEventLoop) {
         assert this.registration == null;

         ((IoEventLoop)loop).register((LocalServerChannel.LocalServerUnsafe)this.unsafe()).addListener(f -> {
            if (f.isSuccess()) {
               this.registration = f.getNow();
               promise.setSuccess();
            } else {
               promise.setFailure(f.cause());
            }
         });
      } else {
         try {
            ((LocalServerChannel.LocalServerUnsafe)this.unsafe()).registered();
         } catch (Throwable var4) {
            promise.setFailure(var4);
            return;
         }

         promise.setSuccess();
      }
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
      this.state = 1;
   }

   @Override
   protected void doClose() throws Exception {
      if (this.state <= 1) {
         if (this.localAddress != null) {
            LocalChannelRegistry.unregister(this.localAddress);
            this.localAddress = null;
         }

         this.state = 2;
      }
   }

   @Override
   protected void doDeregister() throws Exception {
      EventLoop loop = this.eventLoop();
      if (loop instanceof IoEventLoop) {
         IoRegistration registration = this.registration;
         if (registration != null) {
            this.registration = null;
            registration.cancel();
         }
      } else {
         ((LocalServerChannel.LocalServerUnsafe)this.unsafe()).unregistered();
      }
   }

   @Override
   protected void doBeginRead() throws Exception {
      if (!this.acceptInProgress) {
         Queue<Object> inboundBuffer = this.inboundBuffer;
         if (inboundBuffer.isEmpty()) {
            this.acceptInProgress = true;
         } else {
            this.readInbound();
         }
      }
   }

   LocalChannel serve(LocalChannel peer) {
      final LocalChannel child = this.newLocalChannel(peer);
      if (this.eventLoop().inEventLoop()) {
         this.serve0(child);
      } else {
         this.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               LocalServerChannel.this.serve0(child);
            }
         });
      }

      return child;
   }

   private void readInbound() {
      RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
      handle.reset(this.config());
      ChannelPipeline pipeline = this.pipeline();

      do {
         Object m = this.inboundBuffer.poll();
         if (m == null) {
            break;
         }

         pipeline.fireChannelRead(m);
      } while (handle.continueReading());

      handle.readComplete();
      pipeline.fireChannelReadComplete();
   }

   protected LocalChannel newLocalChannel(LocalChannel peer) {
      return new LocalChannel(this, peer);
   }

   private void serve0(LocalChannel child) {
      this.inboundBuffer.add(child);
      if (this.acceptInProgress) {
         this.acceptInProgress = false;
         this.readInbound();
      }
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new LocalServerChannel.LocalServerUnsafe();
   }

   private class LocalServerUnsafe extends AbstractChannel.AbstractUnsafe implements LocalIoHandle {
      private LocalServerUnsafe() {
      }

      @Override
      public void close() {
         this.close(this.voidPromise());
      }

      @Override
      public void handle(IoRegistration registration, IoEvent event) {
      }

      @Override
      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         this.safeSetFailure(promise, new UnsupportedOperationException());
      }

      @Override
      public void registered() {
         ((SingleThreadEventExecutor)LocalServerChannel.this.eventLoop()).addShutdownHook(LocalServerChannel.this.shutdownHook);
      }

      @Override
      public void unregistered() {
         ((SingleThreadEventExecutor)LocalServerChannel.this.eventLoop()).removeShutdownHook(LocalServerChannel.this.shutdownHook);
      }

      @Override
      public void closeNow() {
         this.close(this.voidPromise());
      }
   }
}
