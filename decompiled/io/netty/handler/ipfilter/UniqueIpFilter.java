package io.netty.handler.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class UniqueIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final Set<InetAddress> connected = ConcurrentHashMap.newKeySet();

   protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
      final InetAddress remoteIp = remoteAddress.getAddress();
      if (!this.connected.add(remoteIp)) {
         return false;
      } else {
         ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
               UniqueIpFilter.this.connected.remove(remoteIp);
            }
         });
         return true;
      }
   }
}
