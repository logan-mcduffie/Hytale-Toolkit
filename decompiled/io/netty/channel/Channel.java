package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {
   ChannelId id();

   EventLoop eventLoop();

   Channel parent();

   ChannelConfig config();

   boolean isOpen();

   boolean isRegistered();

   boolean isActive();

   ChannelMetadata metadata();

   SocketAddress localAddress();

   SocketAddress remoteAddress();

   ChannelFuture closeFuture();

   default boolean isWritable() {
      ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
      return buf != null && buf.isWritable();
   }

   default long bytesBeforeUnwritable() {
      ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
      return buf != null ? buf.bytesBeforeUnwritable() : 0L;
   }

   default long bytesBeforeWritable() {
      ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
      return buf != null ? buf.bytesBeforeWritable() : Long.MAX_VALUE;
   }

   Channel.Unsafe unsafe();

   ChannelPipeline pipeline();

   default ByteBufAllocator alloc() {
      return this.config().getAllocator();
   }

   default <T> T getOption(ChannelOption<T> option) {
      return this.config().getOption(option);
   }

   default <T> boolean setOption(ChannelOption<T> option, T value) {
      return this.config().setOption(option, value);
   }

   default Channel read() {
      this.pipeline().read();
      return this;
   }

   default Channel flush() {
      this.pipeline().flush();
      return this;
   }

   @Override
   default ChannelFuture writeAndFlush(Object msg) {
      return this.pipeline().writeAndFlush(msg);
   }

   @Override
   default ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      return this.pipeline().writeAndFlush(msg, promise);
   }

   @Override
   default ChannelFuture write(Object msg, ChannelPromise promise) {
      return this.pipeline().write(msg, promise);
   }

   @Override
   default ChannelFuture write(Object msg) {
      return this.pipeline().write(msg);
   }

   @Override
   default ChannelFuture deregister(ChannelPromise promise) {
      return this.pipeline().deregister(promise);
   }

   @Override
   default ChannelFuture close(ChannelPromise promise) {
      return this.pipeline().close(promise);
   }

   @Override
   default ChannelFuture disconnect(ChannelPromise promise) {
      return this.pipeline().disconnect(promise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      return this.pipeline().connect(remoteAddress, localAddress, promise);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.pipeline().connect(remoteAddress, promise);
   }

   @Override
   default ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
      return this.pipeline().bind(localAddress, promise);
   }

   @Override
   default ChannelFuture deregister() {
      return this.pipeline().deregister();
   }

   @Override
   default ChannelFuture close() {
      return this.pipeline().close();
   }

   @Override
   default ChannelFuture disconnect() {
      return this.pipeline().disconnect();
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.pipeline().connect(remoteAddress, localAddress);
   }

   @Override
   default ChannelFuture connect(SocketAddress remoteAddress) {
      return this.pipeline().connect(remoteAddress);
   }

   @Override
   default ChannelFuture bind(SocketAddress localAddress) {
      return this.pipeline().bind(localAddress);
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

   public interface Unsafe {
      RecvByteBufAllocator.Handle recvBufAllocHandle();

      SocketAddress localAddress();

      SocketAddress remoteAddress();

      void register(EventLoop var1, ChannelPromise var2);

      void bind(SocketAddress var1, ChannelPromise var2);

      void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

      void disconnect(ChannelPromise var1);

      void close(ChannelPromise var1);

      void closeForcibly();

      void deregister(ChannelPromise var1);

      void beginRead();

      void write(Object var1, ChannelPromise var2);

      void flush();

      ChannelPromise voidPromise();

      ChannelOutboundBuffer outboundBuffer();
   }
}
