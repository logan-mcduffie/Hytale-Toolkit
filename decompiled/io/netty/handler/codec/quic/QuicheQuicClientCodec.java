package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicClientCodec extends QuicheQuicCodec {
   private final Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider;
   private final Executor sslTaskExecutor;

   QuicheQuicClientCodec(
      QuicheConfig config,
      Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider,
      Executor sslTaskExecutor,
      int localConnIdLength,
      FlushStrategy flushStrategy
   ) {
      super(config, localConnIdLength, flushStrategy);
      this.sslEngineProvider = sslEngineProvider;
      this.sslTaskExecutor = sslTaskExecutor;
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
   ) {
      ByteBuffer key = dcid.internalNioBuffer(dcid.readerIndex(), dcid.readableBytes());
      return this.getChannel(key);
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
      try {
         channel.connectNow(
            this.sslEngineProvider,
            this.sslTaskExecutor,
            freeTask,
            config.nativeAddress(),
            localConnIdLength,
            config.isDatagramSupported(),
            senderSockaddrMemory.internalNioBuffer(0, senderSockaddrMemory.capacity()),
            recipientSockaddrMemory.internalNioBuffer(0, recipientSockaddrMemory.capacity())
         );
      } catch (Throwable var11) {
         promise.setFailure(var11);
         return;
      }

      this.addChannel(channel);
      channel.finishConnect();
      promise.setSuccess();
   }
}
