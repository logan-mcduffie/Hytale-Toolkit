package io.netty.channel;

import java.net.SocketAddress;

public interface ChannelOutboundInvoker {
   default ChannelFuture bind(SocketAddress localAddress) {
      return this.bind(localAddress, this.newPromise());
   }

   default ChannelFuture connect(SocketAddress remoteAddress) {
      return this.connect(remoteAddress, this.newPromise());
   }

   default ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.connect(remoteAddress, localAddress, this.newPromise());
   }

   default ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   default ChannelFuture close() {
      return this.close(this.newPromise());
   }

   default ChannelFuture deregister() {
      return this.deregister(this.newPromise());
   }

   ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

   ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

   ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

   ChannelFuture disconnect(ChannelPromise var1);

   ChannelFuture close(ChannelPromise var1);

   ChannelFuture deregister(ChannelPromise var1);

   ChannelOutboundInvoker read();

   default ChannelFuture write(Object msg) {
      return this.write(msg, this.newPromise());
   }

   ChannelFuture write(Object var1, ChannelPromise var2);

   ChannelOutboundInvoker flush();

   ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

   default ChannelFuture writeAndFlush(Object msg) {
      return this.writeAndFlush(msg, this.newPromise());
   }

   ChannelPromise newPromise();

   ChannelProgressivePromise newProgressivePromise();

   ChannelFuture newSucceededFuture();

   ChannelFuture newFailedFuture(Throwable var1);

   ChannelPromise voidPromise();
}
