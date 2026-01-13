package io.netty.util.concurrent;

public interface EventExecutor extends EventExecutorGroup, ThreadAwareExecutor {
   EventExecutorGroup parent();

   @Override
   default boolean isExecutorThread(Thread thread) {
      return this.inEventLoop(thread);
   }

   default boolean inEventLoop() {
      return this.inEventLoop(Thread.currentThread());
   }

   boolean inEventLoop(Thread var1);

   default <V> Promise<V> newPromise() {
      return new DefaultPromise<>(this);
   }

   default <V> ProgressivePromise<V> newProgressivePromise() {
      return new DefaultProgressivePromise<>(this);
   }

   default <V> Future<V> newSucceededFuture(V result) {
      return new SucceededFuture<>(this, result);
   }

   default <V> Future<V> newFailedFuture(Throwable cause) {
      return new FailedFuture<>(this, cause);
   }

   default boolean isSuspended() {
      return false;
   }

   default boolean trySuspend() {
      return false;
   }
}
