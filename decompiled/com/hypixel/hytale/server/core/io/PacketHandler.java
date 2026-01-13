package com.hypixel.hytale.server.core.io;

import com.google.common.flogger.LazyArgs;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.NetworkUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.metrics.metric.Metric;
import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.protocol.packets.connection.Ping;
import com.hypixel.hytale.protocol.packets.connection.Pong;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.handlers.login.AuthenticationPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.login.PasswordPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.receiver.IPacketReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PacketHandler implements IPacketReceiver {
   public static final int MAX_PACKET_ID = 512;
   private static final HytaleLogger LOGIN_TIMING_LOGGER = HytaleLogger.get("LoginTiming");
   private static final AttributeKey<Long> LOGIN_START_ATTRIBUTE_KEY = AttributeKey.newInstance("LOGIN_START");
   @Nonnull
   protected final Channel channel;
   @Nonnull
   protected final ProtocolVersion protocolVersion;
   @Nullable
   protected PlayerAuthentication auth;
   protected boolean queuePackets;
   protected final AtomicInteger queuedPackets = new AtomicInteger();
   protected final SecureRandom pingIdRandom = new SecureRandom();
   @Nonnull
   protected final PacketHandler.PingInfo[] pingInfo;
   private float pingTimer;
   protected boolean registered;
   private ScheduledFuture<?> timeoutTask;
   @Nullable
   protected Throwable clientReadyForChunksFutureStack;
   @Nullable
   protected CompletableFuture<Void> clientReadyForChunksFuture;
   @Nonnull
   protected final PacketHandler.DisconnectReason disconnectReason = new PacketHandler.DisconnectReason();

   public PacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion) {
      this.channel = channel;
      this.protocolVersion = protocolVersion;
      this.pingInfo = new PacketHandler.PingInfo[PongType.VALUES.length];

      for (PongType pingType : PongType.VALUES) {
         this.pingInfo[pingType.ordinal()] = new PacketHandler.PingInfo(pingType);
      }
   }

   @Nonnull
   public Channel getChannel() {
      return this.channel;
   }

   @Deprecated(forRemoval = true)
   public void setCompressionEnabled(boolean compressionEnabled) {
      HytaleLogger.getLogger().at(Level.INFO).log(this.getIdentifier() + " compression now handled by encoder");
   }

   @Deprecated(forRemoval = true)
   public boolean isCompressionEnabled() {
      return true;
   }

   @Nonnull
   public abstract String getIdentifier();

   @Nonnull
   public ProtocolVersion getProtocolVersion() {
      return this.protocolVersion;
   }

   public final void registered(@Nullable PacketHandler oldHandler) {
      this.registered = true;
      this.registered0(oldHandler);
   }

   protected void registered0(@Nullable PacketHandler oldHandler) {
   }

   public final void unregistered(@Nullable PacketHandler newHandler) {
      this.registered = false;
      this.clearTimeout();
      this.unregistered0(newHandler);
   }

   protected void unregistered0(@Nullable PacketHandler newHandler) {
   }

   public void handle(@Nonnull Packet packet) {
      this.accept(packet);
   }

   public abstract void accept(@Nonnull Packet var1);

   public void logCloseMessage() {
      HytaleLogger.getLogger().at(Level.INFO).log("%s was closed.", this.getIdentifier());
   }

   public void closed(ChannelHandlerContext ctx) {
      this.clearTimeout();
   }

   public void setQueuePackets(boolean queuePackets) {
      this.queuePackets = queuePackets;
   }

   public void tryFlush() {
      if (this.queuedPackets.getAndSet(0) > 0) {
         this.channel.flush();
      }
   }

   public void write(@Nonnull Packet... packets) {
      Packet[] cachedPackets = new Packet[packets.length];
      this.handleOutboundAndCachePackets(packets, cachedPackets);
      if (this.queuePackets) {
         this.channel.write(cachedPackets, this.channel.voidPromise());
         this.queuedPackets.getAndIncrement();
      } else {
         this.channel.writeAndFlush(cachedPackets, this.channel.voidPromise());
      }
   }

   public void write(@Nonnull Packet[] packets, @Nonnull Packet finalPacket) {
      Packet[] cachedPackets = new Packet[packets.length + 1];
      this.handleOutboundAndCachePackets(packets, cachedPackets);
      cachedPackets[cachedPackets.length - 1] = this.handleOutboundAndCachePacket(finalPacket);
      if (this.queuePackets) {
         this.channel.write(cachedPackets, this.channel.voidPromise());
         this.queuedPackets.getAndIncrement();
      } else {
         this.channel.writeAndFlush(cachedPackets, this.channel.voidPromise());
      }
   }

   @Override
   public void write(@Nonnull Packet packet) {
      this.writePacket(packet, true);
   }

   @Override
   public void writeNoCache(@Nonnull Packet packet) {
      this.writePacket(packet, false);
   }

   public void writePacket(@Nonnull Packet packet, boolean cache) {
      if (!PacketAdapters.__handleOutbound(this, packet)) {
         Packet toSend;
         if (cache) {
            toSend = this.handleOutboundAndCachePacket(packet);
         } else {
            toSend = packet;
         }

         if (this.queuePackets) {
            this.channel.write(toSend, this.channel.voidPromise());
            this.queuedPackets.getAndIncrement();
         } else {
            this.channel.writeAndFlush(toSend, this.channel.voidPromise());
         }
      }
   }

   private void handleOutboundAndCachePackets(@Nonnull Packet[] packets, @Nonnull Packet[] cachedPackets) {
      for (int i = 0; i < packets.length; i++) {
         Packet packet = packets[i];
         if (!PacketAdapters.__handleOutbound(this, packet)) {
            cachedPackets[i] = this.handleOutboundAndCachePacket(packet);
         }
      }
   }

   @Nonnull
   private Packet handleOutboundAndCachePacket(@Nonnull Packet packet) {
      return (Packet)(packet instanceof CachedPacket ? packet : CachedPacket.cache(packet));
   }

   public void disconnect(@Nonnull String message) {
      this.disconnectReason.setServerDisconnectReason(message);
      HytaleLogger.getLogger().at(Level.INFO).log("Disconnecting %s with the message: %s", NettyUtil.formatRemoteAddress(this.channel), message);
      this.disconnect0(message);
   }

   protected void disconnect0(@Nonnull String message) {
      this.channel.writeAndFlush(new Disconnect(message, DisconnectType.Disconnect)).addListener(ProtocolUtil.CLOSE_ON_COMPLETE);
   }

   @Nullable
   public PacketStatsRecorder getPacketStatsRecorder() {
      return this.channel.attr(PacketStatsRecorder.CHANNEL_KEY).get();
   }

   @Nonnull
   public PacketHandler.PingInfo getPingInfo(@Nonnull PongType pongType) {
      return this.pingInfo[pongType.ordinal()];
   }

   public long getOperationTimeoutThreshold() {
      double average = this.getPingInfo(PongType.Tick).getPingMetricSet().getAverage(0);
      return PacketHandler.PingInfo.TIME_UNIT.toMillis(Math.round(average * 2.0)) + 3000L;
   }

   public void tickPing(float dt) {
      this.pingTimer -= dt;
      if (this.pingTimer <= 0.0F) {
         this.pingTimer = 1.0F;
         this.sendPing();
      }
   }

   public void sendPing() {
      int id = this.pingIdRandom.nextInt();
      Instant nowInstant = Instant.now();
      long nowTimestamp = System.nanoTime();

      for (PacketHandler.PingInfo info : this.pingInfo) {
         info.recordSent(id, nowTimestamp);
      }

      this.writeNoCache(
         new Ping(
            id,
            WorldTimeResource.instantToInstantData(nowInstant),
            (int)this.getPingInfo(PongType.Raw).getPingMetricSet().getLastValue(),
            (int)this.getPingInfo(PongType.Direct).getPingMetricSet().getLastValue(),
            (int)this.getPingInfo(PongType.Tick).getPingMetricSet().getLastValue()
         )
      );
   }

   public void handlePong(@Nonnull Pong packet) {
      this.pingInfo[packet.type.ordinal()].handlePacket(packet);
   }

   protected void setTimeout(@Nonnull String stageId, @Nonnull BooleanSupplier meets, long def, @Nonnull TimeUnit timeUnit) {
      if (this.timeoutTask != null) {
         this.timeoutTask.cancel(false);
      }

      if (this instanceof AuthenticationPacketHandler || !(this instanceof PasswordPacketHandler) || this.auth != null) {
         logConnectionTimings(this.channel, "setTimeout-" + stageId, Level.FINEST);
         Map<String, Duration> timeouts = HytaleServer.get().getConfig().getConnectionTimeouts().getJoinTimeouts();
         long timeout;
         if (timeouts.containsKey(stageId)) {
            timeout = timeouts.get(stageId).toMillis();
         } else {
            timeout = TimeUnit.MILLISECONDS.convert(def, timeUnit);
         }

         this.timeoutTask = this.channel
            .eventLoop()
            .schedule(
               () -> {
                  if (this.channel.isOpen()) {
                     if (!meets.getAsBoolean()) {
                        this.disconnect("Either you took too long to login or we took too long to process your request! Retry again in a moment.");
                        HytaleLogger.getLogger()
                           .at(Level.WARNING)
                           .log(
                              "Took longer than %s for %s to log in at stage %s! Aborting!",
                              FormatUtil.timeUnitToString(timeout, TimeUnit.MILLISECONDS),
                              this.getIdentifier(),
                              stageId
                           );
                     }
                  }
               },
               timeout,
               TimeUnit.MILLISECONDS
            );
      }
   }

   protected void clearTimeout() {
      if (this.timeoutTask != null) {
         this.timeoutTask.cancel(false);
      }

      if (this.clientReadyForChunksFuture != null) {
         this.clientReadyForChunksFuture.cancel(true);
         this.clientReadyForChunksFuture = null;
         this.clientReadyForChunksFutureStack = null;
      }
   }

   @Nullable
   public PlayerAuthentication getAuth() {
      return this.auth;
   }

   public boolean stillActive() {
      return this.channel.isActive();
   }

   public int getQueuedPacketsCount() {
      return this.queuedPackets.get();
   }

   public boolean isLocalConnection() {
      SocketAddress socketAddress;
      if (this.channel instanceof QuicStreamChannel quicStreamChannel) {
         socketAddress = quicStreamChannel.parent().remoteSocketAddress();
      } else {
         socketAddress = this.channel.remoteAddress();
      }

      if (socketAddress instanceof InetSocketAddress) {
         InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
         return NetworkUtil.addressMatchesAny(address, NetworkUtil.AddressType.ANY_LOCAL, NetworkUtil.AddressType.LOOPBACK);
      } else {
         return socketAddress instanceof DomainSocketAddress || socketAddress instanceof LocalAddress;
      }
   }

   public boolean isLANConnection() {
      SocketAddress socketAddress;
      if (this.channel instanceof QuicStreamChannel quicStreamChannel) {
         socketAddress = quicStreamChannel.parent().remoteSocketAddress();
      } else {
         socketAddress = this.channel.remoteAddress();
      }

      if (socketAddress instanceof InetSocketAddress) {
         InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
         return NetworkUtil.addressMatchesAny(address);
      } else {
         return socketAddress instanceof DomainSocketAddress || socketAddress instanceof LocalAddress;
      }
   }

   @Nonnull
   public PacketHandler.DisconnectReason getDisconnectReason() {
      return this.disconnectReason;
   }

   public void setClientReadyForChunksFuture(@Nonnull CompletableFuture<Void> clientReadyFuture) {
      if (this.clientReadyForChunksFuture != null) {
         throw new IllegalStateException("Tried to hook client ready but something is already waiting for it!", this.clientReadyForChunksFutureStack);
      } else {
         HytaleLogger.getLogger().at(Level.WARNING).log("%s Added future for ClientReady packet?", this.getIdentifier());
         this.clientReadyForChunksFutureStack = new Throwable();
         this.clientReadyForChunksFuture = clientReadyFuture;
      }
   }

   @Nullable
   public CompletableFuture<Void> getClientReadyForChunksFuture() {
      return this.clientReadyForChunksFuture;
   }

   public static void logConnectionTimings(@Nonnull Channel channel, @Nonnull String message, @Nonnull Level level) {
      Attribute<Long> loginStartAttribute = channel.attr(LOGIN_START_ATTRIBUTE_KEY);
      long now = System.nanoTime();
      Long before = loginStartAttribute.getAndSet(now);
      if (before == null) {
         LOGIN_TIMING_LOGGER.at(level).log(message);
      } else {
         LOGIN_TIMING_LOGGER.at(level).log("%s took %s", message, LazyArgs.lazy(() -> FormatUtil.nanosToString(now - before)));
      }
   }

   static {
      LOGIN_TIMING_LOGGER.setLevel(Level.ALL);
   }

   public static class DisconnectReason {
      @Nullable
      private String serverDisconnectReason;
      @Nullable
      private DisconnectType clientDisconnectType;

      protected DisconnectReason() {
      }

      @Nullable
      public String getServerDisconnectReason() {
         return this.serverDisconnectReason;
      }

      public void setServerDisconnectReason(String serverDisconnectReason) {
         this.serverDisconnectReason = serverDisconnectReason;
         this.clientDisconnectType = null;
      }

      @Nullable
      public DisconnectType getClientDisconnectType() {
         return this.clientDisconnectType;
      }

      public void setClientDisconnectType(DisconnectType clientDisconnectType) {
         this.clientDisconnectType = clientDisconnectType;
         this.serverDisconnectReason = null;
      }

      @Nonnull
      @Override
      public String toString() {
         return "DisconnectReason{serverDisconnectReason='" + this.serverDisconnectReason + "', clientDisconnectType=" + this.clientDisconnectType + "}";
      }
   }

   public static class PingInfo {
      public static final MetricsRegistry<PacketHandler.PingInfo> METRICS_REGISTRY = new MetricsRegistry<PacketHandler.PingInfo>()
         .register("PingType", pingInfo -> pingInfo.pingType, new EnumCodec<>(PongType.class))
         .register("PingMetrics", PacketHandler.PingInfo::getPingMetricSet, HistoricMetric.METRICS_CODEC)
         .register("PacketQueueMin", pingInfo -> pingInfo.packetQueueMetric.getMin(), Codec.LONG)
         .register("PacketQueueAvg", pingInfo -> pingInfo.packetQueueMetric.getAverage(), Codec.DOUBLE)
         .register("PacketQueueMax", pingInfo -> pingInfo.packetQueueMetric.getMax(), Codec.LONG);
      public static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;
      public static final int ONE_SECOND_INDEX = 0;
      public static final int ONE_MINUTE_INDEX = 1;
      public static final int FIVE_MINUTE_INDEX = 2;
      public static final double PERCENTILE = 0.99F;
      public static final int PING_FREQUENCY = 1;
      public static final TimeUnit PING_FREQUENCY_UNIT = TimeUnit.SECONDS;
      public static final int PING_FREQUENCY_MILLIS = 1000;
      public static final int PING_HISTORY_MILLIS = 15000;
      public static final int PING_HISTORY_LENGTH = 15;
      protected final PongType pingType;
      protected final Lock queueLock = new ReentrantLock();
      protected final IntPriorityQueue pingIdQueue = new IntArrayFIFOQueue(15);
      protected final LongPriorityQueue pingTimestampQueue = new LongArrayFIFOQueue(15);
      protected final Lock pingLock = new ReentrantLock();
      @Nonnull
      protected final HistoricMetric pingMetricSet;
      protected final Metric packetQueueMetric = new Metric();

      public PingInfo(PongType pingType) {
         this.pingType = pingType;
         this.pingMetricSet = HistoricMetric.builder(1000L, TimeUnit.MILLISECONDS)
            .addPeriod(1L, TimeUnit.SECONDS)
            .addPeriod(1L, TimeUnit.MINUTES)
            .addPeriod(5L, TimeUnit.MINUTES)
            .build();
      }

      protected void recordSent(int id, long timestamp) {
         this.queueLock.lock();

         try {
            this.pingIdQueue.enqueue(id);
            this.pingTimestampQueue.enqueue(timestamp);
         } finally {
            this.queueLock.unlock();
         }
      }

      protected void handlePacket(@Nonnull Pong packet) {
         if (packet.type != this.pingType) {
            throw new IllegalArgumentException("Got packet for " + packet.type + " but expected " + this.pingType);
         } else {
            this.queueLock.lock();

            int nextIdToHandle;
            long sentTimestamp;
            try {
               nextIdToHandle = this.pingIdQueue.dequeueInt();
               sentTimestamp = this.pingTimestampQueue.dequeueLong();
            } finally {
               this.queueLock.unlock();
            }

            if (packet.id != nextIdToHandle) {
               throw new IllegalArgumentException(String.valueOf(packet.id));
            } else {
               long nanoTime = System.nanoTime();
               long pingValue = nanoTime - sentTimestamp;
               if (pingValue <= 0L) {
                  throw new IllegalArgumentException(String.format("Ping must be received after its sent! %s", pingValue));
               } else {
                  this.pingLock.lock();

                  try {
                     this.pingMetricSet.add(nanoTime, TIME_UNIT.convert(pingValue, TimeUnit.NANOSECONDS));
                     this.packetQueueMetric.add(packet.packetQueueSize);
                  } finally {
                     this.pingLock.unlock();
                  }
               }
            }
         }
      }

      public PongType getPingType() {
         return this.pingType;
      }

      @Nonnull
      public Metric getPacketQueueMetric() {
         return this.packetQueueMetric;
      }

      @Nonnull
      public HistoricMetric getPingMetricSet() {
         return this.pingMetricSet;
      }

      public void clear() {
         this.pingLock.lock();

         try {
            this.packetQueueMetric.clear();
            this.pingMetricSet.clear();
         } finally {
            this.pingLock.unlock();
         }
      }
   }
}
