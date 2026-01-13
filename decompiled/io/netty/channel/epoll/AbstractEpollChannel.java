package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.IoEvent;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoRegistration;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeUnit;

abstract class AbstractEpollChannel extends AbstractChannel implements UnixChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   protected final LinuxSocket socket;
   private ChannelPromise connectPromise;
   private Future<?> connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;
   private volatile SocketAddress local;
   private volatile SocketAddress remote;
   private IoRegistration registration;
   boolean inputClosedSeenErrorOnRead;
   private EpollIoOps ops;
   private EpollIoOps inital;
   protected volatile boolean active;

   AbstractEpollChannel(Channel parent, LinuxSocket fd, boolean active, EpollIoOps initialOps) {
      super(parent);
      this.socket = ObjectUtil.checkNotNull(fd, "fd");
      this.active = active;
      if (active) {
         this.local = fd.localAddress();
         this.remote = fd.remoteAddress();
      }

      this.ops = initialOps;
   }

   AbstractEpollChannel(Channel parent, LinuxSocket fd, SocketAddress remote, EpollIoOps initialOps) {
      super(parent);
      this.socket = ObjectUtil.checkNotNull(fd, "fd");
      this.active = true;
      this.remote = remote;
      this.local = fd.localAddress();
      this.ops = initialOps;
   }

   static boolean isSoErrorZero(Socket fd) {
      try {
         return fd.getSoError() == 0;
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   protected void setFlag(int flag) throws IOException {
      if (!this.ops.contains(flag)) {
         this.ops = this.ops.with(EpollIoOps.valueOf(flag));
         if (this.isRegistered()) {
            IoRegistration registration = this.registration();
            registration.submit(this.ops);
         } else {
            this.ops = this.ops.with(EpollIoOps.valueOf(flag));
         }
      }
   }

   void clearFlag(int flag) throws IOException {
      IoRegistration registration = this.registration();
      if (this.ops.contains(flag)) {
         this.ops = this.ops.without(EpollIoOps.valueOf(flag));
         registration.submit(this.ops);
      }
   }

   protected final IoRegistration registration() {
      assert this.registration != null;

      return this.registration;
   }

   boolean isFlagSet(int flag) {
      return (this.ops.value & flag) != 0;
   }

   @Override
   public final FileDescriptor fd() {
      return this.socket;
   }

   public abstract EpollChannelConfig config();

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   protected void doClose() throws Exception {
      this.active = false;
      this.inputClosedSeenErrorOnRead = true;

      try {
         ChannelPromise promise = this.connectPromise;
         if (promise != null) {
            promise.tryFailure(new ClosedChannelException());
            this.connectPromise = null;
         }

         Future<?> future = this.connectTimeoutFuture;
         if (future != null) {
            future.cancel(false);
            this.connectTimeoutFuture = null;
         }

         if (this.isRegistered()) {
            EventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
               this.doDeregister();
            } else {
               loop.execute(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        AbstractEpollChannel.this.doDeregister();
                     } catch (Throwable var2) {
                        AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
                     }
                  }
               });
            }
         }
      } finally {
         this.socket.close();
      }
   }

   void resetCachedAddresses() {
      this.local = this.socket.localAddress();
      this.remote = this.socket.remoteAddress();
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   public boolean isOpen() {
      return this.socket.isOpen();
   }

   @Override
   protected void doDeregister() throws Exception {
      IoRegistration registration = this.registration;
      if (registration != null) {
         this.ops = this.inital;
         registration.cancel();
      }
   }

   @Override
   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof IoEventLoop && ((IoEventLoop)loop).isCompatible(AbstractEpollChannel.AbstractEpollUnsafe.class);
   }

   @Override
   protected void doBeginRead() throws Exception {
      AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)this.unsafe();
      unsafe.readPending = true;
      this.setFlag(Native.EPOLLIN);
   }

   final boolean shouldBreakEpollInReady(ChannelConfig config) {
      return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config));
   }

   private static boolean isAllowHalfClosure(ChannelConfig config) {
      return config instanceof EpollDomainSocketChannelConfig
         ? ((EpollDomainSocketChannelConfig)config).isAllowHalfClosure()
         : config instanceof SocketChannelConfig && ((SocketChannelConfig)config).isAllowHalfClosure();
   }

   final void clearEpollIn() {
      if (this.isRegistered()) {
         EventLoop loop = this.eventLoop();
         final AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)this.unsafe();
         if (loop.inEventLoop()) {
            unsafe.clearEpollIn0();
         } else {
            loop.execute(new Runnable() {
               @Override
               public void run() {
                  if (!unsafe.readPending && !AbstractEpollChannel.this.config().isAutoRead()) {
                     unsafe.clearEpollIn0();
                  }
               }
            });
         }
      } else {
         this.ops = this.ops.without(EpollIoOps.EPOLLIN);
      }
   }

   @Override
   protected void doRegister(ChannelPromise promise) {
      ((IoEventLoop)this.eventLoop()).register((AbstractEpollChannel.AbstractEpollUnsafe)this.unsafe()).addListener(f -> {
         if (f.isSuccess()) {
            this.registration = f.getNow();
            this.registration.submit(this.ops);
            this.inital = this.ops;
            promise.setSuccess();
         } else {
            promise.setFailure(f.cause());
         }
      });
   }

   protected abstract AbstractEpollChannel.AbstractEpollUnsafe newUnsafe();

   protected final ByteBuf newDirectBuffer(ByteBuf buf) {
      return this.newDirectBuffer(buf, buf);
   }

   protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
      int readableBytes = buf.readableBytes();
      if (readableBytes == 0) {
         ReferenceCountUtil.release(holder);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator alloc = this.alloc();
         if (alloc.isDirectBufferPooled()) {
            return newDirectBuffer0(holder, buf, alloc, readableBytes);
         } else {
            ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
            if (directBuf == null) {
               return newDirectBuffer0(holder, buf, alloc, readableBytes);
            } else {
               directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
               ReferenceCountUtil.safeRelease(holder);
               return directBuf;
            }
         }
      }
   }

   private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
      ByteBuf directBuf = alloc.directBuffer(capacity);
      directBuf.writeBytes(buf, buf.readerIndex(), capacity);
      ReferenceCountUtil.safeRelease(holder);
      return directBuf;
   }

   protected static void checkResolvable(InetSocketAddress addr) {
      if (addr.isUnresolved()) {
         throw new UnresolvedAddressException();
      }
   }

   protected final int doReadBytes(ByteBuf byteBuf) throws Exception {
      int writerIndex = byteBuf.writerIndex();
      this.unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
      int localReadAmount;
      if (byteBuf.hasMemoryAddress()) {
         localReadAmount = this.socket.recvAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
      } else {
         ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
         localReadAmount = this.socket.recv(buf, buf.position(), buf.limit());
      }

      if (localReadAmount > 0) {
         byteBuf.writerIndex(writerIndex + localReadAmount);
      }

      return localReadAmount;
   }

   protected final int doWriteBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
      if (buf.hasMemoryAddress()) {
         int localFlushedAmount = this.socket.sendAddress(buf.memoryAddress(), buf.readerIndex(), buf.writerIndex());
         if (localFlushedAmount > 0) {
            in.removeBytes(localFlushedAmount);
            return 1;
         }
      } else {
         ByteBuffer nioBuf = buf.nioBufferCount() == 1 ? buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()) : buf.nioBuffer();
         int localFlushedAmount = this.socket.send(nioBuf, nioBuf.position(), nioBuf.limit());
         if (localFlushedAmount > 0) {
            ((Buffer)nioBuf).position(nioBuf.position() + localFlushedAmount);
            in.removeBytes(localFlushedAmount);
            return 1;
         }
      }

      return Integer.MAX_VALUE;
   }

   final long doWriteOrSendBytes(ByteBuf data, InetSocketAddress remoteAddress, boolean fastOpen) throws IOException {
      assert !fastOpen || remoteAddress != null : "fastOpen requires a remote address";

      if (data.hasMemoryAddress()) {
         long memoryAddress = data.memoryAddress();
         return remoteAddress == null
            ? this.socket.sendAddress(memoryAddress, data.readerIndex(), data.writerIndex())
            : this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
      } else if (data.nioBufferCount() > 1) {
         IovArray array = this.registration.<NativeArrays>attachment().cleanIovArray();
         array.add(data, data.readerIndex(), data.readableBytes());
         int cnt = array.count();

         assert cnt != 0;

         return remoteAddress == null
            ? this.socket.writevAddresses(array.memoryAddress(0), cnt)
            : this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
      } else {
         ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
         return remoteAddress == null
            ? this.socket.send(nioData, nioData.position(), nioData.limit())
            : this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
      }
   }

   @Override
   protected void doBind(SocketAddress local) throws Exception {
      if (local instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)local);
      }

      this.socket.bind(local);
      this.local = this.socket.localAddress();
   }

   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (localAddress instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)localAddress);
      }

      InetSocketAddress remoteSocketAddr = remoteAddress instanceof InetSocketAddress ? (InetSocketAddress)remoteAddress : null;
      if (remoteSocketAddr != null) {
         checkResolvable(remoteSocketAddr);
      }

      if (this.remote != null) {
         throw new AlreadyConnectedException();
      } else {
         if (localAddress != null) {
            this.socket.bind(localAddress);
         }

         boolean connected = this.doConnect0(remoteAddress);
         if (connected) {
            this.remote = (SocketAddress)(remoteSocketAddr == null
               ? remoteAddress
               : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress()));
         }

         this.local = this.socket.localAddress();
         return connected;
      }
   }

   boolean doConnect0(SocketAddress remote) throws Exception {
      boolean success = false;

      boolean var4;
      try {
         boolean connected = this.socket.connect(remote);
         if (!connected) {
            this.setFlag(Native.EPOLLOUT);
         }

         success = true;
         var4 = connected;
      } finally {
         if (!success) {
            this.doClose();
         }
      }

      return var4;
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.local;
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.remote;
   }

   protected abstract class AbstractEpollUnsafe extends AbstractChannel.AbstractUnsafe implements EpollIoHandle {
      boolean readPending;
      private EpollRecvByteAllocatorHandle allocHandle;

      Channel channel() {
         return AbstractEpollChannel.this;
      }

      @Override
      public FileDescriptor fd() {
         return AbstractEpollChannel.this.fd();
      }

      @Override
      public void close() {
         this.close(this.voidPromise());
      }

      @Override
      public void handle(IoRegistration registration, IoEvent event) {
         EpollIoEvent epollEvent = (EpollIoEvent)event;
         int ops = epollEvent.ops().value;
         if ((ops & EpollIoOps.EPOLL_ERR_OUT_MASK) != 0) {
            this.epollOutReady();
         }

         if ((ops & EpollIoOps.EPOLL_ERR_IN_MASK) != 0) {
            this.epollInReady();
         }

         if ((ops & EpollIoOps.EPOLL_RDHUP_MASK) != 0) {
            this.epollRdHupReady();
         }
      }

      abstract void epollInReady();

      final boolean shouldStopReading(ChannelConfig config) {
         return !this.readPending && !config.isAutoRead();
      }

      final void epollRdHupReady() {
         this.recvBufAllocHandle().receivedRdHup();
         if (AbstractEpollChannel.this.isActive()) {
            this.epollInReady();
         } else {
            this.shutdownInput(false);
         }

         this.clearEpollRdHup();
      }

      private void clearEpollRdHup() {
         try {
            AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
         } catch (IOException var2) {
            AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
            this.close(this.voidPromise());
         }
      }

      void shutdownInput(boolean allDataRead) {
         if (!AbstractEpollChannel.this.socket.isInputShutdown()) {
            if (!AbstractEpollChannel.isAllowHalfClosure(AbstractEpollChannel.this.config())) {
               this.close(this.voidPromise());
               return;
            }

            try {
               AbstractEpollChannel.this.socket.shutdown(true, false);
            } catch (IOException var3) {
               this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
               return;
            } catch (NotYetConnectedException var4) {
            }

            if (this.shouldStopReading(AbstractEpollChannel.this.config())) {
               this.clearEpollIn0();
            }

            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
         }

         if (allDataRead && !AbstractEpollChannel.this.inputClosedSeenErrorOnRead) {
            AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
         }
      }

      private void fireEventAndClose(Object evt) {
         AbstractEpollChannel.this.pipeline().fireUserEventTriggered(evt);
         this.close(this.voidPromise());
      }

      public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
         if (this.allocHandle == null) {
            this.allocHandle = this.newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
         }

         return this.allocHandle;
      }

      EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle handle) {
         return new EpollRecvByteAllocatorHandle(handle);
      }

      @Override
      protected final void flush0() {
         if (!AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT)) {
            super.flush0();
         }
      }

      final void epollOutReady() {
         if (AbstractEpollChannel.this.connectPromise != null) {
            this.finishConnect();
         } else if (!AbstractEpollChannel.this.socket.isOutputShutdown()) {
            super.flush0();
         }
      }

      protected final void clearEpollIn0() {
         assert AbstractEpollChannel.this.eventLoop().inEventLoop();

         try {
            this.readPending = false;
            if (!AbstractEpollChannel.this.ops.contains(EpollIoOps.EPOLLIN)) {
               return;
            }

            AbstractEpollChannel.this.ops = AbstractEpollChannel.this.ops.without(EpollIoOps.EPOLLIN);
            IoRegistration registration = AbstractEpollChannel.this.registration();
            registration.submit(AbstractEpollChannel.this.ops);
         } catch (UncheckedIOException var2) {
            AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
            AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
         }
      }

      @Override
      public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if (!promise.isDone() && this.ensureOpen(promise)) {
            try {
               if (AbstractEpollChannel.this.connectPromise != null) {
                  throw new ConnectionPendingException();
               }

               boolean wasActive = AbstractEpollChannel.this.isActive();
               if (AbstractEpollChannel.this.doConnect(remoteAddress, localAddress)) {
                  this.fulfillConnectPromise(promise, wasActive);
               } else {
                  AbstractEpollChannel.this.connectPromise = promise;
                  AbstractEpollChannel.this.requestedRemoteAddress = remoteAddress;
                  final int connectTimeoutMillis = AbstractEpollChannel.this.config().getConnectTimeoutMillis();
                  if (connectTimeoutMillis > 0) {
                     AbstractEpollChannel.this.connectTimeoutFuture = AbstractEpollChannel.this.eventLoop()
                        .schedule(
                           new Runnable() {
                              @Override
                              public void run() {
                                 ChannelPromise connectPromise = AbstractEpollChannel.this.connectPromise;
                                 if (connectPromise != null
                                    && !connectPromise.isDone()
                                    && connectPromise.tryFailure(
                                       new ConnectTimeoutException("connection timed out after " + connectTimeoutMillis + " ms: " + remoteAddress)
                                    )) {
                                    AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                                 }
                              }
                           },
                           connectTimeoutMillis,
                           TimeUnit.MILLISECONDS
                        );
                  }

                  promise.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture future) {
                        if (future.isCancelled()) {
                           if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                              AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractEpollChannel.this.connectPromise = null;
                           AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                        }
                     }
                  });
               }
            } catch (Throwable var6) {
               this.closeIfClosed();
               promise.tryFailure(this.annotateConnectException(var6, remoteAddress));
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
         if (promise != null) {
            AbstractEpollChannel.this.active = true;
            boolean active = AbstractEpollChannel.this.isActive();
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
               AbstractEpollChannel.this.pipeline().fireChannelActive();
            }

            if (!promiseSet) {
               this.close(this.voidPromise());
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
         if (promise != null) {
            promise.tryFailure(cause);
            this.closeIfClosed();
         }
      }

      private void finishConnect() {
         assert AbstractEpollChannel.this.eventLoop().inEventLoop();

         boolean connectStillInProgress = false;

         try {
            boolean wasActive = AbstractEpollChannel.this.isActive();
            if (this.doFinishConnect()) {
               this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, wasActive);
               return;
            }

            connectStillInProgress = true;
         } catch (Throwable var6) {
            this.fulfillConnectPromise(
               AbstractEpollChannel.this.connectPromise, this.annotateConnectException(var6, AbstractEpollChannel.this.requestedRemoteAddress)
            );
            return;
         } finally {
            if (!connectStillInProgress) {
               if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                  AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
               }

               AbstractEpollChannel.this.connectPromise = null;
            }
         }
      }

      private boolean doFinishConnect() throws Exception {
         if (AbstractEpollChannel.this.socket.finishConnect()) {
            AbstractEpollChannel.this.clearFlag(Native.EPOLLOUT);
            if (AbstractEpollChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
               AbstractEpollChannel.this.remote = UnixChannelUtil.computeRemoteAddr(
                  (InetSocketAddress)AbstractEpollChannel.this.requestedRemoteAddress, AbstractEpollChannel.this.socket.remoteAddress()
               );
            }

            AbstractEpollChannel.this.requestedRemoteAddress = null;
            return true;
         } else {
            AbstractEpollChannel.this.setFlag(Native.EPOLLOUT);
            return false;
         }
      }
   }
}
