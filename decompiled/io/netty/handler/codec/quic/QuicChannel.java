package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.net.SocketAddress;
import javax.net.ssl.SSLEngine;
import org.jetbrains.annotations.Nullable;

public interface QuicChannel extends Channel {
   @Override
   default ChannelFuture bind(SocketAddress localAddress) {
      return this.pipeline().bind(localAddress);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress) {
      return this.pipeline().connect(remoteAddress);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.pipeline().connect(remoteAddress, localAddress);
   }

   @Override
   default ChannelFuture disconnect() {
      return this.pipeline().disconnect();
   }

   @Override
   default ChannelFuture close() {
      return this.pipeline().close();
   }

   @Override
   default ChannelFuture deregister() {
      return this.pipeline().deregister();
   }

   @Override
   default ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
      return this.pipeline().bind(localAddress, promise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.pipeline().connect(remoteAddress, promise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      return this.pipeline().connect(remoteAddress, localAddress, promise);
   }

   @Override
   default ChannelFuture disconnect(ChannelPromise promise) {
      return this.pipeline().disconnect(promise);
   }

   @Override
   default ChannelFuture close(ChannelPromise promise) {
      return this.pipeline().close(promise);
   }

   @Override
   default ChannelFuture deregister(ChannelPromise promise) {
      return this.pipeline().deregister(promise);
   }

   @Override
   default ChannelFuture write(Object msg) {
      return this.pipeline().write(msg);
   }

   @Override
   default ChannelFuture write(Object msg, ChannelPromise promise) {
      return this.pipeline().write(msg, promise);
   }

   @Override
   default ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      return this.pipeline().writeAndFlush(msg, promise);
   }

   @Override
   default ChannelFuture writeAndFlush(Object msg) {
      return this.pipeline().writeAndFlush(msg);
   }

   @Override
   default ChannelPromise newPromise() {
      return this.pipeline().newPromise();
   }

   @Override
   default ChannelProgressivePromise newProgressivePromise() {
      return this.pipeline().newProgressivePromise();
   }

   @Override
   default ChannelFuture newSucceededFuture() {
      return this.pipeline().newSucceededFuture();
   }

   @Override
   default ChannelFuture newFailedFuture(Throwable cause) {
      return this.pipeline().newFailedFuture(cause);
   }

   @Override
   default ChannelPromise voidPromise() {
      return this.pipeline().voidPromise();
   }

   QuicChannel read();

   QuicChannel flush();

   QuicChannelConfig config();

   @Nullable
   SSLEngine sslEngine();

   long peerAllowedStreams(QuicStreamType var1);

   boolean isTimedOut();

   @Nullable
   QuicTransportParameters peerTransportParameters();

   @Nullable
   QuicConnectionAddress localAddress();

   @Nullable
   QuicConnectionAddress remoteAddress();

   @Nullable
   SocketAddress localSocketAddress();

   @Nullable
   SocketAddress remoteSocketAddress();

   default Future<QuicStreamChannel> createStream(QuicStreamType type, @Nullable ChannelHandler handler) {
      return this.createStream(type, handler, this.eventLoop().newPromise());
   }

   Future<QuicStreamChannel> createStream(QuicStreamType var1, @Nullable ChannelHandler var2, Promise<QuicStreamChannel> var3);

   default QuicStreamChannelBootstrap newStreamBootstrap() {
      return new QuicStreamChannelBootstrap(this);
   }

   default ChannelFuture close(boolean applicationClose, int error, ByteBuf reason) {
      return this.close(applicationClose, error, reason, this.newPromise());
   }

   ChannelFuture close(boolean var1, int var2, ByteBuf var3, ChannelPromise var4);

   default Future<QuicConnectionStats> collectStats() {
      return this.collectStats(this.eventLoop().newPromise());
   }

   Future<QuicConnectionStats> collectStats(Promise<QuicConnectionStats> var1);

   default Future<QuicConnectionPathStats> collectPathStats(int pathIdx) {
      return this.collectPathStats(pathIdx, this.eventLoop().newPromise());
   }

   Future<QuicConnectionPathStats> collectPathStats(int var1, Promise<QuicConnectionPathStats> var2);

   static QuicChannelBootstrap newBootstrap(Channel channel) {
      return new QuicChannelBootstrap(channel);
   }
}
