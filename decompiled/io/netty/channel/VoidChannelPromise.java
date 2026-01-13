package io.netty.channel;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public final class VoidChannelPromise extends AbstractFuture<Void> implements ChannelPromise {
   private final Channel channel;
   private final ChannelFutureListener fireExceptionListener;

   public VoidChannelPromise(Channel channel, boolean fireException) {
      ObjectUtil.checkNotNull(channel, "channel");
      this.channel = channel;
      if (fireException) {
         this.fireExceptionListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
               Throwable cause = future.cause();
               if (cause != null) {
                  VoidChannelPromise.this.fireException0(cause);
               }
            }
         };
      } else {
         this.fireExceptionListener = null;
      }
   }

   public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      fail();
      return this;
   }

   public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      fail();
      return this;
   }

   public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      return this;
   }

   public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      return this;
   }

   public VoidChannelPromise await() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this;
      }
   }

   @Override
   public boolean await(long timeout, TimeUnit unit) {
      fail();
      return false;
   }

   @Override
   public boolean await(long timeoutMillis) {
      fail();
      return false;
   }

   public VoidChannelPromise awaitUninterruptibly() {
      fail();
      return this;
   }

   @Override
   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
      fail();
      return false;
   }

   @Override
   public boolean awaitUninterruptibly(long timeoutMillis) {
      fail();
      return false;
   }

   @Override
   public Channel channel() {
      return this.channel;
   }

   @Override
   public boolean isDone() {
      return false;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   @Override
   public boolean setUncancellable() {
      return true;
   }

   @Override
   public boolean isCancellable() {
      return false;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public Throwable cause() {
      return null;
   }

   public VoidChannelPromise sync() {
      fail();
      return this;
   }

   public VoidChannelPromise syncUninterruptibly() {
      fail();
      return this;
   }

   public VoidChannelPromise setFailure(Throwable cause) {
      this.fireException0(cause);
      return this;
   }

   public VoidChannelPromise setSuccess() {
      return this;
   }

   @Override
   public boolean tryFailure(Throwable cause) {
      this.fireException0(cause);
      return false;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      return false;
   }

   @Override
   public boolean trySuccess() {
      return false;
   }

   private static void fail() {
      throw new IllegalStateException("void future");
   }

   public VoidChannelPromise setSuccess(Void result) {
      return this;
   }

   public boolean trySuccess(Void result) {
      return false;
   }

   public Void getNow() {
      return null;
   }

   @Override
   public ChannelPromise unvoid() {
      ChannelPromise promise = new DefaultChannelPromise(this.channel);
      if (this.fireExceptionListener != null) {
         promise.addListener(this.fireExceptionListener);
      }

      return promise;
   }

   @Override
   public boolean isVoid() {
      return true;
   }

   private void fireException0(Throwable cause) {
      if (this.fireExceptionListener != null && this.channel.isRegistered()) {
         this.channel.pipeline().fireExceptionCaught(cause);
      }
   }
}
