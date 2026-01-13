package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

public final class PcapWriteHandler extends ChannelDuplexHandler implements Closeable {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(PcapWriteHandler.class);
   private PcapWriter pCapWriter;
   private final OutputStream outputStream;
   private final boolean captureZeroByte;
   private final boolean writePcapGlobalHeader;
   private final boolean sharedOutputStream;
   private long sendSegmentNumber = 1L;
   private long receiveSegmentNumber = 1L;
   private PcapWriteHandler.ChannelType channelType;
   private InetSocketAddress initiatorAddr;
   private InetSocketAddress handlerAddr;
   private boolean isServerPipeline;
   private final AtomicReference<State> state = new AtomicReference<>(State.INIT);

   @Deprecated
   public PcapWriteHandler(OutputStream outputStream) {
      this(outputStream, false, true);
   }

   @Deprecated
   public PcapWriteHandler(OutputStream outputStream, boolean captureZeroByte, boolean writePcapGlobalHeader) {
      this.outputStream = ObjectUtil.checkNotNull(outputStream, "OutputStream");
      this.captureZeroByte = captureZeroByte;
      this.writePcapGlobalHeader = writePcapGlobalHeader;
      this.sharedOutputStream = false;
   }

   private PcapWriteHandler(PcapWriteHandler.Builder builder, OutputStream outputStream) {
      this.outputStream = outputStream;
      this.captureZeroByte = builder.captureZeroByte;
      this.sharedOutputStream = builder.sharedOutputStream;
      this.writePcapGlobalHeader = builder.writePcapGlobalHeader;
      this.channelType = builder.channelType;
      this.handlerAddr = builder.handlerAddr;
      this.initiatorAddr = builder.initiatorAddr;
      this.isServerPipeline = builder.isServerPipeline;
   }

   public static void writeGlobalHeader(OutputStream outputStream) throws IOException {
      PcapHeaders.writeGlobalHeader(outputStream);
   }

   private void initializeIfNecessary(ChannelHandlerContext ctx) throws Exception {
      if (this.state.get() == State.INIT) {
         this.pCapWriter = new PcapWriter(this);
         if (this.channelType == null) {
            if (ctx.channel() instanceof SocketChannel) {
               this.channelType = PcapWriteHandler.ChannelType.TCP;
               if (ctx.channel().parent() instanceof ServerSocketChannel) {
                  this.isServerPipeline = true;
                  this.initiatorAddr = (InetSocketAddress)ctx.channel().remoteAddress();
                  this.handlerAddr = getLocalAddress(ctx.channel(), this.initiatorAddr);
               } else {
                  this.isServerPipeline = false;
                  this.handlerAddr = (InetSocketAddress)ctx.channel().remoteAddress();
                  this.initiatorAddr = getLocalAddress(ctx.channel(), this.handlerAddr);
               }
            } else if (ctx.channel() instanceof DatagramChannel) {
               this.channelType = PcapWriteHandler.ChannelType.UDP;
               DatagramChannel datagramChannel = (DatagramChannel)ctx.channel();
               if (datagramChannel.isConnected()) {
                  this.handlerAddr = (InetSocketAddress)ctx.channel().remoteAddress();
                  this.initiatorAddr = getLocalAddress(ctx.channel(), this.handlerAddr);
               }
            }
         }

         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.logger.debug("Initiating Fake TCP 3-Way Handshake");
            ByteBuf tcpBuf = ctx.alloc().buffer();

            try {
               TCPPacket.writePacket(tcpBuf, null, 0L, 0L, this.initiatorAddr.getPort(), this.handlerAddr.getPort(), TCPPacket.TCPFlag.SYN);
               this.completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
               TCPPacket.writePacket(
                  tcpBuf, null, 0L, 1L, this.handlerAddr.getPort(), this.initiatorAddr.getPort(), TCPPacket.TCPFlag.SYN, TCPPacket.TCPFlag.ACK
               );
               this.completeTCPWrite(this.handlerAddr, this.initiatorAddr, tcpBuf, ctx.alloc(), ctx);
               TCPPacket.writePacket(tcpBuf, null, 1L, 1L, this.initiatorAddr.getPort(), this.handlerAddr.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
            } finally {
               tcpBuf.release();
            }

            this.logger.debug("Finished Fake TCP 3-Way Handshake");
         }

         this.state.set(State.WRITING);
      }
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      this.initializeIfNecessary(ctx);
      super.channelActive(ctx);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (this.state.get() == State.INIT) {
         try {
            this.initializeIfNecessary(ctx);
         } catch (Exception var4) {
            ReferenceCountUtil.release(msg);
            throw var4;
         }
      }

      if (this.state.get() == State.WRITING) {
         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.handleTCP(ctx, msg, false);
         } else if (this.channelType == PcapWriteHandler.ChannelType.UDP) {
            this.handleUDP(ctx, msg, false);
         } else {
            this.logDiscard();
         }
      }

