package io.netty.handler.codec.quic;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DuplexChannel;
import java.net.SocketAddress;
import org.jetbrains.annotations.Nullable;

public interface QuicStreamChannel extends DuplexChannel {
   ChannelFutureListener SHUTDOWN_OUTPUT = f -> ((QuicStreamChannel)f.channel()).shutdownOutput();

   @Override
   default ChannelFuture bind(SocketAddress socketAddress) {
      return this.pipeline().bind(socketAddress);
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
   default ChannelFuture bind(SocketAddress localAddress, ChannelPromise channelPromise) {
      return this.pipeline().bind(localAddress, channelPromise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise channelPromise) {
      return this.pipeline().connect(remoteAddress, channelPromise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise channelPromise) {
      return this.pipeline().connect(remoteAddress, localAddress, channelPromise);
   }

   @Override
   default ChannelFuture disconnect(ChannelPromise channelPromise) {
      return this.pipeline().disconnect(channelPromise);
   }

   @Override
   default ChannelFuture close(ChannelPromise channelPromise) {
      return this.pipeline().close(channelPromise);
   }

   @Override
   default ChannelFuture deregister(ChannelPromise channelPromise) {
      return this.pipeline().deregister(channelPromise);
   }

   @Override
   default ChannelFuture write(Object msg) {
      return this.pipeline().write(msg);
   }

   @Override
   default ChannelFuture write(Object msg, ChannelPromise channelPromise) {
      return this.pipeline().write(msg, channelPromise);
   }

   @Override
   default ChannelFuture writeAndFlush(Object msg, ChannelPromise channelPromise) {
      return this.pipeline().writeAndFlush(msg, channelPromise);
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

   @Override
   default ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   @Override
   default ChannelFuture shutdownInput(ChannelPromise promise) {
      return this.shutdownInput(0, promise);
   }

   @Override
   default ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   @Override
   default ChannelFuture shutdown() {
      return this.shutdown(this.newPromise());
   }

   default ChannelFuture shutdown(int error) {
      return this.shutdown(error, this.newPromise());
   }

   ChannelFuture shutdown(int var1, ChannelPromise var2);

   default ChannelFuture shutdownInput(int error) {
      return this.shutdownInput(error, this.newPromise());
   }

   ChannelFuture shutdownInput(int var1, ChannelPromise var2);

   default ChannelFuture shutdownOutput(int error) {
      return this.shutdownOutput(error, this.newPromise());
   }

   ChannelFuture shutdownOutput(int var1, ChannelPromise var2);

   QuicStreamAddress localAddress();

   QuicStreamAddress remoteAddress();

   boolean isLocalCreated();

   QuicStreamType type();

   long streamId();

   @Nullable
   QuicStreamPriority priority();

   default ChannelFuture updatePriority(QuicStreamPriority priority) {
      return this.updatePriority(priority, this.newPromise());
   }

   ChannelFuture updatePriority(QuicStreamPriority var1, ChannelPromise var2);

   QuicChannel parent();

   QuicStreamChannel read();

   QuicStreamChannel flush();

   QuicStreamChannelConfig config();
}
