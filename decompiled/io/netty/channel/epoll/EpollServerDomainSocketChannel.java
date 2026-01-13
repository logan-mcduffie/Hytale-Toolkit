package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class EpollServerDomainSocketChannel extends AbstractEpollServerChannel implements ServerDomainSocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollServerDomainSocketChannel.class);
   private final EpollServerChannelConfig config = new EpollServerChannelConfig(this);
   private volatile DomainSocketAddress local;

   public EpollServerDomainSocketChannel() {
      super(LinuxSocket.newSocketDomain(), false);
   }

   public EpollServerDomainSocketChannel(int fd) {
      super(fd);
   }

   public EpollServerDomainSocketChannel(int fd, boolean active) {
      super(new LinuxSocket(fd), active);
   }

   EpollServerDomainSocketChannel(LinuxSocket fd) {
      super(fd);
   }

   EpollServerDomainSocketChannel(LinuxSocket fd, boolean active) {
      super(fd, active);
   }

   @Override
   protected Channel newChildChannel(int fd, byte[] addr, int offset, int len) throws Exception {
      return new EpollDomainSocketChannel(this, new Socket(fd));
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.socket.bind(localAddress);
      this.socket.listen(this.config.getBacklog());
      this.local = (DomainSocketAddress)localAddress;
      this.active = true;
   }

   @Override
   protected void doClose() throws Exception {
      try {
         super.doClose();
      } finally {
         DomainSocketAddress local = this.local;
         if (local != null) {
            File socketFile = new File(local.path());
            boolean success = socketFile.delete();
            if (!success && logger.isDebugEnabled()) {
               logger.debug("Failed to delete a domain socket file: {}", local.path());
            }
         }
      }
   }

   public EpollServerChannelConfig config() {
      return this.config;
   }

   @Override
   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   @Override
   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }
}
