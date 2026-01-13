package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Iterator;

public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture> {
   ChannelGroup group();

   ChannelFuture find(Channel var1);

   @Override
   boolean isSuccess();

   ChannelGroupException cause();

   boolean isPartialSuccess();

   boolean isPartialFailure();

   ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

   ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1);

   ChannelGroupFuture await() throws InterruptedException;

   ChannelGroupFuture awaitUninterruptibly();

   ChannelGroupFuture syncUninterruptibly();

   ChannelGroupFuture sync() throws InterruptedException;

   @Override
   Iterator<ChannelFuture> iterator();
}
