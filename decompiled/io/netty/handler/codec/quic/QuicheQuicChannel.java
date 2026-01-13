package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.ssl.SniCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.AttributeKey;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicChannel extends AbstractChannel implements QuicChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(QuicheQuicChannel.class);
   private static final String QLOG_FILE_EXTENSION = ".qlog";
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private long[] readableStreams = new long[4];
   private long[] writableStreams = new long[4];
   private final LongObjectMap<QuicheQuicStreamChannel> streams = new LongObjectHashMap<>();
   private final QuicheQuicChannelConfig config;
   private final boolean server;
   private final QuicStreamIdGenerator idGenerator;
   private final ChannelHandler streamHandler;
   private final Entry<ChannelOption<?>, Object>[] streamOptionsArray;
   private final Entry<AttributeKey<?>, Object>[] streamAttrsArray;
   private final QuicheQuicChannel.TimeoutHandler timeoutHandler;
   private final QuicConnectionIdGenerator connectionIdAddressGenerator;
   private final QuicResetTokenGenerator resetTokenGenerator;
   private final Set<ByteBuffer> sourceConnectionIds = new HashSet<>();
   private Consumer<QuicheQuicChannel> freeTask;
   private Executor sslTaskExecutor;
   private boolean inFireChannelReadCompleteQueue;
   private boolean fireChannelReadCompletePending;
   private ByteBuf finBuffer;
   private ByteBuf outErrorCodeBuffer;
   private ChannelPromise connectPromise;
   private ScheduledFuture<?> connectTimeoutFuture;
   private QuicConnectionAddress connectAddress;
   private QuicheQuicChannel.CloseData closeData;
   private QuicConnectionCloseEvent connectionCloseEvent;
   private QuicConnectionStats statsAtClose;
   private boolean supportsDatagram;
   private boolean recvDatagramPending;
   private boolean datagramReadable;
   private boolean recvStreamPending;
   private boolean streamReadable;
   private boolean handshakeCompletionNotified;
   private boolean earlyDataReadyNotified;
   private int reantranceGuard;
   private static final int IN_RECV = 2;
   private static final int IN_CONNECTION_SEND = 4;
   private static final int IN_HANDLE_WRITABLE_STREAMS = 8;
   private volatile QuicheQuicChannel.ChannelState state = QuicheQuicChannel.ChannelState.OPEN;
   private volatile boolean timedOut;
   private volatile String traceId;
   private volatile QuicheQuicConnection connection;
   private volatile InetSocketAddress local;
   private volatile InetSocketAddress remote;
   private final ChannelFutureListener continueSendingListener = f -> {
      if (this.connectionSend(this.connection) != QuicheQuicChannel.SendResult.NONE) {
         this.flushParent();
      }
   };
   private static final AtomicLongFieldUpdater<QuicheQuicChannel> UNI_STREAMS_LEFT_UPDATER = AtomicLongFieldUpdater.newUpdater(
      QuicheQuicChannel.class, "uniStreamsLeft"
   );
   private volatile long uniStreamsLeft;
   private static final AtomicLongFieldUpdater<QuicheQuicChannel> BIDI_STREAMS_LEFT_UPDATER = AtomicLongFieldUpdater.newUpdater(
      QuicheQuicChannel.class, "bidiStreamsLeft"
   );
   private volatile long bidiStreamsLeft;
   private static final int MAX_ARRAY_LEN = 128;

   private QuicheQuicChannel(
      Channel parent,
      boolean server,
      @Nullable ByteBuffer key,
      InetSocketAddress local,
      InetSocketAddress remote,
      boolean supportsDatagram,
      ChannelHandler streamHandler,
      Entry<ChannelOption<?>, Object>[] streamOptionsArray,
      Entry<AttributeKey<?>, Object>[] streamAttrsArray,
      @Nullable Consumer<QuicheQuicChannel> freeTask,
      @Nullable Executor sslTaskExecutor,
      @Nullable QuicConnectionIdGenerator connectionIdAddressGenerator,
      @Nullable QuicResetTokenGenerator resetTokenGenerator
   ) {
      super(parent);
      this.config = new QuicheQuicChannelConfig(this);
      this.freeTask = freeTask;
      this.server = server;
      this.idGenerator = new QuicStreamIdGenerator(server);
      this.connectionIdAddressGenerator = connectionIdAddressGenerator;
      this.resetTokenGenerator = resetTokenGenerator;
      if (key != null) {
         this.sourceConnectionIds.add(key);
      }

      this.supportsDatagram = supportsDatagram;
      this.local = local;
      this.remote = remote;
      this.streamHandler = streamHandler;
      this.streamOptionsArray = streamOptionsArray;
      this.streamAttrsArray = streamAttrsArray;
      this.timeoutHandler = new QuicheQuicChannel.TimeoutHandler();
      this.sslTaskExecutor = (Executor)(sslTaskExecutor == null ? ImmediateExecutor.INSTANCE : sslTaskExecutor);
   }

   static QuicheQuicChannel forClient(
      Channel parent,
      InetSocketAddress local,
      InetSocketAddress remote,
      ChannelHandler streamHandler,
      Entry<ChannelOption<?>, Object>[] streamOptionsArray,
      Entry<AttributeKey<?>, Object>[] streamAttrsArray
   ) {
      return new QuicheQuicChannel(parent, false, null, local, remote, false, streamHandler, streamOptionsArray, streamAttrsArray, null, null, null, null);
   }

   static QuicheQuicChannel forServer(
      Channel parent,
      ByteBuffer key,
      InetSocketAddress local,
      InetSocketAddress remote,
      boolean supportsDatagram,
      ChannelHandler streamHandler,
      Entry<ChannelOption<?>, Object>[] streamOptionsArray,
      Entry<AttributeKey<?>, Object>[] streamAttrsArray,
      Consumer<QuicheQuicChannel> freeTask,
      Executor sslTaskExecutor,
      QuicConnectionIdGenerator connectionIdAddressGenerator,
      QuicResetTokenGenerator resetTokenGenerator
   ) {
      return new QuicheQuicChannel(
         parent,
         true,
         key,
         local,
         remote,
         supportsDatagram,
         streamHandler,
         streamOptionsArray,
         streamAttrsArray,
         freeTask,
         sslTaskExecutor,
         connectionIdAddressGenerator,
         resetTokenGenerator
      );
   }

   private static long[] growIfNeeded(long[] array, int maxLength) {
      if (maxLength > array.length) {
         return array.length == 128 ? array : new long[Math.min(128, array.length + 4)];
      } else {
         return array;
      }
   }

   @Override
   public boolean isTimedOut() {
      return this.timedOut;
   }

   @Override
   public SSLEngine sslEngine() {
      QuicheQuicConnection connection = this.connection;
      return connection == null ? null : connection.engine();
   }

   private void notifyAboutHandshakeCompletionIfNeeded(QuicheQuicConnection conn, @Nullable SSLHandshakeException cause) {
      if (!this.handshakeCompletionNotified) {
         if (cause != null) {
            this.pipeline().fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
         } else if (!conn.isFreed()) {
            switch (this.connection.engine().getHandshakeStatus()) {
               case NOT_HANDSHAKING:
               case FINISHED:
                  this.handshakeCompletionNotified = true;
                  this.pipeline().fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
            }
         }
      }
   }

   @Override
   public long peerAllowedStreams(QuicStreamType type) {
      switch (type) {
         case BIDIRECTIONAL:
            return this.bidiStreamsLeft;
         case UNIDIRECTIONAL:
            return this.uniStreamsLeft;
         default:
            return 0L;
      }
   }

   void attachQuicheConnection(QuicheQuicConnection connection) {
      this.connection = connection;
      byte[] traceId = Quiche.quiche_conn_trace_id(connection.address());
      if (traceId != null) {
         this.traceId = new String(traceId);
      }

      connection.init(this.local, this.remote, sniHostname -> this.pipeline().fireUserEventTriggered(new SniCompletionEvent(sniHostname)));
      QLogConfiguration configuration = this.config.getQLogConfiguration();
      if (configuration != null) {
         File file = new File(configuration.path());
         String fileName;
         if (file.isDirectory()) {
            file.mkdir();
            if (this.traceId != null) {
               fileName = configuration.path() + File.separatorChar + this.traceId + "-" + this.id().asShortText() + ".qlog";
            } else {
               fileName = configuration.path() + File.separatorChar + this.id().asShortText() + ".qlog";
            }
         } else {
            fileName = configuration.path();
         }

         if (!Quiche.quiche_conn_set_qlog_path(connection.address(), fileName, configuration.logTitle(), configuration.logDescription())) {
            logger.info("Unable to create qlog file: {} ", fileName);
         }
      }
   }

   void connectNow(
      Function<QuicChannel, ? extends QuicSslEngine> engineProvider,
      Executor sslTaskExecutor,
      Consumer<QuicheQuicChannel> freeTask,
      long configAddr,
      int localConnIdLength,
      boolean supportsDatagram,
      ByteBuffer fromSockaddrMemory,
      ByteBuffer toSockaddrMemory
   ) throws Exception {
      assert this.connection == null;

      assert this.traceId == null;

      assert this.sourceConnectionIds.isEmpty();

      this.sslTaskExecutor = sslTaskExecutor;
      this.freeTask = freeTask;
      QuicConnectionAddress address = this.connectAddress;
      if (address == QuicConnectionAddress.EPHEMERAL) {
         address = QuicConnectionAddress.random(localConnIdLength);
      }

      ByteBuffer connectId = address.id();
      if (connectId.remaining() != localConnIdLength) {
         this.failConnectPromiseAndThrow(
            new IllegalArgumentException("connectionAddress has length " + connectId.remaining() + " instead of " + localConnIdLength)
         );
      }

      QuicSslEngine engine = engineProvider.apply(this);
      if (!(engine instanceof QuicheQuicSslEngine)) {
         this.failConnectPromiseAndThrow(new IllegalArgumentException("QuicSslEngine is not of type " + QuicheQuicSslEngine.class.getSimpleName()));
      } else {
         if (!engine.getUseClientMode()) {
            this.failConnectPromiseAndThrow(new IllegalArgumentException("QuicSslEngine is not create in client mode"));
         }

         QuicheQuicSslEngine quicheEngine = (QuicheQuicSslEngine)engine;
         ByteBuf idBuffer = this.alloc().directBuffer(connectId.remaining()).writeBytes(connectId.duplicate());

         try {
            int fromSockaddrLen = SockaddrIn.setAddress(fromSockaddrMemory, this.local);
            int toSockaddrLen = SockaddrIn.setAddress(toSockaddrMemory, this.remote);
            QuicheQuicConnection connection = quicheEngine.createConnection(
               ssl -> Quiche.quiche_conn_new_with_tls(
                  Quiche.readerMemoryAddress(idBuffer),
                  idBuffer.readableBytes(),
                  -1L,
                  -1,
                  Quiche.memoryAddressWithPosition(fromSockaddrMemory),
                  fromSockaddrLen,
                  Quiche.memoryAddressWithPosition(toSockaddrMemory),
                  toSockaddrLen,
                  configAddr,
                  ssl,
                  false
               )
            );
            if (connection != null) {
               this.attachQuicheConnection(connection);
               QuicClientSessionCache sessionCache = quicheEngine.ctx.getSessionCache();
               if (sessionCache != null) {
                  byte[] sessionBytes = sessionCache.getSession(quicheEngine.getSession().getPeerHost(), quicheEngine.getSession().getPeerPort());
                  if (sessionBytes != null) {
                     Quiche.quiche_conn_set_session(connection.address(), sessionBytes);
                  }
               }

               this.supportsDatagram = supportsDatagram;
               this.sourceConnectionIds.add(connectId);
               return;
            }

            this.failConnectPromiseAndThrow(new ConnectException());
         } finally {
            idBuffer.release();
         }
      }
   }

   private void failConnectPromiseAndThrow(Exception e) throws Exception {
      this.tryFailConnectPromise(e);
      throw e;
   }

   private boolean tryFailConnectPromise(Exception e) {
      ChannelPromise promise = this.connectPromise;
      if (promise != null) {
         this.connectPromise = null;
         promise.tryFailure(e);
         return true;
      } else {
         return false;
      }
   }

   Set<ByteBuffer> sourceConnectionIds() {
      return this.sourceConnectionIds;
   }

   boolean markInFireChannelReadCompleteQueue() {
      if (this.inFireChannelReadCompleteQueue) {
         return false;
      } else {
         this.inFireChannelReadCompleteQueue = true;
         return true;
      }
   }

   private void failPendingConnectPromise() {
      ChannelPromise promise = this.connectPromise;
      if (promise != null) {
         this.connectPromise = null;
         promise.tryFailure(new QuicClosedChannelException(this.connectionCloseEvent));
      }
   }

   void forceClose() {
      this.unsafe().close(this.voidPromise());
   }

   @Override
   protected DefaultChannelPipeline newChannelPipeline() {
      return new DefaultChannelPipeline(this) {
         @Override
         protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof QuicStreamChannel) {
               QuicStreamChannel channel = (QuicStreamChannel)msg;
               Quic.setupChannel(
                  channel,
                  QuicheQuicChannel.this.streamOptionsArray,
                  QuicheQuicChannel.this.streamAttrsArray,
                  QuicheQuicChannel.this.streamHandler,
                  QuicheQuicChannel.logger
               );
               ctx.channel().eventLoop().register(channel);
            } else {
               super.onUnhandledInboundMessage(ctx, msg);
            }
         }
      };
   }

   @Override
   public QuicChannel flush() {
      super.flush();
      return this;
   }

   @Override
   public QuicChannel read() {
      super.read();
      return this;
   }

   @Override
   public Future<QuicStreamChannel> createStream(QuicStreamType type, @Nullable ChannelHandler handler, Promise<QuicStreamChannel> promise) {
      if (this.eventLoop().inEventLoop()) {
         ((QuicheQuicChannel.QuicChannelUnsafe)this.unsafe()).connectStream(type, handler, promise);
      } else {
         this.eventLoop().execute(() -> ((QuicheQuicChannel.QuicChannelUnsafe)this.unsafe()).connectStream(type, handler, promise));
      }

      return promise;
   }

   @Override
   public ChannelFuture close(boolean applicationClose, int error, ByteBuf reason, ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.close0(applicationClose, error, reason, promise);
      } else {
         this.eventLoop().execute(() -> this.close0(applicationClose, error, reason, promise));
      }

      return promise;
   }

   private void close0(boolean applicationClose, int error, ByteBuf reason, ChannelPromise promise) {
      if (this.closeData == null) {
         if (!reason.hasMemoryAddress()) {
            ByteBuf copy = this.alloc().directBuffer(reason.readableBytes()).writeBytes(reason);
            reason.release();
            reason = copy;
         }

         this.closeData = new QuicheQuicChannel.CloseData(applicationClose, error, reason);
         promise.addListener(this.closeData);
      } else {
         reason.release();
      }

      this.close(promise);
   }

   @Override
   public String toString() {
      String traceId = this.traceId;
      return traceId == null ? "()" + super.toString() : '(' + traceId + ')' + super.toString();
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new QuicheQuicChannel.QuicChannelUnsafe();
   }

   @Override
   protected boolean isCompatible(EventLoop eventLoop) {
      return this.parent().eventLoop() == eventLoop;
   }

   @Nullable
   protected QuicConnectionAddress localAddress0() {
      QuicheQuicConnection connection = this.connection;
      return connection == null ? null : connection.sourceId();
   }

   @Nullable
   protected QuicConnectionAddress remoteAddress0() {
      QuicheQuicConnection connection = this.connection;
      return connection == null ? null : connection.destinationId();
   }

   @Nullable
   @Override
   public QuicConnectionAddress localAddress() {
      return this.localAddress0();
   }

   @Nullable
   @Override
   public QuicConnectionAddress remoteAddress() {
      return this.remoteAddress0();
   }

   @Nullable
   @Override
   public SocketAddress localSocketAddress() {
      return this.local;
   }

   @Nullable
   @Override
   public SocketAddress remoteSocketAddress() {
      return this.remote;
   }

   @Override
   protected void doBind(SocketAddress socketAddress) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   protected void doClose() throws Exception {
      if (this.state != QuicheQuicChannel.ChannelState.CLOSED) {
         this.state = QuicheQuicChannel.ChannelState.CLOSED;
         QuicheQuicConnection conn = this.connection;
         if (conn != null && !conn.isFreed()) {
            QuicheQuicChannel.SendResult sendResult = this.connectionSend(conn);
            boolean app;
            int err;
            ByteBuf reason;
            if (this.closeData == null) {
               app = false;
               err = 0;
               reason = Unpooled.EMPTY_BUFFER;
            } else {
               app = this.closeData.applicationClose;
               err = this.closeData.err;
               reason = this.closeData.reason;
               this.closeData = null;
            }

            this.failPendingConnectPromise();

            try {
               int res = Quiche.quiche_conn_close(conn.address(), app, err, Quiche.readerMemoryAddress(reason), reason.readableBytes());
               if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
                  throw Quiche.convertToException(res);
               }

               if (this.connectionSend(conn) == QuicheQuicChannel.SendResult.SOME) {
                  sendResult = QuicheQuicChannel.SendResult.SOME;
               }
            } finally {
               this.statsAtClose = this.collectStats0(conn, this.eventLoop().newPromise());

               try {
                  this.timedOut = Quiche.quiche_conn_is_timed_out(conn.address());
                  this.closeStreams();
                  if (this.finBuffer != null) {
                     this.finBuffer.release();
                     this.finBuffer = null;
                  }

                  if (this.outErrorCodeBuffer != null) {
                     this.outErrorCodeBuffer.release();
                     this.outErrorCodeBuffer = null;
                  }
               } finally {
                  if (sendResult == QuicheQuicChannel.SendResult.SOME) {
                     this.forceFlushParent();
                  } else {
                     this.flushParent();
                  }

                  conn.free();
                  if (this.freeTask != null) {
                     this.freeTask.accept(this);
                  }

                  this.timeoutHandler.cancel();
                  this.local = null;
                  this.remote = null;
               }
            }
         } else {
            if (this.closeData != null) {
               this.closeData.reason.release();
               this.closeData = null;
            }

            this.failPendingConnectPromise();
         }
      }
   }

   @Override
   protected void doBeginRead() {
      this.recvDatagramPending = true;
      this.recvStreamPending = true;
      if (this.datagramReadable || this.streamReadable) {
         ((QuicheQuicChannel.QuicChannelUnsafe)this.unsafe()).recv();
      }
   }

   @Override
   protected Object filterOutboundMessage(Object msg) {
      if (msg instanceof ByteBuf) {
         return msg;
      } else {
         throw new UnsupportedOperationException("Unsupported message type: " + StringUtil.simpleClassName(msg));
      }
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) throws Exception {
      if (!this.supportsDatagram) {
         throw new UnsupportedOperationException("Datagram extension is not supported");
      } else {
         boolean sendSomething = false;
         boolean retry = false;
         QuicheQuicConnection conn = this.connection;

         try {
            while (true) {
               ByteBuf buffer = (ByteBuf)channelOutboundBuffer.current();
               if (buffer == null) {
                  return;
               }

               int readable = buffer.readableBytes();
               if (readable == 0) {
                  channelOutboundBuffer.remove();
               } else {
                  int res;
                  if (buffer.isDirect() && buffer.nioBufferCount() <= 1) {
                     res = sendDatagram(conn, buffer);
                  } else {
                     ByteBuf tmpBuffer = this.alloc().directBuffer(readable);

                     try {
                        tmpBuffer.writeBytes(buffer, buffer.readerIndex(), readable);
                        res = sendDatagram(conn, tmpBuffer);
                     } finally {
                        tmpBuffer.release();
                     }
                  }

                  if (res >= 0) {
                     channelOutboundBuffer.remove();
                     sendSomething = true;
                     retry = false;
                  } else if (res == Quiche.QUICHE_ERR_BUFFER_TOO_SHORT) {
                     retry = false;
                     channelOutboundBuffer.remove(new BufferUnderflowException());
                  } else {
                     if (res == Quiche.QUICHE_ERR_INVALID_STATE) {
                        throw new UnsupportedOperationException("Remote peer does not support Datagram extension");
                     }

                     if (res != Quiche.QUICHE_ERR_DONE) {
                        throw Quiche.convertToException(res);
                     }

                     if (!retry) {
                        sendSomething = false;
                        if (this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
                           this.forceFlushParent();
                        }

                        retry = true;
                     } else {
                        while (channelOutboundBuffer.remove()) {
                        }

                        return;
                     }
                  }
               }
            }
         } finally {
            if (sendSomething && this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
               this.flushParent();
            }
         }
      }
   }

   private static int sendDatagram(QuicheQuicConnection conn, ByteBuf buf) throws ClosedChannelException {
      return Quiche.quiche_conn_dgram_send(connectionAddressChecked(conn), Quiche.readerMemoryAddress(buf), buf.readableBytes());
   }

   @Override
   public QuicChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isOpen() {
      return this.state != QuicheQuicChannel.ChannelState.CLOSED;
   }

   @Override
   public boolean isActive() {
      return this.state == QuicheQuicChannel.ChannelState.ACTIVE;
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   private void flushParent() {
      if (!this.inFireChannelReadCompleteQueue) {
         this.forceFlushParent();
      }
   }

   private void forceFlushParent() {
      this.parent().flush();
   }

   private static long connectionAddressChecked(@Nullable QuicheQuicConnection conn) throws ClosedChannelException {
      if (conn != null && !conn.isFreed()) {
         return conn.address();
      } else {
         throw new ClosedChannelException();
      }
   }

   boolean freeIfClosed() {
      QuicheQuicConnection conn = this.connection;
      if (conn == null || conn.isFreed()) {
         return true;
      } else if (conn.isClosed()) {
         this.unsafe().close(this.newPromise());
         return true;
      } else {
         return false;
      }
   }

   private void closeStreams() {
      if (!this.streams.isEmpty()) {
         ClosedChannelException closedChannelException;
         if (this.isTimedOut()) {
            closedChannelException = new QuicTimeoutClosedChannelException();
         } else {
            closedChannelException = new ClosedChannelException();
         }

         for (QuicheQuicStreamChannel stream : this.streams.values().toArray(new QuicheQuicStreamChannel[0])) {
            stream.unsafe().close(closedChannelException, this.voidPromise());
         }

         this.streams.clear();
      }
   }

   void streamPriority(long streamId, byte priority, boolean incremental) throws Exception {
      int res = Quiche.quiche_conn_stream_priority(connectionAddressChecked(this.connection), streamId, priority, incremental);
      if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
         throw Quiche.convertToException(res);
      }
   }

   void streamClosed(long streamId) {
      this.streams.remove(streamId);
   }

   boolean isStreamLocalCreated(long streamId) {
      return (streamId & 1L) == (this.server ? 1 : 0);
   }

   QuicStreamType streamType(long streamId) {
      return (streamId & 2L) == 0L ? QuicStreamType.BIDIRECTIONAL : QuicStreamType.UNIDIRECTIONAL;
   }

   void streamShutdown(long streamId, boolean read, boolean write, int err, ChannelPromise promise) {
      QuicheQuicConnection conn = this.connection;

      long connectionAddress;
      try {
         connectionAddress = connectionAddressChecked(conn);
      } catch (ClosedChannelException var11) {
         promise.setFailure(var11);
         return;
      }

      int res = 0;
      if (read) {
         res |= Quiche.quiche_conn_stream_shutdown(connectionAddress, streamId, Quiche.QUICHE_SHUTDOWN_READ, err);
      }

      if (write) {
         res |= Quiche.quiche_conn_stream_shutdown(connectionAddress, streamId, Quiche.QUICHE_SHUTDOWN_WRITE, err);
      }

      if (this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
         this.forceFlushParent();
      }

      if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
         promise.setFailure(Quiche.convertToException(res));
      } else {
         promise.setSuccess();
      }
   }

   void streamSendFin(long streamId) throws Exception {
      QuicheQuicConnection conn = this.connection;

      try {
         int res = this.streamSend0(conn, streamId, Unpooled.EMPTY_BUFFER, true);
         if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
            throw Quiche.convertToException(res);
         }
      } finally {
         if (this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
            this.flushParent();
         }
      }
   }

   int streamSend(long streamId, ByteBuf buffer, boolean fin) throws ClosedChannelException {
      QuicheQuicConnection conn = this.connection;
      if (buffer.nioBufferCount() == 1) {
         return this.streamSend0(conn, streamId, buffer, fin);
      } else {
         ByteBuffer[] nioBuffers = buffer.nioBuffers();
         int lastIdx = nioBuffers.length - 1;
         int res = 0;

         for (int i = 0; i < lastIdx; i++) {
            ByteBuffer nioBuffer = nioBuffers[i];

            while (nioBuffer.hasRemaining()) {
               int localRes = this.streamSend(conn, streamId, nioBuffer, false);
               if (localRes <= 0) {
                  return res;
               }

               res += localRes;
               ((Buffer)nioBuffer).position(nioBuffer.position() + localRes);
            }
         }

         int localRes = this.streamSend(conn, streamId, nioBuffers[lastIdx], fin);
         if (localRes > 0) {
            res += localRes;
         }

         return res;
      }
   }

   void connectionSendAndFlush() {
      if (!this.inFireChannelReadCompleteQueue && (this.reantranceGuard & 8) == 0) {
         if (this.connectionSend(this.connection) != QuicheQuicChannel.SendResult.NONE) {
            this.flushParent();
         }
      }
   }

   private int streamSend0(QuicheQuicConnection conn, long streamId, ByteBuf buffer, boolean fin) throws ClosedChannelException {
      return Quiche.quiche_conn_stream_send(connectionAddressChecked(conn), streamId, Quiche.readerMemoryAddress(buffer), buffer.readableBytes(), fin);
   }

   private int streamSend(QuicheQuicConnection conn, long streamId, ByteBuffer buffer, boolean fin) throws ClosedChannelException {
      return Quiche.quiche_conn_stream_send(connectionAddressChecked(conn), streamId, Quiche.memoryAddressWithPosition(buffer), buffer.remaining(), fin);
   }

   QuicheQuicChannel.StreamRecvResult streamRecv(long streamId, ByteBuf buffer) throws Exception {
      QuicheQuicConnection conn = this.connection;
      long connAddr = connectionAddressChecked(conn);
      if (this.finBuffer == null) {
         this.finBuffer = this.alloc().directBuffer(1);
      }

      if (this.outErrorCodeBuffer == null) {
         this.outErrorCodeBuffer = this.alloc().directBuffer(8);
      }

      this.outErrorCodeBuffer.setLongLE(0, -1L);
      int writerIndex = buffer.writerIndex();
      int recvLen = Quiche.quiche_conn_stream_recv(
         connAddr,
         streamId,
         Quiche.writerMemoryAddress(buffer),
         buffer.writableBytes(),
         Quiche.writerMemoryAddress(this.finBuffer),
         Quiche.writerMemoryAddress(this.outErrorCodeBuffer)
      );
      long errorCode = this.outErrorCodeBuffer.getLongLE(0);
      if (recvLen == Quiche.QUICHE_ERR_DONE) {
         return QuicheQuicChannel.StreamRecvResult.DONE;
      } else if (recvLen < 0) {
         throw Quiche.convertToException(recvLen, errorCode);
      } else {
         buffer.writerIndex(writerIndex + recvLen);
         return this.finBuffer.getBoolean(0) ? QuicheQuicChannel.StreamRecvResult.FIN : QuicheQuicChannel.StreamRecvResult.OK;
      }
   }

   void recv(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer) {
      ((QuicheQuicChannel.QuicChannelUnsafe)this.unsafe()).connectionRecv(sender, recipient, buffer);
   }

   List<ByteBuffer> retiredSourceConnectionId() {
      QuicheQuicConnection connection = this.connection;
      if (connection != null && !connection.isFreed()) {
         long connAddr = connection.address();

         assert connAddr != -1L;

         List<ByteBuffer> retiredSourceIds = null;

         while (true) {
            byte[] retired = Quiche.quiche_conn_retired_scid_next(connAddr);
            if (retired == null) {
               if (retiredSourceIds == null) {
                  return Collections.emptyList();
               }

               return retiredSourceIds;
            }

            if (retiredSourceIds == null) {
               retiredSourceIds = new ArrayList<>();
            }

            ByteBuffer retiredId = ByteBuffer.wrap(retired);
            retiredSourceIds.add(retiredId);
            this.sourceConnectionIds.remove(retiredId);
         }
      } else {
         return Collections.emptyList();
      }
   }

   List<ByteBuffer> newSourceConnectionIds() {
      if (this.connectionIdAddressGenerator != null && this.resetTokenGenerator != null) {
         QuicheQuicConnection connection = this.connection;
         if (connection == null || connection.isFreed()) {
            return Collections.emptyList();
         }

         long connAddr = connection.address();
         int left = Quiche.quiche_conn_scids_left(connAddr);
         if (left > 0) {
            QuicConnectionAddress sourceAddr = connection.sourceId();
            if (sourceAddr == null) {
               return Collections.emptyList();
            }

            List<ByteBuffer> generatedIds = new ArrayList<>(left);
            boolean sendAndFlush = false;
            ByteBuffer key = sourceAddr.id();
            ByteBuf connIdBuffer = this.alloc().directBuffer(key.remaining());
            byte[] resetTokenArray = new byte[16];

            try {
               do {
                  ByteBuffer srcId = this.connectionIdAddressGenerator.newId(key.duplicate(), key.remaining()).asReadOnlyBuffer();
                  connIdBuffer.clear();
                  connIdBuffer.writeBytes(srcId.duplicate());
                  ByteBuffer resetToken = this.resetTokenGenerator.newResetToken(srcId.duplicate());
                  resetToken.get(resetTokenArray);
                  long result = Quiche.quiche_conn_new_scid(
                     connAddr, Quiche.memoryAddress(connIdBuffer, 0, connIdBuffer.readableBytes()), connIdBuffer.readableBytes(), resetTokenArray, false, -1L
                  );
                  if (result < 0L) {
                     break;
                  }

                  sendAndFlush = true;
                  generatedIds.add(srcId.duplicate());
                  this.sourceConnectionIds.add(srcId);
               } while (--left > 0);
            } finally {
               connIdBuffer.release();
            }

            if (sendAndFlush) {
               this.connectionSendAndFlush();
            }

            return generatedIds;
         }
      }

      return Collections.emptyList();
   }

   void writable() {
      QuicheQuicConnection conn = this.connection;
      QuicheQuicChannel.SendResult result = this.connectionSend(conn);
      this.handleWritableStreams(conn);
      if (this.connectionSend(conn) == QuicheQuicChannel.SendResult.SOME) {
         result = QuicheQuicChannel.SendResult.SOME;
      }

      if (result == QuicheQuicChannel.SendResult.SOME) {
         this.forceFlushParent();
      }

      this.freeIfClosed();
   }

   long streamCapacity(long streamId) {
      QuicheQuicConnection conn = this.connection;
      return conn.isClosed() ? 0L : Quiche.quiche_conn_stream_capacity(conn.address(), streamId);
   }

   private boolean handleWritableStreams(QuicheQuicConnection conn) {
      if (conn.isFreed()) {
         return false;
      } else {
         this.reantranceGuard |= 8;

         boolean var23;
         try {
            long connAddr = conn.address();
            boolean mayNeedWrite = false;
            if (Quiche.quiche_conn_is_established(connAddr) || Quiche.quiche_conn_is_in_early_data(connAddr)) {
               long writableIterator = Quiche.quiche_conn_writable(connAddr);
               int totalWritable = 0;

               int writable;
               try {
                  do {
                     writable = Quiche.quiche_stream_iter_next(writableIterator, this.writableStreams);

                     for (int i = 0; i < writable; i++) {
                        long streamId = this.writableStreams[i];
                        QuicheQuicStreamChannel streamChannel = this.streams.get(streamId);
                        if (streamChannel != null) {
                           long capacity = Quiche.quiche_conn_stream_capacity(connAddr, streamId);
                           if (streamChannel.writable(capacity)) {
                              mayNeedWrite = true;
                           }
                        }
                     }

                     if (writable > 0) {
                        totalWritable += writable;
                     }
                  } while (writable >= this.writableStreams.length);
               } finally {
                  Quiche.quiche_stream_iter_free(writableIterator);
               }

               this.writableStreams = growIfNeeded(this.writableStreams, totalWritable);
            }

            var23 = mayNeedWrite;
         } finally {
            this.reantranceGuard &= -9;
         }

         return var23;
      }
   }

   void recvComplete() {
      try {
         QuicheQuicConnection conn = this.connection;
         if (!conn.isFreed()) {
            this.fireChannelReadCompleteIfNeeded();
            this.connectionSend(conn);
            this.forceFlushParent();
            this.freeIfClosed();
            return;
         }

         this.forceFlushParent();
      } finally {
         this.inFireChannelReadCompleteQueue = false;
      }
   }

   private void fireChannelReadCompleteIfNeeded() {
      if (this.fireChannelReadCompletePending) {
         this.fireChannelReadCompletePending = false;
         this.pipeline().fireChannelReadComplete();
      }
   }

   private void fireExceptionEvents(QuicheQuicConnection conn, Throwable cause) {
      if (cause instanceof SSLHandshakeException) {
         this.notifyAboutHandshakeCompletionIfNeeded(conn, (SSLHandshakeException)cause);
      }

      this.pipeline().fireExceptionCaught(cause);
   }

   private boolean runTasksDirectly() {
      return this.sslTaskExecutor == null || this.sslTaskExecutor == ImmediateExecutor.INSTANCE || this.sslTaskExecutor == ImmediateEventExecutor.INSTANCE;
   }

   private void runAllTaskSend(QuicheQuicConnection conn, Runnable task) {
      this.sslTaskExecutor.execute(this.decorateTaskSend(conn, task));
   }

   private void runAll(QuicheQuicConnection conn, Runnable task) {
      do {
         task.run();
      } while ((task = conn.sslTask()) != null);
   }

   private Runnable decorateTaskSend(QuicheQuicConnection conn, Runnable task) {
      return () -> {
         try {
            this.runAll(conn, task);
         } finally {
            this.eventLoop().execute(() -> {
               if (this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
                  this.forceFlushParent();
               }

               this.freeIfClosed();
            });
         }
      };
   }

   private QuicheQuicChannel.SendResult connectionSendSegments(QuicheQuicConnection conn, SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator) {
      if (conn.isClosed()) {
         return QuicheQuicChannel.SendResult.NONE;
      } else {
         List<ByteBuf> bufferList = new ArrayList<>(segmentedDatagramPacketAllocator.maxNumSegments());
         long connAddr = conn.address();
         int maxDatagramSize = Quiche.quiche_conn_max_send_udp_payload_size(connAddr);
         QuicheQuicChannel.SendResult sendResult = QuicheQuicChannel.SendResult.NONE;
         boolean close = false;

         while (true) {
            int len = calculateSendBufferLength(connAddr, maxDatagramSize);
            ByteBuf out = this.alloc().directBuffer(len);
            ByteBuffer sendInfo = conn.nextSendInfo();
            InetSocketAddress sendToAddress = this.remote;
            int writerIndex = out.writerIndex();
            int written = Quiche.quiche_conn_send(connAddr, Quiche.writerMemoryAddress(out), out.writableBytes(), Quiche.memoryAddressWithPosition(sendInfo));
            if (written == 0) {
               out.release();
            } else {
               boolean done;
               if (written < 0) {
                  done = true;
                  if (written != Quiche.QUICHE_ERR_DONE) {
                     close = Quiche.shouldClose(written);
                     Exception e = Quiche.convertToException(written);
                     if (!this.tryFailConnectPromise(e)) {
                        this.fireExceptionEvents(conn, e);
                     }
                  }
               } else {
                  done = false;
               }

               int size = bufferList.size();
               if (done) {
                  out.release();
                  switch (size) {
                     case 0:
                        break;
                     case 1:
                        this.parent().write(new DatagramPacket(bufferList.get(0), sendToAddress));
                        sendResult = QuicheQuicChannel.SendResult.SOME;
                        break;
                     default:
                        int segmentSize = segmentSize(bufferList);
                        ByteBuf compositeBuffer = Unpooled.wrappedBuffer(bufferList.toArray(new ByteBuf[0]));
                        this.parent().write(segmentedDatagramPacketAllocator.newPacket(compositeBuffer, segmentSize, sendToAddress));
                        sendResult = QuicheQuicChannel.SendResult.SOME;
                  }

                  bufferList.clear();
                  if (close) {
                     sendResult = QuicheQuicChannel.SendResult.CLOSE;
                  }

                  return sendResult;
               }

               out.writerIndex(writerIndex + written);
               int segmentSize = -1;
               if (conn.isSendInfoChanged()) {
                  this.remote = QuicheSendInfo.getToAddress(sendInfo);
                  this.local = QuicheSendInfo.getFromAddress(sendInfo);
                  if (size > 0) {
                     segmentSize = segmentSize(bufferList);
                  }
               } else if (size > 0) {
                  int lastReadable = segmentSize(bufferList);
                  if (lastReadable != out.readableBytes() || size == segmentedDatagramPacketAllocator.maxNumSegments()) {
                     segmentSize = lastReadable;
                  }
               }

               if (segmentSize != -1) {
                  boolean stop;
                  if (size == 1) {
                     stop = this.writePacket(new DatagramPacket(bufferList.get(0), sendToAddress), maxDatagramSize, len);
                  } else {
                     ByteBuf compositeBuffer = Unpooled.wrappedBuffer(bufferList.toArray(new ByteBuf[0]));
                     stop = this.writePacket(segmentedDatagramPacketAllocator.newPacket(compositeBuffer, segmentSize, sendToAddress), maxDatagramSize, len);
                  }

                  bufferList.clear();
                  sendResult = QuicheQuicChannel.SendResult.SOME;
                  if (stop) {
                     if (out.isReadable()) {
                        this.parent().write(new DatagramPacket(out, sendToAddress));
                     } else {
                        out.release();
                     }

                     if (close) {
                        sendResult = QuicheQuicChannel.SendResult.CLOSE;
                     }

                     return sendResult;
                  }
               }

               out.touch(bufferList);
               bufferList.add(out);
            }
         }
      }
   }

   private static int segmentSize(List<ByteBuf> bufferList) {
      assert !bufferList.isEmpty();

      int size = bufferList.size();
      return bufferList.get(size - 1).readableBytes();
   }

   private QuicheQuicChannel.SendResult connectionSendSimple(QuicheQuicConnection conn) {
      if (conn.isClosed()) {
         return QuicheQuicChannel.SendResult.NONE;
      } else {
         long connAddr = conn.address();
         QuicheQuicChannel.SendResult sendResult = QuicheQuicChannel.SendResult.NONE;
         boolean close = false;
         int maxDatagramSize = Quiche.quiche_conn_max_send_udp_payload_size(connAddr);

         while (true) {
            ByteBuffer sendInfo = conn.nextSendInfo();
            int len = calculateSendBufferLength(connAddr, maxDatagramSize);
            ByteBuf out = this.alloc().directBuffer(len);
            int writerIndex = out.writerIndex();
            int written = Quiche.quiche_conn_send(connAddr, Quiche.writerMemoryAddress(out), out.writableBytes(), Quiche.memoryAddressWithPosition(sendInfo));
            if (written != 0) {
               if (written < 0) {
                  out.release();
                  if (written != Quiche.QUICHE_ERR_DONE) {
                     close = Quiche.shouldClose(written);
                     Exception e = Quiche.convertToException(written);
                     if (!this.tryFailConnectPromise(e)) {
                        this.fireExceptionEvents(conn, e);
                     }
                  }
                  break;
               }

               if (conn.isSendInfoChanged()) {
                  this.remote = QuicheSendInfo.getToAddress(sendInfo);
                  this.local = QuicheSendInfo.getFromAddress(sendInfo);
               }

               out.writerIndex(writerIndex + written);
               boolean stop = this.writePacket(new DatagramPacket(out, this.remote), maxDatagramSize, len);
               sendResult = QuicheQuicChannel.SendResult.SOME;
               if (stop) {
                  break;
               }
            } else {
               out.release();
            }
         }

         if (close) {
            sendResult = QuicheQuicChannel.SendResult.CLOSE;
         }

         return sendResult;
      }
   }

   private boolean writePacket(DatagramPacket packet, int maxDatagramSize, int len) {
      ChannelFuture future = this.parent().write(packet);
      if (isSendWindowUsed(maxDatagramSize, len)) {
         future.addListener(this.continueSendingListener);
         return true;
      } else {
         return false;
      }
   }

   private static boolean isSendWindowUsed(int maxDatagramSize, int len) {
      return len < maxDatagramSize;
   }

   private static int calculateSendBufferLength(long connAddr, int maxDatagramSize) {
      int len = Math.min(maxDatagramSize, Quiche.quiche_conn_send_quantum(connAddr));
      return len <= 0 ? 8 : len;
   }

   private QuicheQuicChannel.SendResult connectionSend(final QuicheQuicConnection conn) {
      if (conn.isFreed()) {
         return QuicheQuicChannel.SendResult.NONE;
      } else if ((this.reantranceGuard & 4) != 0) {
         this.notifyEarlyDataReadyIfNeeded(conn);
         return QuicheQuicChannel.SendResult.NONE;
      } else {
         this.reantranceGuard |= 4;

         QuicheQuicChannel.SendResult var5;
         try {
            SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator = this.config.getSegmentedDatagramPacketAllocator();
            QuicheQuicChannel.SendResult sendResult;
            if (segmentedDatagramPacketAllocator.maxNumSegments() > 0) {
               sendResult = this.connectionSendSegments(conn, segmentedDatagramPacketAllocator);
            } else {
               sendResult = this.connectionSendSimple(conn);
            }

            Runnable task = conn.sslTask();
            if (task == null) {
               this.notifyEarlyDataReadyIfNeeded(conn);
            } else if (!this.runTasksDirectly()) {
               this.runAllTaskSend(conn, task);
            } else {
               do {
                  task.run();
                  this.notifyEarlyDataReadyIfNeeded(conn);
               } while ((task = conn.sslTask()) != null);

               this.eventLoop().execute(new Runnable() {
                  @Override
                  public void run() {
                     if (QuicheQuicChannel.this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
                        QuicheQuicChannel.this.forceFlushParent();
                     }

                     QuicheQuicChannel.this.freeIfClosed();
                  }
               });
            }

            this.timeoutHandler.scheduleTimeout();
            var5 = sendResult;
         } finally {
            this.reantranceGuard &= -5;
         }

         return var5;
      }
   }

   void finishConnect() {
      assert !this.server;

      assert this.connection != null;

      if (this.connectionSend(this.connection) != QuicheQuicChannel.SendResult.NONE) {
         this.flushParent();
      }
   }

   private void notifyEarlyDataReadyIfNeeded(QuicheQuicConnection conn) {
      if (!this.server && !this.earlyDataReadyNotified && !conn.isFreed() && Quiche.quiche_conn_is_in_early_data(conn.address())) {
         this.earlyDataReadyNotified = true;
         this.pipeline().fireUserEventTriggered(SslEarlyDataReadyEvent.INSTANCE);
      }
   }

   @Override
   public Future<QuicConnectionStats> collectStats(Promise<QuicConnectionStats> promise) {
      if (this.eventLoop().inEventLoop()) {
         this.collectStats0(promise);
      } else {
         this.eventLoop().execute(() -> this.collectStats0(promise));
      }

      return promise;
   }

   private void collectStats0(Promise<QuicConnectionStats> promise) {
      QuicheQuicConnection conn = this.connection;
      if (conn.isFreed()) {
         promise.setSuccess(this.statsAtClose);
      } else {
         this.collectStats0(this.connection, promise);
      }
   }

   @Nullable
   private QuicConnectionStats collectStats0(QuicheQuicConnection connection, Promise<QuicConnectionStats> promise) {
      long[] stats = Quiche.quiche_conn_stats(connection.address());
      if (stats == null) {
         promise.setFailure(new IllegalStateException("native quiche_conn_stats(...) failed"));
         return null;
      } else {
         QuicheQuicConnectionStats connStats = new QuicheQuicConnectionStats(stats);
         promise.setSuccess(connStats);
         return connStats;
      }
   }

   @Override
   public Future<QuicConnectionPathStats> collectPathStats(int pathIdx, Promise<QuicConnectionPathStats> promise) {
      if (this.eventLoop().inEventLoop()) {
         this.collectPathStats0(pathIdx, promise);
      } else {
         this.eventLoop().execute(() -> this.collectPathStats0(pathIdx, promise));
      }

      return promise;
   }

   private void collectPathStats0(int pathIdx, Promise<QuicConnectionPathStats> promise) {
      QuicheQuicConnection conn = this.connection;
      if (conn.isFreed()) {
         promise.setFailure(new IllegalStateException("Connection is closed"));
      } else {
         Object[] stats = Quiche.quiche_conn_path_stats(this.connection.address(), pathIdx);
         if (stats == null) {
            promise.setFailure(new IllegalStateException("native quiche_conn_path_stats(...) failed"));
         } else {
            promise.setSuccess(new QuicheQuicConnectionPathStats(stats));
         }
      }
   }

   @Override
   public QuicTransportParameters peerTransportParameters() {
      return this.connection.peerParameters();
   }

   private static enum ChannelState {
      OPEN,
      ACTIVE,
      CLOSED;
   }

   private static final class CloseData implements ChannelFutureListener {
      final boolean applicationClose;
      final int err;
      final ByteBuf reason;

      CloseData(boolean applicationClose, int err, ByteBuf reason) {
         this.applicationClose = applicationClose;
         this.err = err;
         this.reason = reason;
      }

      public void operationComplete(ChannelFuture future) {
         this.reason.release();
      }
   }

   private final class QuicChannelUnsafe extends AbstractChannel.AbstractUnsafe {
      private QuicChannelUnsafe() {
      }

      void connectStream(QuicStreamType type, @Nullable ChannelHandler handler, Promise<QuicStreamChannel> promise) {
         if (promise.setUncancellable()) {
            long streamId = QuicheQuicChannel.this.idGenerator.nextStreamId(type == QuicStreamType.BIDIRECTIONAL);

            try {
               int res = QuicheQuicChannel.this.streamSend0(QuicheQuicChannel.this.connection, streamId, Unpooled.EMPTY_BUFFER, false);
               if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
                  throw Quiche.convertToException(res);
               }
            } catch (Exception var7) {
               promise.setFailure(var7);
               return;
            }

            if (type == QuicStreamType.UNIDIRECTIONAL) {
               QuicheQuicChannel.UNI_STREAMS_LEFT_UPDATER.decrementAndGet(QuicheQuicChannel.this);
            } else {
               QuicheQuicChannel.BIDI_STREAMS_LEFT_UPDATER.decrementAndGet(QuicheQuicChannel.this);
            }

            QuicheQuicStreamChannel streamChannel = this.addNewStreamChannel(streamId);
            if (handler != null) {
               streamChannel.pipeline().addLast(handler);
            }

            QuicheQuicChannel.this.eventLoop().register(streamChannel).addListener(f -> {
               if (f.isSuccess()) {
                  promise.setSuccess(streamChannel);
               } else {
                  promise.setFailure(f.cause());
                  QuicheQuicChannel.this.streams.remove(streamId);
               }
            });
         }
      }

      @Override
      public void connect(SocketAddress remote, SocketAddress local, ChannelPromise channelPromise) {
         assert QuicheQuicChannel.this.eventLoop().inEventLoop();

         if (channelPromise.setUncancellable()) {
            if (QuicheQuicChannel.this.server) {
               channelPromise.setFailure(new UnsupportedOperationException());
            } else if (QuicheQuicChannel.this.connectPromise != null) {
               channelPromise.setFailure(new ConnectionPendingException());
            } else if (remote instanceof QuicConnectionAddress) {
               if (!QuicheQuicChannel.this.sourceConnectionIds.isEmpty()) {
                  channelPromise.setFailure(new AlreadyConnectedException());
               } else {
                  QuicheQuicChannel.this.connectAddress = (QuicConnectionAddress)remote;
                  QuicheQuicChannel.this.connectPromise = channelPromise;
                  int connectTimeoutMillis = QuicheQuicChannel.this.config().getConnectTimeoutMillis();
                  if (connectTimeoutMillis > 0) {
                     QuicheQuicChannel.this.connectTimeoutFuture = QuicheQuicChannel.this.eventLoop()
                        .schedule(
                           () -> {
                              ChannelPromise connectPromise = QuicheQuicChannel.this.connectPromise;
                              if (connectPromise != null
                                 && !connectPromise.isDone()
                                 && connectPromise.tryFailure(new ConnectTimeoutException("connection timed out: " + remote))) {
                                 this.close(this.voidPromise());
                              }
                           },
                           connectTimeoutMillis,
                           TimeUnit.MILLISECONDS
                        );
                  }

                  QuicheQuicChannel.this.connectPromise.addListener(future -> {
                     if (future.isCancelled()) {
                        if (QuicheQuicChannel.this.connectTimeoutFuture != null) {
                           QuicheQuicChannel.this.connectTimeoutFuture.cancel(false);
                        }

                        QuicheQuicChannel.this.connectPromise = null;
                        this.close(this.voidPromise());
                     }
                  });
                  QuicheQuicChannel.this.parent().connect(new QuicheQuicChannelAddress(QuicheQuicChannel.this)).addListener(f -> {
                     ChannelPromise connectPromise = QuicheQuicChannel.this.connectPromise;
                     if (connectPromise != null && !f.isSuccess()) {
                        connectPromise.tryFailure(f.cause());
                        QuicheQuicChannel.this.unsafe().closeForcibly();
                     }
                  });
               }
            } else {
               channelPromise.setFailure(new UnsupportedOperationException());
            }
         }
      }

      private void fireConnectCloseEventIfNeeded(QuicheQuicConnection conn) {
         if (QuicheQuicChannel.this.connectionCloseEvent == null && !conn.isFreed()) {
            QuicheQuicChannel.this.connectionCloseEvent = Quiche.quiche_conn_peer_error(conn.address());
            if (QuicheQuicChannel.this.connectionCloseEvent != null) {
               QuicheQuicChannel.this.pipeline().fireUserEventTriggered(QuicheQuicChannel.this.connectionCloseEvent);
            }
         }
      }

      void connectionRecv(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer) {
         QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
         if (!conn.isFreed()) {
            int bufferReadable = buffer.readableBytes();
            if (bufferReadable != 0) {
               QuicheQuicChannel.this.reantranceGuard |= 2;
               boolean close = false;

               try {
                  ByteBuf tmpBuffer = null;
                  if (buffer.isReadOnly()) {
                     tmpBuffer = QuicheQuicChannel.this.alloc().directBuffer(buffer.readableBytes());
                     tmpBuffer.writeBytes(buffer);
                     buffer = tmpBuffer;
                  }

                  long memoryAddress = Quiche.readerMemoryAddress(buffer);
                  ByteBuffer recvInfo = conn.nextRecvInfo();
                  QuicheRecvInfo.setRecvInfo(recvInfo, sender, recipient);
                  QuicheQuicChannel.this.remote = sender;
                  QuicheQuicChannel.this.local = recipient;

                  try {
                     do {
                        int res = Quiche.quiche_conn_recv(conn.address(), memoryAddress, bufferReadable, Quiche.memoryAddressWithPosition(recvInfo));
                        boolean done;
                        if (res < 0) {
                           done = true;
                           if (res != Quiche.QUICHE_ERR_DONE) {
                              close = Quiche.shouldClose(res);
                              Exception e = Quiche.convertToException(res);
                              if (QuicheQuicChannel.this.tryFailConnectPromise(e)) {
                                 break;
                              }

                              QuicheQuicChannel.this.fireExceptionEvents(conn, e);
                           }
                        } else {
                           done = false;
                        }

                        Runnable task = conn.sslTask();
                        if (task != null) {
                           if (QuicheQuicChannel.this.runTasksDirectly()) {
                              while (true) {
                                 task.run();
                                 if ((task = conn.sslTask()) == null) {
                                    this.processReceived(conn);
                                    break;
                                 }
                              }
                           } else {
                              this.runAllTaskRecv(conn, task);
                           }
                        } else {
                           this.processReceived(conn);
                        }

                        if (done) {
                           break;
                        }

                        memoryAddress += res;
                        bufferReadable -= res;
                     } while (bufferReadable > 0 && !conn.isFreed());
                  } finally {
                     buffer.skipBytes((int)(memoryAddress - Quiche.readerMemoryAddress(buffer)));
                     if (tmpBuffer != null) {
                        tmpBuffer.release();
                     }
                  }

                  if (close) {
                     QuicheQuicChannel.this.unsafe().close(QuicheQuicChannel.this.newPromise());
                  }
               } finally {
                  QuicheQuicChannel.this.reantranceGuard &= -3;
               }
            }
         }
      }

      private void processReceived(QuicheQuicConnection conn) {
         if (!this.handlePendingChannelActive(conn)) {
            QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
            this.fireConnectCloseEventIfNeeded(conn);
            if (!conn.isFreed()) {
               long connAddr = conn.address();
               if (Quiche.quiche_conn_is_established(connAddr) || Quiche.quiche_conn_is_in_early_data(connAddr)) {
                  long uniLeftOld = QuicheQuicChannel.this.uniStreamsLeft;
                  long bidiLeftOld = QuicheQuicChannel.this.bidiStreamsLeft;
                  if (uniLeftOld == 0L || bidiLeftOld == 0L) {
                     long uniLeft = Quiche.quiche_conn_peer_streams_left_uni(connAddr);
                     long bidiLeft = Quiche.quiche_conn_peer_streams_left_bidi(connAddr);
                     QuicheQuicChannel.this.uniStreamsLeft = uniLeft;
                     QuicheQuicChannel.this.bidiStreamsLeft = bidiLeft;
                     if (uniLeftOld != uniLeft || bidiLeftOld != bidiLeft) {
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(QuicStreamLimitChangedEvent.INSTANCE);
                     }
                  }

                  this.handlePathEvents(conn);
                  if (QuicheQuicChannel.this.handleWritableStreams(conn)) {
                     QuicheQuicChannel.this.flushParent();
                  }

                  QuicheQuicChannel.this.datagramReadable = true;
                  QuicheQuicChannel.this.streamReadable = true;
                  this.recvDatagram(conn);
                  this.recvStream(conn);
               }
            }
         }
      }

      private void handlePathEvents(QuicheQuicConnection conn) {
         long event;
         while (!conn.isFreed() && (event = Quiche.quiche_conn_path_event_next(conn.address())) > 0L) {
            try {
               int type = Quiche.quiche_path_event_type(event);
               if (type == Quiche.QUICHE_PATH_EVENT_NEW) {
                  Object[] ret = Quiche.quiche_path_event_new(event);
                  InetSocketAddress local = (InetSocketAddress)ret[0];
                  InetSocketAddress peer = (InetSocketAddress)ret[1];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.New(local, peer));
               } else if (type == Quiche.QUICHE_PATH_EVENT_VALIDATED) {
                  Object[] ret = Quiche.quiche_path_event_validated(event);
                  InetSocketAddress local = (InetSocketAddress)ret[0];
                  InetSocketAddress peer = (InetSocketAddress)ret[1];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.Validated(local, peer));
               } else if (type == Quiche.QUICHE_PATH_EVENT_FAILED_VALIDATION) {
                  Object[] ret = Quiche.quiche_path_event_failed_validation(event);
                  InetSocketAddress local = (InetSocketAddress)ret[0];
                  InetSocketAddress peer = (InetSocketAddress)ret[1];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.FailedValidation(local, peer));
               } else if (type == Quiche.QUICHE_PATH_EVENT_CLOSED) {
                  Object[] ret = Quiche.quiche_path_event_closed(event);
                  InetSocketAddress local = (InetSocketAddress)ret[0];
                  InetSocketAddress peer = (InetSocketAddress)ret[1];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.Closed(local, peer));
               } else if (type == Quiche.QUICHE_PATH_EVENT_REUSED_SOURCE_CONNECTION_ID) {
                  Object[] ret = Quiche.quiche_path_event_reused_source_connection_id(event);
                  Long seq = (Long)ret[0];
                  InetSocketAddress localOld = (InetSocketAddress)ret[1];
                  InetSocketAddress peerOld = (InetSocketAddress)ret[2];
                  InetSocketAddress local = (InetSocketAddress)ret[3];
                  InetSocketAddress peer = (InetSocketAddress)ret[4];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.ReusedSourceConnectionId(seq, localOld, peerOld, local, peer));
               } else if (type == Quiche.QUICHE_PATH_EVENT_PEER_MIGRATED) {
                  Object[] ret = Quiche.quiche_path_event_peer_migrated(event);
                  InetSocketAddress local = (InetSocketAddress)ret[0];
                  InetSocketAddress peer = (InetSocketAddress)ret[1];
                  QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.PeerMigrated(local, peer));
               }
            } finally {
               Quiche.quiche_path_event_free(event);
            }
         }
      }

      private void runAllTaskRecv(QuicheQuicConnection conn, Runnable task) {
         QuicheQuicChannel.this.sslTaskExecutor.execute(this.decorateTaskRecv(conn, task));
      }

      private Runnable decorateTaskRecv(QuicheQuicConnection conn, Runnable task) {
         return () -> {
            try {
               QuicheQuicChannel.this.runAll(conn, task);
            } finally {
               QuicheQuicChannel.this.eventLoop().execute(() -> {
                  if (!conn.isFreed()) {
                     this.processReceived(conn);
                     if (QuicheQuicChannel.this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
                        QuicheQuicChannel.this.forceFlushParent();
                     }

                     QuicheQuicChannel.this.freeIfClosed();
                  }
               });
            }
         };
      }

      void recv() {
         QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
         if ((QuicheQuicChannel.this.reantranceGuard & 2) == 0 && !conn.isFreed()) {
            long connAddr = conn.address();
            if (Quiche.quiche_conn_is_established(connAddr) || Quiche.quiche_conn_is_in_early_data(connAddr)) {
               QuicheQuicChannel.this.reantranceGuard |= 2;

               try {
                  this.recvDatagram(conn);
                  this.recvStream(conn);
               } finally {
                  QuicheQuicChannel.this.fireChannelReadCompleteIfNeeded();
                  QuicheQuicChannel.this.reantranceGuard &= -3;
               }
            }
         }
      }

      private void recvStream(QuicheQuicConnection conn) {
         if (!conn.isFreed()) {
            long connAddr = conn.address();
            long readableIterator = Quiche.quiche_conn_readable(connAddr);
            int totalReadable = 0;
            if (readableIterator != -1L) {
               try {
                  if (QuicheQuicChannel.this.recvStreamPending && QuicheQuicChannel.this.streamReadable) {
                     while (true) {
                        int readable = Quiche.quiche_stream_iter_next(readableIterator, QuicheQuicChannel.this.readableStreams);

                        for (int i = 0; i < readable; i++) {
                           long streamId = QuicheQuicChannel.this.readableStreams[i];
                           QuicheQuicStreamChannel streamChannel = QuicheQuicChannel.this.streams.get(streamId);
                           if (streamChannel == null) {
                              QuicheQuicChannel.this.recvStreamPending = false;
                              QuicheQuicChannel.this.fireChannelReadCompletePending = true;
                              streamChannel = this.addNewStreamChannel(streamId);
                              streamChannel.readable();
                              QuicheQuicChannel.this.pipeline().fireChannelRead(streamChannel);
                           } else {
                              streamChannel.readable();
                           }
                        }

                        if (readable < QuicheQuicChannel.this.readableStreams.length) {
                           QuicheQuicChannel.this.streamReadable = false;
                           break;
                        }

                        if (readable > 0) {
                           totalReadable += readable;
                        }
                     }
                  }
               } finally {
                  Quiche.quiche_stream_iter_free(readableIterator);
               }

               QuicheQuicChannel.this.readableStreams = QuicheQuicChannel.growIfNeeded(QuicheQuicChannel.this.readableStreams, totalReadable);
            }
         }
      }

      private void recvDatagram(QuicheQuicConnection conn) {
         if (QuicheQuicChannel.this.supportsDatagram) {
            while (QuicheQuicChannel.this.recvDatagramPending && QuicheQuicChannel.this.datagramReadable && !conn.isFreed()) {
               RecvByteBufAllocator.Handle recvHandle = this.recvBufAllocHandle();
               recvHandle.reset(QuicheQuicChannel.this.config());
               int numMessagesRead = 0;

               while (true) {
                  long connAddr = conn.address();
                  int len = Quiche.quiche_conn_dgram_recv_front_len(connAddr);
                  if (len == Quiche.QUICHE_ERR_DONE) {
                     QuicheQuicChannel.this.datagramReadable = false;
                     return;
                  }

                  label39: {
                     ByteBuf datagramBuffer = QuicheQuicChannel.this.alloc().directBuffer(len);
                     recvHandle.attemptedBytesRead(datagramBuffer.writableBytes());
                     int writerIndex = datagramBuffer.writerIndex();
                     long memoryAddress = Quiche.writerMemoryAddress(datagramBuffer);
                     int written = Quiche.quiche_conn_dgram_recv(connAddr, memoryAddress, datagramBuffer.writableBytes());
                     if (written < 0) {
                        datagramBuffer.release();
                        if (written == Quiche.QUICHE_ERR_DONE) {
                           QuicheQuicChannel.this.datagramReadable = false;
                           break label39;
                        }

                        QuicheQuicChannel.this.pipeline().fireExceptionCaught(Quiche.convertToException(written));
                     }

                     recvHandle.lastBytesRead(written);
                     recvHandle.incMessagesRead(1);
                     numMessagesRead++;
                     datagramBuffer.writerIndex(writerIndex + written);
                     QuicheQuicChannel.this.recvDatagramPending = false;
                     QuicheQuicChannel.this.fireChannelReadCompletePending = true;
                     QuicheQuicChannel.this.pipeline().fireChannelRead(datagramBuffer);
                     if (recvHandle.continueReading() && !conn.isFreed()) {
                        continue;
                     }
                  }

                  recvHandle.readComplete();
                  if (numMessagesRead > 0) {
                     QuicheQuicChannel.this.fireChannelReadCompleteIfNeeded();
                  }
                  break;
               }
            }
         }
      }

      private boolean handlePendingChannelActive(QuicheQuicConnection conn) {
         if (!conn.isFreed() && QuicheQuicChannel.this.state != QuicheQuicChannel.ChannelState.CLOSED) {
            if (QuicheQuicChannel.this.server) {
               if (QuicheQuicChannel.this.state == QuicheQuicChannel.ChannelState.OPEN && Quiche.quiche_conn_is_established(conn.address())) {
                  QuicheQuicChannel.this.state = QuicheQuicChannel.ChannelState.ACTIVE;
                  QuicheQuicChannel.this.pipeline().fireChannelActive();
                  QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
                  this.fireDatagramExtensionEvent(conn);
               }
            } else if (QuicheQuicChannel.this.connectPromise != null && Quiche.quiche_conn_is_established(conn.address())) {
               ChannelPromise promise = QuicheQuicChannel.this.connectPromise;
               QuicheQuicChannel.this.connectPromise = null;
               QuicheQuicChannel.this.state = QuicheQuicChannel.ChannelState.ACTIVE;
               boolean promiseSet = promise.trySuccess();
               QuicheQuicChannel.this.pipeline().fireChannelActive();
               QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
               this.fireDatagramExtensionEvent(conn);
               if (!promiseSet) {
                  this.fireConnectCloseEventIfNeeded(conn);
                  this.close(this.voidPromise());
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }

      private void fireDatagramExtensionEvent(QuicheQuicConnection conn) {
         if (!conn.isClosed()) {
            long connAddr = conn.address();
            int len = Quiche.quiche_conn_dgram_max_writable_len(connAddr);
            if (len != Quiche.QUICHE_ERR_DONE) {
               QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicDatagramExtensionEvent(len));
            }
         }
      }

      private QuicheQuicStreamChannel addNewStreamChannel(long streamId) {
         QuicheQuicStreamChannel streamChannel = new QuicheQuicStreamChannel(QuicheQuicChannel.this, streamId);
         QuicheQuicStreamChannel old = QuicheQuicChannel.this.streams.put(streamId, streamChannel);

         assert old == null;

         streamChannel.writable(QuicheQuicChannel.this.streamCapacity(streamId));
         return streamChannel;
      }
   }

   private static enum SendResult {
      SOME,
      NONE,
      CLOSE;
   }

   static enum StreamRecvResult {
      DONE,
      FIN,
      OK;
   }

   private final class TimeoutHandler implements Runnable {
      private ScheduledFuture<?> timeoutFuture;

      private TimeoutHandler() {
      }

      @Override
      public void run() {
         QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
         if (!conn.isFreed()) {
            if (!QuicheQuicChannel.this.freeIfClosed()) {
               long connAddr = conn.address();
               this.timeoutFuture = null;
               Quiche.quiche_conn_on_timeout(connAddr);
               if (!QuicheQuicChannel.this.freeIfClosed()) {
                  if (QuicheQuicChannel.this.connectionSend(conn) != QuicheQuicChannel.SendResult.NONE) {
                     QuicheQuicChannel.this.flushParent();
                  }

                  boolean closed = QuicheQuicChannel.this.freeIfClosed();
                  if (!closed) {
                     this.scheduleTimeout();
                  }
               }
            }
         }
      }

      void scheduleTimeout() {
         QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
         if (conn.isFreed()) {
            this.cancel();
         } else if (conn.isClosed()) {
            this.cancel();
            QuicheQuicChannel.this.unsafe().close(QuicheQuicChannel.this.newPromise());
         } else {
            long nanos = Quiche.quiche_conn_timeout_as_nanos(conn.address());
            if (nanos >= 0L && nanos != Long.MAX_VALUE) {
               if (this.timeoutFuture == null) {
                  this.timeoutFuture = QuicheQuicChannel.this.eventLoop().schedule(this, nanos, TimeUnit.NANOSECONDS);
               } else {
                  long remaining = this.timeoutFuture.getDelay(TimeUnit.NANOSECONDS);
                  if (remaining <= 0L) {
                     this.cancel();
                     this.run();
                  } else if (remaining > nanos) {
                     this.cancel();
                     this.timeoutFuture = QuicheQuicChannel.this.eventLoop().schedule(this, nanos, TimeUnit.NANOSECONDS);
                  }
               }
            } else {
               this.cancel();
            }
         }
      }

      void cancel() {
         if (this.timeoutFuture != null) {
            this.timeoutFuture.cancel(false);
            this.timeoutFuture = null;
         }
      }
   }
}
