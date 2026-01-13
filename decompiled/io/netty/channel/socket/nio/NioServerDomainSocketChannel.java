package io.netty.channel.socket.nio;

import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.ServerChannel;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NioServerDomainSocketChannel extends AbstractNioMessageChannel implements ServerChannel {
   private static final Method OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY = SelectorProviderUtil.findOpenMethod("openServerSocketChannel");
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerDomainSocketChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private final NioServerDomainSocketChannel.NioDomainServerSocketChannelConfig config;
   private volatile boolean bound;

   static ServerSocketChannel newChannel(SelectorProvider provider) {
      if (PlatformDependent.javaVersion() < 16) {
         throw new UnsupportedOperationException("Only supported with Java 16+");
      } else {
         try {
            ServerSocketChannel channel = SelectorProviderUtil.newDomainSocketChannel(OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY, provider);
            if (channel == null) {
               throw new ChannelException("Failed to open a socket.");
            } else {
               return channel;
            }
         } catch (IOException var2) {
            throw new ChannelException("Failed to open a socket.", var2);
         }
      }
   }

   protected ServerSocketChannel javaChannel() {
      return (ServerSocketChannel)super.javaChannel();
   }

   public NioServerDomainSocketChannel() {
      this(DEFAULT_SELECTOR_PROVIDER);
   }

   public NioServerDomainSocketChannel(SelectorProvider provider) {
      this(newChannel(provider));
   }

   public NioServerDomainSocketChannel(ServerSocketChannel channel) {
      super(null, channel, 16);
      if (PlatformDependent.javaVersion() < 16) {
         throw new UnsupportedOperationException("Only supported with Java 16+");
      } else {
         this.config = new NioServerDomainSocketChannel.NioDomainServerSocketChannelConfig(this);

         try {
            this.bound = channel.getLocalAddress() != null;
         } catch (IOException var3) {
            throw new ChannelException(var3);
         }
      }
   }

   @Override
   public ChannelConfig config() {
      return this.config;
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public boolean isActive() {
      return this.isOpen() && this.bound;
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.javaChannel().bind(localAddress, this.config.getBacklog());
      this.bound = true;
   }

   @Override
   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected int doReadMessages(List<Object> buf) throws Exception {
      SocketChannel ch = SocketUtils.accept(this.javaChannel());

      try {
         if (ch != null) {
            buf.add(new NioDomainSocketChannel(this, ch));
            return 1;
         }
      } catch (Throwable var6) {
         logger.warn("Failed to create a new channel from an accepted socket.", var6);

         try {
            ch.close();
         } catch (Throwable var5) {
            logger.warn("Failed to close a socket.", var5);
         }
      }

      return 0;
   }

   @Override
   protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void doClose() throws Exception {
      SocketAddress local = this.localAddress();

      try {
         super.doClose();
      } finally {
         this.javaChannel().close();
         if (local != null) {
            NioDomainSocketUtil.deleteSocketFile(local);
         }
      }
   }

   @Override
   protected SocketAddress localAddress0() {
      try {
         return this.javaChannel().getLocalAddress();
      } catch (Exception var2) {
         return null;
      }
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return null;
   }

   @Override
   protected boolean closeOnReadError(Throwable cause) {
      return super.closeOnReadError(cause);
   }

   @Override
   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void doFinishConnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   private final class NioDomainServerSocketChannelConfig extends DefaultChannelConfig {
      private volatile int backlog = NetUtil.SOMAXCONN;

      private NioDomainServerSocketChannelConfig(NioServerDomainSocketChannel channel) {
         super(channel, new ServerChannelRecvByteBufAllocator());
      }

      @Override
      protected void autoReadCleared() {
         NioServerDomainSocketChannel.this.clearReadPending();
      }

      @Override
      public Map<ChannelOption<?>, Object> getOptions() {
         List<ChannelOption<?>> options = new ArrayList<>();
         options.add(ChannelOption.SO_BACKLOG);

         for (ChannelOption<?> opt : NioChannelOption.getOptions(this.jdkChannel())) {
            options.add(opt);
         }

         return this.getOptions(super.getOptions(), options.toArray(new ChannelOption[0]));
      }

      @Override
      public <T> T getOption(ChannelOption<T> option) {
         if (option == ChannelOption.SO_BACKLOG) {
            return (T)this.getBacklog();
         } else {
            return option instanceof NioChannelOption ? NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption<T>)option) : super.getOption(option);
         }
      }

      @Override
      public <T> boolean setOption(ChannelOption<T> option, T value) {
         if (option == ChannelOption.SO_BACKLOG) {
            this.validate(option, value);
            this.setBacklog((Integer)value);
            return true;
         } else {
            return option instanceof NioChannelOption
               ? NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption<T>)option, value)
               : super.setOption(option, value);
         }
      }

      private int getBacklog() {
         return this.backlog;
      }

      private NioServerDomainSocketChannel.NioDomainServerSocketChannelConfig setBacklog(int backlog) {
         ObjectUtil.checkPositiveOrZero(backlog, "backlog");
         this.backlog = backlog;
         return this;
      }

      private ServerSocketChannel jdkChannel() {
         return ((NioServerDomainSocketChannel)this.channel).javaChannel();
      }
   }
}
