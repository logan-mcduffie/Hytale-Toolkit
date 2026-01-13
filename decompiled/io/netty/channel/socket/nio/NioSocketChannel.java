package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioIoOps;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Executor;

public class NioSocketChannel extends AbstractNioByteChannel implements SocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private static final Method OPEN_SOCKET_CHANNEL_WITH_FAMILY = SelectorProviderUtil.findOpenMethod("openSocketChannel");
   private final SocketChannelConfig config;

   private static java.nio.channels.SocketChannel newChannel(SelectorProvider provider, SocketProtocolFamily family) {
      try {
         java.nio.channels.SocketChannel channel = SelectorProviderUtil.newChannel(OPEN_SOCKET_CHANNEL_WITH_FAMILY, provider, family);
         return channel == null ? provider.openSocketChannel() : channel;
      } catch (IOException var3) {
         throw new ChannelException("Failed to open a socket.", var3);
      }
   }

   public NioSocketChannel() {
      this(DEFAULT_SELECTOR_PROVIDER);
   }

   public NioSocketChannel(SelectorProvider provider) {
      this(provider, (SocketProtocolFamily)null);
   }

   @Deprecated
   public NioSocketChannel(SelectorProvider provider, InternetProtocolFamily family) {
      this(provider, family == null ? null : family.toSocketProtocolFamily());
   }

   public NioSocketChannel(SelectorProvider provider, SocketProtocolFamily family) {
      this(newChannel(provider, family));
   }

   public NioSocketChannel(java.nio.channels.SocketChannel socket) {
      this(null, socket);
   }

   public NioSocketChannel(Channel parent, java.nio.channels.SocketChannel socket) {
      super(parent, socket);
      this.config = new NioSocketChannel.NioSocketChannelConfig(this, socket.socket());
   }

   @Override
   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   @Override
   public SocketChannelConfig config() {
      return this.config;
   }

   protected java.nio.channels.SocketChannel javaChannel() {
      return (java.nio.channels.SocketChannel)super.javaChannel();
   }

   @Override
   public boolean isActive() {
      java.nio.channels.SocketChannel ch = this.javaChannel();
      return ch.isOpen() && ch.isConnected();
   }

   @Override
   public boolean isOutputShutdown() {
      return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
   }

   @Override
   public boolean isInputShutdown() {
      return this.javaChannel().socket().isInputShutdown() || !this.isActive();
   }

   @Override
   public boolean isShutdown() {
      Socket socket = this.javaChannel().socket();
      return socket.isInputShutdown() && socket.isOutputShutdown() || !this.isActive();
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   protected final void doShutdownOutput() throws Exception {
      this.javaChannel().shutdownOutput();
   }

   @Override
   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   @Override
   public ChannelFuture shutdownOutput(final ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if (loop.inEventLoop()) {
         ((AbstractChannel.AbstractUnsafe)this.unsafe()).shutdownOutput(promise);
      } else {
         loop.execute(new Runnable() {
            @Override
            public void run() {
               ((AbstractChannel.AbstractUnsafe)NioSocketChannel.this.unsafe()).shutdownOutput(promise);
            }
         });
      }

      return promise;
   }

   @Override
   public ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   @Override
   protected boolean isInputShutdown0() {
      return this.isInputShutdown();
   }

   @Override
   public ChannelFuture shutdownInput(final ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if (loop.inEventLoop()) {
         this.shutdownInput0(promise);
      } else {
         loop.execute(new Runnable() {
            @Override
            public void run() {
               NioSocketChannel.this.shutdownInput0(promise);
            }
         });
      }

      return promise;
   }

   @Override
   public ChannelFuture shutdown() {
      return this.shutdown(this.newPromise());
   }

   @Override
   public ChannelFuture shutdown(final ChannelPromise promise) {
      ChannelFuture shutdownOutputFuture = this.shutdownOutput();
      if (shutdownOutputFuture.isDone()) {
         this.shutdownOutputDone(shutdownOutputFuture, promise);
      } else {
         shutdownOutputFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
               NioSocketChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
            }
         });
      }

      return promise;
   }

   private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
      ChannelFuture shutdownInputFuture = this.shutdownInput();
      if (shutdownInputFuture.isDone()) {
         shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
      } else {
         shutdownInputFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
               NioSocketChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
            }
         });
      }
   }

   private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
      Throwable shutdownOutputCause = shutdownOutputFuture.cause();
      Throwable shutdownInputCause = shutdownInputFuture.cause();
      if (shutdownOutputCause != null) {
         if (shutdownInputCause != null) {
            logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
         }

         promise.setFailure(shutdownOutputCause);
      } else if (shutdownInputCause != null) {
         promise.setFailure(shutdownInputCause);
      } else {
         promise.setSuccess();
      }
   }

   private void shutdownInput0(ChannelPromise promise) {
      try {
         this.shutdownInput0();
         promise.setSuccess();
      } catch (Throwable var3) {
         promise.setFailure(var3);
      }
   }

   private void shutdownInput0() throws Exception {
      this.javaChannel().shutdownInput();
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.javaChannel().socket().getLocalSocketAddress();
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.javaChannel().socket().getRemoteSocketAddress();
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.doBind0(localAddress);
   }

   private void doBind0(SocketAddress localAddress) throws Exception {
      SocketUtils.bind(this.javaChannel(), localAddress);
   }

   @Override
   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (localAddress != null) {
         this.doBind0(localAddress);
      }

      boolean success = false;

      boolean var5;
      try {
         boolean connected = SocketUtils.connect(this.javaChannel(), remoteAddress);
         if (!connected) {
            this.addAndSubmit(NioIoOps.CONNECT);
         }

         success = true;
         var5 = connected;
      } finally {
         if (!success) {
            this.doClose();
         }
      }

      return var5;
   }

   @Override
   protected void doFinishConnect() throws Exception {
      if (!this.javaChannel().finishConnect()) {
         throw new UnsupportedOperationException("finishConnect is not supported for " + this.getClass().getName());
      }
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   protected void doClose() throws Exception {
      super.doClose();
      this.javaChannel().close();
   }

   @Override
   protected int doReadBytes(ByteBuf byteBuf) throws Exception {
      RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
      allocHandle.attemptedBytesRead(byteBuf.writableBytes());
      return byteBuf.writeBytes(this.javaChannel(), allocHandle.attemptedBytesRead());
   }

   @Override
   protected int doWriteBytes(ByteBuf buf) throws Exception {
      int expectedWrittenBytes = buf.readableBytes();
      return buf.readBytes(this.javaChannel(), expectedWrittenBytes);
   }

   @Override
   protected long doWriteFileRegion(FileRegion region) throws Exception {
      long position = region.transferred();
      return region.transferTo(this.javaChannel(), position);
   }

   private void adjustMaxBytesPerGatheringWrite(int attempted, int written, int oldMaxBytesPerGatheringWrite) {
      if (attempted == written) {
         if (attempted << 1 > oldMaxBytesPerGatheringWrite) {
            ((NioSocketChannel.NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted << 1);
         }
      } else if (attempted > 4096 && written < attempted >>> 1) {
         ((NioSocketChannel.NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted >>> 1);
      }
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      java.nio.channels.SocketChannel ch = this.javaChannel();
      int writeSpinCount = this.config().getWriteSpinCount();

      while (!in.isEmpty()) {
         int maxBytesPerGatheringWrite = ((NioSocketChannel.NioSocketChannelConfig)this.config).getMaxBytesPerGatheringWrite();
         ByteBuffer[] nioBuffers = in.nioBuffers(1024, maxBytesPerGatheringWrite);
         int nioBufferCnt = in.nioBufferCount();
         switch (nioBufferCnt) {
            case 0:
               writeSpinCount -= this.doWrite0(in);
               break;
            case 1:
               ByteBuffer buffer = nioBuffers[0];
               int attemptedBytes = buffer.remaining();
               int localWrittenBytes = ch.write(buffer);
               if (localWrittenBytes <= 0) {
                  this.incompleteWrite(true);
                  return;
               }

               this.adjustMaxBytesPerGatheringWrite(attemptedBytes, localWrittenBytes, maxBytesPerGatheringWrite);
               in.removeBytes(localWrittenBytes);
               writeSpinCount--;
               break;
            default:
               long attemptedBytes = in.nioBufferSize();
               long localWrittenBytes = ch.write(nioBuffers, 0, nioBufferCnt);
               if (localWrittenBytes <= 0L) {
                  this.incompleteWrite(true);
                  return;
               }

               this.adjustMaxBytesPerGatheringWrite((int)attemptedBytes, (int)localWrittenBytes, maxBytesPerGatheringWrite);
               in.removeBytes(localWrittenBytes);
               writeSpinCount--;
         }

         if (writeSpinCount <= 0) {
            this.incompleteWrite(writeSpinCount < 0);
            return;
         }
      }

      this.clearOpWrite();
   }

   @Override
   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new NioSocketChannel.NioSocketChannelUnsafe();
   }

   private final class NioSocketChannelConfig extends DefaultSocketChannelConfig {
      private volatile int maxBytesPerGatheringWrite = Integer.MAX_VALUE;

      private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {
         super(channel, javaSocket);
         this.calculateMaxBytesPerGatheringWrite();
      }

      @Override
      protected void autoReadCleared() {
         NioSocketChannel.this.clearReadPending();
      }

      public NioSocketChannel.NioSocketChannelConfig setSendBufferSize(int sendBufferSize) {
         super.setSendBufferSize(sendBufferSize);
         this.calculateMaxBytesPerGatheringWrite();
         return this;
      }

      @Override
      public <T> boolean setOption(ChannelOption<T> option, T value) {
         return option instanceof NioChannelOption
            ? NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption<T>)option, value)
            : super.setOption(option, value);
      }

      @Override
      public <T> T getOption(ChannelOption<T> option) {
         return option instanceof NioChannelOption ? NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption<T>)option) : super.getOption(option);
      }

      @Override
      public Map<ChannelOption<?>, Object> getOptions() {
         return this.getOptions(super.getOptions(), NioChannelOption.getOptions(this.jdkChannel()));
      }

      void setMaxBytesPerGatheringWrite(int maxBytesPerGatheringWrite) {
         this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
      }

      int getMaxBytesPerGatheringWrite() {
         return this.maxBytesPerGatheringWrite;
      }

      private void calculateMaxBytesPerGatheringWrite() {
         int newSendBufferSize = this.getSendBufferSize() << 1;
         if (newSendBufferSize > 0) {
            this.setMaxBytesPerGatheringWrite(newSendBufferSize);
         }
      }

      private java.nio.channels.SocketChannel jdkChannel() {
         return ((NioSocketChannel)this.channel).javaChannel();
      }
   }

   private final class NioSocketChannelUnsafe extends AbstractNioByteChannel.NioByteUnsafe {
      private NioSocketChannelUnsafe() {
      }

      @Override
      protected Executor prepareToClose() {
         try {
            if (NioSocketChannel.this.javaChannel().isOpen() && NioSocketChannel.this.config().getSoLinger() > 0) {
               NioSocketChannel.this.doDeregister();
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }
   }
}
