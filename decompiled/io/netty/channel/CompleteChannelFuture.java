package io.netty.channel;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;

abstract class CompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {
   private final Channel channel;

   protected CompleteChannelFuture(Channel channel, EventExecutor executor) {
      super(executor);
      this.channel = ObjectUtil.checkNotNull(channel, "channel");
   }

   @Override
   protected EventExecutor executor() {
      EventExecutor e = super.executor();
      return (EventExecutor)(e == null ? this.channel().eventLoop() : e);
   }

   @Override
   public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      super.addListener(listener);
      return this;
   }

   @Override
   public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      super.addListeners(listeners);
      return this;
   }

   @Override
   public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      super.removeListener(listener);
      return this;
   }

   @Override
   public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      super.removeListeners(listeners);
      return this;
   }

   @Override
   public ChannelFuture syncUninterruptibly() {
      return this;
   }

   @Override
   public ChannelFuture sync() throws InterruptedException {
      return this;
   }

   @Override
   public ChannelFuture await() throws InterruptedException {
      return this;
   }

   @Override
   public ChannelFuture awaitUninterruptibly() {
      return this;
   }

   @Override
   public Channel channel() {
      return this.channel;
   }

   public Void getNow() {
      return null;
   }

   @Override
   public boolean isVoid() {
      return false;
   }
}
