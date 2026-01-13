package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.List;
import java.util.Locale;

@Deprecated
public class OioDatagramChannel extends AbstractOioMessageChannel implements DatagramChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(SocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private final MulticastSocket socket;
   private final OioDatagramChannelConfig config;
   private final java.net.DatagramPacket tmpPacket = new java.net.DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);

   private static MulticastSocket newSocket() {
      try {
         return new MulticastSocket(null);
      } catch (Exception var1) {
         throw new ChannelException("failed to create a new socket", var1);
      }
   }

   public OioDatagramChannel() {
      this(newSocket());
   }

   public OioDatagramChannel(MulticastSocket socket) {
      super(null);
      boolean success = false;

      try {
         socket.setSoTimeout(1000);
         socket.setBroadcast(false);
         success = true;
      } catch (SocketException var7) {
         throw new ChannelException("Failed to configure the datagram socket timeout.", var7);
      } finally {
         if (!success) {
            socket.close();
         }
      }

      this.socket = socket;
      this.config = new DefaultOioDatagramChannelConfig(this, socket);
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public DatagramChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   @Override
   public boolean isActive() {
      return this.isOpen() && (this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && this.isRegistered() || this.socket.isBound());
   }

   @Override
   public boolean isConnected() {
      return this.socket.isConnected();
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.socket.getLocalSocketAddress();
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.socket.getRemoteSocketAddress();
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.socket.bind(localAddress);
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
   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (localAddress != null) {
         this.socket.bind(localAddress);
      }

      boolean success = false;

      try {
         this.socket.connect(remoteAddress);
         success = true;
      } finally {
         if (!success) {
            try {
               this.socket.close();
            } catch (Throwable var10) {
               logger.warn("Failed to close a socket.", var10);
            }
         }
      }
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
   }

   @Override
   protected void doClose() throws Exception {
      this.socket.close();
   }

   @Override
   protected int doReadMessages(List<Object> buf) throws Exception {
      DatagramChannelConfig config = this.config();
      RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
      ByteBuf data = config.getAllocator().heapBuffer(allocHandle.guess());
      boolean free = true;

      byte var17;
      try {
         this.tmpPacket.setAddress(null);
         this.tmpPacket.setData(data.array(), data.arrayOffset(), data.capacity());
         this.socket.receive(this.tmpPacket);
         InetSocketAddress remoteAddr = (InetSocketAddress)this.tmpPacket.getSocketAddress();
         allocHandle.lastBytesRead(this.tmpPacket.getLength());
         buf.add(new DatagramPacket(data.writerIndex(allocHandle.lastBytesRead()), this.localAddress(), remoteAddr));
         free = false;
         return 1;
      } catch (SocketTimeoutException var13) {
         return 0;
      } catch (SocketException var14) {
         if (!var14.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
            throw var14;
         }

         var17 = -1;
      } catch (Throwable var15) {
         PlatformDependent.throwException(var15);
         return -1;
      } finally {
         if (free) {
            data.release();
         }
      }

      return var17;
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while (true) {
         Object o = in.current();
         if (o == null) {
            return;
         }

         ByteBuf data;
         SocketAddress remoteAddress;
         if (o instanceof AddressedEnvelope) {
            AddressedEnvelope<ByteBuf, SocketAddress> envelope = (AddressedEnvelope<ByteBuf, SocketAddress>)o;
            remoteAddress = envelope.recipient();
            data = envelope.content();
         } else {
            data = (ByteBuf)o;
            remoteAddress = null;
         }

         int length = data.readableBytes();

         try {
            if (remoteAddress != null) {
               this.tmpPacket.setSocketAddress(remoteAddress);
            } else {
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }

               this.tmpPacket.setAddress(null);
            }

            if (data.hasArray()) {
               this.tmpPacket.setData(data.array(), data.arrayOffset() + data.readerIndex(), length);
            } else {
               this.tmpPacket.setData(ByteBufUtil.getBytes(data, data.readerIndex(), length));
            }

            this.socket.send(this.tmpPacket);
            in.remove();
         } catch (Exception var7) {
            in.remove(var7);
         }
      }
   }

   private static void checkUnresolved(AddressedEnvelope<?, ?> envelope) {
      if (envelope.recipient() instanceof InetSocketAddress && ((InetSocketAddress)envelope.recipient()).isUnresolved()) {
         throw new UnresolvedAddressException();
      }
   }

   @Override
   protected Object filterOutboundMessage(Object msg) {
      if (msg instanceof DatagramPacket) {
         checkUnresolved((DatagramPacket)msg);
         return msg;
      } else if (msg instanceof ByteBuf) {
         return msg;
      } else {
         if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
            checkUnresolved(e);
            if (e.content() instanceof ByteBuf) {
               return msg;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      }
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress) {
      return this.joinGroup(multicastAddress, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
      this.ensureBound();

      try {
         this.socket.joinGroup(multicastAddress);
         promise.setSuccess();
      } catch (IOException var4) {
         promise.setFailure(var4);
      }

      return promise;
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      this.ensureBound();

      try {
         this.socket.joinGroup(multicastAddress, networkInterface);
         promise.setSuccess();
      } catch (IOException var5) {
         promise.setFailure(var5);
      }

      return promise;
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   @Override
   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   private void ensureBound() {
      if (!this.isActive()) {
         throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
      }
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress) {
      return this.leaveGroup(multicastAddress, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
      try {
         this.socket.leaveGroup(multicastAddress);
         promise.setSuccess();
      } catch (IOException var4) {
         promise.setFailure(var4);
      }

      return promise;
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      try {
         this.socket.leaveGroup(multicastAddress, networkInterface);
         promise.setSuccess();
      } catch (IOException var5) {
         promise.setFailure(var5);
      }

      return promise;
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   @Override
   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }
}
