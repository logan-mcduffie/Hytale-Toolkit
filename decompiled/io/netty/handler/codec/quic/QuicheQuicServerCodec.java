package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicServerCodec extends QuicheQuicCodec {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(QuicheQuicServerCodec.class);
   private final Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider;
   private final Executor sslTaskExecutor;
   private final QuicConnectionIdGenerator connectionIdAddressGenerator;
   private final QuicResetTokenGenerator resetTokenGenerator;
   private final QuicTokenHandler tokenHandler;
   private final ChannelHandler handler;
   private final Entry<ChannelOption<?>, Object>[] optionsArray;
   private final Entry<AttributeKey<?>, Object>[] attrsArray;
   private final ChannelHandler streamHandler;
   private final Entry<ChannelOption<?>, Object>[] streamOptionsArray;
   private final Entry<AttributeKey<?>, Object>[] streamAttrsArray;
   private ByteBuf mintTokenBuffer;
   private ByteBuf connIdBuffer;

   QuicheQuicServerCodec(
      QuicheConfig config,
      int localConnIdLength,
      QuicTokenHandler tokenHandler,
      QuicConnectionIdGenerator connectionIdAddressGenerator,
      QuicResetTokenGenerator resetTokenGenerator,
      FlushStrategy flushStrategy,
      Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider,
      Executor sslTaskExecutor,
      ChannelHandler handler,
      Entry<ChannelOption<?>, Object>[] optionsArray,
      Entry<AttributeKey<?>, Object>[] attrsArray,
      ChannelHandler streamHandler,
      Entry<ChannelOption<?>, Object>[] streamOptionsArray,
      Entry<AttributeKey<?>, Object>[] streamAttrsArray
   ) {
      super(config, localConnIdLength, flushStrategy);
      this.tokenHandler = tokenHandler;
      this.connectionIdAddressGenerator = connectionIdAddressGenerator;
      this.resetTokenGenerator = resetTokenGenerator;
      this.sslEngineProvider = sslEngineProvider;
      this.sslTaskExecutor = sslTaskExecutor;
      this.handler = handler;
      this.optionsArray = optionsArray;
      this.attrsArray = attrsArray;
      this.streamHandler = streamHandler;
      this.streamOptionsArray = streamOptionsArray;
      this.streamAttrsArray = streamAttrsArray;
   }

   @Override
   protected void handlerAdded(ChannelHandlerContext ctx, int localConnIdLength) {
      this.connIdBuffer = Quiche.allocateNativeOrder(localConnIdLength);
      this.mintTokenBuffer = Unpooled.directBuffer(this.tokenHandler.maxTokenLength());
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) {
      super.handlerRemoved(ctx);
      if (this.connIdBuffer != null) {
         this.connIdBuffer.release();
      }

      if (this.mintTokenBuffer != null) {
         this.mintTokenBuffer.release();
      }
   }

   @Nullable
   @Override
   protected QuicheQuicChannel quicPacketRead(
      ChannelHandlerContext ctx,
      InetSocketAddress sender,
      InetSocketAddress recipient,
      QuicPacketType type,
      long version,
      ByteBuf scid,
      ByteBuf dcid,
      ByteBuf token,
      ByteBuf senderSockaddrMemory,
      ByteBuf recipientSockaddrMemory,
      Consumer<QuicheQuicChannel> freeTask,
      int localConnIdLength,
      QuicheConfig config
   ) throws Exception {
      ByteBuffer dcidByteBuffer = dcid.internalNioBuffer(dcid.readerIndex(), dcid.readableBytes());
      QuicheQuicChannel channel = this.getChannel(dcidByteBuffer);
      return channel == null && type == QuicPacketType.INITIAL
         ? this.handleServer(
            ctx, sender, recipient, type, version, scid, dcid, token, senderSockaddrMemory, recipientSockaddrMemory, freeTask, localConnIdLength, config
         )
         : channel;
   }

   private static void writePacket(ChannelHandlerContext ctx, int res, ByteBuf buffer, InetSocketAddress sender) throws Exception {
      if (res < 0) {
         buffer.release();
         if (res != Quiche.QUICHE_ERR_DONE) {
            throw Quiche.convertToException(res);
         }
      } else {
         ctx.writeAndFlush(new DatagramPacket(buffer.writerIndex(buffer.writerIndex() + res), sender));
      }
   }

   @Nullable
   private QuicheQuicChannel handleServer(
      ChannelHandlerContext ctx,
      InetSocketAddress sender,
      InetSocketAddress recipient,
      QuicPacketType type,
      long version,
      ByteBuf scid,
      ByteBuf dcid,
      ByteBuf token,
      ByteBuf senderSockaddrMemory,
      ByteBuf recipientSockaddrMemory,
      Consumer<QuicheQuicChannel> freeTask,
      int localConnIdLength,
      QuicheConfig config
   ) throws Exception {
      if (!Quiche.quiche_version_is_supported((int)version)) {
         ByteBuf out = ctx.alloc().directBuffer(1350);
         int res = Quiche.quiche_negotiate_version(
            Quiche.readerMemoryAddress(scid),
            scid.readableBytes(),
            Quiche.readerMemoryAddress(dcid),
            dcid.readableBytes(),
            Quiche.writerMemoryAddress(out),
            out.writableBytes()
         );
         writePacket(ctx, res, out, sender);
         return null;
      } else {
         boolean noToken = false;
         int offset;
         if (!token.isReadable()) {
            this.mintTokenBuffer.clear();
            this.connIdBuffer.clear();
            if (this.tokenHandler.writeToken(this.mintTokenBuffer, dcid, sender)) {
               ByteBuffer connId = this.connectionIdAddressGenerator
                  .newId(
                     scid.internalNioBuffer(scid.readerIndex(), scid.readableBytes()),
                     dcid.internalNioBuffer(dcid.readerIndex(), dcid.readableBytes()),
                     localConnIdLength
                  );
               this.connIdBuffer.writeBytes(connId);
               ByteBuf out = ctx.alloc().directBuffer(1350);
               int written = Quiche.quiche_retry(
                  Quiche.readerMemoryAddress(scid),
                  scid.readableBytes(),
                  Quiche.readerMemoryAddress(dcid),
                  dcid.readableBytes(),
                  Quiche.readerMemoryAddress(this.connIdBuffer),
                  this.connIdBuffer.readableBytes(),
                  Quiche.readerMemoryAddress(this.mintTokenBuffer),
                  this.mintTokenBuffer.readableBytes(),
                  (int)version,
                  Quiche.writerMemoryAddress(out),
                  out.writableBytes()
               );
               writePacket(ctx, written, out, sender);
               return null;
            }

            offset = 0;
            noToken = true;
         } else {
            offset = this.tokenHandler.validateToken(token.slice(), sender);
            if (offset == -1) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("invalid token: {}", token.toString(CharsetUtil.US_ASCII));
               }

               return null;
            }
         }

         int scidLen;
         long ocidAddr;
         int ocidLen;
         ByteBuffer key;
         long scidAddr;
         if (noToken) {
            this.connIdBuffer.clear();
            key = this.connectionIdAddressGenerator
               .newId(
                  scid.internalNioBuffer(scid.readerIndex(), scid.readableBytes()),
                  dcid.internalNioBuffer(dcid.readerIndex(), dcid.readableBytes()),
                  localConnIdLength
               );
            this.connIdBuffer.writeBytes(key.duplicate());
            scidAddr = Quiche.readerMemoryAddress(this.connIdBuffer);
            scidLen = localConnIdLength;
            ocidAddr = -1L;
            ocidLen = -1;
            QuicheQuicChannel existingChannel = this.getChannel(key);
            if (existingChannel != null) {
               return existingChannel;
            }
         } else {
            scidAddr = Quiche.readerMemoryAddress(dcid);
            scidLen = localConnIdLength;
            ocidLen = token.readableBytes() - offset;
            ocidAddr = Quiche.memoryAddress(token, offset, ocidLen);
            byte[] bytes = new byte[localConnIdLength];
            dcid.getBytes(dcid.readerIndex(), bytes);
            key = ByteBuffer.wrap(bytes);
         }

         QuicheQuicChannel channel = QuicheQuicChannel.forServer(
            ctx.channel(),
            key,
            recipient,
            sender,
            config.isDatagramSupported(),
            this.streamHandler,
            this.streamOptionsArray,
            this.streamAttrsArray,
            freeTask,
            this.sslTaskExecutor,
            this.connectionIdAddressGenerator,
            this.resetTokenGenerator
         );
         byte[] originalId = new byte[dcid.readableBytes()];
         dcid.getBytes(dcid.readerIndex(), originalId);
         channel.sourceConnectionIds().add(ByteBuffer.wrap(originalId));
         Quic.setupChannel(channel, this.optionsArray, this.attrsArray, this.handler, LOGGER);
         QuicSslEngine engine = this.sslEngineProvider.apply(channel);
         if (!(engine instanceof QuicheQuicSslEngine)) {
            channel.unsafe().closeForcibly();
            throw new IllegalArgumentException("QuicSslEngine is not of type " + QuicheQuicSslEngine.class.getSimpleName());
         } else if (engine.getUseClientMode()) {
            channel.unsafe().closeForcibly();
            throw new IllegalArgumentException("QuicSslEngine is not created in server mode");
         } else {
            QuicheQuicSslEngine quicSslEngine = (QuicheQuicSslEngine)engine;
            QuicheQuicConnection connection = quicSslEngine.createConnection(
               ssl -> {
                  ByteBuffer localAddrMemory = recipientSockaddrMemory.internalNioBuffer(0, recipientSockaddrMemory.capacity());
                  int localLen = SockaddrIn.setAddress(localAddrMemory, recipient);
                  ByteBuffer peerAddrMemory = senderSockaddrMemory.internalNioBuffer(0, senderSockaddrMemory.capacity());
                  int peerLen = SockaddrIn.setAddress(peerAddrMemory, sender);
                  return Quiche.quiche_conn_new_with_tls(
                     scidAddr,
                     scidLen,
                     ocidAddr,
                     ocidLen,
                     Quiche.memoryAddressWithPosition(localAddrMemory),
                     localLen,
                     Quiche.memoryAddressWithPosition(peerAddrMemory),
                     peerLen,
                     config.nativeAddress(),
                     ssl,
                     true
                  );
               }
            );
            if (connection == null) {
               channel.unsafe().closeForcibly();
               LOGGER.debug("quiche_accept failed");
               return null;
            } else {
               channel.attachQuicheConnection(connection);
               this.addChannel(channel);
               ctx.channel().eventLoop().register(channel);
               return channel;
            }
         }
      }
   }

   @Override
   protected void connectQuicChannel(
      QuicheQuicChannel channel,
      SocketAddress remoteAddress,
      SocketAddress localAddress,
      ByteBuf senderSockaddrMemory,
      ByteBuf recipientSockaddrMemory,
      Consumer<QuicheQuicChannel> freeTask,
      int localConnIdLength,
      QuicheConfig config,
      ChannelPromise promise
   ) {
      promise.setFailure(new UnsupportedOperationException());
   }
}
