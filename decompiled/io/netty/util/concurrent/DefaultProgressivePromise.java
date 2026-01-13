package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

public class DefaultProgressivePromise<V> extends DefaultPromise<V> implements ProgressivePromise<V> {
   public DefaultProgressivePromise(EventExecutor executor) {
      super(executor);
   }

   protected DefaultProgressivePromise() {
   }

   @Override
   public ProgressivePromise<V> setProgress(long progress, long total) {
      if (total < 0L) {
         total = -1L;
         ObjectUtil.checkPositiveOrZero(progress, "progress");
      } else if (progress < 0L || progress > total) {
         throw new IllegalArgumentException("progress: " + progress + " (expected: 0 <= progress <= total (" + total + "))");
      }

      if (this.isDone()) {
         throw new IllegalStateException("complete already");
      } else {
         this.notifyProgressiveListeners(progress, total);
         return this;
      }
   }

   @Override
   public boolean tryProgress(long progress, long total) {
      if (total < 0L) {
         total = -1L;
         if (progress < 0L || this.isDone()) {
            return false;
         }
      } else if (progress < 0L || progress > total || this.isDone()) {
         return false;
      }

      this.notifyProgressiveListeners(progress, total);
      return true;
   }

   @Override
   public ProgressivePromise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
      super.addListener(listener);
      return this;
   }

   @Override
   public ProgressivePromise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
      super.addListeners(listeners);
      return this;
   }

   @Override
   public ProgressivePromise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
      super.removeListener(listener);
      return this;
   }

   @Override
   public ProgressivePromise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
      super.removeListeners(listeners);
      return this;
   }

   @Override
   public ProgressivePromise<V> sync() throws InterruptedException {
      super.sync();
      return this;
   }

   @Override
   public ProgressivePromise<V> syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   @Override
   public ProgressivePromise<V> await() throws InterruptedException {
      super.await();
      return this;
   }

   @Override
   public ProgressivePromise<V> awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   @Override
   public ProgressivePromise<V> setSuccess(V result) {
      super.setSuccess(result);
      return this;
   }

   @Override
   public ProgressivePromise<V> setFailure(Throwable cause) {
      super.setFailure(cause);
      return this;
   }
}
