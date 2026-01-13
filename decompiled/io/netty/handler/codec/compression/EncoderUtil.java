package io.netty.handler.codec.compression;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class EncoderUtil {
   private static final int THREAD_POOL_DELAY_SECONDS = 10;

   static void closeAfterFinishEncode(final ChannelHandlerContext ctx, ChannelFuture finishFuture, final ChannelPromise promise) {
      if (!finishFuture.isDone()) {
         final Future<?> future = ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
               ctx.close(promise);
            }
         }, 10L, TimeUnit.SECONDS);
         finishFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) {
               future.cancel(true);
               if (!promise.isDone()) {
                  ctx.close(promise);
               }
            }
         });
      } else {
         ctx.close(promise);
      }
   }

   private EncoderUtil() {
   }
}
