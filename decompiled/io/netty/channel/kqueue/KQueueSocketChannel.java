package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.IovArray;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;

public final class KQueueSocketChannel extends AbstractKQueueStreamChannel implements SocketChannel {
   private final KQueueSocketChannelConfig config = new KQueueSocketChannelConfig(this);

   public KQueueSocketChannel() {
      super(null, BsdSocket.newSocketStream(), false);
   }

   @Deprecated
   public KQueueSocketChannel(InternetProtocolFamily protocol) {
      super(null, BsdSocket.newSocketStream(protocol), false);
   }

   public KQueueSocketChannel(SocketProtocolFamily protocol) {
      super(null, BsdSocket.newSocketStream(protocol), false);
   }

   public KQueueSocketChannel(int fd) {
      super(new BsdSocket(fd));
   }

   KQueueSocketChannel(Channel parent, BsdSocket fd, InetSocketAddress remoteAddress) {
      super(parent, fd, remoteAddress);
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public KQueueSocketChannelConfig config() {
      return this.config;
   }

   @Override
   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   @Override
   protected boolean doConnect0(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (this.config.isTcpFastOpenConnect()) {
         ChannelOutboundBuffer outbound = this.unsafe().outboundBuffer();
         outbound.addFlush();
         Object curr;
         if ((curr = outbound.current()) instanceof ByteBuf) {
            ByteBuf initialData = (ByteBuf)curr;
            if (initialData.isReadable()) {
               IovArray iov = new IovArray(this.config.getAllocator().directBuffer());

               boolean var8;
               try {
                  iov.add(initialData, initialData.readerIndex(), initialData.readableBytes());
                  int bytesSent = this.socket.connectx((InetSocketAddress)localAddress, (InetSocketAddress)remoteAddress, iov, true);
                  this.writeFilter(true);
                  outbound.removeBytes(Math.abs(bytesSent));
                  var8 = bytesSent > 0;
               } finally {
                  iov.release();
               }

               return var8;
            }
         }
      }

      return super.doConnect0(remoteAddress, localAddress);
   }

   @Override
   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueSocketChannel.KQueueSocketChannelUnsafe();
   }

   private final class KQueueSocketChannelUnsafe extends AbstractKQueueStreamChannel.KQueueStreamUnsafe {
      private KQueueSocketChannelUnsafe() {
      }

      @Override
      protected Executor prepareToClose() {
         try {
            if (KQueueSocketChannel.this.isOpen() && KQueueSocketChannel.this.config().getSoLinger() > 0) {
               KQueueSocketChannel.this.doDeregister();
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }
   }
}
