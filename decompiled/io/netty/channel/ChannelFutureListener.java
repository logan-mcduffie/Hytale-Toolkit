package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFutureListener extends GenericFutureListener<ChannelFuture> {
   ChannelFutureListener CLOSE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) {
         future.channel().close();
      }
   };
   ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) {
         if (!future.isSuccess()) {
            future.channel().close();
         }
      }
   };
   ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) {
         if (!future.isSuccess()) {
            future.channel().pipeline().fireExceptionCaught(future.cause());
         }
      }
   };
}
