package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.DomainDatagramChannel;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import io.netty.channel.unix.DomainDatagramPacket;
import io.netty.channel.unix.DomainDatagramSocketAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class KQueueDomainDatagramChannel extends AbstractKQueueDatagramChannel implements DomainDatagramChannel {
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DomainDatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(DomainSocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private volatile boolean connected;
   private volatile DomainSocketAddress local;
   private volatile DomainSocketAddress remote;
   private final KQueueDomainDatagramChannelConfig config = new KQueueDomainDatagramChannelConfig(this);

   public KQueueDomainDatagramChannel() {
      this(BsdSocket.newSocketDomainDgram(), false);
   }

   public KQueueDomainDatagramChannel(int fd) {
      this(new BsdSocket(fd), true);
   }

   private KQueueDomainDatagramChannel(BsdSocket socket, boolean active) {
      super(null, socket, active);
   }

   public KQueueDomainDatagramChannelConfig config() {
      return this.config;
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      super.doBind(localAddress);
      this.local = (DomainSocketAddress)localAddress;
      this.active = true;
   }

   @Override
   protected void doClose() throws Exception {
      super.doClose();
      this.connected = this.active = false;
      this.local = null;
      this.remote = null;
   }

   @Override
   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if (super.doConnect(remoteAddress, localAddress)) {
         if (localAddress != null) {
            this.local = (DomainSocketAddress)localAddress;
         }

         this.remote = (DomainSocketAddress)remoteAddress;
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   protected boolean doWriteMessage(Object msg) throws Exception {
      ByteBuf data;
      DomainSocketAddress remoteAddress;
      if (msg instanceof AddressedEnvelope) {
         AddressedEnvelope<ByteBuf, DomainSocketAddress> envelope = (AddressedEnvelope<ByteBuf, DomainSocketAddress>)msg;
         data = envelope.content();
         remoteAddress = envelope.recipient();
      } else {
         data = (ByteBuf)msg;
         remoteAddress = null;
      }

      int dataLen = data.readableBytes();
      if (dataLen == 0) {
         return true;
      } else {
         long writtenBytes;
         if (data.hasMemoryAddress()) {
            long memoryAddress = data.memoryAddress();
            if (remoteAddress == null) {
               writtenBytes = this.socket.writeAddress(memoryAddress, data.readerIndex(), data.writerIndex());
            } else {
               writtenBytes = this.socket
                  .sendToAddressDomainSocket(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
         } else if (data.nioBufferCount() > 1) {
            IovArray array = this.registration().<NativeArrays>attachment().cleanIovArray();
            array.add(data, data.readerIndex(), data.readableBytes());
            int cnt = array.count();

            assert cnt != 0;

            if (remoteAddress == null) {
               writtenBytes = this.socket.writevAddresses(array.memoryAddress(0), cnt);
            } else {
               writtenBytes = this.socket.sendToAddressesDomainSocket(array.memoryAddress(0), cnt, remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
         } else {
            ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            if (remoteAddress == null) {
               writtenBytes = this.socket.write(nioData, nioData.position(), nioData.limit());
            } else {
               writtenBytes = this.socket.sendToDomainSocket(nioData, nioData.position(), nioData.limit(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
         }

         return writtenBytes > 0L;
      }
   }

   @Override
   protected Object filterOutboundMessage(Object msg) {
      if (msg instanceof DomainDatagramPacket) {
         DomainDatagramPacket packet = (DomainDatagramPacket)msg;
         ByteBuf content = packet.content();
         return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DomainDatagramPacket(this.newDirectBuffer(packet, content), packet.recipient()) : msg;
      } else if (msg instanceof ByteBuf) {
         ByteBuf buf = (ByteBuf)msg;
         return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
      } else {
         if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
            if (e.content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof DomainSocketAddress)) {
               ByteBuf content = (ByteBuf)e.content();
               return UnixChannelUtil.isBufferCopyNeededForWrite(content)
                  ? new DefaultAddressedEnvelope<>(this.newDirectBuffer(e, content), (DomainSocketAddress)e.recipient())
                  : e;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      }
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
   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   @Override
   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueDomainDatagramChannel.KQueueDomainDatagramChannelUnsafe();
   }

   public PeerCredentials peerCredentials() throws IOException {
      return this.socket.getPeerCredentials();
   }

   @Override
   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   protected DomainSocketAddress remoteAddress0() {
      return this.remote;
   }

   final class KQueueDomainDatagramChannelUnsafe extends AbstractKQueueChannel.AbstractKQueueUnsafe {
      @Override
      void readReady(KQueueRecvByteAllocatorHandle allocHandle) {
         assert KQueueDomainDatagramChannel.this.eventLoop().inEventLoop();

         DomainDatagramChannelConfig config = KQueueDomainDatagramChannel.this.config();
         if (KQueueDomainDatagramChannel.this.shouldBreakReadReady(config)) {
            this.clearReadFilter0();
         } else {
            ChannelPipeline pipeline = KQueueDomainDatagramChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            Throwable exception = null;

            try {
               ByteBuf byteBuf = null;

               try {
                  boolean connected = KQueueDomainDatagramChannel.this.isConnected();

                  do {
                     byteBuf = allocHandle.allocate(allocator);
                     allocHandle.attemptedBytesRead(byteBuf.writableBytes());
                     DomainDatagramPacket packet;
                     if (connected) {
                        allocHandle.lastBytesRead(KQueueDomainDatagramChannel.this.doReadBytes(byteBuf));
                        if (allocHandle.lastBytesRead() <= 0) {
                           byteBuf.release();
                           break;
                        }

                        packet = new DomainDatagramPacket(byteBuf, (DomainSocketAddress)this.localAddress(), (DomainSocketAddress)this.remoteAddress());
                     } else {
                        DomainDatagramSocketAddress remoteAddress;
                        if (byteBuf.hasMemoryAddress()) {
                           remoteAddress = KQueueDomainDatagramChannel.this.socket
                              .recvFromAddressDomainSocket(byteBuf.memoryAddress(), byteBuf.writerIndex(), byteBuf.capacity());
                        } else {
                           ByteBuffer nioData = byteBuf.internalNioBuffer(byteBuf.writerIndex(), byteBuf.writableBytes());
                           remoteAddress = KQueueDomainDatagramChannel.this.socket.recvFromDomainSocket(nioData, nioData.position(), nioData.limit());
                        }

                        if (remoteAddress == null) {
                           allocHandle.lastBytesRead(-1);
                           byteBuf.release();
                           break;
                        }

                        DomainSocketAddress localAddress = remoteAddress.localAddress();
                        if (localAddress == null) {
                           localAddress = (DomainSocketAddress)this.localAddress();
                        }

                        allocHandle.lastBytesRead(remoteAddress.receivedAmount());
                        byteBuf.writerIndex(byteBuf.writerIndex() + allocHandle.lastBytesRead());
                        packet = new DomainDatagramPacket(byteBuf, localAddress, remoteAddress);
                     }

                     allocHandle.incMessagesRead(1);
                     this.readPending = false;
                     pipeline.fireChannelRead(packet);
                     byteBuf = null;
                  } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
               } catch (Throwable var14) {
                  if (byteBuf != null) {
                     byteBuf.release();
                  }

                  exception = var14;
               }

               allocHandle.readComplete();
               pipeline.fireChannelReadComplete();
               if (exception != null) {
                  pipeline.fireExceptionCaught(exception);
               }
            } finally {
               if (this.shouldStopReading(config)) {
                  this.clearReadFilter0();
               }
            }
         }
      }
   }
}
