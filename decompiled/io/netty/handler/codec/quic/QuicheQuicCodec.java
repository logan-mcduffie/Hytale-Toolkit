package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

abstract class QuicheQuicCodec extends ChannelDuplexHandler {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(QuicheQuicCodec.class);
   private final ConnectionIdChannelMap connectionIdToChannel = new ConnectionIdChannelMap();
   private final Set<QuicheQuicChannel> channels = new HashSet<>();
   private final Queue<QuicheQuicChannel> needsFireChannelReadComplete = new ArrayDeque<>();
   private final Queue<QuicheQuicChannel> delayedRemoval = new ArrayDeque<>();
   private final Consumer<QuicheQuicChannel> freeTask = this::removeChannel;
   private final FlushStrategy flushStrategy;
   private final int localConnIdLength;
   private final QuicheConfig config;
   private MessageSizeEstimator.Handle estimatorHandle;
   private QuicHeaderParser headerParser;
   private QuicHeaderParser.QuicHeaderProcessor parserCallback;
   private int pendingBytes;
   private int pendingPackets;
   private boolean inChannelReadComplete;
   private boolean delayRemoval;
   private ByteBuf senderSockaddrMemory;
   private ByteBuf recipientSockaddrMemory;

   QuicheQuicCodec(QuicheConfig config, int localConnIdLength, FlushStrategy flushStrategy) {
      this.config = config;
      this.localConnIdLength = localConnIdLength;
      this.flushStrategy = flushStrategy;
   }

   @Override
   public final boolean isSharable() {
      return false;
   }

   @Nullable
   protected final QuicheQuicChannel getChannel(ByteBuffer key) {
      return this.connectionIdToChannel.get(key);
   }

   private void addMapping(QuicheQuicChannel channel, ByteBuffer id) {
      QuicheQuicChannel ch = this.connectionIdToChannel.put(id, channel);

      assert ch == null;
   }

   private void removeMapping(QuicheQuicChannel channel, ByteBuffer id) {
      QuicheQuicChannel ch = this.connectionIdToChannel.remove(id);

      assert ch == channel;
   }

   private void processDelayedRemoval() {
      while (true) {
         QuicheQuicChannel toBeRemoved = this.delayedRemoval.poll();
         if (toBeRemoved == null) {
            return;
         }

         this.removeChannel(toBeRemoved);
      }
   }

   private void removeChannel(QuicheQuicChannel channel) {
      if (this.delayRemoval) {
         boolean added = this.delayedRemoval.offer(channel);

         assert added;
      } else {
         boolean removed = this.channels.remove(channel);
         if (removed) {
            for (ByteBuffer id : channel.sourceConnectionIds()) {
               QuicheQuicChannel ch = this.connectionIdToChannel.remove(id);

               assert ch == channel;
            }
         }
      }
   }

   protected final void addChannel(QuicheQuicChannel channel) {
      boolean added = this.channels.add(channel);

      assert added;

      for (ByteBuffer id : channel.sourceConnectionIds()) {
         QuicheQuicChannel ch = this.connectionIdToChannel.put(id.duplicate(), channel);

         assert ch == null;
      }
   }

   @Override
   public final void handlerAdded(ChannelHandlerContext ctx) {
      this.senderSockaddrMemory = Quiche.allocateNativeOrder(Quiche.SIZEOF_SOCKADDR_STORAGE);
      this.recipientSockaddrMemory = Quiche.allocateNativeOrder(Quiche.SIZEOF_SOCKADDR_STORAGE);
      this.headerParser = new QuicHeaderParser(this.localConnIdLength);
      this.parserCallback = new QuicheQuicCodec.QuicCodecHeaderProcessor(ctx);
      this.estimatorHandle = ctx.channel().config().getMessageSizeEstimator().newHandle();
      this.handlerAdded(ctx, this.localConnIdLength);
   }

   protected void handlerAdded(ChannelHandlerContext ctx, int localConnIdLength) {
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) {
      try {
         for (QuicheQuicChannel ch : this.channels.toArray(new QuicheQuicChannel[0])) {
            ch.forceClose();
         }

         if (this.pendingPackets > 0) {
            this.flushNow(ctx);
         }
      } finally {
         this.channels.clear();
         this.connectionIdToChannel.clear();
         this.needsFireChannelReadComplete.clear();
         this.delayedRemoval.clear();
         this.config.free();
         if (this.senderSockaddrMemory != null) {
            this.senderSockaddrMemory.release();
         }

         if (this.recipientSockaddrMemory != null) {
            this.recipientSockaddrMemory.release();
         }

         if (this.headerParser != null) {
            this.headerParser.close();
            this.headerParser = null;
         }
      }
   }

   @Override
   public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      DatagramPacket packet = (DatagramPacket)msg;

