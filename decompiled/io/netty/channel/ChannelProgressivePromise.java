package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;

public interface ChannelProgressivePromise extends ProgressivePromise<Void>, ChannelProgressiveFuture, ChannelPromise {
   ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelProgressivePromise sync() throws InterruptedException;

   ChannelProgressivePromise syncUninterruptibly();

   ChannelProgressivePromise await() throws InterruptedException;

   ChannelProgressivePromise awaitUninterruptibly();

   ChannelProgressivePromise setSuccess(Void var1);

   ChannelProgressivePromise setSuccess();

   ChannelProgressivePromise setFailure(Throwable var1);

   ChannelProgressivePromise setProgress(long var1, long var3);

   ChannelProgressivePromise unvoid();
}
