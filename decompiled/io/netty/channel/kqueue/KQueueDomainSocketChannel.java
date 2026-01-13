package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import java.net.SocketAddress;

public final class KQueueDomainSocketChannel extends AbstractKQueueStreamChannel implements DomainSocketChannel {
   private final KQueueDomainSocketChannelConfig config = new KQueueDomainSocketChannelConfig(this);
   private volatile DomainSocketAddress local;
   private volatile DomainSocketAddress remote;

   public KQueueDomainSocketChannel() {
      super(null, BsdSocket.newSocketDomain(), false);
   }

   public KQueueDomainSocketChannel(int fd) {
      this(null, new BsdSocket(fd));
   }

   KQueueDomainSocketChannel(Channel parent, BsdSocket fd) {
      super(parent, fd, true);
      this.local = fd.localDomainSocketAddress();
      this.remote = fd.remoteDomainSocketAddress();
   }

   @Override
   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueDomainSocketChannel.KQueueDomainUnsafe();
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   protected DomainSocketAddress remoteAddress0() {
      return this.remote;
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.socket.bind(localAddress);
      this.local = (DomainSocketAddress)localAddress;
   }

   public KQueueDomainSocketChannelConfig config() {
      return this.config;
   }

   @Override
   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (super.doConnect(remoteAddress, localAddress)) {
         this.local = localAddress != null ? (DomainSocketAddress)localAddress : this.socket.localDomainSocketAddress();
         this.remote = (DomainSocketAddress)remoteAddress;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   @Override
   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }

   @Override
   protected int doWriteSingle(ChannelOutboundBuffer in) throws Exception {
      Object msg = in.current();
      if (msg instanceof FileDescriptor && this.socket.sendFd(((FileDescriptor)msg).intValue()) > 0) {
         in.remove();
         return 1;
      } else {
         return super.doWriteSingle(in);
      }
   }

   @Override
   protected Object filterOutboundMessage(Object msg) {
      return msg instanceof FileDescriptor ? msg : super.filterOutboundMessage(msg);
   }

   public PeerCredentials peerCredentials() throws IOException {
      return this.socket.getPeerCredentials();
   }

   private final class KQueueDomainUnsafe extends AbstractKQueueStreamChannel.KQueueStreamUnsafe {
      private KQueueDomainUnsafe() {
      }

      @Override
      void readReady(KQueueRecvByteAllocatorHandle allocHandle) {
         switch (KQueueDomainSocketChannel.this.config().getReadMode()) {
            case BYTES:
               super.readReady(allocHandle);
               break;
            case FILE_DESCRIPTORS:
               this.readReadyFd();
               break;
            default:
               throw new Error("Unexpected read mode: " + KQueueDomainSocketChannel.this.config().getReadMode());
         }
      }

      private void readReadyFd() {
         if (KQueueDomainSocketChannel.this.socket.isInputShutdown()) {
            super.clearReadFilter0();
         } else {
            ChannelConfig config = KQueueDomainSocketChannel.this.config();
            KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            ChannelPipeline pipeline = KQueueDomainSocketChannel.this.pipeline();
            allocHandle.reset(config);

            try {
               while (true) {
                  int recvFd = KQueueDomainSocketChannel.this.socket.recvFd();
                  switch (recvFd) {
                     case -1:
                        allocHandle.lastBytesRead(-1);
                        this.close(this.voidPromise());
                        return;
                     case 0:
                        allocHandle.lastBytesRead(0);
                        break;
                     default:
                        allocHandle.lastBytesRead(1);
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead(new FileDescriptor(recvFd));
                        if (allocHandle.continueReading()) {
                           continue;
                        }
                  }

                  allocHandle.readComplete();
                  pipeline.fireChannelReadComplete();
                  return;
               }
            } catch (Throwable var8) {
               allocHandle.readComplete();
               pipeline.fireChannelReadComplete();
               pipeline.fireExceptionCaught(var8);
            } finally {
               if (this.shouldStopReading(config)) {
                  this.clearReadFilter0();
               }
            }
         }
      }
   }
}
