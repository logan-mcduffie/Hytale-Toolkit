package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;

public final class EpollDatagramChannel extends AbstractEpollChannel implements DatagramChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollDatagramChannel.class);
   private static final boolean IP_MULTICAST_ALL = SystemPropertyUtil.getBoolean("io.netty.channel.epoll.ipMulticastAll", false);
   private static final ChannelMetadata METADATA = new ChannelMetadata(true, 16);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(InetSocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private final EpollDatagramChannelConfig config;
   private volatile boolean connected;

   public static boolean isSegmentedDatagramPacketSupported() {
      return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
   }

   public EpollDatagramChannel() {
      this((SocketProtocolFamily)null);
   }

   @Deprecated
   public EpollDatagramChannel(InternetProtocolFamily family) {
      this(LinuxSocket.newSocketDgram(family), false);
   }

   public EpollDatagramChannel(SocketProtocolFamily family) {
      this(LinuxSocket.newSocketDgram(family), false);
   }

   public EpollDatagramChannel(int fd) {
      this(new LinuxSocket(fd), true);
   }

   private EpollDatagramChannel(LinuxSocket fd, boolean active) {
      super(null, fd, active, EpollIoOps.valueOf(0));

      try {
         fd.setIpMulticastAll(IP_MULTICAST_ALL);
      } catch (IOException var4) {
         logger.debug("Failed to set IP_MULTICAST_ALL to {}", IP_MULTICAST_ALL, var4);
      }

      this.config = new EpollDatagramChannelConfig(this);
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public boolean isActive() {
      return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
   }

   @Override
   public boolean isConnected() {
      return this.connected;
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress) {
      return this.joinGroup(multicastAddress, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
      try {
         NetworkInterface iface = this.config().getNetworkInterface();
         if (iface == null) {
            iface = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
         }

         return this.joinGroup(multicastAddress, iface, null, promise);
      } catch (IOException var4) {
         promise.setFailure(var4);
         return promise;
      }
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      return this.joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.joinGroup(multicastAddress, networkInterface, source, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(
      final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise
   ) {
      ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
      ObjectUtil.checkNotNull(networkInterface, "networkInterface");
      if (this.eventLoop().inEventLoop()) {
         this.joinGroup0(multicastAddress, networkInterface, source, promise);
      } else {
         this.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               EpollDatagramChannel.this.joinGroup0(multicastAddress, networkInterface, source, promise);
            }
         });
      }

      return promise;
   }

   private void joinGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      assert this.eventLoop().inEventLoop();

      try {
         this.socket.joinGroup(multicastAddress, networkInterface, source);
         promise.setSuccess();
      } catch (IOException var6) {
         promise.setFailure(var6);
      }
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress) {
      return this.leaveGroup(multicastAddress, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
      try {
         return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
      } catch (IOException var4) {
         promise.setFailure(var4);
         return promise;
      }
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.leaveGroup(multicastAddress, networkInterface, source, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(
      final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise
   ) {
      ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
      ObjectUtil.checkNotNull(networkInterface, "networkInterface");
      if (this.eventLoop().inEventLoop()) {
         this.leaveGroup0(multicastAddress, networkInterface, source, promise);
      } else {
         this.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               EpollDatagramChannel.this.leaveGroup0(multicastAddress, networkInterface, source, promise);
            }
         });
      }

      return promise;
   }

   private void leaveGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      assert this.eventLoop().inEventLoop();

      try {
         this.socket.leaveGroup(multicastAddress, networkInterface, source);
         promise.setSuccess();
      } catch (IOException var6) {
         promise.setFailure(var6);
      }
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
      return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
      ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
      ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
      ObjectUtil.checkNotNull(networkInterface, "networkInterface");
      promise.setFailure(new UnsupportedOperationException("Multicast block not supported"));
      return promise;
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
      return this.block(multicastAddress, sourceToBlock, this.newPromise());
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
      try {
         return this.block(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), sourceToBlock, promise);
      } catch (Throwable var5) {
         promise.setFailure(var5);
         return promise;
      }
   }

   @Override
   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollDatagramChannel.EpollDatagramChannelUnsafe();
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      if (localAddress instanceof InetSocketAddress) {
         InetSocketAddress socketAddress = (InetSocketAddress)localAddress;
         if (socketAddress.getAddress().isAnyLocalAddress()
            && socketAddress.getAddress() instanceof Inet4Address
            && this.socket.family() == SocketProtocolFamily.INET6) {
            localAddress = new InetSocketAddress(Native.INET6_ANY, socketAddress.getPort());
         }
      }

      super.doBind(localAddress);
      this.active = true;
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      int maxMessagesPerWrite = this.maxMessagesPerWrite();

      while (maxMessagesPerWrite > 0) {
         Object msg = in.current();
         if (msg == null) {
            break;
         }

         try {
            if (Native.IS_SUPPORTING_SENDMMSG && in.size() > 1 || in.current() instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
               NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
               array.add(in, this.isConnected(), maxMessagesPerWrite);
               int cnt = array.count();
               if (cnt >= 1) {
                  int offset = 0;
                  NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
                  int send = this.socket.sendmmsg(packets, offset, cnt);
                  if (send == 0) {
                     break;
                  }

                  for (int i = 0; i < send; i++) {
                     in.remove();
                  }

                  maxMessagesPerWrite -= send;
                  continue;
               }
            }

            boolean done = false;

            for (int i = this.config().getWriteSpinCount(); i > 0; i--) {
               if (this.doWriteMessage(msg)) {
                  done = true;
                  break;
               }
            }

            if (!done) {
               break;
            }

            in.remove();
            maxMessagesPerWrite--;
         } catch (IOException var10) {
            maxMessagesPerWrite--;
            in.remove(var10);
         }
      }

      if (in.isEmpty()) {
         this.clearFlag(Native.EPOLLOUT);
      } else {
         this.setFlag(Native.EPOLLOUT);
      }
   }

   private boolean doWriteMessage(Object msg) throws Exception {
      ByteBuf data;
      InetSocketAddress remoteAddress;
      if (msg instanceof AddressedEnvelope) {
         AddressedEnvelope<ByteBuf, InetSocketAddress> envelope = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg;
         data = envelope.content();
         remoteAddress = envelope.recipient();
      } else {
         data = (ByteBuf)msg;
         remoteAddress = null;
      }

      int dataLen = data.readableBytes();
      return dataLen == 0 ? true : this.doWriteOrSendBytes(data, remoteAddress, false) > 0L;
   }

   private static void checkUnresolved(AddressedEnvelope<?, ?> envelope) {
      if (envelope.recipient() instanceof InetSocketAddress && ((InetSocketAddress)envelope.recipient()).isUnresolved()) {
         throw new UnresolvedAddressException();
      }
   }

   @Override
   protected Object filterOutboundMessage(Object msg) {
      if (msg instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
         if (!Native.IS_SUPPORTING_UDP_SEGMENT) {
            throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
         } else {
            io.netty.channel.unix.SegmentedDatagramPacket packet = (io.netty.channel.unix.SegmentedDatagramPacket)msg;
            checkUnresolved(packet);
            ByteBuf content = packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? packet.replace(this.newDirectBuffer(packet, content)) : msg;
         }
      } else if (msg instanceof DatagramPacket) {
         DatagramPacket packet = (DatagramPacket)msg;
         checkUnresolved(packet);
         ByteBuf content = packet.content();
         return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet, content), packet.recipient()) : msg;
      } else if (msg instanceof ByteBuf) {
         ByteBuf buf = (ByteBuf)msg;
         return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
      } else {
         if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
            checkUnresolved(e);
            if (e.content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof InetSocketAddress)) {
               ByteBuf content = (ByteBuf)e.content();
               return UnixChannelUtil.isBufferCopyNeededForWrite(content)
                  ? new DefaultAddressedEnvelope<>(this.newDirectBuffer(e, content), (InetSocketAddress)e.recipient())
                  : e;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      }
   }

   public EpollDatagramChannelConfig config() {
      return this.config;
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
      this.connected = this.active = false;
      this.resetCachedAddresses();
   }

   @Override
   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (super.doConnect(remoteAddress, localAddress)) {
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void doClose() throws Exception {
      super.doClose();
      this.connected = false;
   }

   private boolean connectedRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws Exception {
      boolean var12;
      try {
         int writable = maxDatagramPacketSize != 0 ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
         allocHandle.attemptedBytesRead(writable);
         int writerIndex = byteBuf.writerIndex();
         int localReadAmount;
         if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.socket.recvAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
         } else {
            ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, writable);
            localReadAmount = this.socket.recv(buf, buf.position(), buf.limit());
         }

         if (localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
            allocHandle.lastBytesRead(maxDatagramPacketSize <= 0 ? localReadAmount : writable);
            DatagramPacket packet = new DatagramPacket(byteBuf, this.localAddress(), this.remoteAddress());
            allocHandle.incMessagesRead(1);
            this.pipeline().fireChannelRead(packet);
            byteBuf = null;
            return true;
         }

         allocHandle.lastBytesRead(localReadAmount);
         var12 = false;
      } finally {
         if (byteBuf != null) {
            byteBuf.release();
         }
      }

      return var12;
   }

   private IOException translateForConnected(Errors.NativeIoException e) {
      if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         PortUnreachableException error = new PortUnreachableException(e.getMessage());
         error.initCause(e);
         return error;
      } else {
         return e;
      }
   }

   private static void addDatagramPacketToOut(DatagramPacket packet, RecyclableArrayList out) {
      if (packet instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
         io.netty.channel.unix.SegmentedDatagramPacket segmentedDatagramPacket = (io.netty.channel.unix.SegmentedDatagramPacket)packet;
         ByteBuf content = segmentedDatagramPacket.content();
         InetSocketAddress recipient = segmentedDatagramPacket.recipient();
         InetSocketAddress sender = segmentedDatagramPacket.sender();
         int segmentSize = segmentedDatagramPacket.segmentSize();

         do {
            out.add(new DatagramPacket(content.readRetainedSlice(Math.min(content.readableBytes(), segmentSize)), recipient, sender));
         } while (content.isReadable());

         segmentedDatagramPacket.release();
      } else {
         out.add(packet);
      }
   }

   private static void releaseAndRecycle(ByteBuf byteBuf, RecyclableArrayList packetList) {
      if (byteBuf != null) {
         byteBuf.release();
      }

      if (packetList != null) {
         for (int i = 0; i < packetList.size(); i++) {
            ReferenceCountUtil.release(packetList.get(i));
         }

         packetList.recycle();
      }
   }

   private static void processPacket(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, DatagramPacket packet) {
      handle.lastBytesRead(Math.max(1, bytesRead));
      handle.incMessagesRead(1);
      pipeline.fireChannelRead(packet);
   }

   private static void processPacketList(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, RecyclableArrayList packetList) {
      int messagesRead = packetList.size();
      handle.lastBytesRead(Math.max(1, bytesRead));
      handle.incMessagesRead(messagesRead);

      for (int i = 0; i < messagesRead; i++) {
         pipeline.fireChannelRead(packetList.set(i, Unpooled.EMPTY_BUFFER));
      }
   }

   private boolean recvmsg(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf) throws IOException {
      RecyclableArrayList datagramPackets = null;

      boolean local;
      try {
         int writable = byteBuf.writableBytes();
         boolean added = array.addWritable(byteBuf, byteBuf.writerIndex(), writable);

         assert added;

         allocHandle.attemptedBytesRead(writable);
         NativeDatagramPacketArray.NativeDatagramPacket msg = array.packets()[0];
         int bytesReceived = this.socket.recvmsg(msg);
         if (msg.hasSender()) {
            byteBuf.writerIndex(bytesReceived);
            InetSocketAddress localx = this.localAddress();
            DatagramPacket packet = msg.newDatagramPacket(byteBuf, localx);
            if (!(packet instanceof io.netty.channel.unix.SegmentedDatagramPacket)) {
               processPacket(this.pipeline(), allocHandle, bytesReceived, packet);
            } else {
               datagramPackets = RecyclableArrayList.newInstance();
               addDatagramPacketToOut(packet, datagramPackets);
               processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
               datagramPackets.recycle();
               datagramPackets = null;
            }

            return true;
         }

         allocHandle.lastBytesRead(-1);
         local = false;
      } finally {
         releaseAndRecycle(byteBuf, datagramPackets);
      }

      return local;
   }

   private boolean scatteringRead(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf, int datagramSize, int numDatagram) throws IOException {
      RecyclableArrayList datagramPackets = null;

      try {
         int offset = byteBuf.writerIndex();
         int i = 0;

         while (i < numDatagram && array.addWritable(byteBuf, offset, datagramSize)) {
            i++;
            offset += datagramSize;
         }

         allocHandle.attemptedBytesRead(offset - byteBuf.writerIndex());
         NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
         int received = this.socket.recvmmsg(packets, 0, array.count());
         if (received == 0) {
            allocHandle.lastBytesRead(-1);
            return false;
         } else {
            InetSocketAddress local = this.localAddress();
            int bytesReceived = received * datagramSize;
            byteBuf.writerIndex(byteBuf.writerIndex() + bytesReceived);
            if (received == 1) {
               DatagramPacket packet = packets[0].newDatagramPacket(byteBuf, local);
               if (!(packet instanceof io.netty.channel.unix.SegmentedDatagramPacket)) {
                  processPacket(this.pipeline(), allocHandle, datagramSize, packet);
                  return true;
               }
            }

            datagramPackets = RecyclableArrayList.newInstance();

            for (int ix = 0; ix < received; ix++) {
               DatagramPacket packet = packets[ix].newDatagramPacket(byteBuf, local);
               byteBuf.skipBytes(datagramSize);
               addDatagramPacketToOut(packet, datagramPackets);
            }

            byteBuf.release();
            byteBuf = null;
            processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
            datagramPackets.recycle();
            datagramPackets = null;
            return true;
         }
      } finally {
         releaseAndRecycle(byteBuf, datagramPackets);
      }
   }

   private NativeDatagramPacketArray cleanDatagramPacketArray() {
      return this.registration().<NativeArrays>attachment().cleanDatagramPacketArray();
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.channel.epoll.ipMulticastAll: {}", IP_MULTICAST_ALL);
      }
   }

   final class EpollDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      @Override
      void epollInReady() {
         assert EpollDatagramChannel.this.eventLoop().inEventLoop();

         EpollDatagramChannelConfig config = EpollDatagramChannel.this.config();
         if (EpollDatagramChannel.this.shouldBreakEpollInReady(config)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            ChannelPipeline pipeline = EpollDatagramChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            Throwable exception = null;

            try {
               try {
                  boolean connected = EpollDatagramChannel.this.isConnected();

                  do {
                     int datagramSize = EpollDatagramChannel.this.config().getMaxDatagramPayloadSize();
                     ByteBuf byteBuf = allocHandle.allocate(allocator);
                     int numDatagram = Native.IS_SUPPORTING_RECVMMSG ? (datagramSize == 0 ? 1 : byteBuf.writableBytes() / datagramSize) : 0;

                     boolean read;
                     try {
                        if (numDatagram <= 1) {
                           if (connected && !config.isUdpGro()) {
                              read = EpollDatagramChannel.this.connectedRead(allocHandle, byteBuf, datagramSize);
                           } else {
                              read = EpollDatagramChannel.this.recvmsg(allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf);
                           }
                        } else {
                           read = EpollDatagramChannel.this.scatteringRead(
                              allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf, datagramSize, numDatagram
                           );
                        }
                     } catch (Errors.NativeIoException var16) {
                        if (connected) {
                           throw EpollDatagramChannel.this.translateForConnected(var16);
                        }

                        throw var16;
                     }

                     if (!read) {
                        break;
                     }

                     this.readPending = false;
                  } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
               } catch (Throwable var17) {
                  exception = var17;
               }

               allocHandle.readComplete();
               pipeline.fireChannelReadComplete();
               if (exception != null) {
                  pipeline.fireExceptionCaught(exception);
               }
            } finally {
               if (this.shouldStopReading(config)) {
                  EpollDatagramChannel.this.clearEpollIn();
               }
            }
         }
      }
   }
}
