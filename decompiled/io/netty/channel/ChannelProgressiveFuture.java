package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture extends ChannelFuture, ProgressiveFuture<Void> {
   ChannelProgressiveFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelProgressiveFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelProgressiveFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelProgressiveFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelProgressiveFuture sync() throws InterruptedException;

   ChannelProgressiveFuture syncUninterruptibly();

   ChannelProgressiveFuture await() throws InterruptedException;

   ChannelProgressiveFuture awaitUninterruptibly();
}