      super.channelRead(ctx, msg);
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (this.state.get() == State.INIT) {
         try {
            this.initializeIfNecessary(ctx);
         } catch (Exception var5) {
            ReferenceCountUtil.release(msg);
            promise.setFailure(var5);
            return;
         }
      }

      if (this.state.get() == State.WRITING) {
         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.handleTCP(ctx, msg, true);
         } else if (this.channelType == PcapWriteHandler.ChannelType.UDP) {
            this.handleUDP(ctx, msg, true);
         } else {
            this.logDiscard();
         }
      }

      super.write(ctx, msg, promise);
   }

   private void handleTCP(ChannelHandlerContext ctx, Object msg, boolean isWriteOperation) {
      if (msg instanceof ByteBuf) {
         int totalBytes = ((ByteBuf)msg).readableBytes();
         if (totalBytes == 0 && !this.captureZeroByte) {
            this.logger.debug("Discarding Zero Byte TCP Packet. isWriteOperation {}", isWriteOperation);
            return;
         }

         ByteBufAllocator byteBufAllocator = ctx.alloc();
         if (totalBytes == 0) {
            this.handleTcpPacket(ctx, (ByteBuf)msg, isWriteOperation, byteBufAllocator);
            return;
         }

         int maxTcpPayload = 65495;

         for (int i = 0; i < totalBytes; i += maxTcpPayload) {
            ByteBuf packet = ((ByteBuf)msg).slice(i, Math.min(maxTcpPayload, totalBytes - i));
            this.handleTcpPacket(ctx, packet, isWriteOperation, byteBufAllocator);
         }
      } else {
         this.logger.debug("Discarding Pcap Write for TCP Object: {}", msg);
      }
   }

   private void handleTcpPacket(ChannelHandlerContext ctx, ByteBuf packet, boolean isWriteOperation, ByteBufAllocator byteBufAllocator) {
      ByteBuf tcpBuf = byteBufAllocator.buffer();
      int bytes = packet.readableBytes();

      try {
         if (isWriteOperation) {
            InetSocketAddress srcAddr;
            InetSocketAddress dstAddr;
            if (this.isServerPipeline) {
               srcAddr = this.handlerAddr;
               dstAddr = this.initiatorAddr;
            } else {
               srcAddr = this.initiatorAddr;
               dstAddr = this.handlerAddr;
            }

            TCPPacket.writePacket(
               tcpBuf, packet, this.sendSegmentNumber, this.receiveSegmentNumber, srcAddr.getPort(), dstAddr.getPort(), TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(srcAddr, dstAddr, tcpBuf, byteBufAllocator, ctx);
            this.logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, srcAddr, dstAddr, false);
            this.sendSegmentNumber = incrementUintSegmentNumber(this.sendSegmentNumber, bytes);
            TCPPacket.writePacket(tcpBuf, null, this.receiveSegmentNumber, this.sendSegmentNumber, dstAddr.getPort(), srcAddr.getPort(), TCPPacket.TCPFlag.ACK);
            this.completeTCPWrite(dstAddr, srcAddr, tcpBuf, byteBufAllocator, ctx);
            this.logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr, srcAddr, true);
         } else {
            InetSocketAddress srcAddr;
            InetSocketAddress dstAddr;
            if (this.isServerPipeline) {
               srcAddr = this.initiatorAddr;
               dstAddr = this.handlerAddr;
            } else {
               srcAddr = this.handlerAddr;
               dstAddr = this.initiatorAddr;
            }

            TCPPacket.writePacket(
               tcpBuf, packet, this.receiveSegmentNumber, this.sendSegmentNumber, srcAddr.getPort(), dstAddr.getPort(), TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(srcAddr, dstAddr, tcpBuf, byteBufAllocator, ctx);
            this.logTCP(false, bytes, this.receiveSegmentNumber, this.sendSegmentNumber, srcAddr, dstAddr, false);
            this.receiveSegmentNumber = incrementUintSegmentNumber(this.receiveSegmentNumber, bytes);
            TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr.getPort(), srcAddr.getPort(), TCPPacket.TCPFlag.ACK);
            this.completeTCPWrite(dstAddr, srcAddr, tcpBuf, byteBufAllocator, ctx);
            this.logTCP(false, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr, srcAddr, true);
         }
      } finally {
         tcpBuf.release();
      }
   }

   private void completeTCPWrite(
      InetSocketAddress srcAddr, InetSocketAddress dstAddr, ByteBuf tcpBuf, ByteBufAllocator byteBufAllocator, ChannelHandlerContext ctx
   ) {
      ByteBuf ipBuf = byteBufAllocator.buffer();
      ByteBuf ethernetBuf = byteBufAllocator.buffer();
      ByteBuf pcap = byteBufAllocator.buffer();

      try {
         try {
            if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
               IPPacket.writeTCPv4(
                  ipBuf, tcpBuf, NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress())
               );
               EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
            } else {
               if (!(srcAddr.getAddress() instanceof Inet6Address) || !(dstAddr.getAddress() instanceof Inet6Address)) {
                  this.logger
                     .error(
                        "Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}",
                        srcAddr.getAddress(),
                        dstAddr.getAddress()
                     );
                  return;
               }

               IPPacket.writeTCPv6(ipBuf, tcpBuf, srcAddr.getAddress().getAddress(), dstAddr.getAddress().getAddress());
               EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
            }

            this.pCapWriter.writePacket(pcap, ethernetBuf);
         } catch (IOException var13) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", (Throwable)var13);
            ctx.fireExceptionCaught(var13);
         }
      } finally {
         ipBuf.release();
         ethernetBuf.release();
         pcap.release();
      }
   }

   private static long incrementUintSegmentNumber(long sequenceNumber, int value) {
      return (sequenceNumber + value) % 4294967296L;
   }

   private void handleUDP(ChannelHandlerContext ctx, Object msg, boolean isWriteOperation) {
      ByteBuf udpBuf = ctx.alloc().buffer();

      try {
         if (!(msg instanceof DatagramPacket)) {
            if (!(msg instanceof ByteBuf) || ctx.channel() instanceof DatagramChannel && !((DatagramChannel)ctx.channel()).isConnected()) {
               this.logger.debug("Discarding Pcap Write for UDP Object: {}", msg);
               return;
            }

            if (((ByteBuf)msg).readableBytes() != 0 || this.captureZeroByte) {
               if (((ByteBuf)msg).readableBytes() <= 65507) {
                  ByteBuf byteBuf = ((ByteBuf)msg).duplicate();
                  InetSocketAddress sourceAddr = isWriteOperation ? this.initiatorAddr : this.handlerAddr;
                  InetSocketAddress destinationAddr = isWriteOperation ? this.handlerAddr : this.initiatorAddr;
                  this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", byteBuf.readableBytes(), sourceAddr, destinationAddr);
                  UDPPacket.writePacket(udpBuf, byteBuf, sourceAddr.getPort(), destinationAddr.getPort());
                  this.completeUDPWrite(sourceAddr, destinationAddr, udpBuf, ctx.alloc(), ctx);
                  return;
               }

               this.logger.warn("Unable to write UDP packet to PCAP. Payload of size {} exceeds max size of 65507");
               return;
            }

            this.logger.debug("Discarding Zero Byte UDP Packet");
            return;
         }

         if (((DatagramPacket)msg).content().readableBytes() != 0 || this.captureZeroByte) {
            if (((DatagramPacket)msg).content().readableBytes() <= 65507) {
               DatagramPacket datagramPacket = ((DatagramPacket)msg).duplicate();
               InetSocketAddress srcAddr = datagramPacket.sender();
               InetSocketAddress dstAddr = datagramPacket.recipient();
               if (srcAddr == null) {
                  srcAddr = getLocalAddress(ctx.channel(), dstAddr);
               }

               this.logger
                  .debug(
                     "Writing UDP Data of {} Bytes, isWriteOperation {}, Src Addr {}, Dst Addr {}",
                     datagramPacket.content().readableBytes(),
                     isWriteOperation,
                     srcAddr,
                     dstAddr
                  );
               UDPPacket.writePacket(udpBuf, datagramPacket.content(), srcAddr.getPort(), dstAddr.getPort());
               this.completeUDPWrite(srcAddr, dstAddr, udpBuf, ctx.alloc(), ctx);
               return;
            }

            this.logger.warn("Unable to write UDP packet to PCAP. Payload of size {} exceeds max size of 65507");
            return;
         }

         this.logger.debug("Discarding Zero Byte UDP Packet");
      } finally {
         udpBuf.release();
      }
   }

   private void completeUDPWrite(
      InetSocketAddress srcAddr, InetSocketAddress dstAddr, ByteBuf udpBuf, ByteBufAllocator byteBufAllocator, ChannelHandlerContext ctx
   ) {
      ByteBuf ipBuf = byteBufAllocator.buffer();
      ByteBuf ethernetBuf = byteBufAllocator.buffer();
      ByteBuf pcap = byteBufAllocator.buffer();

      try {
         try {
            if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
               IPPacket.writeUDPv4(
                  ipBuf, udpBuf, NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress())
               );
               EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
            } else {
               if (!(srcAddr.getAddress() instanceof Inet6Address) || !(dstAddr.getAddress() instanceof Inet6Address)) {
                  this.logger
                     .error(
                        "Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}",
                        srcAddr.getAddress(),
                        dstAddr.getAddress()
                     );
                  return;
               }

               IPPacket.writeUDPv6(ipBuf, udpBuf, srcAddr.getAddress().getAddress(), dstAddr.getAddress().getAddress());
               EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
            }

            this.pCapWriter.writePacket(pcap, ethernetBuf);
         } catch (IOException var13) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", (Throwable)var13);
            ctx.fireExceptionCaught(var13);
         }
      } finally {
         ipBuf.release();
         ethernetBuf.release();
         pcap.release();
      }
   }

   private static InetSocketAddress getLocalAddress(Channel ch, InetSocketAddress remote) {
      InetSocketAddress local = (InetSocketAddress)ch.localAddress();
      if (remote != null && local.getAddress().isAnyLocalAddress()) {
         if (local.getAddress() instanceof Inet4Address && remote.getAddress() instanceof Inet6Address) {
            return new InetSocketAddress(PcapWriteHandler.WildcardAddressHolder.wildcard6, local.getPort());
         }

         if (local.getAddress() instanceof Inet6Address && remote.getAddress() instanceof Inet4Address) {
            return new InetSocketAddress(PcapWriteHandler.WildcardAddressHolder.wildcard4, local.getPort());
         }
      }

      return local;
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      if (this.channelType == PcapWriteHandler.ChannelType.TCP && this.state.get() == State.WRITING) {
         this.logger.debug("Starting Fake TCP FIN+ACK Flow to close connection");
         ByteBufAllocator byteBufAllocator = ctx.alloc();
         ByteBuf tcpBuf = byteBufAllocator.buffer();

         try {
            long initiatorSegmentNumber = this.isServerPipeline ? this.receiveSegmentNumber : this.sendSegmentNumber;
            long initiatorAckNumber = this.isServerPipeline ? this.sendSegmentNumber : this.receiveSegmentNumber;
            TCPPacket.writePacket(
               tcpBuf,
               null,
               initiatorSegmentNumber,
               initiatorAckNumber,
               this.initiatorAddr.getPort(),
               this.handlerAddr.getPort(),
               TCPPacket.TCPFlag.FIN,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, byteBufAllocator, ctx);
            TCPPacket.writePacket(
               tcpBuf,
               null,
               initiatorAckNumber,
               initiatorSegmentNumber,
               this.handlerAddr.getPort(),
               this.initiatorAddr.getPort(),
               TCPPacket.TCPFlag.FIN,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.handlerAddr, this.initiatorAddr, tcpBuf, byteBufAllocator, ctx);
            this.sendSegmentNumber = incrementUintSegmentNumber(this.sendSegmentNumber, 1);
            this.receiveSegmentNumber = incrementUintSegmentNumber(this.receiveSegmentNumber, 1);
            initiatorSegmentNumber = this.isServerPipeline ? this.receiveSegmentNumber : this.sendSegmentNumber;
            initiatorAckNumber = this.isServerPipeline ? this.sendSegmentNumber : this.receiveSegmentNumber;
            TCPPacket.writePacket(
               tcpBuf, null, initiatorSegmentNumber, initiatorAckNumber, this.initiatorAddr.getPort(), this.handlerAddr.getPort(), TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, byteBufAllocator, ctx);
         } finally {
            tcpBuf.release();
         }

         this.logger.debug("Finished Fake TCP FIN+ACK Flow to close connection");
      }

      this.close();
      super.handlerRemoved(ctx);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (this.channelType == PcapWriteHandler.ChannelType.TCP && this.state.get() == State.WRITING) {
         ByteBuf tcpBuf = ctx.alloc().buffer();

         try {
            TCPPacket.writePacket(
               tcpBuf,
               null,
               this.sendSegmentNumber,
               this.receiveSegmentNumber,
               this.initiatorAddr.getPort(),
               this.handlerAddr.getPort(),
               TCPPacket.TCPFlag.RST,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
         } finally {
            tcpBuf.release();
         }

         this.logger.debug("Sent Fake TCP RST to close connection");
      }

      this.close();
      ctx.fireExceptionCaught(cause);
   }

   private void logTCP(
      boolean isWriteOperation,
      int bytes,
      long sendSegmentNumber,
      long receiveSegmentNumber,
      InetSocketAddress srcAddr,
      InetSocketAddress dstAddr,
      boolean ackOnly
   ) {
      if (this.logger.isDebugEnabled()) {
         if (ackOnly) {
            this.logger
               .debug(
                  "Writing TCP ACK, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}",
                  isWriteOperation,
                  sendSegmentNumber,
                  receiveSegmentNumber,
                  dstAddr,
                  srcAddr
               );
         } else {
            this.logger
               .debug(
                  "Writing TCP Data of {} Bytes, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}",
                  bytes,
                  isWriteOperation,
                  sendSegmentNumber,
                  receiveSegmentNumber,
                  srcAddr,
                  dstAddr
               );
         }
      }
   }

   OutputStream outputStream() {
      return this.outputStream;
   }

   boolean sharedOutputStream() {
      return this.sharedOutputStream;
   }

   boolean writePcapGlobalHeader() {
      return this.writePcapGlobalHeader;
   }

   public boolean isWriting() {
      return this.state.get() == State.WRITING;
   }

   State state() {
      return this.state.get();
   }

   public void pause() {
      if (!this.state.compareAndSet(State.WRITING, State.PAUSED)) {
         throw new IllegalStateException("State must be 'STARTED' to pause but current state is: " + this.state);
      }
   }

   public void resume() {
      if (!this.state.compareAndSet(State.PAUSED, State.WRITING)) {
         throw new IllegalStateException("State must be 'PAUSED' to resume but current state is: " + this.state);
      }
   }

   void markClosed() {
      if (this.state.get() != State.CLOSED) {
         this.state.set(State.CLOSED);
      }
   }

   PcapWriter pCapWriter() {
      return this.pCapWriter;
   }

   private void logDiscard() {
      this.logger
         .warn(
            "Discarding pcap write because channel type is unknown. The channel this handler is registered on is not a SocketChannel or DatagramChannel, so the inference does not work. Please call forceTcpChannel or forceUdpChannel before registering the handler."
         );
   }

   @Override
   public String toString() {
      return "PcapWriteHandler{captureZeroByte="
         + this.captureZeroByte
         + ", writePcapGlobalHeader="
         + this.writePcapGlobalHeader
         + ", sharedOutputStream="
         + this.sharedOutputStream
         + ", sendSegmentNumber="
         + this.sendSegmentNumber
         + ", receiveSegmentNumber="
         + this.receiveSegmentNumber
         + ", channelType="
         + this.channelType
         + ", initiatorAddr="
         + this.initiatorAddr
         + ", handlerAddr="
         + this.handlerAddr
         + ", isServerPipeline="
         + this.isServerPipeline
         + ", state="
         + this.state
         + '}';
   }

   @Override
   public void close() throws IOException {
      if (this.state.get() == State.CLOSED) {
         this.logger.debug("PcapWriterHandler is already closed");
      } else {
         if (this.pCapWriter == null) {
            this.pCapWriter = new PcapWriter(this);
         }

         this.pCapWriter.close();
         this.markClosed();
         this.logger.debug("PcapWriterHandler is now closed");
      }
   }

   public static PcapWriteHandler.Builder builder() {
      return new PcapWriteHandler.Builder();
   }

   public static final class Builder {
      private boolean captureZeroByte;
      private boolean sharedOutputStream;
      private boolean writePcapGlobalHeader = true;
      private PcapWriteHandler.ChannelType channelType;
      private InetSocketAddress initiatorAddr;
      private InetSocketAddress handlerAddr;
      private boolean isServerPipeline;

      private Builder() {
      }

      public PcapWriteHandler.Builder captureZeroByte(boolean captureZeroByte) {
         this.captureZeroByte = captureZeroByte;
         return this;
      }

      public PcapWriteHandler.Builder sharedOutputStream(boolean sharedOutputStream) {
         this.sharedOutputStream = sharedOutputStream;
         return this;
      }

      public PcapWriteHandler.Builder writePcapGlobalHeader(boolean writePcapGlobalHeader) {
         this.writePcapGlobalHeader = writePcapGlobalHeader;
         return this;
      }

      public PcapWriteHandler.Builder forceTcpChannel(InetSocketAddress serverAddress, InetSocketAddress clientAddress, boolean isServerPipeline) {
         this.channelType = PcapWriteHandler.ChannelType.TCP;
         this.handlerAddr = ObjectUtil.checkNotNull(serverAddress, "serverAddress");
         this.initiatorAddr = ObjectUtil.checkNotNull(clientAddress, "clientAddress");
         this.isServerPipeline = isServerPipeline;
         return this;
      }

      public PcapWriteHandler.Builder forceUdpChannel(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
         this.channelType = PcapWriteHandler.ChannelType.UDP;
         this.handlerAddr = ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
         this.initiatorAddr = ObjectUtil.checkNotNull(localAddress, "localAddress");
         return this;
      }

      public PcapWriteHandler build(OutputStream outputStream) {
         ObjectUtil.checkNotNull(outputStream, "outputStream");
         return new PcapWriteHandler(this, outputStream);
      }
   }

   private static enum ChannelType {
      TCP,
      UDP;
   }

   private static final class WildcardAddressHolder {
      static final InetAddress wildcard4;
      static final InetAddress wildcard6;

      static {
         try {
            wildcard4 = InetAddress.getByAddress(new byte[4]);
            wildcard6 = InetAddress.getByAddress(new byte[16]);
         } catch (UnknownHostException var1) {
            throw new AssertionError(var1);
         }
      }
   }
}
