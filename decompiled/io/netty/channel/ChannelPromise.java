package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public interface ChannelPromise extends ChannelFuture, Promise<Void> {
   @Override
   Channel channel();

   ChannelPromise setSuccess(Void var1);

   ChannelPromise setSuccess();

   boolean trySuccess();

   ChannelPromise setFailure(Throwable var1);

   ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelPromise sync() throws InterruptedException;

   ChannelPromise syncUninterruptibly();

   ChannelPromise await() throws InterruptedException;

   ChannelPromise awaitUninterruptibly();

   ChannelPromise unvoid();
}