      try {
         ByteBuf buffer = ((DatagramPacket)msg).content();
         if (!buffer.isDirect()) {
            ByteBuf direct = ctx.alloc().directBuffer(buffer.readableBytes());

            try {
               direct.writeBytes(buffer, buffer.readerIndex(), buffer.readableBytes());
               this.handleQuicPacket(packet.sender(), packet.recipient(), direct);
            } finally {
               direct.release();
            }
         } else {
            this.handleQuicPacket(packet.sender(), packet.recipient(), buffer);
         }
      } finally {
         packet.release();
      }
   }

   private void handleQuicPacket(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer) {
      try {
         this.headerParser.parse(sender, recipient, buffer, this.parserCallback);
      } catch (Exception var5) {
         LOGGER.debug("Error while processing QUIC packet", (Throwable)var5);
      }
   }

   @Nullable
   protected abstract QuicheQuicChannel quicPacketRead(
      ChannelHandlerContext var1,
      InetSocketAddress var2,
      InetSocketAddress var3,
      QuicPacketType var4,
      long var5,
      ByteBuf var7,
      ByteBuf var8,
      ByteBuf var9,
      ByteBuf var10,
      ByteBuf var11,
      Consumer<QuicheQuicChannel> var12,
      int var13,
      QuicheConfig var14
   ) throws Exception;

   @Override
   public final void channelReadComplete(ChannelHandlerContext ctx) {
      this.inChannelReadComplete = true;

      try {
         while (true) {
            QuicheQuicChannel channel = this.needsFireChannelReadComplete.poll();
            if (channel == null) {
               return;
            }

            channel.recvComplete();
         }
      } finally {
         this.inChannelReadComplete = false;
         if (this.pendingPackets > 0) {
            this.flushNow(ctx);
         }
      }
   }

   @Override
   public final void channelWritabilityChanged(ChannelHandlerContext ctx) {
      if (ctx.channel().isWritable()) {
         this.delayRemoval = true;

         try {
            for (QuicheQuicChannel channel : this.channels) {
               channel.writable();
            }
         } finally {
            this.delayRemoval = false;
            this.processDelayedRemoval();
         }
      } else {
         ctx.flush();
      }

      ctx.fireChannelWritabilityChanged();
   }

   @Override
   public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      this.pendingPackets++;
      int size = this.estimatorHandle.size(msg);
      if (size > 0) {
         this.pendingBytes += size;
      }

      try {
         ctx.write(msg, promise);
      } finally {
         this.flushIfNeeded(ctx);
      }
   }

   @Override
   public final void flush(ChannelHandlerContext ctx) {
      if (this.inChannelReadComplete) {
         this.flushIfNeeded(ctx);
      } else if (this.pendingPackets > 0) {
         this.flushNow(ctx);
      }
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if (remoteAddress instanceof QuicheQuicChannelAddress) {
         QuicheQuicChannelAddress addr = (QuicheQuicChannelAddress)remoteAddress;
         QuicheQuicChannel channel = addr.channel;
         this.connectQuicChannel(
            channel,
            remoteAddress,
            localAddress,
            this.senderSockaddrMemory,
            this.recipientSockaddrMemory,
            this.freeTask,
            this.localConnIdLength,
            this.config,
            promise
         );
      } else {
         ctx.connect(remoteAddress, localAddress, promise);
      }
   }

   protected abstract void connectQuicChannel(
      QuicheQuicChannel var1,
      SocketAddress var2,
      SocketAddress var3,
      ByteBuf var4,
      ByteBuf var5,
      Consumer<QuicheQuicChannel> var6,
      int var7,
      QuicheConfig var8,
      ChannelPromise var9
   );

   private void flushIfNeeded(ChannelHandlerContext ctx) {
      if (this.flushStrategy.shouldFlushNow(this.pendingPackets, this.pendingBytes)) {
         this.flushNow(ctx);
      }
   }

   private void flushNow(ChannelHandlerContext ctx) {
      this.pendingBytes = 0;
      this.pendingPackets = 0;
      ctx.flush();
   }

   private final class QuicCodecHeaderProcessor implements QuicHeaderParser.QuicHeaderProcessor {
      private final ChannelHandlerContext ctx;

      QuicCodecHeaderProcessor(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      @Override
      public void process(
         InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer, QuicPacketType type, long version, ByteBuf scid, ByteBuf dcid, ByteBuf token
      ) throws Exception {
         QuicheQuicChannel channel = QuicheQuicCodec.this.quicPacketRead(
            this.ctx,
            sender,
            recipient,
            type,
            version,
            scid,
            dcid,
            token,
            QuicheQuicCodec.this.senderSockaddrMemory,
            QuicheQuicCodec.this.recipientSockaddrMemory,
            QuicheQuicCodec.this.freeTask,
            QuicheQuicCodec.this.localConnIdLength,
            QuicheQuicCodec.this.config
         );
         if (channel != null) {
            if (channel.markInFireChannelReadCompleteQueue()) {
               QuicheQuicCodec.this.needsFireChannelReadComplete.add(channel);
            }

            channel.recv(sender, recipient, buffer);

            for (ByteBuffer retiredSourceConnectionId : channel.retiredSourceConnectionId()) {
               QuicheQuicCodec.this.removeMapping(channel, retiredSourceConnectionId);
            }

            for (ByteBuffer newSourceConnectionId : channel.newSourceConnectionIds()) {
               QuicheQuicCodec.this.addMapping(channel, newSourceConnectionId);
            }
         }
      }
   }
}
