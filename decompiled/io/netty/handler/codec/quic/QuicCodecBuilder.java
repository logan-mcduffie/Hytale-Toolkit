package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import io.netty.util.internal.ObjectUtil;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class QuicCodecBuilder<B extends QuicCodecBuilder<B>> {
   private final boolean server;
   private Boolean grease;
   private Long maxIdleTimeout;
   private Long maxRecvUdpPayloadSize;
   private Long maxSendUdpPayloadSize;
   private Long initialMaxData;
   private Long initialMaxStreamDataBidiLocal;
   private Long initialMaxStreamDataBidiRemote;
   private Long initialMaxStreamDataUni;
   private Long initialMaxStreamsBidi;
   private Long initialMaxStreamsUni;
   private Long ackDelayExponent;
   private Long maxAckDelay;
   private Boolean disableActiveMigration;
   private Boolean enableHystart;
   private Boolean discoverPmtu;
   private QuicCongestionControlAlgorithm congestionControlAlgorithm;
   private Integer initialCongestionWindowPackets;
   private int localConnIdLength;
   private Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider;
   private FlushStrategy flushStrategy = FlushStrategy.DEFAULT;
   private Integer recvQueueLen;
   private Integer sendQueueLen;
   private Long activeConnectionIdLimit;
   private byte[] statelessResetToken;
   private Executor sslTaskExecutor;
   int version;

   QuicCodecBuilder(boolean server) {
      Quic.ensureAvailability();
      this.version = Quiche.QUICHE_PROTOCOL_VERSION;
      this.localConnIdLength = Quiche.QUICHE_MAX_CONN_ID_LEN;
      this.server = server;
   }

   QuicCodecBuilder(QuicCodecBuilder<B> builder) {
      Quic.ensureAvailability();
      this.server = builder.server;
      this.grease = builder.grease;
      this.maxIdleTimeout = builder.maxIdleTimeout;
      this.maxRecvUdpPayloadSize = builder.maxRecvUdpPayloadSize;
      this.maxSendUdpPayloadSize = builder.maxSendUdpPayloadSize;
      this.initialMaxData = builder.initialMaxData;
      this.initialMaxStreamDataBidiLocal = builder.initialMaxStreamDataBidiLocal;
      this.initialMaxStreamDataBidiRemote = builder.initialMaxStreamDataBidiRemote;
      this.initialMaxStreamDataUni = builder.initialMaxStreamDataUni;
      this.initialMaxStreamsBidi = builder.initialMaxStreamsBidi;
      this.initialMaxStreamsUni = builder.initialMaxStreamsUni;
      this.ackDelayExponent = builder.ackDelayExponent;
      this.maxAckDelay = builder.maxAckDelay;
      this.disableActiveMigration = builder.disableActiveMigration;
      this.enableHystart = builder.enableHystart;
      this.discoverPmtu = builder.discoverPmtu;
      this.congestionControlAlgorithm = builder.congestionControlAlgorithm;
      this.initialCongestionWindowPackets = builder.initialCongestionWindowPackets;
      this.localConnIdLength = builder.localConnIdLength;
      this.sslEngineProvider = builder.sslEngineProvider;
      this.flushStrategy = builder.flushStrategy;
      this.recvQueueLen = builder.recvQueueLen;
      this.sendQueueLen = builder.sendQueueLen;
      this.activeConnectionIdLimit = builder.activeConnectionIdLimit;
      this.statelessResetToken = builder.statelessResetToken;
      this.sslTaskExecutor = builder.sslTaskExecutor;
      this.version = builder.version;
   }

   protected final B self() {
      return (B)this;
   }

   public final B flushStrategy(FlushStrategy flushStrategy) {
      this.flushStrategy = Objects.requireNonNull(flushStrategy, "flushStrategy");
      return this.self();
   }

   public final B congestionControlAlgorithm(QuicCongestionControlAlgorithm congestionControlAlgorithm) {
      this.congestionControlAlgorithm = congestionControlAlgorithm;
      return this.self();
   }

   public final B initialCongestionWindowPackets(int numPackets) {
      this.initialCongestionWindowPackets = numPackets;
      return this.self();
   }

   public final B grease(boolean enable) {
      this.grease = enable;
      return this.self();
   }

   public final B maxIdleTimeout(long amount, TimeUnit unit) {
      this.maxIdleTimeout = unit.toMillis(ObjectUtil.checkPositiveOrZero(amount, "amount"));
      return this.self();
   }

   public final B maxSendUdpPayloadSize(long size) {
      this.maxSendUdpPayloadSize = ObjectUtil.checkPositiveOrZero(size, "value");
      return this.self();
   }

   public final B maxRecvUdpPayloadSize(long size) {
      this.maxRecvUdpPayloadSize = ObjectUtil.checkPositiveOrZero(size, "value");
      return this.self();
   }

   public final B initialMaxData(long value) {
      this.initialMaxData = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B initialMaxStreamDataBidirectionalLocal(long value) {
      this.initialMaxStreamDataBidiLocal = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B initialMaxStreamDataBidirectionalRemote(long value) {
      this.initialMaxStreamDataBidiRemote = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B initialMaxStreamDataUnidirectional(long value) {
      this.initialMaxStreamDataUni = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B initialMaxStreamsBidirectional(long value) {
      this.initialMaxStreamsBidi = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B initialMaxStreamsUnidirectional(long value) {
      this.initialMaxStreamsUni = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B ackDelayExponent(long value) {
      this.ackDelayExponent = ObjectUtil.checkPositiveOrZero(value, "value");
      return this.self();
   }

   public final B maxAckDelay(long amount, TimeUnit unit) {
      this.maxAckDelay = unit.toMillis(ObjectUtil.checkPositiveOrZero(amount, "amount"));
      return this.self();
   }

   public final B activeMigration(boolean enable) {
      this.disableActiveMigration = !enable;
      return this.self();
   }

   public final B hystart(boolean enable) {
      this.enableHystart = enable;
      return this.self();
   }

   public final B discoverPmtu(boolean enable) {
      this.discoverPmtu = enable;
      return this.self();
   }

   public final B localConnectionIdLength(int value) {
      this.localConnIdLength = ObjectUtil.checkInRange(value, 0, Quiche.QUICHE_MAX_CONN_ID_LEN, "value");
      return this.self();
   }

   public final B version(int version) {
      this.version = version;
      return this.self();
   }

   public final B datagram(int recvQueueLen, int sendQueueLen) {
      ObjectUtil.checkPositive(recvQueueLen, "recvQueueLen");
      ObjectUtil.checkPositive(sendQueueLen, "sendQueueLen");
      this.recvQueueLen = recvQueueLen;
      this.sendQueueLen = sendQueueLen;
      return this.self();
   }

   public final B sslContext(QuicSslContext sslContext) {
      if (this.server != sslContext.isServer()) {
         throw new IllegalArgumentException("QuicSslContext.isServer() " + sslContext.isServer() + " isn't supported by this builder");
      } else {
         return this.sslEngineProvider(q -> sslContext.newEngine(q.alloc()));
      }
   }

   public final B sslEngineProvider(Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider) {
      this.sslEngineProvider = sslEngineProvider;
      return this.self();
   }

   public final B sslTaskExecutor(Executor sslTaskExecutor) {
      this.sslTaskExecutor = sslTaskExecutor;
      return this.self();
   }

   public final B activeConnectionIdLimit(long limit) {
      ObjectUtil.checkPositive(limit, "limit");
      this.activeConnectionIdLimit = limit;
      return this.self();
   }

   public final B statelessResetToken(byte[] token) {
      if (token.length != 16) {
         throw new IllegalArgumentException("token must be 16 bytes but was " + token.length);
      } else {
         this.statelessResetToken = (byte[])token.clone();
         return this.self();
      }
   }

   private QuicheConfig createConfig() {
      return new QuicheConfig(
         this.version,
         this.grease,
         this.maxIdleTimeout,
         this.maxSendUdpPayloadSize,
         this.maxRecvUdpPayloadSize,
         this.initialMaxData,
         this.initialMaxStreamDataBidiLocal,
         this.initialMaxStreamDataBidiRemote,
         this.initialMaxStreamDataUni,
         this.initialMaxStreamsBidi,
         this.initialMaxStreamsUni,
         this.ackDelayExponent,
         this.maxAckDelay,
         this.disableActiveMigration,
         this.enableHystart,
         this.discoverPmtu,
         this.congestionControlAlgorithm,
         this.initialCongestionWindowPackets,
         this.recvQueueLen,
         this.sendQueueLen,
         this.activeConnectionIdLimit,
         this.statelessResetToken
      );
   }

   protected void validate() {
      if (this.sslEngineProvider == null) {
         throw new IllegalStateException("sslEngineProvider can't be null");
      }
   }

   public final ChannelHandler build() {
      this.validate();
      QuicheConfig config = this.createConfig();

      try {
         return this.build(config, this.sslEngineProvider, this.sslTaskExecutor, this.localConnIdLength, this.flushStrategy);
      } catch (Throwable var3) {
         config.free();
         throw var3;
      }
   }

   public abstract B clone();

   abstract ChannelHandler build(QuicheConfig var1, Function<QuicChannel, ? extends QuicSslEngine> var2, Executor var3, int var4, FlushStrategy var5);
}
