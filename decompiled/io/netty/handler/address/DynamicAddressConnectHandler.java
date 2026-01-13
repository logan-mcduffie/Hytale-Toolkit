package io.netty.handler.address;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public abstract class DynamicAddressConnectHandler extends ChannelOutboundHandlerAdapter {
   @Override
   public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      SocketAddress remote;
      SocketAddress local;
      try {
         remote = this.remoteAddress(remoteAddress, localAddress);
         local = this.localAddress(remoteAddress, localAddress);
      } catch (Exception var8) {
         promise.setFailure(var8);
         return;
      }

      ctx.connect(remote, local, promise).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
               future.channel().pipeline().remove(DynamicAddressConnectHandler.this);
            }
         }
      });
   }

   protected SocketAddress localAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      return localAddress;
   }

   protected SocketAddress remoteAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      return remoteAddress;
   }
}
